package org.jgum.category.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgum.JGum;
import org.jgum.category.CategoryCreationListener;
import org.jgum.category.CategoryCreationListenersManager;

public class TypeHierarchy {

	private final CategoryCreationListenersManager listenersManager;
	private TypeCategoryRoot typeCategoryRoot;
	private final JGum context;
	private final Map<Class<?>, TypeCategory<?>> nodeIndex;
	
	private final BottomUpTypeTraversalPolicy<TypeCategory<?>> bottomUpTypeTraversalPolicy; //bottom up class traversing strategy for this context.
	private final TopDownTypeTraversalPolicy<TypeCategory<?>> topDownTypeTraversalPolicy; //top down class traversing strategy for this context.
	
	public TypeHierarchy(JGum context) {
		this.context = context;
		bottomUpTypeTraversalPolicy = context.getBottomUpTypeTraversalPolicy();
		topDownTypeTraversalPolicy = context.getTopDownTypeTraversalPolicy();
		nodeIndex = new HashMap<>();
		listenersManager = new CategoryCreationListenersManager();
	}
	
	public TypeCategoryRoot getRoot() {
		if(typeCategoryRoot == null) {
			typeCategoryRoot = new TypeCategoryRoot(this);
			listenersManager.notifyCreationListeners(typeCategoryRoot);
		}
		return typeCategoryRoot;
	}
	
	public JGum getContext() {
		return context;
	}
	
	public <T> TypeCategory<T> getTypeCategory(Class<T> clazz) {
		return (TypeCategory<T>) nodeIndex.get(clazz);
	}
	
	private <T> void putTypeCategory(Class<T> clazz, TypeCategory<T> node) {
		nodeIndex.put(clazz, node);
		listenersManager.notifyCreationListeners(node);
	}

	public <T> TypeCategory<T> getOrCreateTypeCategory(Class<T> clazz) {
		TypeCategory<T> node = getTypeCategory(clazz);
		if(node == null) {
			if(clazz.isInterface())
				node = createInterfaceCategory(clazz);
			else
				node = createClassCategory(clazz);
		}
		return node;
	}
	
	private <T> ClassCategory<T> createClassCategory(Class<T> clazz) {
		ClassCategory classCategory;
		if(Object.class.equals(clazz)) {
			classCategory = ClassCategory.root(this);
		} else {
			ClassCategory parentClassNode = (ClassCategory) getOrCreateTypeCategory(clazz.getSuperclass());
			List<InterfaceCategory> superInterfaceNodes = new ArrayList<>();
			for(Class<?> superInterface : clazz.getInterfaces()) {
				InterfaceCategory superInterfaceNode = (InterfaceCategory) getOrCreateTypeCategory(superInterface);
				superInterfaceNodes.add(superInterfaceNode);
			}
			classCategory = new ClassCategory(this, clazz, parentClassNode, superInterfaceNodes);
		}
		putTypeCategory(clazz, classCategory);
		return classCategory;
	}
	
	private <T> InterfaceCategory<T> createInterfaceCategory(Class<T> clazz) {
		List<InterfaceCategory> superInterfaceNodes = new ArrayList<>();
		for(Class<?> superInterface : clazz.getInterfaces()) {
			InterfaceCategory superInterfaceNode = (InterfaceCategory) getOrCreateTypeCategory(superInterface);
			superInterfaceNodes.add(superInterfaceNode);
		}
		InterfaceCategory interfaceCategory = new InterfaceCategory(this, clazz, superInterfaceNodes);
		if(superInterfaceNodes.isEmpty())
			typeCategoryRoot.addRootInterfaceNode(interfaceCategory);
		putTypeCategory(clazz, interfaceCategory);
		return interfaceCategory;
	}
	
	public void addNodeCreationListener(CategoryCreationListener<TypeCategory<?>> creationListener) {
		listenersManager.addNodeCreationListener(creationListener);
	}

	/**
	 * 
	 * @return the bottom up class traversing strategy for this context.
	 */
	public BottomUpTypeTraversalPolicy<TypeCategory<?>> getBottomUpTypeTraversalPolicy() {
		return bottomUpTypeTraversalPolicy;
	}

	/**
	 * 
	 * @return the top down class traversing strategy for this context.
	 */
	public TopDownTypeTraversalPolicy<TypeCategory<?>> getTopDownTypeTraversalPolicy() {
		return topDownTypeTraversalPolicy;
	}
	
}
