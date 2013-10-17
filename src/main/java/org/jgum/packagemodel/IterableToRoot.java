package org.jgum.packagemodel;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

public class IterableToRoot implements Iterable<PackagePropertiesNode> {
	
	private final PackagePropertiesNode packagePropertiesNode;
	
	public IterableToRoot(final PackagePropertiesNode packagePropertiesNode) {
		this.packagePropertiesNode = packagePropertiesNode;
	}
	
	@Override
	public Iterator<PackagePropertiesNode> iterator() {
		return new AbstractIterator<PackagePropertiesNode>() {
			private PackagePropertiesNode next = packagePropertiesNode;
			@Override
			protected PackagePropertiesNode computeNext() {
				PackagePropertiesNode computedNext;
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
