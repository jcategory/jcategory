package org.jgum.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgum.JGum;

import com.google.common.collect.FluentIterable;

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
	
}
