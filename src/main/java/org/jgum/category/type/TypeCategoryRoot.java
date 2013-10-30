package org.jgum.category.type;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The root node in a hierarchy of classes and interfaces.
 * @author sergioc
 *
 */
public class TypeCategoryRoot extends TypeCategory<Object> {

	private ClassCategory<Object> objectClassNode;
	private final List<InterfaceCategory<?>> rootInterfaceNodes;
	
	//public static class Any {}
	
	TypeCategoryRoot(TypeHierarchy typeHierarchy) {
		super(null, typeHierarchy);
		rootInterfaceNodes = new ArrayList<>();
	}

	void addRootInterfaceNode(InterfaceCategory<?> rootInterfaceNode) {
		rootInterfaceNodes.add(rootInterfaceNode);
	}
	
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

}
