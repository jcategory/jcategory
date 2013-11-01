package org.jgum.category.type;

import static java.util.Arrays.asList;
import static org.jgum.JGum.DEFAULT_BOTTOM_UP_TYPE_LINEARIZATION_FUNCTION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

/**
 * A category wrapping a class (not an interface).
 * @author sergioc
 *
 * @param <T> the type of the wrapped class.
 */
public class ClassCategory<T> extends TypeCategory<T> {

	private TypeCategory<?> parentCategory;
	private List<ClassCategory<? extends T>> knownSubClassNodes;
	
	ClassCategory(TypeCategorization typeCategorization, TypeCategoryRoot parentCategory) {
		this(typeCategorization, (Class<T>) Object.class, parentCategory);
	}
	
	ClassCategory(TypeCategorization typeCategorization, Class<T> wrappedClass, TypeCategory<?> parentCategory) {
		this(typeCategorization, wrappedClass, parentCategory, Collections.<InterfaceCategory<? super T>>emptyList());
	}
	
	ClassCategory(TypeCategorization typeCategorization, Class<T> wrappedClass, TypeCategory<?> parentCategory, List<InterfaceCategory<? super T>> superInterfaceNodes) {
		super(typeCategorization, wrappedClass, superInterfaceNodes);
		knownSubClassNodes = new ArrayList<>();
		this.parentCategory = parentCategory;
		if(parentCategory instanceof ClassCategory)
			((ClassCategory)parentCategory).addKnownSubClassNode((ClassCategory<? extends T>) this);
	}
	
	public TypeCategory<?> getParentCategory() {
		return parentCategory;
	}
	
	public ClassCategory<? super T> getSuperClassNode() {
		if(parentCategory instanceof ClassCategory)
			return (ClassCategory)parentCategory;
		return null;
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

	public FluentIterable<ClassCategory<? super T>> getAncestorClasses() {
		return linearize((Function)DEFAULT_BOTTOM_UP_TYPE_LINEARIZATION_FUNCTION).skip(1).filter(ClassCategory.class);
	}
	
	@Override
	protected List<TypeCategory<?>> getParents(Priority priority, InterfaceOrder interfaceOrder) {
		ClassCategory<? super T> superClassNode = getSuperClassNode();
		if(superClassNode != null) {
			List<InterfaceCategory<? super T>> superInterfaceNodes = (List)getSuperInterfaceNodes();
			if(interfaceOrder.equals(InterfaceOrder.REVERSE)) {
				//the reversed list does not support addition of elements,
				//then a new list is created.
				superInterfaceNodes = new ArrayList<>(Lists.reverse(superInterfaceNodes)); 
			}
			List<TypeCategory<?>> parents = (List)superInterfaceNodes;
			if(priority.equals(Priority.CLASSES_FIRST)) {
				parents.add(0, superClassNode);
			} else {
				parents.add(superClassNode);
			}
			return parents;
		} else {
			return (List)asList(getParentCategory());
		}
		
	}

	@Override
	protected List<TypeCategory<? extends T>> getChildren(Priority priority) {
		return (List)getKnownSubClassNodes();
	}
	
}
