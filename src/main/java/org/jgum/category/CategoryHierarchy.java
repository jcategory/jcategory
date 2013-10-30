package org.jgum.category;

import org.jgum.category.type.TypeCategory;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * A set of categories with a hierarchical organization.
 * @author sergioc
 *
 * @param <T>
 */
public class CategoryHierarchy<T extends Category<?>> {

	private final Function<T, FluentIterable<T>> bottomUpLinearizationFunction; //bottom up type linearization function.
	private final Function<T, FluentIterable<T>> topDownLinearizationFunction; //top down type linearization function.
	private final CategoryCreationListenersManager listenersManager;
	
	public CategoryHierarchy(Function<T, FluentIterable<T>> bottomUpLinearizationFunction, Function<T, FluentIterable<T>> topDownLinearizationFunction) {
		this.bottomUpLinearizationFunction = bottomUpLinearizationFunction;
		this.topDownLinearizationFunction = topDownLinearizationFunction;
		listenersManager = new CategoryCreationListenersManager();
	}
	
	protected void notifyCreationListeners(T newCategory) {
		listenersManager.notifyCreationListeners(newCategory);
	}

	public void addNodeCreationListener(CategoryCreationListener<TypeCategory<?>> creationListener) {
		listenersManager.addNodeCreationListener(creationListener);
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
