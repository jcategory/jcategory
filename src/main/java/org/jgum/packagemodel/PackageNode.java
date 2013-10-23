package org.jgum.packagemodel;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.jgum.graph.PropertyIterable.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgum.JGum;
import org.jgum.graph.DuplicatesDetection;
import org.jgum.graph.Node;
import org.jgum.graph.PropertyIterable;
import org.jgum.graph.SearchStrategy;
import org.jgum.graph.TraversalPolicy;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class PackageNode extends Node {

	private Map<String, PackageNode> children; //the children nodes
	private PackageNode parent; //the parent node
	private String packageFragment; //the package fragment name (i.e., the last sub-package in the full package name)
	private String packageName; //the full name of the package, lazily initialized
	
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
	
	public String getPackageFragment() {
		return packageFragment;
	}
	
	public List<PackageNode> getSubpackages() {
		return new ArrayList<>(children.values());
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
			sb.append(packageFragment);
			packageName = sb.toString();
		}
		return packageName;
	}
	
	@Override
	public FluentIterable<PackageNode> bottomUpPath() {
		return path(getContext().getBottomUpPackageTraversalPolicy());
	}

	@Override
	public FluentIterable<PackageNode> topDownPath() {
		return path(getContext().getTopDownPackageTraversalPolicy());
	}
	
	public FluentIterable<PackageNode> pathToDescendant(String relativePackageName) {
		return path(new TraversalPolicy<PackageNode>(SearchStrategy.PRE_ORDER, DuplicatesDetection.IGNORE, new ToDescendantFunction(this, relativePackageName)));
	}
	
	public <U> FluentIterable<U> pathToDescendantProperties(String relativePackageName, Object key) {
		return properties(pathToDescendant(relativePackageName), key);
	}
	
	@Override
	public String toString() {
		return getPackageName() + super.toString();
	}

	
	public class ToDescendantFunction implements Function<PackageNode, List<PackageNode>> {

		private final PackageNode firstPackageNode;
		private final Iterable<String> packageFragmentsIterable;
		private Iterator<String> packageFragmentsIterator;
		
		public ToDescendantFunction(PackageNode packageNode, String relativePackageName) {
			this(packageNode, PackageNode.asPackageFragmentsList(relativePackageName));
		}
		
		private ToDescendantFunction(PackageNode packageNode, Iterable<String> packageFragmentsIterable) {
			this.firstPackageNode = packageNode;
			this.packageFragmentsIterable = packageFragmentsIterable;
		}
		
		@Override
		public List<PackageNode> apply(PackageNode packageNode) {
			if(packageNode.equals(firstPackageNode)) {
				packageFragmentsIterator = packageFragmentsIterable.iterator();
			}
			List<PackageNode> children = new ArrayList<>();
			PackageNode child = null;
			if(packageFragmentsIterator.hasNext()) {
				child = packageNode.getChild(packageFragmentsIterator.next());
			}
			if(child != null)
				children.add(child);
			return children;
		}
		
	}

}
