package org.jgum.category;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * A set of categories with a hierarchical organization.
 * @author sergioc
 *
 * @param <T>
 */
public abstract class Categorization<T extends Category> {

	private final Function<T, FluentIterable<T>> bottomUpLinearizationFunction; //bottom up type linearization function.
	private final Function<T, FluentIterable<T>> topDownLinearizationFunction; //top down type linearization function.
	private final CategoryCreationListenersManager listenersManager; //category listeners notified when a new category is created.
	
	public Categorization(Function<T, FluentIterable<T>> bottomUpLinearizationFunction, Function<T, FluentIterable<T>> topDownLinearizationFunction) {
		this.bottomUpLinearizationFunction = bottomUpLinearizationFunction;
		this.topDownLinearizationFunction = topDownLinearizationFunction;
		listenersManager = new CategoryCreationListenersManager();
	}
	
	/**
	 * 
	 * @return the root category in this hierarchy.
	 */
	public abstract T getRoot();
	
	protected void notifyCreationListeners(T newCategory) {
		listenersManager.notifyCreationListeners(newCategory);
	}

	public void addCreationListener(CategoryCreationListener<Category> creationListener) {
		listenersManager.addCreationListener(creationListener);
	}
	
	/**
	 * 
	 * @return the bottom up class traversing strategy for this context.
	 */
	protected Function<T, FluentIterable<T>> getBottomUpLinearizationFunction() {
		return bottomUpLinearizationFunction;
	}

	/**
	 * 
	 * @return the top down class traversing strategy for this context.
	 */
	protected Function<T, FluentIterable<T>> getTopDownLinearizationFunction() {
		return topDownLinearizationFunction;
	}
	
}
