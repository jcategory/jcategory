package org.jgum.graph;

import java.util.List;

import com.google.common.base.Function;

public class TraversalPolicy<T extends Node> {

	public final SearchStrategy searchStrategy;
	public final DuplicatesDetection duplicatesDetection;
	public final Function<T, List<T>> nextNodesFunction;

	public TraversalPolicy(SearchStrategy searchStrategy, DuplicatesDetection duplicatesDetection, Function<T, List<T>> nextNodesFunction) {
		this.searchStrategy = searchStrategy;
		this.duplicatesDetection = duplicatesDetection;
		this.nextNodesFunction = nextNodesFunction;
	}
	
}
