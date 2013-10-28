package org.jgum;

import org.jgum.category.DuplicatesDetection;
import org.jgum.category.SearchStrategy;
import org.jgum.category.name.BottomUpNameTraversalPolicy;
import org.jgum.category.name.NameCategory;
import org.jgum.category.name.NameCategoryRoot;
import org.jgum.category.name.NameHierarchy;
import org.jgum.category.name.TopDownNameTraversalPolicy;
import org.jgum.category.type.BottomUpTypeTraversalPolicy;
import org.jgum.category.type.InterfaceOrder;
import org.jgum.category.type.Priority;
import org.jgum.category.type.TopDownTypeTraversalPolicy;
import org.jgum.category.type.TypeCategory;
import org.jgum.category.type.TypeHierarchy;

/**
 * Defines a context for a hierarchical graph of Java meta-object artifacts (classes, interfaces and packages).
 * Those meta-objects can be associated with custom properties. Such associations holds in this context only.
 * It also provides convenient routines for traversing a graph of meta-objects and querying properties associated with the graph nodes.
 * @author sergioc
 *
 */
public class JGum {

	/**
	 * Default strategy for bottom up traversing (given a descendant class ) of a graph denoting a class hierarchy.
	 */
	public static final BottomUpTypeTraversalPolicy<TypeCategory<?>> DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY = 
			new BottomUpTypeTraversalPolicy<>(SearchStrategy.PRE_ORDER, Priority.INTERFACES_FIRST, InterfaceOrder.REVERSE, DuplicatesDetection.ENFORCE);
	
	/**
	 * Default strategy for top down traversing (given an ancestor class) of a graph denoting a class hierarchy.
	 */
	public static final TopDownTypeTraversalPolicy<TypeCategory<?>> DEFAULT_TOP_DOWN_CLASS_TRAVERSAL_POLICY = 
			new TopDownTypeTraversalPolicy<>(SearchStrategy.BREADTH_FIRST, Priority.INTERFACES_FIRST, DuplicatesDetection.ENFORCE);
	
	/**
	 * Default strategy for bottom up traversing (given a subpackage) of a tree denoting a package hierarchy.
	 */
	public static final BottomUpNameTraversalPolicy DEFAULT_BOTTOM_UP_PACKAGE_TRAVERSAL_POLICY = 
			new BottomUpNameTraversalPolicy(SearchStrategy.PRE_ORDER);
	
	/**
	 * Default strategy for top down traversing (given an ancestor package) of a tree denoting a package hierarchy.
	 */
	public static final TopDownNameTraversalPolicy DEFAULT_TOP_DOWN_PACKAGE_TRAVERSAL_POLICY = 
			new TopDownNameTraversalPolicy(SearchStrategy.PRE_ORDER);
	
	
	private final TypeHierarchy typeHierarchy; //the class (and interface) hierarchy graph.
	private final NameHierarchy nameHierarchy; //the package tree.
	
	private final BottomUpTypeTraversalPolicy<TypeCategory<?>> bottomUpTypeTraversalPolicy; //bottom up class traversing strategy for this context.
	private final TopDownTypeTraversalPolicy<TypeCategory<?>> topDownTypeTraversalPolicy; //top down class traversing strategy for this context.
	private final BottomUpNameTraversalPolicy bottomUpNameTraversalPolicy; //bottom up package traversing strategy for this context.
	private final TopDownNameTraversalPolicy topDownNameTraversalPolicy; //top down package traversing strategy for this context.

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
	public JGum(BottomUpTypeTraversalPolicy<TypeCategory<?>> bottomUpTypeTraversalPolicy, TopDownTypeTraversalPolicy<TypeCategory<?>> topDownTypeTraversalPolicy) {
		this(bottomUpTypeTraversalPolicy, topDownTypeTraversalPolicy, DEFAULT_BOTTOM_UP_PACKAGE_TRAVERSAL_POLICY, DEFAULT_TOP_DOWN_PACKAGE_TRAVERSAL_POLICY);
	}
	
	/**
	 * Creates a new context with the given package traversing strategies.
	 * Uses default traversing strategies for class hierarchies.
	 * @param bottomUpNameTraversalPolicy the bottom up package traversing strategy.
	 * @param topDownNameTraversalPolicy the top down package traversing strategy.
	 */
	public JGum(BottomUpNameTraversalPolicy bottomUpNameTraversalPolicy, TopDownNameTraversalPolicy topDownNameTraversalPolicy) {
		this(DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY, DEFAULT_TOP_DOWN_CLASS_TRAVERSAL_POLICY, bottomUpNameTraversalPolicy, topDownNameTraversalPolicy);
	}
	
	/**
	 * Creates a new context with the given class and package traversing strategies.
	 * @param bottomUpTypeTraversalPolicy the bottom up class traversing strategy.
	 * @param topDownTypeTraversalPolicy the top down class traversing strategy.
	 * @param bottomUpNameTraversalPolicy the bottom up package traversing strategy.
	 * @param topDownNameTraversalPolicy the top down package traversing strategy.
	 */
	public JGum(BottomUpTypeTraversalPolicy<TypeCategory<?>> bottomUpTypeTraversalPolicy, TopDownTypeTraversalPolicy<TypeCategory<?>> topDownTypeTraversalPolicy,
			BottomUpNameTraversalPolicy bottomUpNameTraversalPolicy, TopDownNameTraversalPolicy topDownNameTraversalPolicy) {
		this.bottomUpTypeTraversalPolicy = bottomUpTypeTraversalPolicy;
		this.topDownTypeTraversalPolicy = topDownTypeTraversalPolicy;
		this.bottomUpNameTraversalPolicy = bottomUpNameTraversalPolicy;
		this.topDownNameTraversalPolicy = topDownNameTraversalPolicy;
		nameHierarchy = new NameHierarchy(this);
		typeHierarchy = new TypeHierarchy(this);
	}
	
	/**
	 * 
	 * @return the package tree associated with this context.
	 */
	public NameHierarchy getPackageTree() {
		return nameHierarchy;
	}
	
	/**
	 * 
	 * @return the node corresponding to the root package.
	 */
	public NameCategoryRoot forPackageRoot() {
		return nameHierarchy.getRoot();
	}

	/**
	 * 
	 * @param packageName the package name (e.g., "a.b.c") for which a node is requested.
	 * @return a package node corresponding to the given package name.
	 */
	public NameCategory forPackage(String packageName) {
		return forPackageRoot().getOrCreateNode(packageName);
	}
	
	/**
	 * 
	 * @param pakkage the package object for which a node is requested.
	 * @return a package node corresponding to the given Package object.
	 */
	public NameCategory forPackage(Package pakkage) {
		return forPackageRoot().getOrCreateNode(pakkage);
	}
	
	/**
	 * 
	 * @return the class hierarchy graph associated with this context.
	 */
	public TypeHierarchy getTypeHierarchy() {
		return typeHierarchy;
	}

	/**
	 * 
	 * @param clazz the class (or interface) for which a node is requested.
	 * @return a node corresponding to the given class.
	 */
	public <T> TypeCategory<T> forClass(Class<T> clazz) {
		return typeHierarchy.getOrCreateTypeCategory(clazz);
	}
	
	/**
	 * 
	 * @return the bottom up class traversing strategy for this context.
	 */
	public BottomUpTypeTraversalPolicy<TypeCategory<?>> getBottomUpTypeTraversalPolicy() {
		return bottomUpTypeTraversalPolicy;
	}

	/**
	 * 
	 * @return the top down class traversing strategy for this context.
	 */
	public TopDownTypeTraversalPolicy<TypeCategory<?>> getTopDownTypeTraversalPolicy() {
		return topDownTypeTraversalPolicy;
	}

	/**
	 * 
	 * @return the bottom up package traversing strategy for this context.
	 */
	public BottomUpNameTraversalPolicy getBottomUpNameTraversalPolicy() {
		return bottomUpNameTraversalPolicy;
	}

	/**
	 * 
	 * @return the top down package traversing strategy for this context.
	 */
	public TopDownNameTraversalPolicy getTopDownNameTraversalPolicy() {
		return topDownNameTraversalPolicy;
	}
	
}
