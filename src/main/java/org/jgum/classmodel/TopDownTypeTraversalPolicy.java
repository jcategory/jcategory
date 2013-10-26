package org.jgum.classmodel;

import java.util.List;

import org.jgum.graph.DuplicatesDetection;
import org.jgum.graph.SearchStrategy;
import org.jgum.graph.TraversalPolicy;

import com.google.common.base.Function;

/**
 * A class facilitating the definition of top-down traversal policies in a graph of class nodes.
 * @author sergioc
 *
 */
public class TopDownTypeTraversalPolicy<T extends TypeNode<?>> extends TraversalPolicy<T> {

	/**
	 * 
	 * @param priority if classes should be visited before interfaces.
	 * @return a function mapping a TypeNode to its children.
	 */
	public static <U extends TypeNode<?>> Function<U, List<U>> childrenTypeFunction(final Priority priority) {
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
	 * @param duplicatesDetection if a node can be traversed only once in a given path.
	 * @param priority if classes should be visited before interfaces.
	 */
	public TopDownTypeTraversalPolicy(SearchStrategy searchStrategy, DuplicatesDetection duplicatesDetection, Priority priority) {
		super(searchStrategy, duplicatesDetection, TopDownTypeTraversalPolicy.<T>childrenTypeFunction(priority));
	}
	
}
