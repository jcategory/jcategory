package org.jgum.graph;

import java.util.List;

import com.google.common.base.Function;

public class TraversalPolicy<T extends Node> {

	public final SearchStrategy searchStrategy;
	public final CycleDetection cycleDetection;
	public final Function<T, List<T>> nextNodesFunction;

	public TraversalPolicy(SearchStrategy searchStrategy, CycleDetection cycleDetection, Function<T, List<T>> nextNodesFunction) {
		this.searchStrategy = searchStrategy;
		this.cycleDetection = cycleDetection;
		this.nextNodesFunction = nextNodesFunction;
	}
	
}
