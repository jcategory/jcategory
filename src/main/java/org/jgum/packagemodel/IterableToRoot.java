package org.jgum.packagemodel;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

public class IterableToRoot implements Iterable<PackageNode> {
	
	private final PackageNode packageNode;
	
	public IterableToRoot(final PackageNode packageNode) {
		this.packageNode = packageNode;
	}
	
	@Override
	public Iterator<PackageNode> iterator() {
		return new AbstractIterator<PackageNode>() {
			private PackageNode next = packageNode;
			@Override
			protected PackageNode computeNext() {
				PackageNode computedNext;
				if(next != null) {
					computedNext = next;
					next = next.getParent();
				} else
					return endOfData();
				return computedNext;
			}
		};
	}
}
