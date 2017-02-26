package org.jcategory.category.type;

import java.util.List;
import java.util.function.Function;

import org.jcategory.traversal.RedundancyCheck;
import org.jcategory.traversal.SearchStrategy;
import org.jcategory.traversal.TraversalPolicy;

/**
 * A class facilitating the definition of bottom-up linearization functions in a type categorization.
 * @author sergioc
 *
 */
public class BottomUpTypeTraversalPolicy<T extends TypeCategory<?>> extends TraversalPolicy<T> {

	/**
	 * 
	 * @param priority if classes should be visited before interfaces or vice versa.
	 * @param interfaceOrder if the interfaces should be traversed following their declaration order or reversing such order.
	 * @return a function mapping a TypeCategory to a bottom-up linearization.
	 */
	public static <U extends TypeCategory<?>> Function<U, List<U>> parentsFunction(Priority priority,
																				   InterfaceOrder interfaceOrder) {
		return typeNode -> (List) typeNode.getParents(priority, interfaceOrder);
	}

	
	/**
	 * 
	 * @param searchStrategy how the nodes in the bottom-up path should be traversed.
	 * @param priority if classes should be visited before interfaces or vice versa.
	 * @param interfaceOrder if the interfaces should be traversed following their declaration order or reversing such order.
	 * @param redundancyCheck determines how to deal with redundancy.
	 */
	public BottomUpTypeTraversalPolicy(SearchStrategy searchStrategy, Priority priority, InterfaceOrder interfaceOrder,
									   RedundancyCheck redundancyCheck) {
		super(searchStrategy, BottomUpTypeTraversalPolicy.<T>parentsFunction(priority, interfaceOrder), redundancyCheck);
	}

	public static BottomUpTypeTraversalPolicy bottomUpTypeTraversalPolicy(SearchStrategy searchStrategy,
																		  Priority priority,
																		  InterfaceOrder interfaceOrder,
																		  RedundancyCheck redundancyCheck) {
		return new BottomUpTypeTraversalPolicy(searchStrategy, priority, interfaceOrder, redundancyCheck);
	}
	
}
