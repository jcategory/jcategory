package org.jgum.category;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgum.JGum;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * A node in a hierarchical graph associated with some arbitrary named properties.
 * @author sergioc
 *
 */
public abstract class Category<T> {

	private T value; //the value of this node.
	private Map<Object, Object> properties; //properties associated with this node are backed up in this map.
	
	/**
	 * 
	 * @param context the context in which this node is created.
	 * @param value the value of this node.
	 */
	public Category(T value) {
		this.value = value;
		properties = new HashMap<>();
	}

	/**
	 * 
	 * @return the value of this node.
	 */
	public T getValue() {
		return value;
	}
	
	/**
	 * @param key the property name.
	 * @return the value of the property in the current node (if any).
	 * @see Map#get(Object)
	 */
	public Object get(Object key) {
		return properties.get(key);
	}
	
	/**
	 * 
	 * @see Map#containsKey(Object)
	 */
	public boolean containsKey(Object key) {
		return properties.containsKey(key);
	}
	
	/**
	 * 
	 * @see Map#put(Object,Object)
	 */
	public Object put(Object key, Object value) {
		return properties.put(key, value);
	}
	
	/**
	 * 
	 * @param property the property.
	 * @param propertyValue the value of the property.
	 * @param canOverride a boolean indicating if the property can be overridden or not. 
	 * @throws RuntimeException if the property exists and it cannot be overridden.
	 */
	public void put(Object property, Object propertyValue, boolean canOverride) {
		Object currentPropertyValue = get(property);
		if(currentPropertyValue!=null && !canOverride)
			throw new RuntimeException("The node already has a value for the property \"" + property + "\":" + currentPropertyValue +
				". Attempting to override this property with: " + propertyValue + ".");
		else
			put(property, propertyValue);
	}
	
	/**
	 * 
	 * @param traversalPolicy determines the nodes in the iterable.
	 * @return An iterable of nodes, according to the given traversal policy.
	 */
	public <U extends Category<?>> FluentIterable<U> linearization(TraversalPolicy<U> traversalPolicy) {
		FluentIterable<U> it = CategoryTraverser.<U>iterable((U)this, traversalPolicy.searchStrategy, traversalPolicy.nextNodesFunction);
		if(traversalPolicy.duplicatesDetection.equals(DuplicatesDetection.ENFORCE)) {
			final Iterable<U> itAux = it;
			it = FluentIterable.from(new Iterable<U>() {
				@Override
				public Iterator<U> iterator() {
					return new DuplicatesDetectionIterator<U>(itAux.iterator());
				}
			});
		}
		return it;
	}
	
	/**
	 * 
	 * @param traversalPolicy determines the nodes which values will be part of the returning iterable.
	 * @return An iterable of node values, according to the given traversal policy.
	 */
	public <U> FluentIterable<U> linearizationValues(TraversalPolicy<?> traversalPolicy) {
		return Category.<U>linearizationValues((FluentIterable)linearization(traversalPolicy));
	}
	
	public static <U> FluentIterable<U> linearizationValues(FluentIterable<? extends Category<?>> path) {
		return path.transform(new Function<Category<?>, U>() {
			@Override
			public U apply(Category<?> node) {
				return (U)node.getValue();
			}
		});
	} 
	
	/**
	 * 
	 * @return the bottom up path according to the default policies configured in the context.
	 */
	public abstract <U extends Category<?>> FluentIterable<U> bottomUpLinearization();

	/**
	 * 
	 * @return the top down path according to the default policies configured in the context.
	 */
	public abstract <U extends Category<?>> FluentIterable<U> topDownLinearization();
	
	/**
	 * 
	 * @param key the property
	 * @return An iterable of node properties according to the default bottom-up traversal policy (only nodes in the path including the given property will be considered).
	 */
	public <U> FluentIterable<U> bottomUpLinearizationProperties(Object key) {
		return PropertyIterable.<U>properties(bottomUpLinearization(), key);
	}
	
	/**
	 * 
	 * @param key the property
	 * @return An iterable of node properties according to the default top-down traversal policy (only nodes in the path including the given property will be considered).
	 */
	public <U> FluentIterable<U> topDownLinearizationProperties(Object key) {
		return PropertyIterable.<U>properties(topDownLinearization(), key);
	}
	
	/**
	 * 
	 * @param traversalPolicy determines the nodes which properties will be part of the returning iterable.
	 * @param key the property
	 * @return An iterable of node properties according to the given traversal policy (only nodes in the path including the given property will be considered).
	 */
	public <U> FluentIterable<U> linearizationProperties(TraversalPolicy<?> traversalPolicy, Object key) {
		return PropertyIterable.<U>properties(linearization(traversalPolicy), key);
	}
	
	@Override
	public String toString() {
		return properties.toString();
	}

}
