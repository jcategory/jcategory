package org.jgum.category;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;

/**
 * A class facilitating the definition of typical linearization functions in terms of few parameters.
 * @author sergioc
 *
 * @param <T>
 */
public class TraversalPolicy<T extends Category<?>> implements Function<T, FluentIterable<T>> {

	public final SearchStrategy searchStrategy;
	public final DuplicatesDetection duplicatesDetection;
	public final Function<T, List<T>> nextNodesFunction;

	public TraversalPolicy(SearchStrategy searchStrategy, Function<T, List<T>> nextNodesFunction, DuplicatesDetection duplicatesDetection) {
		this.searchStrategy = searchStrategy;
		this.duplicatesDetection = duplicatesDetection;
		this.nextNodesFunction = nextNodesFunction;
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
	
	
	public static class CategoryTraverser<T extends Category<?>> extends TreeTraverser<T> {

		public static <T extends Category<?>> FluentIterable<T> iterable(T node, SearchStrategy searchStrategy, Function<T, List<T>> nextNodesFunction) {
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
