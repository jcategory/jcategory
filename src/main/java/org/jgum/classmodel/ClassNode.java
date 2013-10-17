package org.jgum.classmodel;

import java.util.Collections;
import java.util.List;

import org.jgum.JGum;

public class ClassNode<T> extends AbstractClassNode<T> {
	
	public static ClassNode<Object> root(JGum context) {
		return new ClassNode(context);
	}
	
	private ClassNode<? super T> superClassNode;
	private List<ClassNode<? extends T>> knownSubClassNodes;
	
	private ClassNode(JGum context) {
		this(context, (Class<T>) Object.class, null);
	}
	
	public ClassNode(JGum context, Class<T> wrappedClass, ClassNode<? super T> parentClassNode) {
		this(context, wrappedClass, parentClassNode, Collections.<InterfaceNode<? super T>>emptyList());
	}
	
	public ClassNode(JGum context, Class<T> wrappedClass, ClassNode<? super T> superClassNode, List<InterfaceNode<? super T>> superInterfaceNodes) {
		super(context, wrappedClass, superInterfaceNodes);
		if(superClassNode != null)
			setSuperClassNode(superClassNode);
	}
	
	public ClassNode<? super T> getSuperClassNode() {
		return superClassNode;
	}
	
	public void setSuperClassNode(ClassNode<? super T> superClassNode) {
		this.superClassNode = superClassNode;
		superClassNode.addKnownSubClassNode((ClassNode<? extends T>) this);
	}
	
	protected void addKnownSubClassNode(ClassNode<? extends T> subClassNode) {
		knownSubClassNodes.add(subClassNode);
	}
	
	@Override
	protected void setSuperInterfaceNodes(List<InterfaceNode<? super T>> superInterfaceNodes) {
		super.setSuperInterfaceNodes(superInterfaceNodes);
		for(InterfaceNode<? super T> superInterfaceNode : superInterfaceNodes) {
			superInterfaceNode.addKnownImplementorNode(this);
		}
	}

	public List<ClassNode<? extends T>> getKnownSubClassNodes() {
		return knownSubClassNodes;
	}
	
}
