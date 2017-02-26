package org.jcategory.category.name;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.jcategory.category.Categorization;
import org.jcategory.category.CategorizationListener;
import org.jcategory.category.CategorizationListenersManager;

/**
 * The hierarchy of a named categorization is implicitly specified by the names of its categories. 
 * Those names contain a list of ancestors using dots as token separator.
 * For example, a category named "org" is the parent of a category named "org.jcategory" and the ancestor of a category named "org.jcategory.JCategory".
 * @author sergioc
 *
 */
public class NameCategorization extends Categorization<NameCategory> {

	private NameCategory nameCategoryRoot;
	private final CategorizationListenersManager listenersManager; //category listeners notified when a new category is created.
	
	/**
     * @param bottomUpLinearizationFunction the bottom-up linearization function.
	 * @param topDownLinearizationFunction the top-down linearization function.
	 */
	public NameCategorization(Function<NameCategory, List<NameCategory>> bottomUpLinearizationFunction,
                              Function<NameCategory, List<NameCategory>> topDownLinearizationFunction) {
		this(bottomUpLinearizationFunction, topDownLinearizationFunction, (List)Collections.emptyList());
	}
	
	/**
     * @param bottomUpLinearizationFunction the bottom-up linearization function.
	 * @param topDownLinearizationFunction the top-down linearization function.
	 * @param categorizationListeners listeners to be notified when a new named category is added to this categorization.
	 */
	public NameCategorization(Function<NameCategory, List<NameCategory>> bottomUpLinearizationFunction,
                              Function<NameCategory, List<NameCategory>> topDownLinearizationFunction,
                              List<? extends CategorizationListener<NameCategory>> categorizationListeners) {
		super((Function)bottomUpLinearizationFunction, (Function)topDownLinearizationFunction);
		listenersManager = new CategorizationListenersManager((List)categorizationListeners);
	}
	
	/**
	 * The root id category (with an empty id)
	 */
	public NameCategory getRoot() {
		if(nameCategoryRoot == null) {
			nameCategoryRoot = new NameCategory(this);
			notifyCategorizationListeners(nameCategoryRoot);
		}
		return nameCategoryRoot;
	}

	
	protected void notifyCategorizationListeners(NameCategory newCategory) {
		listenersManager.notifyCategorizationListeners(newCategory);
	}

	/**
	 * Adds a listener to be notified when a new named category is added to this categorization.
	 * @param creationListener the listener to be notified.
	 */
	public void addCategorizationListener(CategorizationListener<NameCategory> creationListener) {
		listenersManager.add(creationListener);
	}
	
}
