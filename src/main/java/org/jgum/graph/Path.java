package org.jgum.graph;

import java.util.Iterator;

import com.google.common.base.Predicate;

public final class Path<T extends Node> implements Iterable<T> {

	private final Iterable<T> wrappedIterable;
	private final CycleDetection cycleDetection;
	private final Predicate<T> stopCondition;
	
	public Path(Iterable<T> wrappedIterable) {
		this(wrappedIterable, null, CycleDetection.IGNORE);
	}
	
	public Path(Iterable<T> wrappedIterable, Predicate<T> stopCondition) {
		this(wrappedIterable, stopCondition, CycleDetection.IGNORE);
	}
	
	public Path(Iterable<T> wrappedIterable, CycleDetection cycleDetection) {
		this(wrappedIterable, null, cycleDetection);
	}
	
	public Path(Iterable<T> wrappedIterable, Predicate<T> stopCondition, CycleDetection cycleDetection) {
		this.wrappedIterable = wrappedIterable;
		this.stopCondition = stopCondition;
		this.cycleDetection = cycleDetection;
	}
	
	public <V> V first(Object key) {
		Iterator<V> it = (Iterator<V>) values(key).iterator();
		if(it.hasNext()) {
			V next = it.next();
			return next;
		}
		else
			return null;
	}
	
	public <V> Iterable<V> values(final Object key) {
		return new Iterable<V>() {
			@Override
			public Iterator<V> iterator() {
				return new PropertyIterator<V>(Path.this.iterator(), key);
			}
		};
	}

	@Override
	public Iterator<T> iterator() {
		Iterator<T> it;
		if(cycleDetection.equals(CycleDetection.IGNORE))
			it = wrappedIterable.iterator();
		else
			it = new CyclesDetectionIterator<T>(wrappedIterable.iterator());
		if(stopCondition != null)
			it = new StopOnConditionIterator(it, stopCondition);
		return it;
	}

}
