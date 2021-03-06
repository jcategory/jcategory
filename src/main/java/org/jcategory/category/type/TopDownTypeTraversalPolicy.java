package org.jcategory.category.type;

import java.util.List;
import java.util.function.Function;

import org.jcategory.traversal.RedundancyCheck;
import org.jcategory.traversal.SearchStrategy;
import org.jcategory.traversal.TraversalPolicy;

/**
 * A class facilitating the definition of top-down linearization functions in a type categorization.
 * @author sergioc
 *
 */
public class TopDownTypeTraversalPolicy<T extends TypeCategory<?>> extends TraversalPolicy<T> {

	/**
	 * 
	 * @param priority if classes should be visited before interfaces or vice versa.
	 * @return a function mapping a TypeCategory to a top-down linearization.
	 */
	public static <U extends TypeCategory<?>> Function<U, List<U>> childrenFunction(final Priority priority) {
		return typeNode -> (List) typeNode.getChildren(priority);
	}
	
	
	/**
	 * 
	 * @param searchStrategy how the categories in the top-down path should be traversed.
	 * @param priority if classes should be visited before interfaces or vice versa.
	 * @param redundancyCheck determines how to deal with redundancy.
	 */
	public TopDownTypeTraversalPolicy(SearchStrategy searchStrategy, Priority priority, RedundancyCheck redundancyCheck) {
		super(searchStrategy, TopDownTypeTraversalPolicy.<T>childrenFunction(priority), redundancyCheck);
	}

	public static TopDownTypeTraversalPolicy topDownTypeTraversalPolicy(SearchStrategy searchStrategy,
																		   Priority priority,
																		   RedundancyCheck redundancyCheck) {
		return new TopDownTypeTraversalPolicy(searchStrategy, priority, redundancyCheck);
	}
}
