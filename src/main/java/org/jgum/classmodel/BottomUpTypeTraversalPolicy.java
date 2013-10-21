package org.jgum.classmodel;

import java.util.List;

import org.jgum.graph.DuplicatesDetection;
import org.jgum.graph.SearchStrategy;
import org.jgum.graph.TraversalPolicy;

import com.google.common.base.Function;

public class BottomUpTypeTraversalPolicy<T extends TypeNode<?>> extends TraversalPolicy<T> {

	public static <U extends TypeNode<?>> Function<U, List<U>> parentTypeFunction(final Priority priority, final InterfaceOrder interfaceOrder) {
		return new Function<U, List<U>>() {
			@Override
			public List<U> apply(U typeNode) {
				return (List)typeNode.getParents(priority, interfaceOrder);
			}
		};
	}
	
	public BottomUpTypeTraversalPolicy(SearchStrategy searchStrategy, DuplicatesDetection duplicatesDetection, Priority priority, InterfaceOrder interfaceOrder) {
		super(searchStrategy, duplicatesDetection, BottomUpTypeTraversalPolicy.<T>parentTypeFunction(priority, interfaceOrder));
	}
	
}
