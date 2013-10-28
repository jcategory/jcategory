package org.jgum.category;

import java.util.List;

import com.google.common.base.Function;

public class TraversalPolicy<T extends Category<?>> {

	public final SearchStrategy searchStrategy;
	public final DuplicatesDetection duplicatesDetection;
	public final Function<T, List<T>> nextNodesFunction;

	public TraversalPolicy(SearchStrategy searchStrategy, Function<T, List<T>> nextNodesFunction, DuplicatesDetection duplicatesDetection) {
		this.searchStrategy = searchStrategy;
		this.duplicatesDetection = duplicatesDetection;
		this.nextNodesFunction = nextNodesFunction;
	}
	
}
