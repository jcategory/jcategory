package org.jgum.category.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * A node wrapping a class (not an interface) object.
 * @author sergioc
 *
 * @param <T> the type of the wrapped class.
 */
public class ClassCategory<T> extends TypeCategory<T> {
	
	static ClassCategory<Object> root(TypeHierarchy typeHierarchy) {
		return new ClassCategory(typeHierarchy);
	}
	
	private ClassCategory<? super T> superClassNode;
	private List<ClassCategory<? extends T>> knownSubClassNodes;
	
	private ClassCategory(TypeHierarchy typeHierarchy) {
		this(typeHierarchy, (Class<T>) Object.class, null);
	}
	
	ClassCategory(TypeHierarchy typeHierarchy, Class<T> wrappedClass, ClassCategory<? super T> parentClassNode) {
		this(typeHierarchy, wrappedClass, parentClassNode, Collections.<InterfaceCategory<? super T>>emptyList());
	}
	
	ClassCategory(TypeHierarchy typeHierarchy, Class<T> wrappedClass, ClassCategory<? super T> superClassNode, List<InterfaceCategory<? super T>> superInterfaceNodes) {
		super(typeHierarchy, wrappedClass, superInterfaceNodes);
		knownSubClassNodes = new ArrayList<>();
		if(superClassNode != null)
			setSuperClassNode(superClassNode);
	}
	
	public ClassCategory<? super T> getSuperClassNode() {
		return superClassNode;
	}
	
	private void setSuperClassNode(ClassCategory<? super T> superClassNode) {
		this.superClassNode = superClassNode;
		superClassNode.addKnownSubClassNode((ClassCategory<? extends T>) this);
	}
	
	private void addKnownSubClassNode(ClassCategory<? extends T> subClassNode) {
		knownSubClassNodes.add(subClassNode);
	}
	
	@Override
	protected void setSuperInterfaceNodes(List<InterfaceCategory<? super T>> superInterfaceNodes) {
		super.setSuperInterfaceNodes(superInterfaceNodes);
		for(InterfaceCategory<? super T> superInterfaceNode : superInterfaceNodes) {
			superInterfaceNode.addKnownImplementorNode(this);
		}
	}

	public List<ClassCategory<? extends T>> getKnownSubClassNodes() {
		return new ArrayList<>(knownSubClassNodes);
	}

	@Override
	protected List<TypeCategory<? super T>> getParents(Priority priority, InterfaceOrder interfaceOrder) {
		List<InterfaceCategory<? super T>> superInterfaceNodes = (List)getSuperInterfaceNodes();
		if(interfaceOrder.equals(InterfaceOrder.REVERSE)) {
			//the reversed list does not support addition of elements,
			//then a new list is created.
			superInterfaceNodes = new ArrayList<>(Lists.reverse(superInterfaceNodes)); 
		}
		List<TypeCategory<? super T>> parents = (List)superInterfaceNodes;
		ClassCategory<? super T> superClassNode = getSuperClassNode();
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
	protected List<TypeCategory<? extends T>> getChildren(Priority priority) {
		return (List)getKnownSubClassNodes();
	}
	
}
