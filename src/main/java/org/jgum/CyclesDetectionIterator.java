package org.jgum;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.AbstractIterator;

public class CyclesDetectionIterator<T> extends AbstractIterator<T> {

	private final Set<T> visitedNodes;
	private final Iterator<T> wrappedIterator;
	
	public CyclesDetectionIterator(Iterator<T> wrappedIterator) {
		this.wrappedIterator = wrappedIterator;
		visitedNodes = new HashSet<>();
	}

	@Override
	protected T computeNext() {
		if(wrappedIterator.hasNext()) {
			T next = wrappedIterator.next();
			if(visitedNodes.add(next)) //true if next was not on the set
				return next;
		} 
		return super.endOfData();
	}

}
