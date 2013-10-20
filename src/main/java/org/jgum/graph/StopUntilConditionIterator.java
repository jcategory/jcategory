package org.jgum.graph;

import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;

public class StopUntilConditionIterator<T> extends AbstractIterator<T> {
	
	private final Predicate<T> stopCondition;
	private final Iterator<T> wrappedIterator;
	private boolean shouldStop;
	
	public StopUntilConditionIterator(Iterator<T> wrappedIterator, Predicate<T> stopCondition) {
		this.wrappedIterator = wrappedIterator;
		this.stopCondition = stopCondition;
	}
	
	@Override
	protected T computeNext() {
		if(!shouldStop && wrappedIterator.hasNext()) {
			T next = wrappedIterator.next();
			if(stopCondition.apply(next))
				shouldStop = true; //will stop in the next call to next
			return next;
		} 
		return super.endOfData();
	}
	
}
