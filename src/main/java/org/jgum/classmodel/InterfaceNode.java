package org.jgum.classmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgum.JGum;

import com.google.common.collect.Lists;

public class InterfaceNode<T> extends TypeNode<T> {

	public List<ClassNode<? extends T>> knownImplementorNodes;
	public List<InterfaceNode<? extends T>> knownSubInterfaceNodes;
	
	public InterfaceNode(JGum context, Class<T> interfaze) {
		this(context, interfaze, Collections.<InterfaceNode<? super T>>emptyList());
	}
	
	public InterfaceNode(JGum context, Class<T> wrappedInterface, List<InterfaceNode<? super T>> superInterfaceNodes) {
		super(context, wrappedInterface, superInterfaceNodes);
		knownImplementorNodes = new ArrayList<>();
		knownSubInterfaceNodes = new ArrayList<>();
	}	
	
	protected void addKnownImplementorNode(ClassNode<? extends T> implementorNode) {
		knownImplementorNodes.add(implementorNode);
	}
	
	protected void addKnownSubInterfaceNode(InterfaceNode<? extends T> subInterfaceNode) {
		knownSubInterfaceNodes.add(subInterfaceNode);
	}
	
	@Override
	protected void setSuperInterfaceNodes(List<InterfaceNode<? super T>> superInterfaceNodes) {
		super.setSuperInterfaceNodes(superInterfaceNodes);
		for(InterfaceNode<? super T> superInterfaceNode : superInterfaceNodes) {
			superInterfaceNode.addKnownSubInterfaceNode(this);
		}
	}
	
	public List<ClassNode<? extends T>> getKnownImplementorNodes() {
		return knownImplementorNodes;
	}
	
	public List<InterfaceNode<? extends T>> getKnownSubInterfaceNodes() {
		return knownSubInterfaceNodes;
	}

	@Override
	protected List<TypeNode<? super T>> getParents(Priority priority, InterfaceOrder interfaceOrder) {
		List<TypeNode<? super T>> superInterfaceNodes = (List)getSuperInterfaceNodes();
		if(interfaceOrder.equals(InterfaceOrder.INVERSE)) {
			superInterfaceNodes = Lists.reverse(superInterfaceNodes);
		}
		return superInterfaceNodes;
	}

	@Override
	protected List<TypeNode<? extends T>> getChildren(Priority priority) {
		List<TypeNode<? extends T>> children;
		if(priority.equals(Priority.CLASSES_FIRST)) {
			children = (List)getKnownImplementorNodes();
			children.addAll(getKnownSubInterfaceNodes());
		} else {
			children = (List)getKnownSubInterfaceNodes();
			children.addAll(getKnownImplementorNodes());
		}
		return children;
	}
	
}
