package org.jgum.classmodel;

import com.google.common.collect.TreeTraverser;

public class ClassHierarchyTraverser<T extends AbstractClassNode<?>> extends TreeTraverser<T> {

	private final Direction direction;
	private final ClassTraversalPolicy classTraversalPolicy;
	
	public ClassHierarchyTraverser(Direction direction, ClassTraversalPolicy classTraversalPolicy) {
		this.direction = direction;
		this.classTraversalPolicy = classTraversalPolicy;
	}

	@Override
	public Iterable<T> children(T abstractClassPropertiesNode) {
		return new ConfigurableIterable(abstractClassPropertiesNode, direction, classTraversalPolicy).getChildren();
	}
	
}
