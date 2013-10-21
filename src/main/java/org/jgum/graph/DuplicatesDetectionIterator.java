package org.jgum.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.AbstractIterator;

/**
 * An iterator that remembers the visited nodes. It will stop if an already visited node is returned.
 * @author sergioc
 *
 * @param <T> the type of the iterator
 */
public class DuplicatesDetectionIterator<T> extends AbstractIterator<T> {

	private final Set<T> visitedNodes;
	private final Iterator<T> wrappedIterator;
	
	public DuplicatesDetectionIterator(Iterator<T> wrappedIterator) {
		this.wrappedIterator = wrappedIterator;
		visitedNodes = new HashSet<>();
	}

	@Override
	protected T computeNext() {
		if(wrappedIterator.hasNext()) {
			T next = wrappedIterator.next();
			if(visitedNodes.contains(next)) {
				next = computeNext();
			} else {
				visitedNodes.add(next);
			}
			return next;
		} 
		return endOfData();
	}

}
