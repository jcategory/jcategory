package org.jgum.category.type;

import static org.jgum.JGum.DEFAULT_BOTTOM_UP_TYPE_LINEARIZATION_FUNCTION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgum.category.Category;
import org.jgum.category.LabeledCategory;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * A category wrapping a class or interface object.
 * @author sergioc
 *
 * @param <T> the type of the wrapped class.
 */
public abstract class TypeCategory<T> extends LabeledCategory<Class<T>> {

	private static final Priority DEFAULT_BOTTOM_UP_PRIORITY = Priority.CLASSES_FIRST;
	private static final InterfaceOrder DEFAULT_INTERFACE_ORDER = InterfaceOrder.DECLARATION;
	private static final Priority DEFAULT_TOP_DOWN_PRIORITY = Priority.INTERFACES_FIRST;
	
	
	private List<InterfaceCategory<? super T>> superInterfaceNodes;
	
	TypeCategory(TypeCategorization typeCategorization, Class<T> wrappedClazz) {
		this(typeCategorization, wrappedClazz, Collections.<InterfaceCategory<? super T>>emptyList());
	}
	
	TypeCategory(TypeCategorization typeCategorization, Class<T> wrappedClazz, List<InterfaceCategory<? super T>> superInterfaceNodes) {
		super(typeCategorization, wrappedClazz);
		setSuperInterfaceNodes(superInterfaceNodes);
	}

	public TypeCategorization getTypeHierarchy() {
		return (TypeCategorization)super.getCategorization();
	}
	
	public List<InterfaceCategory<? super T>> getSuperInterfaceNodes() {
		return new ArrayList<>((superInterfaceNodes));
	}
	
	protected void setSuperInterfaceNodes(List<InterfaceCategory<? super T>> superInterfaceNodes) {
		this.superInterfaceNodes = superInterfaceNodes;
	}
	
	public FluentIterable<InterfaceCategory<? super T>> getAncestorInterfaces() {
		return linearize((Function)DEFAULT_BOTTOM_UP_TYPE_LINEARIZATION_FUNCTION).skip(1).filter(InterfaceCategory.class);
	}
	
	@Override
	public <U extends Category> List<U> getParents() {
		return (List)getParents(DEFAULT_BOTTOM_UP_PRIORITY, DEFAULT_INTERFACE_ORDER);
	}

	@Override
	public <U extends Category> List<U> getChildren() {
		return (List)getChildren(DEFAULT_TOP_DOWN_PRIORITY);
	}
	
	protected abstract <U extends TypeCategory<? super T>> List<U> getParents(Priority priority, InterfaceOrder interfaceOrder);

	protected abstract <U extends TypeCategory<? extends T>> List<U> getChildren(Priority priority);

}
