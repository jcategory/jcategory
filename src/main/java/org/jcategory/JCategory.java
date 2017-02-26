package org.jcategory;

import static org.jcategory.category.Key.key;
import static org.jcategory.category.type.BottomUpTypeTraversalPolicy.bottomUpTypeTraversalPolicy;
import static org.jcategory.category.type.TopDownTypeTraversalPolicy.topDownTypeTraversalPolicy;
import static org.jcategory.traversal.TraversalPolicy.bottomUpTraversalPolicy;
import static org.jcategory.traversal.TraversalPolicy.topDownTraversalPolicy;

import java.util.List;
import java.util.function.Function;

import org.jcategory.category.Key;
import org.jcategory.category.name.NameCategorization;
import org.jcategory.category.name.NameCategory;
import org.jcategory.category.type.InterfaceOrder;
import org.jcategory.category.type.Priority;
import org.jcategory.category.type.TypeCategorization;
import org.jcategory.category.type.TypeCategory;
import org.jcategory.category.type.TypeCategoryRoot;
import org.jcategory.traversal.RedundancyCheck;
import org.jcategory.traversal.SearchStrategy;


/**
 * Defines a register of categorizations that pre-includes a named categorization and a type categorization.
 * It also provides high-level methods to ease the manipulation of these categorizations.
 * @author sergioc
 *
 */
public class JCategory extends CategorizationContext {

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
	public static final Function<NameCategory, List<NameCategory>> DEFAULT_BOTTOM_UP_NAME_LINEARIZATION_FUNCTION =
			bottomUpTraversalPolicy(SearchStrategy.PRE_ORDER, RedundancyCheck.IGNORE);
	
	/**
	 * Default linearization function for top-down traversing of a named categorization.
	 */
	public static final Function<NameCategory, List<NameCategory>> DEFAULT_TOP_DOWN_NAME_LINEARIZATION_FUNCTION =
			topDownTraversalPolicy(SearchStrategy.PRE_ORDER, RedundancyCheck.IGNORE);
	
	
	private final TypeCategorization typeCategorization; //a type categorization.
	private final NameCategorization namedCategorization; //a named categorization.

	/**
	 * Id of the pre-defined type categorization.
	 */
	public static final Key TYPE_HIERARCHY_ID = key(); //the id under which the type hierarchy is registered on the categorization register.
	/**
	 * Id of the pre-defined named categorization.
	 */
	public static final Key NAME_HIERARCHY_ID = key(); //the id under which the named hierarchy is registered on the categorization register.
	
	
	/**
	 * Creates a new context with default linearization functions.
	 */
	public JCategory() {
		this(DEFAULT_BOTTOM_UP_TYPE_LINEARIZATION_FUNCTION, DEFAULT_TOP_DOWN_TYPE_LINEARIZATION_FUNCTION, DEFAULT_BOTTOM_UP_NAME_LINEARIZATION_FUNCTION, DEFAULT_TOP_DOWN_NAME_LINEARIZATION_FUNCTION);
	}
	
	/**
	 * Creates a new context with the given bottom-up type linearization function.
	 * @param bottomUpTypeLinearizationFunction the bottom-up type linearization function.
	 */
	public JCategory(Function<TypeCategory<?>, List<TypeCategory<?>>> bottomUpTypeLinearizationFunction) {
		this(bottomUpTypeLinearizationFunction, DEFAULT_TOP_DOWN_TYPE_LINEARIZATION_FUNCTION, DEFAULT_BOTTOM_UP_NAME_LINEARIZATION_FUNCTION, DEFAULT_TOP_DOWN_NAME_LINEARIZATION_FUNCTION);
	}
	
	/**
	 * Creates a new context with the given type and id linearization functions.
	 * @param bottomUpTypeLinearizationFunction the bottom-up type linearization function.
	 * @param topDownTypeLinearizationFunction the top-down type linearization function.
	 * @param bottomUpNameLinearizationFunction the bottom-up id linearization function.
	 * @param topDownNameLinearizationFunction the top-down id linearization function.
	 */
	public JCategory(Function<TypeCategory<?>, List<TypeCategory<?>>> bottomUpTypeLinearizationFunction,
                     Function<TypeCategory<?>, List<TypeCategory<?>>> topDownTypeLinearizationFunction,
                     Function<NameCategory, List<NameCategory>> bottomUpNameLinearizationFunction,
                     Function<NameCategory, List<NameCategory>> topDownNameLinearizationFunction) {
		
		namedCategorization = new NameCategorization(bottomUpNameLinearizationFunction, topDownNameLinearizationFunction);
		register(TYPE_HIERARCHY_ID, namedCategorization);
		typeCategorization = new TypeCategorization(bottomUpTypeLinearizationFunction, topDownTypeLinearizationFunction);
		register(NAME_HIERARCHY_ID, typeCategorization);
	}
	
	/**
	 * 
	 * @return the named categorization associated with this context.
	 */
	public NameCategorization getNamedCategorization() {
		return namedCategorization;
	}
	
	/**
	 * 
	 * @return the category corresponding to the root in the named categorization (the empty id).
	 */
	public NameCategory forNameRoot() {
		return namedCategorization.getRoot();
	}

	/**
	 * 
	 * @param name the full id (e.g., "a.b.c") of a category.
	 * @return a category corresponding to the given id.
	 */
	public NameCategory forName(String name) {
		return forNameRoot().getOrCreateCategory(name);
	}
	
	/**
	 * 
	 * @param pakkage the package object for which a category is requested.
	 * @return a id category corresponding to the id of the given Package object.
	 */
	public NameCategory forPackage(Package pakkage) {
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
