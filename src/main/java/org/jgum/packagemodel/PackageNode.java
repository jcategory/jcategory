package org.jgum.packagemodel;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgum.JGum;
import org.jgum.path.JGumNode;
import org.jgum.path.Path;
import org.jgum.path.SearchStrategy;

public class PackageNode extends JGumNode {
	
	private Map<String, PackageNode> children; //the children nodes
	private PackageNode parent; //the parent node
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
	 * Creates a root PackageNode
	 */
	protected PackageNode(JGum context) {
		super(context);
		packageFragment = "";
		parent = null;
		children = new TreeMap<>(); //to preserve insertion order
	}
	
	/**
	 * 
	 * @param packageFragment the name of this node package fragment
	 * @param parent the parent node 
	 */
	public PackageNode(JGum context, String packageFragment, PackageNode parent) {
		this(context);
		requireNonNull(packageFragment);
		checkArgument( (parent != null && !packageFragment.isEmpty()) || (parent == null && packageFragment.isEmpty()) );
		this.packageFragment = packageFragment;
		this.parent = parent;
		
	}
	
	public Collection<PackageNode> getSubpackages() {
		return children.values();
	}
	
	public PackageNode getParent() {
		return parent;
	}
	
	public Object get(String relativePackageName, Object key) {
		PackageNode packageNode = getDescendant(relativePackageName);
		if(packageNode == null)
			return null;
		else
			return packageNode.get(key);
	}
	
	public PackageNode getDescendant(String relativePackageName) {
		return getDescendant(relativePackageName, false);
	}
	
	public PackageNode getOrCreateDescendant(String relativePackageName) {
		return getDescendant(relativePackageName, true);
	}
	
	private PackageNode getDescendant(String relativePackageName, boolean createIfAbsent) {
		List<String> packageFragmentsList = asPackageFragmentsList(relativePackageName);
		PackageNode packageNode = this;
		for(String packageFragmentName : packageFragmentsList) {
			if(createIfAbsent)
				packageNode = packageNode.getOrCreateChild(packageFragmentName);
			else {
				packageNode = packageNode.getChild(packageFragmentName);
				if(packageNode == null)
					break;
			}
		}
		return packageNode;
	}
	
	public PackageNode getChild(String subpackageName) {
		return children.get(subpackageName);
	}

	public PackageNode getOrCreateChild(String subpackageName) {
		PackageNode child = children.get(subpackageName);
		if(child == null)
			child = addChild(subpackageName);
		return child;
	}
	
	private PackageNode addChild(String subpackageName) {
		PackageNode child = new PackageNode(getContext(), subpackageName, this);
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
		PackageNode root = this;
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
	
	public Path<PackageNode> pathToRoot() {
		return new Path(new IterableToRoot(this));
	}
	
	public Path<PackageNode> pathToDescendant(String relativePackageName) {
		return new Path(new IterableToDescendant(this, relativePackageName));
	}

	public Path<PackageNode> allDescendants() {
		return allDescendants(getContext().getSubPackagesTraversalStrategy());
	}
	
	public Path<PackageNode> allDescendants(SearchStrategy searchStrategy) {
		PackageTraverser packageTraverser = new PackageTraverser();
		Iterable<PackageNode> it;
		if(searchStrategy.equals(SearchStrategy.PRE_ORDER))
			it = packageTraverser.preOrderTraversal(this);
		else if(searchStrategy.equals(SearchStrategy.POST_ORDER))
			it = packageTraverser.postOrderTraversal(this);
		else
			it = packageTraverser.breadthFirstTraversal(this);
		return new Path<>(it);
	}
	
}
