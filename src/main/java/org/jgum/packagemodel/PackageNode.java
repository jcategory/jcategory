package org.jgum.packagemodel;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static org.jgum.graph.PropertyIterable.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgum.JGum;
import org.jgum.graph.Node;
import org.jgum.graph.SearchStrategy;
import org.jgum.graph.StopUntilConditionIterable;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class PackageNode extends Node<String> {

	private Map<String, PackageNode> children; //the children nodes
	private PackageNode parent; //the parent node
	private String packageName; //the full name of the package, lazily initialized
	
	static List<String> asPackageFragmentsList(String packageName) {
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
		super(context, "");
		parent = null;
		children = new TreeMap<>(); //to preserve insertion order
	}
	
	/**
	 * 
	 * @param packageFragment the name of this node package fragment.
	 * @param parent the parent node 
	 */
	protected PackageNode(JGum context, String packageFragment, PackageNode parent) {
		super(context, packageFragment);
		checkArgument( (parent != null && !packageFragment.isEmpty()) || (parent == null && packageFragment.isEmpty()) );
		this.parent = parent;
		children = new TreeMap<>();
	}

	public List<PackageNode> getChildren() {
		return new ArrayList<>(children.values());
	}
	
	public PackageNode getParent() {
		return parent;
	}
	
	public Object get(String relativePackageName, Object key) {
		PackageNode packageNode = getNode(relativePackageName);
		if(packageNode == null)
			return null;
		else
			return packageNode.get(key);
	}
	
	public PackageNode getNode(String relativePackageName) {
		return getNode(relativePackageName, false);
	}
	
	protected PackageNode getOrCreateNode(String relativePackageName) {
		return getNode(relativePackageName, true);
	}
	
	private PackageNode getNode(String relativePackageName, boolean createIfAbsent) {
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
	
	private PackageNode getChild(String subpackageName) {
		return children.get(subpackageName);
	}

	private PackageNode getOrCreateChild(String subpackageName) {
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
	
	public PackageRoot getRoot() {
		PackageNode root = this;
		while(!root.isRoot())
			root = root.getParent();
		return (PackageRoot)root;
	}
	
	/**
	 * 
	 * @return the package name of this node
	 */
	public String getPackageName() {
		if(packageName == null) {
			StringBuilder sb = new StringBuilder();
			if(!isRoot() && !parent.isRoot()) {
				sb.append(parent.getPackageName());
				sb.append(".");
			}
			sb.append(getValue());
			packageName = sb.toString();
		}
		return packageName;
	}
	
	@Override
	public FluentIterable<PackageNode> bottomUpPath() {
		return path(getContext().getBottomUpPackageTraversalPolicy());
	}

	public <U> FluentIterable<U> bottomUpPathProperties(Object key) {
		return properties(bottomUpPath(), key);
	}
	
	@Override
	public FluentIterable<PackageNode> topDownPath() {
		return path(getContext().getTopDownPackageTraversalPolicy());
	}
	
	public FluentIterable<PackageNode> topDownPath(String relativePackageName) {
		Iterable<PackageNode> bottomUpIterable = getOrCreateNode(relativePackageName).path(new BottomUpPackageTraversalPolicy(SearchStrategy.PRE_ORDER));
		Iterable<PackageNode> filteredBottomUpIterable = new StopUntilConditionIterable(bottomUpIterable, new Predicate<PackageNode>() {
			@Override
			public boolean apply(PackageNode node) {
				return PackageNode.this.equals(node);
			}
		});
		Iterable<PackageNode> topDownIterable = Lists.reverse(Lists.newArrayList(filteredBottomUpIterable));
		return FluentIterable.from(topDownIterable);
	}
	
	public <U> FluentIterable<U> topDownPathProperties(String relativePackageName, Object key) {
		return properties(topDownPath(relativePackageName), key);
	}
	
	@Override
	public String toString() {
		return getPackageName() + super.toString();
	}

}
