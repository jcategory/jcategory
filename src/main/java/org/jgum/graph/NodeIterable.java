package org.jgum.graph;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.TreeTraverser;

public class NodeIterable<T extends Node> implements Iterable<T> {

	private List<T> children;
	private Iterable<T> nextNodeIterable;
	
	public NodeIterable(T node, SearchStrategy searchStrategy, Function<T, List<T>> nextNodesFunction) {
		NodeTraverser<T> traverser = new NodeTraverser<>(nextNodesFunction);
		if(searchStrategy.equals(SearchStrategy.PRE_ORDER)) {
			nextNodeIterable = traverser.preOrderTraversal(node);
		} else if(searchStrategy.equals(SearchStrategy.POST_ORDER)) {
			nextNodeIterable = traverser.postOrderTraversal(node);
		} else { //BREADTH_FIRST
			nextNodeIterable = traverser.breadthFirstTraversal(node);
		}
	}

	public Iterable<T> getChildren() {
		return children;
	}

	@Override
	public Iterator<T> iterator() {
		return nextNodeIterable.iterator();
	}

	
	public class NodeTraverser<T extends Node> extends TreeTraverser<T> {

		private final Function<T, List<T>> nextNodesFunction;
		
		public NodeTraverser(Function<T, List<T>> nextNodesFunction) {
			this.nextNodesFunction = nextNodesFunction;
		}

		@Override
		public Iterable<T> children(T typeNode) {
			return nextNodesFunction.apply(typeNode);
		}
		
	}
}
