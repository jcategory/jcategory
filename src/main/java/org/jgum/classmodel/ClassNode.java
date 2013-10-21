package org.jgum.classmodel;

import java.util.ArrayList;
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
		knownSubClassNodes = new ArrayList<>();
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
		List<InterfaceNode<? super T>> superInterfaceNodes = (List)getSuperInterfaceNodes();
		if(interfaceOrder.equals(InterfaceOrder.REVERSE)) {
			superInterfaceNodes = Lists.reverse(superInterfaceNodes); 
		}
		//the reversed list does not support addition of elements,
		//then a new list is created.
		List<TypeNode<? super T>> parents = new ArrayList<TypeNode<? super T>>(superInterfaceNodes);
		ClassNode<? super T> superClassNode = getSuperClassNode();
		if(superClassNode != null) {
			if(priority.equals(Priority.CLASSES_FIRST)) {
				parents.add(0, superClassNode);
			} else {
				parents.add(superClassNode);
			}
		}
		return parents;
	}

	@Override
	protected List<TypeNode<? extends T>> getChildren(Priority priority) {
		return (List)getKnownSubClassNodes();
	}
	
}
