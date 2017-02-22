package org.jgum.category;

import static org.jgum.traversal.TraversalPolicy.bottomUpTraversalPolicy;
import static org.jgum.traversal.TraversalPolicy.topDownTraversalPolicy;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import org.jgum.traversal.RedundancyCheck;
import org.jgum.traversal.SearchStrategy;

/**
 * A set of categories with a hierarchical organization.
 * A categorization is defined by a root and two linearization functions (bottom-up and top-down).
 * If no specified, the default bottom-up linearization function is a pre-order search, with a keep-last redundancy check policy.
 * If no specified, the default top-down linearization function is a breadth-first search, with a keep-first redundancy check policy.
 * @author sergioc
 *
 * @param <T>
 */
public class Categorization<T extends Category> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Function<T, List<T>> bottomUpLinearizationFunction; //bottom-up type linearization function.
	private final Function<T, List<T>> topDownLinearizationFunction; //top-down type linearization function.
	private T root;
	

	/**
	 * Creates a categorization with default linearization functions.
	 */
	public Categorization() {
		this(bottomUpTraversalPolicy(SearchStrategy.PRE_ORDER, RedundancyCheck.KEEP_LAST));
	}
	
	/**
	 * Creates a categorization with the given bottom-up linearization function and default top-down linearization function.
	 * @param bottomUpLinearizationFunction the bottom-up linearization function.
	 */
	public Categorization(Function<T, List<T>> bottomUpLinearizationFunction) {
		this(bottomUpLinearizationFunction, topDownTraversalPolicy(SearchStrategy.BREADTH_FIRST, RedundancyCheck.KEEP_FIRST));
	}

	/**
	 * Creates a categorization with the given linearization functions.
	 * @param bottomUpLinearizationFunction the bottom-up linearization function.
	 * @param topDownLinearizationFunction the top-down linearization function.
	 */
	public Categorization(Function<T, List<T>> bottomUpLinearizationFunction, 
			Function<T, List<T>> topDownLinearizationFunction) {
		this.bottomUpLinearizationFunction = bottomUpLinearizationFunction;
		this.topDownLinearizationFunction = topDownLinearizationFunction;
	}
	
	/**
	 * 
	 * @return the bottom-up linearization function.
	 */
	protected Function<T, List<T>> getBottomUpLinearizationFunction() {
		return bottomUpLinearizationFunction;
	}

	/**
	 * 
	 * @return the top-down linearization function.
	 */
	protected Function<T, List<T>> getTopDownLinearizationFunction() {
		return topDownLinearizationFunction;
	}

	/**
	 * 
	 * @return the root category (if already set).
	 */
	public T getRoot() {
		return root;
	}

	void setRoot(T root) {
		if(this.root != null)
			throw new RuntimeException("This categorization is already associated with a root category");
		this.root = root;
	}

	
}
