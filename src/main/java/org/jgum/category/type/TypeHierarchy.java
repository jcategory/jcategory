package org.jgum.category.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgum.category.CategoryHierarchy;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class TypeHierarchy extends CategoryHierarchy<TypeCategory<?>> {

	private final Map<Class<?>, TypeCategory<?>> categoryIndex;	
	private TypeCategoryRoot typeCategoryRoot;
	
	public TypeHierarchy(Function<? extends TypeCategory<?>, FluentIterable<? extends TypeCategory<?>>> bottomUpLinearization, Function<? extends TypeCategory<?>, FluentIterable<? extends TypeCategory<?>>> topDownLinearization) {
		super((Function)bottomUpLinearization, (Function)topDownLinearization);
		categoryIndex = new HashMap<>();
	}
	
	public TypeCategoryRoot getRoot() {
		if(typeCategoryRoot == null) {
			typeCategoryRoot = new TypeCategoryRoot(this);
			notifyCreationListeners(typeCategoryRoot);
		}
		return typeCategoryRoot;
	}

	public <T> TypeCategory<T> getTypeCategory(Class<T> clazz) {
		return (TypeCategory<T>) categoryIndex.get(clazz);
	}
	
	private <T> void putTypeCategory(Class<T> clazz, TypeCategory<T> node) {
		categoryIndex.put(clazz, node);
		notifyCreationListeners(node);
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
			classCategory = new ClassCategory(this, getRoot());
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
		InterfaceCategory interfaceCategory;
		if(superInterfaceNodes.isEmpty()) {
			interfaceCategory = new InterfaceCategory(this, clazz, typeCategoryRoot);
			typeCategoryRoot.addRootInterfaceNode(interfaceCategory);
		} else {
			interfaceCategory = new InterfaceCategory(this, clazz, superInterfaceNodes);
		}
		putTypeCategory(clazz, interfaceCategory);
		return interfaceCategory;
	}

	@Override
	protected void notifyCreationListeners(TypeCategory<?> newCategory) {
		super.notifyCreationListeners(newCategory);
	}
	
}
