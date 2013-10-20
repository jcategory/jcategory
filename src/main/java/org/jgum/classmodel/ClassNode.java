package org.jgum.classmodel;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;

import org.jgum.JGum;

import com.google.common.collect.Lists;

public class ClassNode<T> extends TypeNode<T> {
	
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

	@Override
	protected List<TypeNode<? super T>> getParents(Priority priority, InterfaceOrder interfaceOrder) {
		List<TypeNode<? super T>> superInterfaceNodes = (List)getSuperInterfaceNodes();
		if(interfaceOrder.equals(InterfaceOrder.INVERSE)) {
			superInterfaceNodes = Lists.reverse(superInterfaceNodes);
		}
		List<TypeNode<? super T>> parents;
		if(priority.equals(Priority.CLASSES_FIRST)) {
			parents = (List)asList(getSuperClassNode());
			parents.addAll(superInterfaceNodes);
		} else {
			parents = superInterfaceNodes;
			parents.add(getSuperClassNode());
		}
		return parents;
	}

	@Override
	protected List<TypeNode<? extends T>> getChildren(Priority priority) {
		return (List)getKnownSubClassNodes();
	}
	
}
