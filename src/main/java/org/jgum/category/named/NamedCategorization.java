package org.jgum.category.named;

import java.util.Collections;
import java.util.List;

import org.jgum.category.Categorization;
import org.jgum.category.CategorizationListener;
import org.jgum.category.CategorizationListenersManager;

import com.google.common.base.Function;

/**
 * The hierarchy of a named categorization is implicitly specified by the names of its categories. 
 * Those names contain a list of ancestors using dots as token separator.
 * For example, a category named "org" is the parent of a category named "org.jgum" and the ancestor of a category named "org.jgum.JGum".
 * @author sergioc
 *
 */
public class NamedCategorization extends Categorization<NamedCategory> {

	private NamedCategory nameCategoryRoot;
	private final CategorizationListenersManager listenersManager; //category listeners notified when a new category is created.
	
	/**
     * @param bottomUpLinearizationFunction the bottom-up linearization function.
	 * @param topDownLinearizationFunction the top-down linearization function.
	 */
	public NamedCategorization(Function<NamedCategory, List<NamedCategory>> bottomUpLinearizationFunction, 
			Function<NamedCategory, List<NamedCategory>> topDownLinearizationFunction) {
		this(bottomUpLinearizationFunction, topDownLinearizationFunction, (List)Collections.emptyList());
	}
	
	/**
     * @param bottomUpLinearizationFunction the bottom-up linearization function.
	 * @param topDownLinearizationFunction the top-down linearization function.
	 * @param categorizationListeners listeners to be notified when a new named category is added to this categorization.
	 */
	public NamedCategorization(Function<NamedCategory, List<NamedCategory>> bottomUpLinearizationFunction, 
			Function<NamedCategory, List<NamedCategory>> topDownLinearizationFunction,
			List<? extends CategorizationListener<NamedCategory>> categorizationListeners) {
		super((Function)bottomUpLinearizationFunction, (Function)topDownLinearizationFunction);
		listenersManager = new CategorizationListenersManager((List)categorizationListeners);
	}
	
	/**
	 * The root name category (with an empty name)
	 */
	public NamedCategory getRoot() {
		if(nameCategoryRoot == null) {
			nameCategoryRoot = new NamedCategory(this);
			notifyCategorizationListeners(nameCategoryRoot);
		}
		return nameCategoryRoot;
	}

	
	protected void notifyCategorizationListeners(NamedCategory newCategory) {
		listenersManager.notifyCategorizationListeners(newCategory);
	}

	/**
	 * Adds a listener to be notified when a new named category is added to this categorization.
	 * @param creationListener the listener to be notified.
	 */
	public void addCategorizationListener(CategorizationListener<NamedCategory> creationListener) {
		listenersManager.add(creationListener);
	}
	
}
