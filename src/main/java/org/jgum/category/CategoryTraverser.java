package org.jgum.category;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;

public class CategoryTraverser<T extends Category<?>> extends TreeTraverser<T> {

	public static <T extends Category<?>> FluentIterable<T> iterable(T node, SearchStrategy searchStrategy, Function<T, List<T>> nextNodesFunction) {
		FluentIterable<T> nextNodeIterable;
		CategoryTraverser<T> traverser = new CategoryTraverser<>(nextNodesFunction);
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
	
	public CategoryTraverser(Function<T, List<T>> nextNodesFunction) {
		this.nextNodesFunction = nextNodesFunction;
	}

	@Override
	public Iterable<T> children(T typeNode) {
		return nextNodesFunction.apply(typeNode);
	}
	
}