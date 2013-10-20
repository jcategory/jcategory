package org.jgum.graph;

import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;

public class StopOnConditionIterator<T> extends AbstractIterator<T> {

	private final Predicate<T> stopCondition;
	private final Iterator<T> wrappedIterator;
	
	public StopOnConditionIterator(Iterator<T> wrappedIterator, Predicate<T> stopCondition) {
		this.wrappedIterator = wrappedIterator;
		this.stopCondition = stopCondition;
	}
	
	@Override
	protected T computeNext() {
		if(wrappedIterator.hasNext()) {
			T next = wrappedIterator.next();
			if(!stopCondition.apply(next))
				return next;
		} 
		return super.endOfData();
	}
	
}
