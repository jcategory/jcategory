package org.jgum.category;

import java.util.Iterator;

import com.google.common.collect.FluentIterable;

public class PropertyIterable<T> implements Iterable<T> {
	
	public static <U> FluentIterable<U> properties(Iterable<? extends Category> categories, Object key) {
		return FluentIterable.<U>from(new PropertyIterable<U>(categories, key));
	}
	
	private final Iterable<? extends Category> propertyNodes;
	private final Object key;
	
	public PropertyIterable(Iterable<? extends Category> propertyNodes, Object key) {
		this.propertyNodes = propertyNodes;
		this.key = key;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new PropertyIterator<>(propertyNodes.iterator(), key);
	}

}
