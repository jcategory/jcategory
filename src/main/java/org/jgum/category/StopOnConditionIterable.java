package org.jgum.category;

import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;

public class StopOnConditionIterable<U> implements Iterable<U> {

	private final Iterable<U> wrappedIterable;
	private final Predicate<U> stopCondition;
	
	public StopOnConditionIterable(Iterable<U> wrappedIterable, Predicate<U> stopCondition) {
		this.wrappedIterable = wrappedIterable;
		this.stopCondition = stopCondition;
	}
	
	@Override
	public Iterator<U> iterator() {
		return new StopOnConditionIterator<U>(wrappedIterable.iterator(), stopCondition);
	}
	
	public static class StopOnConditionIterator<T> extends AbstractIterator<T> {

		private final Iterator<T> wrappedIterator;
		private final Predicate<T> stopCondition;
		
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
	
}
