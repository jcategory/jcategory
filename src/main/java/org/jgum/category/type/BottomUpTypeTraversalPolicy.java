package org.jgum.category.type;

import java.util.List;

import org.jgum.category.DuplicatesDetection;
import org.jgum.category.SearchStrategy;
import org.jgum.category.TraversalPolicy;

import com.google.common.base.Function;

/**
 * A class facilitating the definition of bottom-up traversal policies in a graph of class nodes.
 * @author sergioc
 *
 */
public class BottomUpTypeTraversalPolicy<T extends TypeCategory<?>> extends TraversalPolicy<T> {

	/**
	 * 
	 * @param priority if classes should be visited before interfaces.
	 * @param interfaceOrder if the interfaces should be traversed in declaration order or inversing the order.
	 * @return a function mapping a TypeCategory to its parent.
	 */
	public static <U extends TypeCategory<?>> Function<U, List<U>> parentTypeFunction(final Priority priority, final InterfaceOrder interfaceOrder) {
		return new Function<U, List<U>>() {
			@Override
			public List<U> apply(U typeNode) {
				return (List)typeNode.getParents(priority, interfaceOrder);
			}
		};
	}
	
	/**
	 * 
	 * @param searchStrategy how the nodes in the bottom-up path should be traversed.
	 * @param duplicatesDetection if a node can be traversed only once in a given path.
	 * @param priority if classes should be visited before interfaces.
	 * @param interfaceOrder if the interfaces should be traversed in declaration order or inversing the order.
	 */
	public BottomUpTypeTraversalPolicy(SearchStrategy searchStrategy, Priority priority, InterfaceOrder interfaceOrder, DuplicatesDetection duplicatesDetection) {
		super(searchStrategy, BottomUpTypeTraversalPolicy.<T>parentTypeFunction(priority, interfaceOrder), duplicatesDetection);
	}
	
}
