package org.jgum.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgum.JGum;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * A node in a graph with some arbitrary named properties.
 * @author sergioc
 *
 */
public abstract class Node<T> {

	private T value;
	private Map<Object, Object> properties; //properties associated with this node are backed up in this map
	private final JGum context; //the context where this node exists
	
	public Node(JGum context, T value) {
		this.context = context;
		this.value = value;
		properties = new HashMap<>();
	}

	/**
	 * 
	 * @return the value of this node
	 */
	public T getValue() {
		return value;
	}
	
	/**
	 * 
	 * @return the context
	 */
	public JGum getContext() {
		return context;
	}
	
	/**
	 * @param key the property name
	 * @return the value of the property in the current node (if any)
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
	 * @param traversalPolicy determines the nodes in the iterable
	 * @return An iterable of nodes
	 */
	public <U extends Node<?>> FluentIterable<U> path(TraversalPolicy<U> traversalPolicy) {
		FluentIterable<U> it = NodeTraverser.<U>iterable((U)this, traversalPolicy.searchStrategy, traversalPolicy.nextNodesFunction);
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
	
	public <U> FluentIterable<U> pathValues(TraversalPolicy<?> traversalPolicy) {
		return Node.<U>pathValues((FluentIterable)path(traversalPolicy));
	}
	
	public static <U> FluentIterable<U> pathValues(FluentIterable<? extends Node<?>> path) {
		return path.transform(new Function<Node<?>, U>() {
			@Override
			public U apply(Node<?> node) {
				return (U)node.getValue();
			}
		});
	} 
	
	public abstract <U extends Node<?>> FluentIterable<U> bottomUpPath();

	public abstract <U extends Node<?>> FluentIterable<U> topDownPath();
	
	public <U> FluentIterable<U> bottomUpPathProperties(Object key) {
		return PropertyIterable.<U>properties(bottomUpPath(), key);
	}
	
	public <U> FluentIterable<U> topDownPathProperties(Object key) {
		return PropertyIterable.<U>properties(topDownPath(), key);
	}
	
	public <U> FluentIterable<U> pathProperties(TraversalPolicy<?> traversalPolicy, Object key) {
		return PropertyIterable.<U>properties(path(traversalPolicy), key);
	}
	
	@Override
	public String toString() {
		return properties.toString();
	}

}
