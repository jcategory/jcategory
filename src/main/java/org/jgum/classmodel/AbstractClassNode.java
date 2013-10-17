package org.jgum.classmodel;

import java.util.Collections;
import java.util.List;

import org.jgum.JGum;
import org.jgum.path.JGumNode;
import org.jgum.path.Path;

public class AbstractClassNode<T> extends JGumNode {

	protected Class<T> wrappedClazz;
	private List<InterfaceNode<? super T>> superInterfaceNodes;

	public AbstractClassNode(JGum context, Class<T> wrappedClazz) {
		this(context, wrappedClazz, Collections.<InterfaceNode<? super T>>emptyList());
	}
	
	public AbstractClassNode(JGum context, Class<T> wrappedClazz, List<InterfaceNode<? super T>> superInterfaceNodes) {
		super(context);
		this.wrappedClazz = wrappedClazz;
		setSuperInterfaceNodes(superInterfaceNodes);
	}
	
	public Class<T> getWrappedClass() {
		return wrappedClazz;
	}
	
	public List<InterfaceNode<? super T>> getSuperInterfaceNodes() {
		return superInterfaceNodes;
	}
	
	protected void setSuperInterfaceNodes(List<InterfaceNode<? super T>> superInterfaceNodes) {
		this.superInterfaceNodes = superInterfaceNodes;
	}
	
	public <U extends AbstractClassNode<?>> Path<U> path(Direction direction) {
		ClassTraversalPolicy classTraversalPolicy;
		if(direction.equals(Direction.BOTTOM_UP))
			classTraversalPolicy = JGum.DEFAULT_CLASS_TRAVERSAL_BOTTOM_UP_POLICY;
		else
			classTraversalPolicy = JGum.DEFAULT_CLASS_TRAVERSAL_TOP_DOWN_POLICY;
		return new Path(new ConfigurableIterable(this, direction, classTraversalPolicy));
	}
	
	public <U extends AbstractClassNode<?>> Path<U> path(Direction direction, ClassTraversalPolicy classTraversalPolicy) {
		return new Path(new ConfigurableIterable(this, direction, classTraversalPolicy));
	}

}
