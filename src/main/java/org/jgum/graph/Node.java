package org.jgum.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgum.JGum;

import com.google.common.collect.FluentIterable;

/**
 * An abstract node in a graph with some arbitrary properties stored in a map.
 * @author sergioc
 *
 */
public abstract class Node {

	private Map<Object, Object> properties; //properties associated with this node
	private final JGum context; //the context where this node exists
	
	public Node(JGum context) {
		this.context = context;
		properties = new HashMap<>();
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
	public <U extends Node> FluentIterable<U> path(TraversalPolicy<U> traversalPolicy) {
		FluentIterable<U> it = NodeTraverser.<U>iterable((U)this, traversalPolicy.searchStrategy, traversalPolicy.nextNodesFunction);
		if(traversalPolicy.cycleDetection.equals(CycleDetection.ENFORCE)) {
			final Iterable<U> itAux = it;
			it = FluentIterable.from(new Iterable<U>() {
				@Override
				public Iterator<U> iterator() {
					return new CyclesDetectionIterator<U>(itAux.iterator());
				}
			});
		}
		return it;
	}
	
	@Override
	public String toString() {
		return properties.toString();
	}

}
