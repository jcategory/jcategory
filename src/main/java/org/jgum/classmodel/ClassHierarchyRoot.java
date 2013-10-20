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

	private Map<Class<?>, Node> propertiesNodeIndex;
	
	private ClassNode objectClassNode;
	private List<InterfaceNode> rootInterfaceNodes;
	
	static class Any {}
	
	public ClassHierarchyRoot(JGum context) {
		super(context, Any.class);
		propertiesNodeIndex = new HashMap<>();
		objectClassNode = ClassNode.root(context);
		rootInterfaceNodes = new ArrayList<>();
	}

	ClassNode getObjectClassNode() {
		return objectClassNode;
	}

	List<InterfaceNode> getRootInterfaceNodes() {
		return rootInterfaceNodes;
	}

	public Node get(Class<?> clazz) {
		return propertiesNodeIndex.get(clazz);
	}
	
	protected void put(Class<?> clazz, Node node) {
		propertiesNodeIndex.put(clazz, node);
	}
	
	public Node getOrCreateNode(Class<?> clazz) {
		Node node = propertiesNodeIndex.get(clazz);
		if(node == null) {
			if(clazz.isInterface())
				node = createInterfaceNode(clazz);
			else
				node = createClassNode(clazz);
		}
		return node;
	}
	
	private Node createClassNode(Class<?> clazz) {
		ClassNode parentClassNode = (ClassNode) getOrCreateNode(clazz.getSuperclass());
		List<InterfaceNode> superInterfaceNodes = new ArrayList<>();
		for(Class<?> superInterface : clazz.getInterfaces()) {
			InterfaceNode superInterfaceNode = (InterfaceNode) getOrCreateNode(superInterface);
			superInterfaceNodes.add(superInterfaceNode);
		}
		ClassNode classNode = new ClassNode(getContext(), clazz, parentClassNode, superInterfaceNodes);
		propertiesNodeIndex.put(clazz, classNode);
		return classNode;
	}
	
	private Node createInterfaceNode(Class<?> clazz) {
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
	protected List<Node> getParents(Priority priority, InterfaceOrder interfaceOrder) {
		return Collections.emptyList();
	}

	@Override
	protected List<Node> getChildren(Priority priority) {
		List<Node> children;
		if(priority.equals(Priority.CLASSES_FIRST)) {
			children = (List)asList(getObjectClassNode());
			children.addAll(getRootInterfaceNodes());
		} else {
			children = (List)getRootInterfaceNodes();
			children.add(getObjectClassNode());
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
