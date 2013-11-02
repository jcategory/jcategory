package org.jgum;

import org.jgum.category.named.NamedCategorization;
import org.jgum.category.named.NamedCategory;
import org.jgum.category.type.BottomUpTypeTraversalPolicy;
import org.jgum.category.type.InterfaceOrder;
import org.jgum.category.type.Priority;
import org.jgum.category.type.TopDownTypeTraversalPolicy;
import org.jgum.category.type.TypeCategorization;
import org.jgum.category.type.TypeCategory;
import org.jgum.traversal.DuplicatesDetection;
import org.jgum.traversal.SearchStrategy;
import org.jgum.traversal.TraversalPolicy;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * Defines a register of categorizations that includes a named categorization and a type categorization.
 * @author sergioc
 *
 */
public class JGum extends CategorizationContext {

	/**
	 * Default linearization function for bottom up traversing (given a descendant category) of a categorization denoting a type hierarchy.
	 */
	public static final Function<? extends TypeCategory<?>, FluentIterable<? extends TypeCategory<?>>> DEFAULT_BOTTOM_UP_TYPE_LINEARIZATION_FUNCTION = 
			new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.INTERFACES_FIRST, InterfaceOrder.REVERSE, DuplicatesDetection.ENFORCE);
	
	/**
	 * Default linearization function for top down traversing (given an ancestor category) of a categorization denoting a type hierarchy.
	 */
	public static final Function<? extends TypeCategory<?>, FluentIterable<? extends TypeCategory<?>>> DEFAULT_TOP_DOWN_TYPE_LINEARIZATION_FUNCTION = 
			new TopDownTypeTraversalPolicy(SearchStrategy.BREADTH_FIRST, Priority.INTERFACES_FIRST, DuplicatesDetection.ENFORCE);
	
	/**
	 * Default linearization function for bottom up traversing (given a name) of a categorization denoting a named hierarchy.
	 */
	public static final Function<? extends NamedCategory, FluentIterable<? extends NamedCategory>> DEFAULT_BOTTOM_UP_NAME_LINEARIZATION_FUNCTION = 
			TraversalPolicy.bottomUpTraversalPolicy(SearchStrategy.PRE_ORDER, DuplicatesDetection.IGNORE);
	
	/**
	 * Default linearization function for top down traversing (given an ancestor name) of a categorization denoting a named hierarchy.
	 */
	public static final Function<? extends NamedCategory, FluentIterable<? extends NamedCategory>> DEFAULT_TOP_DOWN_NAME_LINEARIZATION_FUNCTION = 
			TraversalPolicy.topDownTraversalPolicy(SearchStrategy.PRE_ORDER, DuplicatesDetection.IGNORE);
	
	
	private final TypeCategorization typeCategorization; //a type categorization.
	private final NamedCategorization namedCategorization; //a named categorization.

	public static final Object JGUM_TYPE_HIERARCHY_ID = new Object(); //the id under which the type hierarchy is registered on the categorization register.
	public static final Object JGUM_NAME_HIERARCHY_ID = new Object(); //the id under which the named hierarchy is registered on the categorization register.
	
	/**
	 * Creates a new context with default linearization functions.
	 */
	public JGum() {
		this(DEFAULT_BOTTOM_UP_TYPE_LINEARIZATION_FUNCTION, DEFAULT_TOP_DOWN_TYPE_LINEARIZATION_FUNCTION, DEFAULT_BOTTOM_UP_NAME_LINEARIZATION_FUNCTION, DEFAULT_TOP_DOWN_NAME_LINEARIZATION_FUNCTION);
	}
	
	/**
	 * Creates a new context with the given bottom up class linearization function.
	 * @param bottomUpTypeLinearizationFunction the bottom up class linearization function.
	 */
	public JGum(Function<? extends TypeCategory<?>, FluentIterable<? extends TypeCategory<?>>> bottomUpTypeLinearizationFunction) {
		this(bottomUpTypeLinearizationFunction, DEFAULT_TOP_DOWN_TYPE_LINEARIZATION_FUNCTION, DEFAULT_BOTTOM_UP_NAME_LINEARIZATION_FUNCTION, DEFAULT_TOP_DOWN_NAME_LINEARIZATION_FUNCTION);
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
			Function<? extends NamedCategory, FluentIterable<? extends NamedCategory>> bottomUpNameLinearizationFunction, 
			Function<? extends NamedCategory, FluentIterable<? extends NamedCategory>> topDownNameLinearizationFunction) {
		
		namedCategorization = new NamedCategorization(bottomUpNameLinearizationFunction, topDownNameLinearizationFunction);
		register(JGUM_TYPE_HIERARCHY_ID, namedCategorization);
		typeCategorization = new TypeCategorization(bottomUpTypeLinearizationFunction, topDownTypeLinearizationFunction);
		register(JGUM_NAME_HIERARCHY_ID, typeCategorization);
	}
	
	/**
	 * 
	 * @return the name hierarchy associated with this context.
	 */
	public NamedCategorization getNameHierarchy() {
		return namedCategorization;
	}
	
	/**
	 * 
	 * @return the category corresponding to the root in the name hierarchy (the empty name).
	 */
	public NamedCategory forNameRoot() {
		return namedCategorization.getRoot();
	}

	/**
	 * 
	 * @param name the full name (e.g., "a.b.c") of a category.
	 * @return a category corresponding to the given name.
	 */
	public NamedCategory forName(String name) {
		return forNameRoot().getOrCreateCategory(name);
	}
	
	/**
	 * 
	 * @param pakkage the package object for which a category is requested.
	 * @return a name category corresponding to the name of the given Package object.
	 */
	public NamedCategory forPackage(Package pakkage) {
		return forNameRoot().getOrCreateCategory(pakkage);
	}
	
	/**
	 * 
	 * @return the class hierarchy graph associated with this context.
	 */
	public TypeCategorization getTypeHierarchy() {
		return typeCategorization;
	}

	/**
	 * 
	 * @param clazz the class (or interface) for which a category is requested.
	 * @return a category corresponding to the given class.
	 */
	public <T> TypeCategory<T> forClass(Class<T> clazz) {
		return typeCategorization.getOrCreateTypeCategory(clazz);
	}
	
}
