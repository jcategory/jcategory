package org.jgum;

import static org.jgum.category.type.BottomUpTypeTraversalPolicy.bottomUpTypeTraversalPolicy;
import static org.jgum.category.type.TopDownTypeTraversalPolicy.topDownTypeTraversalPolicy;
import static org.jgum.traversal.TraversalPolicy.bottomUpTraversalPolicy;
import static org.jgum.traversal.TraversalPolicy.topDownTraversalPolicy;

import java.util.List;

import org.jgum.category.named.NamedCategorization;
import org.jgum.category.named.NamedCategory;
import org.jgum.category.type.BottomUpTypeTraversalPolicy;
import org.jgum.category.type.InterfaceOrder;
import org.jgum.category.type.Priority;
import org.jgum.category.type.TopDownTypeTraversalPolicy;
import org.jgum.category.type.TypeCategorization;
import org.jgum.category.type.TypeCategory;
import org.jgum.category.type.TypeCategoryRoot;
import org.jgum.traversal.RedundancyCheck;
import org.jgum.traversal.SearchStrategy;
import org.jgum.traversal.TraversalPolicy;

import java.util.function.Function;


/**
 * Defines a register of categorizations that pre-includes a named categorization and a type categorization.
 * It also provides high-level methods to ease the manipulation of these categorizations.
 * @author sergioc
 *
 */
public class JGum extends CategorizationContext {

	/**
	 * Default linearization function for bottom-up traversing of a type categorization.
	 */
	public static final Function<TypeCategory<?>, List<TypeCategory<?>>> DEFAULT_BOTTOM_UP_TYPE_LINEARIZATION_FUNCTION =
			bottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.CLASSES_FIRST, InterfaceOrder.DECLARATION, RedundancyCheck.KEEP_LAST);
			//bottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.INTERFACES_FIRST, InterfaceOrder.REVERSE, RedundancyCheck.KEEP_LAST);
	
	/**
	 * Default linearization function for top-down traversing of a type categorization.
	 */
	public static final Function<TypeCategory<?>, List<TypeCategory<?>>> DEFAULT_TOP_DOWN_TYPE_LINEARIZATION_FUNCTION = 
			topDownTypeTraversalPolicy(SearchStrategy.BREADTH_FIRST, Priority.CLASSES_FIRST, RedundancyCheck.KEEP_FIRST);
	
	/**
	 * Default linearization function for bottom-up traversing of a named categorization.
	 */
	public static final Function<NamedCategory, List<NamedCategory>> DEFAULT_BOTTOM_UP_NAME_LINEARIZATION_FUNCTION = 
			bottomUpTraversalPolicy(SearchStrategy.PRE_ORDER, RedundancyCheck.IGNORE);
	
	/**
	 * Default linearization function for top-down traversing of a named categorization.
	 */
	public static final Function<NamedCategory, List<NamedCategory>> DEFAULT_TOP_DOWN_NAME_LINEARIZATION_FUNCTION = 
			topDownTraversalPolicy(SearchStrategy.PRE_ORDER, RedundancyCheck.IGNORE);
	
	
	private final TypeCategorization typeCategorization; //a type categorization.
	private final NamedCategorization namedCategorization; //a named categorization.

	/**
	 * Id of the pre-defined type categorization.
	 */
	public static final Object JGUM_TYPE_HIERARCHY_ID = new Object(); //the id under which the type hierarchy is registered on the categorization register.
	/**
	 * Id of the pre-defined named categorization.
	 */
	public static final Object JGUM_NAME_HIERARCHY_ID = new Object(); //the id under which the named hierarchy is registered on the categorization register.
	
	
	/**
	 * Creates a new context with default linearization functions.
	 */
	public JGum() {
		this(DEFAULT_BOTTOM_UP_TYPE_LINEARIZATION_FUNCTION, DEFAULT_TOP_DOWN_TYPE_LINEARIZATION_FUNCTION, DEFAULT_BOTTOM_UP_NAME_LINEARIZATION_FUNCTION, DEFAULT_TOP_DOWN_NAME_LINEARIZATION_FUNCTION);
	}
	
	/**
	 * Creates a new context with the given bottom-up type linearization function.
	 * @param bottomUpTypeLinearizationFunction the bottom-up type linearization function.
	 */
	public JGum(Function<TypeCategory<?>, List<TypeCategory<?>>> bottomUpTypeLinearizationFunction) {
		this(bottomUpTypeLinearizationFunction, DEFAULT_TOP_DOWN_TYPE_LINEARIZATION_FUNCTION, DEFAULT_BOTTOM_UP_NAME_LINEARIZATION_FUNCTION, DEFAULT_TOP_DOWN_NAME_LINEARIZATION_FUNCTION);
	}
	
	/**
	 * Creates a new context with the given type and id linearization functions.
	 * @param bottomUpTypeLinearizationFunction the bottom-up type linearization function.
	 * @param topDownTypeLinearizationFunction the top-down type linearization function.
	 * @param bottomUpNameLinearizationFunction the bottom-up id linearization function.
	 * @param topDownNameLinearizationFunction the top-down id linearization function.
	 */
	public JGum(Function<TypeCategory<?>, List<TypeCategory<?>>> bottomUpTypeLinearizationFunction, 
			Function<TypeCategory<?>, List<TypeCategory<?>>> topDownTypeLinearizationFunction,
			Function<NamedCategory, List<NamedCategory>> bottomUpNameLinearizationFunction, 
			Function<NamedCategory, List<NamedCategory>> topDownNameLinearizationFunction) {
		
		namedCategorization = new NamedCategorization(bottomUpNameLinearizationFunction, topDownNameLinearizationFunction);
		register(JGUM_TYPE_HIERARCHY_ID, namedCategorization);
		typeCategorization = new TypeCategorization(bottomUpTypeLinearizationFunction, topDownTypeLinearizationFunction);
		register(JGUM_NAME_HIERARCHY_ID, typeCategorization);
	}
	
	/**
	 * 
	 * @return the named categorization associated with this context.
	 */
	public NamedCategorization getNamedCategorization() {
		return namedCategorization;
	}
	
	/**
	 * 
	 * @return the category corresponding to the root in the named categorization (the empty id).
	 */
	public NamedCategory forNameRoot() {
		return namedCategorization.getRoot();
	}

	/**
	 * 
	 * @param id the full id (e.g., "a.b.c") of a category.
	 * @return a category corresponding to the given id.
	 */
	public NamedCategory forName(String name) {
		return forNameRoot().getOrCreateCategory(name);
	}
	
	/**
	 * 
	 * @param pakkage the package object for which a category is requested.
	 * @return a id category corresponding to the id of the given Package object.
	 */
	public NamedCategory forPackage(Package pakkage) {
		return forNameRoot().getOrCreateCategory(pakkage);
	}
	
	/**
	 * 
	 * @return the type categorization associated with this context.
	 */
	public TypeCategorization getTypeCategorization() {
		return typeCategorization;
	}

	/**
	 * 
	 * @return the category corresponding to the root in the type categorization (the Any class).
	 */
	public TypeCategoryRoot forTypeRoot() {
		return getTypeCategorization().getRoot();
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
