package org.jgum.category.type;

import static org.jgum.JGum.DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgum.category.Category;

import com.google.common.collect.FluentIterable;

/**
 * A node wrapping a class object (either a class or an interface).
 * @author sergioc
 *
 * @param <T> the type of the wrapped class.
 */
public abstract class TypeCategory<T> extends Category<Class<T>> {

	private List<InterfaceCategory<? super T>> superInterfaceNodes;
	private final TypeHierarchy typeHierarchy;
	
	TypeCategory(TypeHierarchy typeHierarchy, Class<T> wrappedClazz) {
		this(typeHierarchy, wrappedClazz, Collections.<InterfaceCategory<? super T>>emptyList());
	}
	
	TypeCategory(TypeHierarchy typeHierarchy, Class<T> wrappedClazz, List<InterfaceCategory<? super T>> superInterfaceNodes) {
		super(wrappedClazz);
		this.typeHierarchy = typeHierarchy;
		setSuperInterfaceNodes(superInterfaceNodes);
	}

	public TypeHierarchy getTypeHierarchy() {
		return typeHierarchy;
	}
	
	public List<InterfaceCategory<? super T>> getSuperInterfaceNodes() {
		return new ArrayList<>((superInterfaceNodes));
	}
	
	protected void setSuperInterfaceNodes(List<InterfaceCategory<? super T>> superInterfaceNodes) {
		this.superInterfaceNodes = superInterfaceNodes;
	}

	public FluentIterable<ClassCategory<? super T>> getAncestorClasses() {
		return (FluentIterable)linearization(DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY).skip(1).filter(ClassCategory.class);
	}
	
	public FluentIterable<InterfaceCategory<? super T>> getAncestorInterfaces() {
		return (FluentIterable)linearization(DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY).skip(1).filter(InterfaceCategory.class);
	}
	
	@Override
	public <U extends Category<?>> FluentIterable<U> bottomUpLinearization() {
		return linearization((BottomUpTypeTraversalPolicy)typeHierarchy.getBottomUpTypeTraversalPolicy());
	}
	
	@Override
	public <U extends Category<?>> FluentIterable<U> topDownLinearization() {
		return linearization((TopDownTypeTraversalPolicy)typeHierarchy.getTopDownTypeTraversalPolicy());
	}
	
	protected abstract <U extends TypeCategory<? super T>> List<U> getParents(Priority priority, InterfaceOrder interfaceOrder);

	protected abstract <U extends TypeCategory<? extends T>> List<U> getChildren(Priority priority);
	
	@Override
	public String toString() {
		return getValue().getName() + super.toString();
	}
	
}
