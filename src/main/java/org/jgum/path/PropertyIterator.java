package org.jgum.path;

import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;

public class PropertyIterator<T> extends AbstractIterator<T> {

	private final Iterator<? extends PropertiesNode> propertyNodes;
	private final Object key;
	
	public PropertyIterator(Iterator<? extends PropertiesNode> propertyNodes, final Object key) {
		this.key = key;
		this.propertyNodes = Iterators.filter(propertyNodes, new Predicate<PropertiesNode>() {
			@Override
			public boolean apply(PropertiesNode propertiesNode) {
				return propertiesNode.containsKey(key);
			}
		});
	}
	
	@Override
	protected T computeNext() {
		if(propertyNodes.hasNext()) {
			PropertiesNode nextPropertiesNode = propertyNodes.next();
			return (T) nextPropertiesNode.get(key);
		} else
			return endOfData();
	}

}
