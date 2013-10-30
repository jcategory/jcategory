package org.jgum.category.type;

import java.util.List;

import org.jgum.category.DuplicatesDetection;
import org.jgum.category.SearchStrategy;
import org.jgum.category.TraversalPolicy;

import com.google.common.base.Function;

/**
 * A class facilitating the definition of top-down linearization functions in a graph of type nodes.
 * @author sergioc
 *
 */
public class TopDownTypeTraversalPolicy<T extends TypeCategory<?>> extends TraversalPolicy<T> {

	/**
	 * 
	 * @param priority if classes should be visited before interfaces.
	 * @return a function mapping a TypeCategory to a top-down linearization.
	 */
	public static <U extends TypeCategory<?>> Function<U, List<U>> childrenTypeFunction(final Priority priority) {
		return new Function<U, List<U>>() {
			@Override
			public List<U> apply(U typeNode) {
				return (List)typeNode.getChildren(priority);
			}
		};
	}
	
	/**
	 * 
	 * @param searchStrategy how the nodes in the bottom-up path should be traversed.
	 * @param priority if classes should be visited before interfaces.
	 * @param duplicatesDetection if a node can be traversed only once in a given path.
	 */
	public TopDownTypeTraversalPolicy(SearchStrategy searchStrategy, Priority priority, DuplicatesDetection duplicatesDetection) {
		super(searchStrategy, TopDownTypeTraversalPolicy.<T>childrenTypeFunction(priority), duplicatesDetection);
	}
	
}
