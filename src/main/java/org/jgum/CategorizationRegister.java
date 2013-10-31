package org.jgum;

import java.util.HashMap;
import java.util.Map;

import org.jgum.category.Categorization;
import org.jgum.category.Category;

/**
 * Defines a register of categorizations.
 * @author sergioc
 *
 */
public class CategorizationRegister {

	private Map<Object, Categorization<?>> categorizationIndex; //categorizations are backed up in this map.
	
	public CategorizationRegister () {
		categorizationIndex = new HashMap<>();
	}
	
	/**
	 * @param key the key under which the requested categorization is registered.
	 * @return the categorization registered under the key sent as a parameter.
	 */
	public <T extends Category<?>> Categorization<T> getHierarchy(Object key) {
		return (Categorization<T>)categorizationIndex.get(key);
	}
	
	/**
	 * Adds a new categorization to the register.
	 * @param key the key under which the given categorization will be registered.
	 * @param categorization  the categorization to register under the key sent as a parameter.
	 */
	public void register(Object key, Categorization<?> categorization) {
		categorizationIndex.put(key, categorization);
	}

}
