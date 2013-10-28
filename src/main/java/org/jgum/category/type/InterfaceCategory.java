package org.jgum.category.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * A node wrapping an interface object.
 * @author sergioc
 *
 * @param <T> the type of the wrapped interface.
 */
public class InterfaceCategory<T> extends TypeCategory<T> {

	private List<ClassCategory<? extends T>> knownImplementorNodes;
	private List<InterfaceCategory<? extends T>> knownSubInterfaceNodes;
	
	InterfaceCategory(TypeHierarchy typeHierarchy, Class<T> interfaze) {
		this(typeHierarchy, interfaze, Collections.<InterfaceCategory<? super T>>emptyList());
	}
	
	InterfaceCategory(TypeHierarchy typeHierarchy, Class<T> wrappedInterface, List<InterfaceCategory<? super T>> superInterfaceNodes) {
		super(typeHierarchy, wrappedInterface, superInterfaceNodes);
		knownImplementorNodes = new ArrayList<>();
		knownSubInterfaceNodes = new ArrayList<>();
	}	
	
	void addKnownImplementorNode(ClassCategory<? extends T> implementorNode) {
		knownImplementorNodes.add(implementorNode);
	}
	
	void addKnownSubInterfaceNode(InterfaceCategory<? extends T> subInterfaceNode) {
		knownSubInterfaceNodes.add(subInterfaceNode);
	}
	
	@Override
	protected void setSuperInterfaceNodes(List<InterfaceCategory<? super T>> superInterfaceNodes) {
		super.setSuperInterfaceNodes(superInterfaceNodes);
		for(InterfaceCategory<? super T> superInterfaceNode : superInterfaceNodes) {
			superInterfaceNode.addKnownSubInterfaceNode(this);
		}
	}
	
	public List<ClassCategory<? extends T>> getKnownImplementorNodes() {
		return new ArrayList<>(knownImplementorNodes);
	}
	
	public List<InterfaceCategory<? extends T>> getKnownSubInterfaceNodes() {
		return new ArrayList<>(knownSubInterfaceNodes);
	}

	@Override
	protected List<TypeCategory<? super T>> getParents(Priority priority, InterfaceOrder interfaceOrder) {
		List<TypeCategory<? super T>> superInterfaceNodes = (List)getSuperInterfaceNodes();
		if(interfaceOrder.equals(InterfaceOrder.REVERSE)) {
			superInterfaceNodes = Lists.reverse(superInterfaceNodes);
		}
		return superInterfaceNodes;
	}

	@Override
	protected List<TypeCategory<? extends T>> getChildren(Priority priority) {
		List<TypeCategory<? extends T>> children;
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
