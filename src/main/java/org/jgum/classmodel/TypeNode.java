package org.jgum.classmodel;

import java.util.Collections;
import java.util.List;

import org.jgum.JGum;
import org.jgum.graph.Node;

public abstract class TypeNode<T> extends Node {
	
	protected Class<T> wrappedClazz;
	private List<InterfaceNode<? super T>> superInterfaceNodes;

	public TypeNode(JGum context, Class<T> wrappedClazz) {
		this(context, wrappedClazz, Collections.<InterfaceNode<? super T>>emptyList());
	}
	
	public TypeNode(JGum context, Class<T> wrappedClazz, List<InterfaceNode<? super T>> superInterfaceNodes) {
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

	protected abstract <U extends TypeNode<? super T>> List<U> getParents(Priority priority, InterfaceOrder interfaceOrder);

	protected abstract <U extends TypeNode<? extends T>> List<U> getChildren(Priority priority);
	
}
