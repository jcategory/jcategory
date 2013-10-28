package org.jgum.category;

import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;

public class PropertyIterator<T> extends AbstractIterator<T> {
	
	private final Iterator<? extends Category> propertyNodes;
	private final Object key;
	
	public PropertyIterator(Iterator<? extends Category> propertyNodes, final Object key) {
		this.key = key;
		this.propertyNodes = Iterators.filter(propertyNodes, new Predicate<Category>() {
			@Override
			public boolean apply(Category category) {
				return category.containsKey(key);
			}
		});
	}
	
	@Override
	protected T computeNext() {
		if(propertyNodes.hasNext()) {
			Category nextPropertiesNode = propertyNodes.next();
			return (T) nextPropertiesNode.get(key);
		} else
			return endOfData();
	}

}
