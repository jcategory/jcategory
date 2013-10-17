package org.jgum.path;

import java.util.Iterator;

public final class Path<T extends PropertiesNode> implements Iterable<T> {

	private final Iterable<T> wrappedIterable;
	private final CycleDetection cycleDetection;
	
	public Path(Iterable<T> wrappedIterable) {
		this(wrappedIterable, CycleDetection.IGNORE);
	}
	
	public Path(Iterable<T> wrappedIterable, CycleDetection cycleDetection) {
		this.wrappedIterable = wrappedIterable;
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
		if(cycleDetection.equals(CycleDetection.IGNORE))
			return wrappedIterable.iterator();
		else
			return new CyclesDetectionIterator<T>(wrappedIterable.iterator());
	}

}
