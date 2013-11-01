package org.jgum.category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * A hierarchical category associated with some arbitrary named properties.
 * @author sergioc
 *
 */
public abstract class Category {

	private final Map<Object, Object> properties; //properties associated with this category are backed up in this map.
	private final Categorization<?> categorization; //the hierarchy where this category exists.
	
	
	/**
	 * @param categorization the hierarchy where this category exists.
	 * @param label a label identifying this category.
	 */
	public Category(Categorization<?> categorization) {
		this.categorization = categorization;
		properties = new HashMap<>();
	}

	public Categorization<?> getCategoryHierarchy() {
		return categorization;
	}

	
	/**
	 * @param key the property name.
	 * @return the label of the property in the current node (if any).
	 * @see Map#get(Object)
	 */
	public Object getProperty(Object key) {
		return properties.get(key);
	}
	
	/**
	 * 
	 * @see Map#containsKey(Object)
	 */
	public boolean containsProperty(Object key) {
		return properties.containsKey(key);
	}
	
	/**
	 * 
	 * @see Map#put(Object,Object)
	 */
	public Object putProperty(Object key, Object value) {
		return properties.put(key, value);
	}
	
	/**
	 * 
	 * @param property the property.
	 * @param propertyValue the label of the property.
	 * @param canOverride a boolean indicating if the property can be overridden or not. 
	 * @throws RuntimeException if the property exists and it cannot be overridden.
	 */
	public void putProperty(Object key, Object propertyValue, boolean canOverride) {
		Object currentPropertyValue = getProperty(key);
		if(currentPropertyValue!=null && !canOverride)
			throw new RuntimeException("The node already has a label for the property \"" + key + "\":" + currentPropertyValue +
				". Attempting to override this property with: " + propertyValue + ".");
		else
			putProperty(key, propertyValue);
	}
	
	/**
	 * 
	 * @param linearizationFunction is a linearization function.
	 * @return An iterable of nodes, according to the given linearization function.
	 */
	public <U extends Category> FluentIterable<U> linearize(Function<U,FluentIterable<U>> linearizationFunction) {
		return linearizationFunction.apply((U)this);
	}
	
	/**
	 * 
	 * @return a linearization using the default bottom up linearization function.
	 */
	public <U extends Category> FluentIterable<U> bottomUpLinearization() {
		return (FluentIterable<U>)linearize(categorization.getBottomUpLinearizationFunction());
	}

	public abstract <U extends Category> List<U> getParents();

	public abstract <U extends Category> List<U> getChildren();
	
	/**
	 * 
	 * @return a linearization using the default top down linearization function.
	 */
	public <U extends Category> FluentIterable<U> topDownLinearization() {
		return (FluentIterable<U>)linearize(categorization.getTopDownLinearizationFunction());
	}
	
	@Override
	public String toString() {
		return properties.toString();
	}

}
