package org.jgum.classmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgum.JGum;
import org.jgum.classmodel.ClassHierarchyRoot.Any;
import org.jgum.path.PropertiesNode;

public class ClassHierarchyRoot extends AbstractClassNode<Any> {

	private Map<Class<?>, PropertiesNode> propertiesNodeIndex;
	
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

	public PropertiesNode get(Class<?> clazz) {
		return propertiesNodeIndex.get(clazz);
	}
	
	protected void put(Class<?> clazz, PropertiesNode propertiesNode) {
		propertiesNodeIndex.put(clazz, propertiesNode);
	}
	
	public PropertiesNode getOrCreateNode(Class<?> clazz) {
		PropertiesNode propertiesNode = propertiesNodeIndex.get(clazz);
		if(propertiesNode == null) {
			if(clazz.isInterface())
				propertiesNode = createInterfaceNode(clazz);
			else
				propertiesNode = createClassNode(clazz);
		}
		return propertiesNode;
	}
	
	private PropertiesNode createClassNode(Class<?> clazz) {
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
	
	private PropertiesNode createInterfaceNode(Class<?> clazz) {
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
	
}
