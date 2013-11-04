package org.jgum.category;

import java.util.List;

import org.jgum.traversal.RedundancyCheck;
import org.jgum.traversal.SearchStrategy;
import org.jgum.traversal.TraversalPolicy;

import com.google.common.base.Function;

/**
 * A set of categories with a hierarchical organization.
 * @author sergioc
 *
 * @param <T>
 */
public class Categorization<T extends Category> {

	private final Function<T, List<T>> bottomUpLinearizationFunction; //bottom up type linearization function.
	private final Function<T, List<T>> topDownLinearizationFunction; //top down type linearization function.
	private T root;
	
	public Categorization() {
		this(TraversalPolicy.bottomUpTraversalPolicy(SearchStrategy.PRE_ORDER, RedundancyCheck.KEEP_LAST));
	}
	
	public Categorization(Function<T, List<T>> bottomUpLinearizationFunction) {
		this(bottomUpLinearizationFunction, TraversalPolicy.topDownTraversalPolicy(SearchStrategy.BREADTH_FIRST, RedundancyCheck.KEEP_FIRST));
	}

	public Categorization(Function<T, List<T>> bottomUpLinearizationFunction, 
			Function<T, List<T>> topDownLinearizationFunction) {
		this.bottomUpLinearizationFunction = bottomUpLinearizationFunction;
		this.topDownLinearizationFunction = topDownLinearizationFunction;
	}
	
	/**
	 * 
	 * @return the bottom up class traversing strategy for this context.
	 */
	protected Function<T, List<T>> getBottomUpLinearizationFunction() {
		return bottomUpLinearizationFunction;
	}

	/**
	 * 
	 * @return the top down class traversing strategy for this context.
	 */
	protected Function<T, List<T>> getTopDownLinearizationFunction() {
		return topDownLinearizationFunction;
	}

	public T getRoot() {
		return root;
	}

	void setRoot(T root) {
		this.root = root;
	}

	
}
