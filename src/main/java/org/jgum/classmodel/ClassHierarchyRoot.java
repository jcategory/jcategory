package org.jgum.classmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgum.PropertiesNode;
import org.jgum.classmodel.ClassHierarchyRoot.Any;

public class ClassHierarchyRoot extends AbstractClassPropertiesNode<Any> {

	private Map<Class<?>, PropertiesNode> propertiesNodeIndex;
	
	private ClassPropertiesNode objectClassNode;
	private List<InterfacePropertiesNode> rootInterfaceNodes;
	
	static class Any {}
	
	public ClassHierarchyRoot() {
		super(Any.class);
		propertiesNodeIndex = new HashMap<>();
		objectClassNode = ClassPropertiesNode.root();
		rootInterfaceNodes = new ArrayList<>();
	}

	ClassPropertiesNode getObjectClassNode() {
		return objectClassNode;
	}

	List<InterfacePropertiesNode> getRootInterfaceNodes() {
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
		ClassPropertiesNode parentClassNode = (ClassPropertiesNode) getOrCreateNode(clazz.getSuperclass());
		List<InterfacePropertiesNode> superInterfaceNodes = new ArrayList<>();
		for(Class<?> superInterface : clazz.getInterfaces()) {
			InterfacePropertiesNode superInterfaceNode = (InterfacePropertiesNode) getOrCreateNode(superInterface);
			superInterfaceNodes.add(superInterfaceNode);
		}
		ClassPropertiesNode classPropertiesNode = new ClassPropertiesNode(clazz, parentClassNode, superInterfaceNodes);
		propertiesNodeIndex.put(clazz, classPropertiesNode);
		return classPropertiesNode;
	}
	
	private PropertiesNode createInterfaceNode(Class<?> clazz) {
		List<InterfacePropertiesNode> superInterfaceNodes = new ArrayList<>();
		for(Class<?> superInterface : clazz.getInterfaces()) {
			InterfacePropertiesNode superInterfaceNode = (InterfacePropertiesNode) getOrCreateNode(superInterface);
			superInterfaceNodes.add(superInterfaceNode);
		}
		InterfacePropertiesNode interfacePropertiesNode = new InterfacePropertiesNode(clazz, superInterfaceNodes);
		if(superInterfaceNodes.isEmpty())
			rootInterfaceNodes.add(interfacePropertiesNode);
		propertiesNodeIndex.put(clazz, interfacePropertiesNode);
		return interfacePropertiesNode;
	}
	
}
