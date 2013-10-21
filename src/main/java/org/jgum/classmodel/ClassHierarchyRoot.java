package org.jgum.classmodel;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgum.JGum;
import org.jgum.classmodel.ClassHierarchyRoot.Any;
import org.jgum.graph.Node;
import org.jgum.graph.TraversalPolicy;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public class ClassHierarchyRoot extends TypeNode<Any> {

	private Map<Class<?>, TypeNode<?>> propertiesNodeIndex;
	
	private ClassNode<Object> objectClassNode;
	private List<InterfaceNode<?>> rootInterfaceNodes;
	
	static class Any {}
	
	public ClassHierarchyRoot(JGum context) {
		super(context, Any.class);
		propertiesNodeIndex = new HashMap<>();
		objectClassNode = ClassNode.root(context);
		putNode(Object.class, objectClassNode);
		rootInterfaceNodes = new ArrayList<>();
	}

	ClassNode<Object> getRootClassNode() {
		return objectClassNode;
	}

	List<InterfaceNode<?>> getRootInterfaceNodes() {
		return rootInterfaceNodes;
	}

	public <T> TypeNode<T> getNode(Class<T> clazz) {
		return (TypeNode<T>) propertiesNodeIndex.get(clazz);
	}
	
	protected <T> void putNode(Class<T> clazz, TypeNode<T> node) {
		propertiesNodeIndex.put(clazz, node);
	}
	
	public <T> TypeNode<T> getOrCreateNode(Class<T> clazz) {
		TypeNode<T> node = getNode(clazz);
		if(node == null) {
			if(clazz.isInterface())
				node = createInterfaceNode(clazz);
			else
				node = createClassNode(clazz);
		}
		return node;
	}
	
	private <T> TypeNode<T> createClassNode(Class<T> clazz) {
		ClassNode parentClassNode = (ClassNode) getOrCreateNode(clazz.getSuperclass());
		List<InterfaceNode> superInterfaceNodes = new ArrayList<>();
		for(Class<?> superInterface : clazz.getInterfaces()) {
			InterfaceNode superInterfaceNode = (InterfaceNode) getOrCreateNode(superInterface);
			superInterfaceNodes.add(superInterfaceNode);
		}
		ClassNode classNode = new ClassNode(getContext(), clazz, parentClassNode, superInterfaceNodes);
		putNode(clazz, classNode);
		return classNode;
	}
	
	private <T> TypeNode<T> createInterfaceNode(Class<T> clazz) {
		List<InterfaceNode> superInterfaceNodes = new ArrayList<>();
		for(Class<?> superInterface : clazz.getInterfaces()) {
			InterfaceNode superInterfaceNode = (InterfaceNode) getOrCreateNode(superInterface);
			superInterfaceNodes.add(superInterfaceNode);
		}
		InterfaceNode interfaceNode = new InterfaceNode(getContext(), clazz, superInterfaceNodes);
		if(superInterfaceNodes.isEmpty())
			rootInterfaceNodes.add(interfaceNode);
		propertiesNodeIndex.put(clazz, interfaceNode);
		return interfaceNode;
	}

	@Override
	protected List<TypeNode<?>> getParents(Priority priority, InterfaceOrder interfaceOrder) {
		return Collections.emptyList();
	}

	@Override
	protected List<TypeNode<?>> getChildren(Priority priority) {
		List<TypeNode<?>> children;
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
	public <U extends Node> FluentIterable<U> path(TraversalPolicy<U> traversalPolicy) {
		FluentIterable<U> it = super.path(traversalPolicy);
		return it.filter(new Predicate<U>() {
			@Override
			public boolean apply(Node node) {
				return !(node instanceof ClassHierarchyRoot);
			}
		});	
	}

}
