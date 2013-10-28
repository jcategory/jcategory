package org.jgum.category.type;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgum.category.Category;
import org.jgum.category.TraversalPolicy;
import org.jgum.category.type.TypeCategoryRoot.Any;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * The root node in a hierarchy of classes and interfaces.
 * @author sergioc
 *
 */
public class TypeCategoryRoot extends TypeCategory<Any> {

	private ClassCategory<Object> objectClassNode;
	private final List<InterfaceCategory<?>> rootInterfaceNodes;
	
	public static class Any {}
	
	public TypeCategoryRoot(TypeHierarchy typeHierarchy) {
		super(typeHierarchy, Any.class);
		rootInterfaceNodes = new ArrayList<>();
	}

	void addRootInterfaceNode(InterfaceCategory<?> rootInterfaceNode) {
		rootInterfaceNodes.add(rootInterfaceNode);
	}
	
//	@Override
//	public Object put(Object key, Object value) {
//		throw new UnsupportedOperationException("The root of the class and interface hierarchy cannot hold properties");
//	}
//	
//	@Override
//	public void put(Object key, Object value, boolean canOverride) {
//		throw new UnsupportedOperationException("The root of the class and interface hierarchy cannot hold properties");
//	}
	
	public ClassCategory<Object> getRootClassNode() {
		if(objectClassNode == null) {
			objectClassNode = (ClassCategory<Object>)getTypeHierarchy().getOrCreateTypeCategory(Object.class);
		}
		return objectClassNode;
	}

	public List<InterfaceCategory<?>> getRootInterfaceNodes() {
		return new ArrayList<>(rootInterfaceNodes);
	}

	@Override
	protected List<TypeCategory<?>> getParents(Priority priority, InterfaceOrder interfaceOrder) {
		return Collections.emptyList();
	}

	@Override
	protected List<TypeCategory<?>> getChildren(Priority priority) {
		List<TypeCategory<?>> children;
		if(priority.equals(Priority.CLASSES_FIRST)) {
			children = (List)asList(getRootClassNode());
			children.addAll(getRootInterfaceNodes());
		} else {
			children = (List)getRootInterfaceNodes();
			children.add(getRootClassNode());
		}
		return children;
	}

	@Override
	public <U extends Category<?>> FluentIterable<U> linearization(TraversalPolicy<U> traversalPolicy) {
		FluentIterable<U> it = super.linearization(traversalPolicy);
		return it.filter(new Predicate<U>() {
			@Override
			public boolean apply(Category category) {
				return !(category instanceof TypeCategoryRoot);
			}
		});	
	}

}
