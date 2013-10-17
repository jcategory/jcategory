package org.jgum.packagemodel;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgum.path.Path;
import org.jgum.path.PropertiesNode;
import org.jgum.path.SearchStrategy;

public class PackagePropertiesNode extends PropertiesNode {
	
	private Map<String, PackagePropertiesNode> children; //the children nodes
	private PackagePropertiesNode parent; //the parent node
	private String packageFragment; //the package fragment name (i.e., the last sub-package in the full package name)
	
	
	public static List<String> asPackageFragmentsList(String packageName) {
		List<String> packageFragmentsList;
		if(packageName.isEmpty())
			packageFragmentsList = new ArrayList<>();
		else {
			String[] packageFragments = packageName.split("\\.");
			packageFragmentsList = asList(packageFragments);
		}
		return packageFragmentsList;
	}
	
	/**
	 * Creates a root PackagePropertiesNode
	 */
	protected PackagePropertiesNode() {
		packageFragment = "";
		parent = null;
		children = new TreeMap<>(); //to preserve insertion order
	}
	
	/**
	 * 
	 * @param packageFragment the name of this node package fragment
	 * @param parent the parent node 
	 */
	public PackagePropertiesNode(String packageFragment, PackagePropertiesNode parent) {
		this();
		requireNonNull(packageFragment);
		checkArgument( (parent != null && !packageFragment.isEmpty()) || (parent == null && packageFragment.isEmpty()) );
		this.packageFragment = packageFragment;
		this.parent = parent;
		
	}
	
	public Collection<PackagePropertiesNode> getSubpackages() {
		return children.values();
	}
	
	public PackagePropertiesNode getParent() {
		return parent;
	}
	
	public Object get(String relativePackageName, Object key) {
		PackagePropertiesNode packagePropertiesNode = getDescendant(relativePackageName);
		if(packagePropertiesNode == null)
			return null;
		else
			return packagePropertiesNode.get(key);
	}
	
	public PackagePropertiesNode getDescendant(String relativePackageName) {
		return getDescendant(relativePackageName, false);
	}
	
	public PackagePropertiesNode getOrCreateDescendant(String relativePackageName) {
		return getDescendant(relativePackageName, true);
	}
	
	private PackagePropertiesNode getDescendant(String relativePackageName, boolean createIfAbsent) {
		List<String> packageFragmentsList = asPackageFragmentsList(relativePackageName);
		PackagePropertiesNode packagePropertiesNode = this;
		for(String packageFragmentName : packageFragmentsList) {
			if(createIfAbsent)
				packagePropertiesNode = packagePropertiesNode.getOrCreateChild(packageFragmentName);
			else {
				packagePropertiesNode = packagePropertiesNode.getChild(packageFragmentName);
				if(packagePropertiesNode == null)
					break;
			}
		}
		return packagePropertiesNode;
	}
	
	public PackagePropertiesNode getChild(String subpackageName) {
		return children.get(subpackageName);
	}

	public PackagePropertiesNode getOrCreateChild(String subpackageName) {
		PackagePropertiesNode child = children.get(subpackageName);
		if(child == null)
			child = addChild(subpackageName);
		return child;
	}
	
	private PackagePropertiesNode addChild(String subpackageName) {
		PackagePropertiesNode child = new PackagePropertiesNode(subpackageName, this);
		children.put(subpackageName, child);
		return child;
	}
	
	/**
	 * 
	 * @return if the current Node corresponds to the root package.
	 */
	public boolean isRoot() {
		return parent == null;
	}
	
	public PackageHierarchyRoot getRoot() {
		PackagePropertiesNode root = this;
		while(!root.isRoot())
			root = root.getParent();
		return (PackageHierarchyRoot)root;
	}
	
	/**
	 * 
	 * @return the package name of this node
	 */
	public String getPackageName() {
		StringBuilder sb = new StringBuilder();
		if(!isRoot() && !parent.isRoot()) {
			sb.append(parent.getPackageName());
			sb.append(".");
		}
		sb.append(packageFragment);
		return sb.toString();
	}
	
	public Path<PackagePropertiesNode> pathToRoot() {
		return new Path(new IterableToRoot(this));
	}
	
	public Path<PackagePropertiesNode> pathToDescendant(String relativePackageName) {
		return new Path(new IterableToDescendant(this, relativePackageName));
	}

	public Path<PackagePropertiesNode> allDescendants(SearchStrategy searchStrategy) {
		PackageTraverser packageTraverser = new PackageTraverser();
		Iterable<PackagePropertiesNode> it;
		if(searchStrategy.equals(SearchStrategy.PRE_ORDER))
			it = packageTraverser.preOrderTraversal(this);
		else if(searchStrategy.equals(SearchStrategy.POST_ORDER))
			it = packageTraverser.postOrderTraversal(this);
		else
			it = packageTraverser.breadthFirstTraversal(this);
		return new Path<>(it);
	}
	
}
