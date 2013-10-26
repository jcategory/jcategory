package org.jgum;

import org.jgum.classmodel.AnyClassRoot;
import org.jgum.classmodel.BottomUpTypeTraversalPolicy;
import org.jgum.classmodel.InterfaceOrder;
import org.jgum.classmodel.Priority;
import org.jgum.classmodel.TopDownTypeTraversalPolicy;
import org.jgum.classmodel.TypeNode;
import org.jgum.graph.DuplicatesDetection;
import org.jgum.graph.SearchStrategy;
import org.jgum.packagemodel.BottomUpPackageTraversalPolicy;
import org.jgum.packagemodel.PackageNode;
import org.jgum.packagemodel.PackageRoot;
import org.jgum.packagemodel.TopDownPackageTraversalPolicy;

/**
 * Defines a context for a hierarchical graph of Java meta-object artifacts (classes, interfaces and packages).
 * Those meta-objects can be associated with custom properties. Such associations holds in this context only.
 * It also provides convenient routines for traversing a graph of meta-objects and querying properties associated with the graph nodes.
 * @author sergioc
 *
 */
public class JGum {

	//Default strategy for bottom up traversing (given a descendant class ) of a graph denoting a class hierarchy.
	public static final BottomUpTypeTraversalPolicy<TypeNode<?>> DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY = 
			new BottomUpTypeTraversalPolicy<>(SearchStrategy.PRE_ORDER, DuplicatesDetection.ENFORCE, Priority.INTERFACES_FIRST, InterfaceOrder.REVERSE);
	
	//Default strategy for top down traversing (given an ancestor class) of a graph denoting a class hierarchy.
	public static final TopDownTypeTraversalPolicy<TypeNode<?>> DEFAULT_TOP_DOWN_CLASS_TRAVERSAL_POLICY = 
			new TopDownTypeTraversalPolicy<>(SearchStrategy.BREADTH_FIRST, DuplicatesDetection.ENFORCE, Priority.INTERFACES_FIRST);
	
	//Default strategy for bottom up traversing (given a subpackage) of a tree denoting a package hierarchy.
	public static final BottomUpPackageTraversalPolicy DEFAULT_BOTTOM_UP_PACKAGE_TRAVERSAL_POLICY = 
			new BottomUpPackageTraversalPolicy(SearchStrategy.PRE_ORDER);
	
	//Default strategy for top down traversing (given an ancestor package) of a tree denoting a package hierarchy.
	public static final TopDownPackageTraversalPolicy DEFAULT_TOP_DOWN_PACKAGE_TRAVERSAL_POLICY = 
			new TopDownPackageTraversalPolicy(SearchStrategy.PRE_ORDER);
	
	private final PackageRoot packageRoot; //the root of the package tree.
	private final AnyClassRoot anyClassRoot; //the root of the class (and interfaces) hierarchy graph.
	
	
	private final BottomUpTypeTraversalPolicy<TypeNode<?>> bottomUpTypeTraversalPolicy; //bottom up class traversing strategy for this context.
	private final TopDownTypeTraversalPolicy<TypeNode<?>> topDownTypeTraversalPolicy; //top down class traversing strategy for this context.
	private final BottomUpPackageTraversalPolicy bottomUpPackageTraversalPolicy; //bottom up package traversing strategy for this context.
	private final TopDownPackageTraversalPolicy topDownPackageTraversalPolicy; //top down package traversing strategy for this context.
	
	/**
	 * Creates a new context with default traversing strategies.
	 */
	public JGum() {
		this(DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY, DEFAULT_TOP_DOWN_CLASS_TRAVERSAL_POLICY, DEFAULT_BOTTOM_UP_PACKAGE_TRAVERSAL_POLICY, DEFAULT_TOP_DOWN_PACKAGE_TRAVERSAL_POLICY);
	}

	/**
	 * Creates a new context with the given class traversing strategies.
	 * Uses default traversing strategies for package hierarchies.
	 * @param bottomUpTypeTraversalPolicy the bottom up class traversing strategy.
	 * @param topDownTypeTraversalPolicy the top down class traversing strategy.
	 */
	public JGum(BottomUpTypeTraversalPolicy<TypeNode<?>> bottomUpTypeTraversalPolicy, TopDownTypeTraversalPolicy<TypeNode<?>> topDownTypeTraversalPolicy) {
		this(bottomUpTypeTraversalPolicy, topDownTypeTraversalPolicy, DEFAULT_BOTTOM_UP_PACKAGE_TRAVERSAL_POLICY, DEFAULT_TOP_DOWN_PACKAGE_TRAVERSAL_POLICY);
	}
	
	/**
	 * Creates a new context with the given package traversing strategies.
	 * Uses default traversing strategies for class hierarchies.
	 * @param bottomUpPackageTraversalPolicy the bottom up package traversing strategy.
	 * @param topDownPackageTraversalPolicy the top down package traversing strategy.
	 */
	public JGum(BottomUpPackageTraversalPolicy bottomUpPackageTraversalPolicy, TopDownPackageTraversalPolicy topDownPackageTraversalPolicy) {
		this(DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY, DEFAULT_TOP_DOWN_CLASS_TRAVERSAL_POLICY, bottomUpPackageTraversalPolicy, topDownPackageTraversalPolicy);
	}
	
	/**
	 * Creates a new context with the given class and package traversing strategies.
	 * @param bottomUpTypeTraversalPolicy the bottom up class traversing strategy.
	 * @param topDownTypeTraversalPolicy the top down class traversing strategy.
	 * @param bottomUpPackageTraversalPolicy the bottom up package traversing strategy.
	 * @param topDownPackageTraversalPolicy the top down package traversing strategy.
	 */
	public JGum(BottomUpTypeTraversalPolicy<TypeNode<?>> bottomUpTypeTraversalPolicy, TopDownTypeTraversalPolicy<TypeNode<?>> topDownTypeTraversalPolicy,
			BottomUpPackageTraversalPolicy bottomUpPackageTraversalPolicy, TopDownPackageTraversalPolicy topDownPackageTraversalPolicy) {
		packageRoot = new PackageRoot(this);
		anyClassRoot = new AnyClassRoot(this);
		this.bottomUpTypeTraversalPolicy = bottomUpTypeTraversalPolicy;
		this.topDownTypeTraversalPolicy = topDownTypeTraversalPolicy;
		this.bottomUpPackageTraversalPolicy = bottomUpPackageTraversalPolicy;
		this.topDownPackageTraversalPolicy = topDownPackageTraversalPolicy;
	}
	
	/**
	 * 
	 * @return the node corresponding to the root package.
	 */
	public PackageRoot forPackageRoot() {
		return packageRoot;
	}

	/**
	 * 
	 * @param packageName the package name (e.g., "a.b.c") for which a node is requested.
	 * @return a package node corresponding to the given package name.
	 */
	public PackageNode forPackage(String packageName) {
		return packageRoot.getOrCreateNode(packageName);
	}
	
	/**
	 * 
	 * @param pakkage the package object for which a node is requested.
	 * @return a package node corresponding to the given Package object.
	 */
	public PackageNode forPackage(Package pakkage) {
		return packageRoot.getOrCreateNode(pakkage);
	}
	
	/**
	 * 
	 * @return a node corresponding to the root of the class and interface hierarchy.
	 */
	public AnyClassRoot forAnyClassRoot() {
		return anyClassRoot;
	}

	/**
	 * 
	 * @param clazz the class (or interface) for which a node is requested.
	 * @return a node corresponding to the given class.
	 */
	public <T> TypeNode<T> forClass(Class<T> clazz) {
		return anyClassRoot.getOrCreateNode(clazz);
	}
	
	public BottomUpTypeTraversalPolicy<TypeNode<?>> getBottomUpTypeTraversalPolicy() {
		return bottomUpTypeTraversalPolicy;
	}

	public TopDownTypeTraversalPolicy<TypeNode<?>> getTopDownTypeTraversalPolicy() {
		return topDownTypeTraversalPolicy;
	}

	public BottomUpPackageTraversalPolicy getBottomUpPackageTraversalPolicy() {
		return bottomUpPackageTraversalPolicy;
	}

	public TopDownPackageTraversalPolicy getTopDownPackageTraversalPolicy() {
		return topDownPackageTraversalPolicy;
	}
	
}
