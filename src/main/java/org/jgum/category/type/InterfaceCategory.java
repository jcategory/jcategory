package org.jgum.category.type;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * A category wrapping an interface.
 * @author sergioc
 *
 * @param <T> the type of the wrapped interface.
 */
public class InterfaceCategory<T> extends TypeCategory<T> {

	private List<ClassCategory<? extends T>> knownImplementorNodes;
	private List<InterfaceCategory<? extends T>> knownSubInterfaceNodes;
	private TypeCategoryRoot defaultParentCategory; //the parent category of this interface in case it does not have super interfaces.
	
	InterfaceCategory(TypeCategorization typeCategorization, Class<T> interfaze, TypeCategoryRoot parentCategory) {
		this(typeCategorization, interfaze, Collections.<InterfaceCategory<? super T>>emptyList());
		this.defaultParentCategory = parentCategory;
	}
	
	InterfaceCategory(TypeCategorization typeCategorization, Class<T> wrappedInterface, List<InterfaceCategory<? super T>> superInterfaceNodes) {
		super(typeCategorization, wrappedInterface, superInterfaceNodes);
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
		if(defaultParentCategory != null) {
			return (List)asList(defaultParentCategory);
		} else {
			List<TypeCategory<? super T>> superInterfaceNodes = (List)getSuperInterfaceNodes();
			if(interfaceOrder.equals(InterfaceOrder.REVERSE)) {
				superInterfaceNodes = Lists.reverse(superInterfaceNodes);
			}
			return superInterfaceNodes;
		}
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
