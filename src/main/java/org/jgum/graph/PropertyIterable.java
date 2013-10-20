package org.jgum.graph;

import java.util.Iterator;

import com.google.common.collect.FluentIterable;

public class PropertyIterable<T> implements Iterable<T> {
	
	public static <U> FluentIterable<U> properties(Iterable<? extends Node> nodes, Object key) {
		return FluentIterable.from(new PropertyIterable(nodes, key));
	}
	
	private final Iterable<? extends Node> propertyNodes;
	private final Object key;
	
	public PropertyIterable(Iterable<? extends Node> propertyNodes, Object key) {
		this.propertyNodes = propertyNodes;
		this.key = key;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new PropertyIterator<>(propertyNodes.iterator(), key);
	}

}
