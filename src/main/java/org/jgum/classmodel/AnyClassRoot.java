package org.jgum.classmodel;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgum.JGum;
import org.jgum.classmodel.AnyClassRoot.Any;
import org.jgum.graph.Node;
import org.jgum.graph.NodeCreationListener;
import org.jgum.graph.NodeCreationListenersManager;
import org.jgum.graph.TraversalPolicy;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * The root node in a hierarchy of classes and interfaces.
 * @author sergioc
 *
 */
public class AnyClassRoot extends TypeNode<Any> {

	private final NodeCreationListenersManager listenersManager;
	private final Map<Class<?>, TypeNode<?>> nodeIndex;
	private ClassNode<Object> objectClassNode;
	private final List<InterfaceNode<?>> rootInterfaceNodes;
	
	static class Any {}
	
	public AnyClassRoot(JGum context) {
		super(context, Any.class);
		nodeIndex = new HashMap<>();
		listenersManager = new NodeCreationListenersManager();
		putNode(Object.class, objectClassNode);
		rootInterfaceNodes = new ArrayList<>();
	}

	@Override
	public Object put(Object key, Object value) {
		throw new UnsupportedOperationException("The root of the class and interface hierarchy cannot hold properties");
	}
	
	@Override
	public void put(Object key, Object value, boolean canOverride) {
		throw new UnsupportedOperationException("The root of the class and interface hierarchy cannot hold properties");
	}
	
	public ClassNode<Object> getRootClassNode() {
		if(objectClassNode == null) {
			objectClassNode = (ClassNode<Object>)getOrCreateNode(Object.class);
		}
		return objectClassNode;
	}

	public List<InterfaceNode<?>> getRootInterfaceNodes() {
		return new ArrayList<>(rootInterfaceNodes);
	}

	public <T> TypeNode<T> getNode(Class<T> clazz) {
		return (TypeNode<T>) nodeIndex.get(clazz);
	}
	
	private <T> void putNode(Class<T> clazz, TypeNode<T> node) {
		nodeIndex.put(clazz, node);
		notifyCreationListeners(node);
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
		ClassNode classNode;
		if(Object.class.equals(clazz)) {
			classNode = ClassNode.root(getContext());
		} else {
			ClassNode parentClassNode = (ClassNode) getOrCreateNode(clazz.getSuperclass());
			List<InterfaceNode> superInterfaceNodes = new ArrayList<>();
			for(Class<?> superInterface : clazz.getInterfaces()) {
				InterfaceNode superInterfaceNode = (InterfaceNode) getOrCreateNode(superInterface);
				superInterfaceNodes.add(superInterfaceNode);
			}
			classNode = new ClassNode(getContext(), clazz, parentClassNode, superInterfaceNodes);
		}
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
		putNode(clazz, interfaceNode);
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
	public <U extends Node<?>> FluentIterable<U> path(TraversalPolicy<U> traversalPolicy) {
		FluentIterable<U> it = super.path(traversalPolicy);
		return it.filter(new Predicate<U>() {
			@Override
			public boolean apply(Node node) {
				return !(node instanceof AnyClassRoot);
			}
		});	
	}

	public void addNodeCreationListener(NodeCreationListener<TypeNode<?>> creationListener) {
		listenersManager.addNodeCreationListener(creationListener);
	}
	
	void notifyCreationListeners(TypeNode<?> node) {
		listenersManager.notifyCreationListeners(node);
	}
}
