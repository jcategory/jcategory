package org.jgum.graph;

import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;

public class PropertyIterator<T> extends AbstractIterator<T> {
	
	private final Iterator<? extends Node> propertyNodes;
	private final Object key;
	
	public PropertyIterator(Iterator<? extends Node> propertyNodes, final Object key) {
		this.key = key;
		this.propertyNodes = Iterators.filter(propertyNodes, new Predicate<Node>() {
			@Override
			public boolean apply(Node node) {
				return node.containsKey(key);
			}
		});
	}
	
	@Override
	protected T computeNext() {
		if(propertyNodes.hasNext()) {
			Node nextPropertiesNode = propertyNodes.next();
			return (T) nextPropertiesNode.get(key);
		} else
			return endOfData();
	}

}
