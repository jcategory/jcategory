package org.jgum.traversal;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.jgum.category.Category;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;

/**
 * A class facilitating the definition of typical linearization functions in terms of few parameters.
 * @author sergioc
 *
 * @param <T>
 */
public class TraversalPolicy<T extends Category> implements Function<T, FluentIterable<T>> {

	public final SearchStrategy searchStrategy;
	public final DuplicatesDetection duplicatesDetection;
	public final Function<T, List<T>> nextNodesFunction;

	public TraversalPolicy(SearchStrategy searchStrategy, Function<T, List<T>> nextNodesFunction) {
		this(searchStrategy, nextNodesFunction, DuplicatesDetection.IGNORE);
	}
	
	public TraversalPolicy(SearchStrategy searchStrategy, Function<T, List<T>> nextNodesFunction, DuplicatesDetection duplicatesDetection) {
		this.searchStrategy = searchStrategy;
		this.nextNodesFunction = nextNodesFunction;
		this.duplicatesDetection = duplicatesDetection;
	}
	
	@Override
	public FluentIterable<T> apply(T category) {
		FluentIterable<T> it = CategoryTraverser.<T>iterable(category, searchStrategy, nextNodesFunction);
		if(duplicatesDetection.equals(DuplicatesDetection.ENFORCE)) {
			final Iterable<T> itAux = it;
			it = FluentIterable.from(new Iterable<T>() {
				@Override
				public Iterator<T> iterator() {
					return new DuplicatesDetectionIterator<T>(itAux.iterator());
				}
			});
		}
		return it;
	}
	

	public static TraversalPolicy bottomUpTraversalPolicy(SearchStrategy searchStrategy) {
		return new TraversalPolicy(searchStrategy, parentsFunction());
	}
	
	public static TraversalPolicy bottomUpTraversalPolicy(SearchStrategy searchStrategy, DuplicatesDetection duplicatesDetection) {
		return new TraversalPolicy(searchStrategy, parentsFunction(), duplicatesDetection);
	}
	
	public static TraversalPolicy topDownTraversalPolicy(SearchStrategy searchStrategy) {
		return new TraversalPolicy(searchStrategy, childrenFunction());
	}
	
	public static TraversalPolicy topDownTraversalPolicy(SearchStrategy searchStrategy, DuplicatesDetection duplicatesDetection) {
		return new TraversalPolicy(searchStrategy, childrenFunction(), duplicatesDetection);
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
