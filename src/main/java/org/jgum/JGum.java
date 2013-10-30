package org.jgum;

import org.jgum.category.DuplicatesDetection;
import org.jgum.category.SearchStrategy;
import org.jgum.category.name.BottomUpNameTraversalPolicy;
import org.jgum.category.name.NameCategory;
import org.jgum.category.name.NameHierarchy;
import org.jgum.category.name.TopDownNameTraversalPolicy;
import org.jgum.category.type.BottomUpTypeTraversalPolicy;
import org.jgum.category.type.InterfaceOrder;
import org.jgum.category.type.Priority;
import org.jgum.category.type.TopDownTypeTraversalPolicy;
import org.jgum.category.type.TypeCategory;
import org.jgum.category.type.TypeHierarchy;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * Defines a context in which hierarchical meta-object artifacts (like Java classes, interfaces and packages) are associated with properties.
 * Such associations hold in the scope of this context only.
 * @author sergioc
 *
 */
public class JGum {

	/**
	 * Default linearization function for bottom up traversing (given a descendant class) of a graph denoting a class hierarchy.
	 */
	public static final Function<? extends TypeCategory<?>, FluentIterable<? extends TypeCategory<?>>> DEFAULT_BOTTOM_UP_TYPE_LINEARIZATION_FUNCTION = 
			(Function)new BottomUpTypeTraversalPolicy<TypeCategory<?>>(SearchStrategy.PRE_ORDER, Priority.INTERFACES_FIRST, InterfaceOrder.REVERSE, DuplicatesDetection.ENFORCE);
	
	/**
	 * Default linearization function for top down traversing (given an ancestor class) of a graph denoting a class hierarchy.
	 */
	public static final Function<? extends TypeCategory<?>, FluentIterable<? extends TypeCategory<?>>> DEFAULT_TOP_DOWN_TYPE_LINEARIZATION_FUNCTION = 
			(Function)new TopDownTypeTraversalPolicy<TypeCategory<?>>(SearchStrategy.BREADTH_FIRST, Priority.INTERFACES_FIRST, DuplicatesDetection.ENFORCE);
	
	/**
	 * Default linearization function for bottom up traversing (given a name) of a tree denoting a package hierarchy.
	 */
	public static final Function<? extends NameCategory, FluentIterable<? extends NameCategory>> DEFAULT_BOTTOM_UP_NAME_LINEARIZATION_FUNCTION = 
			(Function)new BottomUpNameTraversalPolicy(SearchStrategy.PRE_ORDER);
	
	/**
	 * Default linearization function for top down traversing (given an ancestor name) of a tree denoting a package hierarchy.
	 */
	public static final Function<? extends NameCategory, FluentIterable<? extends NameCategory>> DEFAULT_TOP_DOWN_NAME_LINEARIZATION_FUNCTION = 
			(Function)new TopDownNameTraversalPolicy(SearchStrategy.PRE_ORDER);
	
	
	private final TypeHierarchy typeHierarchy; //the class (and interface) hierarchy graph.
	private final NameHierarchy nameHierarchy; //a name space.
	
	private final Function<? extends TypeCategory<?>, FluentIterable<? extends TypeCategory<?>>> bottomUpTypeLinearizationFunction; //bottom up class linearization function for this context.
	private final Function<? extends TypeCategory<?>, FluentIterable<? extends TypeCategory<?>>> topDownTypeLinearizationFunction; //top down class linearization function for this context.
	private final Function<? extends NameCategory, FluentIterable<? extends NameCategory>> bottomUpNameLinearizationFunction; //bottom up name linearization function for this context.
	private final Function<? extends NameCategory, FluentIterable<? extends NameCategory>> topDownNameLinearizationFunction; //top down name linearization function for this context.

	/**
	 * Creates a new context with default linearization functions.
	 */
	public JGum() {
		this(DEFAULT_BOTTOM_UP_TYPE_LINEARIZATION_FUNCTION, DEFAULT_TOP_DOWN_TYPE_LINEARIZATION_FUNCTION, DEFAULT_BOTTOM_UP_NAME_LINEARIZATION_FUNCTION, DEFAULT_TOP_DOWN_NAME_LINEARIZATION_FUNCTION);
	}
	
	/**
	 * Creates a new context with the given class and name linearization functions.
	 * @param bottomUpTypeLinearizationFunction the bottom up class linearization function.
	 * @param topDownTypeLinearizationFunction the top down class linearization function.
	 * @param bottomUpNameLinearizationFunction the bottom up package linearization function.
	 * @param topDownNameLinearizationFunction the top down package linearization function.
	 */
	public JGum(Function<? extends TypeCategory<?>, FluentIterable<? extends TypeCategory<?>>> bottomUpTypeLinearizationFunction, 
			Function<? extends TypeCategory<?>, FluentIterable<? extends TypeCategory<?>>> topDownTypeLinearizationFunction,
			Function<? extends NameCategory, FluentIterable<? extends NameCategory>> bottomUpNameLinearizationFunction, 
			Function<? extends NameCategory, FluentIterable<? extends NameCategory>> topDownNameLinearizationFunction) {
		this.bottomUpTypeLinearizationFunction = bottomUpTypeLinearizationFunction;
		this.topDownTypeLinearizationFunction = topDownTypeLinearizationFunction;
		this.bottomUpNameLinearizationFunction = bottomUpNameLinearizationFunction;
		this.topDownNameLinearizationFunction = topDownNameLinearizationFunction;
		nameHierarchy = new NameHierarchy(bottomUpNameLinearizationFunction, topDownNameLinearizationFunction);
		typeHierarchy = new TypeHierarchy(bottomUpTypeLinearizationFunction, topDownTypeLinearizationFunction);
	}
	
	/**
	 * 
	 * @return the name hierarchy associated with this context.
	 */
	public NameHierarchy getNameHierarchy() {
		return nameHierarchy;
	}
	
	/**
	 * 
	 * @return the category corresponding to the root in the name hierarchy (the empty name).
	 */
	public NameCategory forNameRoot() {
		return nameHierarchy.getRoot();
	}

	/**
	 * 
	 * @param name the full name (e.g., "a.b.c") of a category.
	 * @return a category corresponding to the given name.
	 */
	public NameCategory forName(String name) {
		return forNameRoot().getOrCreateCategory(name);
	}
	
	/**
	 * 
	 * @param pakkage the package object for which a category is requested.
	 * @return a name category corresponding to the name of the given Package object.
	 */
	public NameCategory forPackage(Package pakkage) {
		return forNameRoot().getOrCreateCategory(pakkage);
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
	 * @param clazz the class (or interface) for which a category is requested.
	 * @return a category corresponding to the given class.
	 */
	public <T> TypeCategory<T> forClass(Class<T> clazz) {
		return typeHierarchy.getOrCreateTypeCategory(clazz);
	}
	
}
