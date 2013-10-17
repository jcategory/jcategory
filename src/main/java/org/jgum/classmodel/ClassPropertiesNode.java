package org.jgum.classmodel;

import java.util.Collections;
import java.util.List;

public class ClassPropertiesNode<T> extends AbstractClassPropertiesNode<T> {
	
	public static ClassPropertiesNode<Object> root() {
		return new ClassPropertiesNode();
	}
	
	private ClassPropertiesNode<? super T> superClassNode;
	private List<ClassPropertiesNode<? extends T>> knownSubClassNodes;
	
	private ClassPropertiesNode() {
		this((Class<T>) Object.class, null);
	}
	
	public ClassPropertiesNode(Class<T> wrappedClass, ClassPropertiesNode<? super T> parentClassNode) {
		this(wrappedClass, parentClassNode, Collections.<InterfacePropertiesNode<? super T>>emptyList());
	}
	
	public ClassPropertiesNode(Class<T> wrappedClass, ClassPropertiesNode<? super T> superClassNode, List<InterfacePropertiesNode<? super T>> superInterfaceNodes) {
		super(wrappedClass, superInterfaceNodes);
		if(superClassNode != null)
			setSuperClassNode(superClassNode);
	}
	
	public ClassPropertiesNode<? super T> getSuperClassNode() {
		return superClassNode;
	}
	
	public void setSuperClassNode(ClassPropertiesNode<? super T> superClassNode) {
		this.superClassNode = superClassNode;
		superClassNode.addKnownSubClassNode((ClassPropertiesNode<? extends T>) this);
	}
	
	protected void addKnownSubClassNode(ClassPropertiesNode<? extends T> subClassNode) {
		knownSubClassNodes.add(subClassNode);
	}
	
	@Override
	protected void setSuperInterfaceNodes(List<InterfacePropertiesNode<? super T>> superInterfaceNodes) {
		super.setSuperInterfaceNodes(superInterfaceNodes);
		for(InterfacePropertiesNode<? super T> superInterfaceNode : superInterfaceNodes) {
			superInterfaceNode.addKnownImplementorNode(this);
		}
	}

	public List<ClassPropertiesNode<? extends T>> getKnownSubClassNodes() {
		return knownSubClassNodes;
	}
	
	
	
}
