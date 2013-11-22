package org.jgum.category.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgum.category.Categorization;
import org.jgum.category.CategorizationListener;
import org.jgum.category.CategorizationListenersManager;
import org.jgum.category.Key;
import org.jgum.traversal.RedundancyCheck;
import org.jgum.traversal.SearchStrategy;
import org.jgum.traversal.TraversalPolicy;

import com.google.common.base.Function;

public class TypeCategorization extends Categorization<TypeCategory<?>> {

	private final Map<Class<?>, TypeCategory<?>> categoryIndex;	
	private TypeCategoryRoot typeCategoryRoot;
	private final CategorizationListenersManager listenersManager; //category listeners notified when a new category is created.
	
	public TypeCategorization(Function<TypeCategory<?>, List<TypeCategory<?>>> bottomUpLinearizationFunction, 
			Function<TypeCategory<?>, List<TypeCategory<?>>> topDownLinearizationFunction) {
		this(bottomUpLinearizationFunction, topDownLinearizationFunction, (List)Collections.emptyList());
	}
	
	public TypeCategorization(Function<TypeCategory<?>, List<TypeCategory<?>>> bottomUpLinearization, 
			Function<TypeCategory<?>, List<TypeCategory<?>>> topDownLinearization,
			List<? extends CategorizationListener<TypeCategory<?>>> categorizationListeners) {
		super((Function)bottomUpLinearization, (Function)topDownLinearization);
		categoryIndex = new HashMap<>();
		listenersManager = new CategorizationListenersManager((List)categorizationListeners);
	}
	
	public TypeCategoryRoot getRoot() {
		if(typeCategoryRoot == null) {
			typeCategoryRoot = new TypeCategoryRoot(this);
			notifyCategorizationListeners(typeCategoryRoot);
		}
		return typeCategoryRoot;
	}

	public <T> TypeCategory<T> getTypeCategory(Class<T> clazz) {
		return (TypeCategory<T>) categoryIndex.get(clazz);
	}
	
	private <T> void putTypeCategory(Class<T> clazz, TypeCategory<T> node) {
		categoryIndex.put(clazz, node);
		notifyCategorizationListeners(node);
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
			classCategory = new ClassCategory(getRoot());
		} else {
			ClassCategory parentClassNode = (ClassCategory) getOrCreateTypeCategory(clazz.getSuperclass());
			List<InterfaceCategory> superInterfaceNodes = new ArrayList<>();
			for(Class<?> superInterface : clazz.getInterfaces()) {
				InterfaceCategory superInterfaceNode = (InterfaceCategory) getOrCreateTypeCategory(superInterface);
				superInterfaceNodes.add(superInterfaceNode);
			}
			classCategory = new ClassCategory(clazz, parentClassNode, superInterfaceNodes);
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
			interfaceCategory = new InterfaceCategory(clazz, getRoot());
			getRoot().addRootInterfaceNode(interfaceCategory);
		} else {
			interfaceCategory = new InterfaceCategory(clazz, superInterfaceNodes);
		}
		putTypeCategory(clazz, interfaceCategory);
		return interfaceCategory;
	}

	
	/**
	 * @param upperBounds a list of upper bounds.
	 * @return a list with type categories that are descendant of all the upper bounds (at the same time) passed by as arguments.
	 */
	public <T extends TypeCategory> List<T> findBoundedTypes(List<Class<?>> upperBounds) {
		if(upperBounds.isEmpty())
			throw new RuntimeException("Empty bounds for quantified property.");
		List<T> boundedTypes = new ArrayList<>();
		TypeCategory typeCategory = getOrCreateTypeCategory(upperBounds.get(0));
		List<TypeCategory<Class<?>>> topDownCategories = typeCategory.linearize(TraversalPolicy.topDownTraversalPolicy(SearchStrategy.BREADTH_FIRST, RedundancyCheck.KEEP_FIRST));
		List<Class<?>> allButFirstBound = upperBounds.subList(1, upperBounds.size());
		for(TypeCategory<Class<?>> candidateCategory : topDownCategories) {
			if(candidateCategory.isInBoundaries(allButFirstBound)) {
				boundedTypes.add((T) candidateCategory);
			}
		}
		return boundedTypes;
	}
	
	/**
	 * Sets a property to all the type categories quantified by the given upper bounds.
	 * @param upperBounds a list of upper bounds.
	 * @param key the key of the property.
	 * @param value the value of the property.
	 */
	public void setQuantified(List<Class<?>> upperBounds, Key key, Object value) {
		for(TypeCategory<Class<?>> boundedCategory : findBoundedTypes(upperBounds)) {
			boundedCategory.setProperty(key, value);
		}
	}
	
	/**
	 * Removes a property from all the type categories quantified by the given upper bounds.
	 * @param upperBounds a list of upper bounds.
	 * @param key the key of the property.
	 */
	public void removeQuantified(List<Class<?>> upperBounds, Key key) {
		for(TypeCategory<Class<?>> boundedCategory : findBoundedTypes(upperBounds)) {
			boundedCategory.removeLocalProperty(key);
		}
	}
	
	protected void notifyCategorizationListeners(TypeCategory<?> newCategory) {
		listenersManager.notifyCategorizationListeners(newCategory);
	}

	public void addCategorizationListener(CategorizationListener<TypeCategory<?>> creationListener) {
		listenersManager.add(creationListener);
	}
	
	public void removeCategorizationListener(CategorizationListener<TypeCategory<?>> creationListener) {
		listenersManager.remove(creationListener);
	}
	
}
