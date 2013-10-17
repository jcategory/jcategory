package org.jgum.packagemodel;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

public class IterableToDescendant implements Iterable<PackageNode> {

	private final PackageNode packageNode;
	private final Iterator<String> packageFragmentsIterator;
	
	public IterableToDescendant(PackageNode packageNode, String relativePackageName) {
		this(packageNode, PackageNode.asPackageFragmentsList(relativePackageName).iterator());
	}
	
	private IterableToDescendant(final PackageNode packageNode, final Iterator<String> packageFragmentsIterator) {
		this.packageNode = packageNode;
		this.packageFragmentsIterator = packageFragmentsIterator;
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
					if(packageFragmentsIterator.hasNext())
						next = next.getOrCreateChild(packageFragmentsIterator.next());
					else
						next = null;
				} else
					return endOfData();
				return computedNext;
			}
		};
	}
	
}
