package org.jgum.graph;

import java.util.HashMap;
import java.util.Map;

import org.jgum.JGum;

public abstract class Node {

	private Map<Object, Object> properties; //properties associated with this node
	
	private final JGum context;
	
	public Node(JGum context) {
		this.context = context;
		properties = new HashMap<>();
	}

	public JGum getContext() {
		return context;
	}
	
	/**
	 * @param key the property name
	 * @return the local value of the property (if any)
	 */
	public Object get(Object key) {
		return properties.get(key);
	}
	
	public boolean containsKey(Object key) {
		return properties.containsKey(key);
	}
	
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
	
	@Override
	public String toString() {
		return properties.toString();
	}
	
	public <U extends Node> Path<U> path(TraversalPolicy<U> traversalPolicy) {
		return new Path<>(new NodeIterable(this, traversalPolicy.searchStrategy, traversalPolicy.nextNodesFunction), traversalPolicy.cycleDetection);
	}
	
}
