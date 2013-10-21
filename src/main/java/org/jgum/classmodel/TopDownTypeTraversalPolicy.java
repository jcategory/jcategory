package org.jgum.classmodel;

import java.util.List;

import org.jgum.graph.DuplicatesDetection;
import org.jgum.graph.SearchStrategy;
import org.jgum.graph.TraversalPolicy;

import com.google.common.base.Function;

public class TopDownTypeTraversalPolicy<T extends TypeNode<?>> extends TraversalPolicy<T> {

	public static <U extends TypeNode<?>> Function<U, List<U>> childrenTypeFunction(final Priority priority) {
		return new Function<U, List<U>>() {
			@Override
			public List<U> apply(U typeNode) {
				return (List)typeNode.getChildren(priority);
			}
		};
	}
	
	public TopDownTypeTraversalPolicy(SearchStrategy searchStrategy, DuplicatesDetection duplicatesDetection, Priority priority) {
		super(searchStrategy, duplicatesDetection, TopDownTypeTraversalPolicy.<T>childrenTypeFunction(priority));
	}
	
}
