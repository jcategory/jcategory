package org.jgum.classmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgum.JGum;

public class InterfaceNode<T> extends AbstractClassNode<T> {

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
	
}
