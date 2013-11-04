package org.jgum.traversal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import org.jgum.category.Category;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.TreeTraverser;

/**
 * A class facilitating the definition of common linearization functions in terms of few intuitive parameters.
 * @author sergioc
 *
 * @param <T>
 */
public class TraversalPolicy<T extends Category> implements Function<T, List<T>> {

	public final SearchStrategy searchStrategy;
	public final RedundancyDetection redundancyDetection;
	public final Function<T, List<T>> nextNodesFunction;

	public TraversalPolicy(SearchStrategy searchStrategy, Function<T, List<T>> nextNodesFunction, RedundancyDetection redundancyDetection) {
		this.searchStrategy = searchStrategy;
		this.nextNodesFunction = nextNodesFunction;
		this.redundancyDetection = redundancyDetection;
	}
	
	@Override
	public List<T> apply(T category) {
		FluentIterable<T> it = CategoryTraverser.<T>iterable(category, searchStrategy, nextNodesFunction);
		if(redundancyDetection.equals(RedundancyDetection.KEEP_FIRST)) {
			return new ArrayList<>(new LinkedHashSet<T>(it.toList()));
		} else if(redundancyDetection.equals(RedundancyDetection.KEEP_LAST)) {
			return Lists.reverse(new ArrayList<>(new LinkedHashSet<T>(it.toList().reverse())));
		} else { //ignore redundancy check
			return it.toList();
		}
	}
	

	public static TraversalPolicy bottomUpTraversalPolicy(SearchStrategy searchStrategy, RedundancyDetection redundancyDetection) {
		return new TraversalPolicy(searchStrategy, parentsFunction(), redundancyDetection);
	}
	
	public static TraversalPolicy topDownTraversalPolicy(SearchStrategy searchStrategy, RedundancyDetection redundancyDetection) {
		return new TraversalPolicy(searchStrategy, childrenFunction(), redundancyDetection);
	}
	
	/**
	 *
	 * @return a function mapping a category to its parents.
	 */
	public static <U extends Category> Function<U, List<U>> parentsFunction() {
		return new Function<U, List<U>>() {
			@Override
			public List<U> apply(U category) {
				return category.getParents();
			}
		};
	}
	
	/**
	 * comparator determines how parents should be ordered.
	 * @return a function mapping a category to its parents, ordering the parents according to the given comparator.
	 */
	public static <U extends Category> Function<U, List<U>> parentsFunction(final Comparator<U> comparator) {
		return new Function<U, List<U>>() {
			@Override
			public List<U> apply(U category) {
				List<U> parents =  category.getParents();
				Collections.sort(parents, comparator);
				return parents;
			}
		};
	}
	
	/**
	 * 
	 * @return a function mapping a category to its children.
	 */
	public static <U extends Category> Function<U, List<U>> childrenFunction() {
		return new Function<U, List<U>>() {
			@Override
			public List<U> apply(U category) {
				return category.getChildren();
			}
		};
	}
	
	/**
	 * 
	 * @param comparator determines how children should be ordered.
	 * @return a function mapping a category to its children, ordering the children according to the given comparator.
	 */
	public static <U extends Category> Function<U, List<U>> childrenFunction(final Comparator<U> comparator) {
		return new Function<U, List<U>>() {
			@Override
			public List<U> apply(U category) {
				List<U> children = category.getChildren();
				Collections.sort(children, comparator);
				return children;
			}
		};
	}
	
	public static class CategoryTraverser<T extends Category> extends TreeTraverser<T> {

		public static <T extends Category> FluentIterable<T> iterable(T node, SearchStrategy searchStrategy, Function<T, List<T>> nextNodesFunction) {
			FluentIterable<T> nextNodeIterable;
			CategoryTraverser<T> traverser = new CategoryTraverser<>(nextNodesFunction);
			if(searchStrategy.equals(SearchStrategy.PRE_ORDER)) {
				nextNodeIterable = traverser.preOrderTraversal(node);
			} else if(searchStrategy.equals(SearchStrategy.POST_ORDER)) {
				nextNodeIterable = traverser.postOrderTraversal(node);
			} else { //BREADTH_FIRST
				nextNodeIterable = traverser.breadthFirstTraversal(node);
			}
			return nextNodeIterable;
		}
		
		
		private final Function<T, List<T>> nextNodesFunction;
		
		public CategoryTraverser(Function<T, List<T>> nextNodesFunction) {
			this.nextNodesFunction = nextNodesFunction;
		}

		@Override
		public Iterable<T> children(T typeCategory) {
			return nextNodesFunction.apply(typeCategory);
		}
		
	}
	
}
