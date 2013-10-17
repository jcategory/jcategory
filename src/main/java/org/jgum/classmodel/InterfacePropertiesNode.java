package org.jgum.classmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InterfacePropertiesNode<T> extends AbstractClassPropertiesNode<T> {

	public List<ClassPropertiesNode<? extends T>> knownImplementorNodes;
	public List<InterfacePropertiesNode<? extends T>> knownSubInterfaceNodes;
	
	public InterfacePropertiesNode(Class<T> interfaze) {
		this(interfaze, Collections.<InterfacePropertiesNode<? super T>>emptyList());
	}
	
	public InterfacePropertiesNode(Class<T> wrappedInterface, List<InterfacePropertiesNode<? super T>> superInterfaceNodes) {
		super(wrappedInterface, superInterfaceNodes);
		knownImplementorNodes = new ArrayList<>();
		knownSubInterfaceNodes = new ArrayList<>();
	}	
	
	protected void addKnownImplementorNode(ClassPropertiesNode<? extends T> implementorNode) {
		knownImplementorNodes.add(implementorNode);
	}
	
	protected void addKnownSubInterfaceNode(InterfacePropertiesNode<? extends T> subInterfaceNode) {
		knownSubInterfaceNodes.add(subInterfaceNode);
	}
	
	@Override
	protected void setSuperInterfaceNodes(List<InterfacePropertiesNode<? super T>> superInterfaceNodes) {
		super.setSuperInterfaceNodes(superInterfaceNodes);
		for(InterfacePropertiesNode<? super T> superInterfaceNode : superInterfaceNodes) {
			superInterfaceNode.addKnownSubInterfaceNode(this);
		}
	}
	
	public List<ClassPropertiesNode<? extends T>> getKnownImplementorNodes() {
		return knownImplementorNodes;
	}
	
	public List<InterfacePropertiesNode<? extends T>> getKnownSubInterfaceNodes() {
		return knownSubInterfaceNodes;
	}
	
}
