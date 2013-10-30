package org.jgum.category.type;

import static org.jgum.JGum.DEFAULT_BOTTOM_UP_TYPE_LINEARIZATION_FUNCTION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgum.category.Category;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * A category wrapping a class or interface object.
 * @author sergioc
 *
 * @param <T> the type of the wrapped class.
 */
public abstract class TypeCategory<T> extends Category<Class<T>> {

	private List<InterfaceCategory<? super T>> superInterfaceNodes;
	
	TypeCategory(Class<T> wrappedClazz, TypeHierarchy typeHierarchy) {
		this(wrappedClazz, typeHierarchy, Collections.<InterfaceCategory<? super T>>emptyList());
	}
	
	TypeCategory(Class<T> wrappedClazz, TypeHierarchy typeHierarchy, List<InterfaceCategory<? super T>> superInterfaceNodes) {
		super(wrappedClazz, typeHierarchy);
		setSuperInterfaceNodes(superInterfaceNodes);
	}

	public TypeHierarchy getTypeHierarchy() {
		return (TypeHierarchy)super.getCategoryHierarchy();
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
	protected String idToString() {
		if(getId() == null)
			return "null";
		return getId().getName();
	}
	
	protected abstract <U extends TypeCategory<? super T>> List<U> getParents(Priority priority, InterfaceOrder interfaceOrder);

	protected abstract <U extends TypeCategory<? extends T>> List<U> getChildren(Priority priority);

}
