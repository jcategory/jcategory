package org.jgum.category.type;

import java.util.List;

import org.jgum.traversal.RedundancyCheck;
import org.jgum.traversal.SearchStrategy;
import org.jgum.traversal.TraversalPolicy;

import com.google.common.base.Function;

/**
 * A class facilitating the definition of bottom-up linearization functions in a graph of type nodes.
 * @author sergioc
 *
 */
public class BottomUpTypeTraversalPolicy<T extends TypeCategory<?>> extends TraversalPolicy<T> {

	/**
	 * 
	 * @param priority if classes should be visited before interfaces.
	 * @param interfaceOrder if the interfaces should be traversed in their declaration order or inversing their order.
	 * @return a function mapping a TypeCategory to a bottom-up linearization.
	 */
	public static <U extends TypeCategory<?>> Function<U, List<U>> parentsFunction(final Priority priority, final InterfaceOrder interfaceOrder) {
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
	 * @param priority if classes should be visited before interfaces.
	 * @param interfaceOrder if the interfaces should be traversed in declaration order or inversing the order.
	 * @param redundancyCheck determines what to do in case a category is traversed more than once.
	 */
	public BottomUpTypeTraversalPolicy(SearchStrategy searchStrategy, Priority priority, InterfaceOrder interfaceOrder, RedundancyCheck redundancyCheck) {
		super(searchStrategy, BottomUpTypeTraversalPolicy.<T>parentsFunction(priority, interfaceOrder), redundancyCheck);
	}
	
}
