package org.jgum.graph;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;

public class NodeTraverser<T extends Node> extends TreeTraverser<T> {

	public static <T extends Node> FluentIterable<T> iterable(T node, SearchStrategy searchStrategy, Function<T, List<T>> nextNodesFunction) {
		FluentIterable<T> nextNodeIterable;
		NodeTraverser<T> traverser = new NodeTraverser<>(nextNodesFunction);
		if(searchStrategy.equals(SearchStrategy.PRE_ORDER)) {
			nextNodeIterable = traverser.preOrderTraversal(node);
		} else if(searchStrategy.equals(SearchStrategy.POST_ORDER)) {
			nextNodeIterable = traverser.postOrderTraversal(node);
		} else { //BREADTH_FIRST
			nextNodeIterable = traverser.breadthFirstTraversal(node);
		}
		return nextNodeIterable;
	}
	
	
	private final Function<T, List<T>> nextNodesFunction;
	
	public NodeTraverser(Function<T, List<T>> nextNodesFunction) {
		this.nextNodesFunction = nextNodesFunction;
	}

	@Override
	public Iterable<T> children(T typeNode) {
		return nextNodesFunction.apply(typeNode);
	}
	
}