package org.jgum.traversal;

import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;

public class StopUntilConditionIterable<U> implements Iterable<U> {

	private final Iterable<U> wrappedIterable;
	private final Predicate<U> stopCondition;
	
	public StopUntilConditionIterable(Iterable<U> wrappedIterable, Predicate<U> stopCondition) {
		this.wrappedIterable = wrappedIterable;
		this.stopCondition = stopCondition;
	}
	
	@Override
	public Iterator<U> iterator() {
		return new StopUntilConditionIterator<U>(wrappedIterable.iterator(), stopCondition);
	}
	
	public static class StopUntilConditionIterator<T> extends AbstractIterator<T> {
		
		private final Iterator<T> wrappedIterator;
		private final Predicate<T> stopCondition;
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
	
}
