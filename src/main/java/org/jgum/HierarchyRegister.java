package org.jgum;

import java.util.HashMap;
import java.util.Map;

import org.jgum.category.Category;
import org.jgum.category.CategoryHierarchy;

/**
 * A register for hierarchies of categories.
 * @author sergioc
 *
 */
public class HierarchyRegister {

	private Map<Object, CategoryHierarchy<?>> hierarchyIndex; //hierarchies are backed up in this map.
	
	public HierarchyRegister () {
		hierarchyIndex = new HashMap<>();
	}
	
	/**
	 * @param key the key under which the requested hierarchy is registered.
	 * @return the hierarchy registered under the key sent as a parameter.
	 */
	public <T extends Category<?>> CategoryHierarchy<T> getHierarchy(Object key) {
		return (CategoryHierarchy<T>)hierarchyIndex.get(key);
	}
	
	/**
	 * Adds a new hierarchy to the register.
	 * @param key the key under which the given hierarchy will be registered.
	 * @param hierarchy  the hierarchy to register under the key sent as a parameter.
	 */
	public void register(Object key, CategoryHierarchy<?> hierarchy) {
		hierarchyIndex.put(key, hierarchy);
	}

}
