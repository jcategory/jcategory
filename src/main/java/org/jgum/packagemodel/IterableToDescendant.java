package org.jgum.packagemodel;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

public class IterableToDescendant implements Iterable<PackagePropertiesNode> {

	private final PackagePropertiesNode packagePropertiesNode;
	private final Iterator<String> packageFragmentsIterator;
	
	public IterableToDescendant(PackagePropertiesNode packagePropertiesNode, String relativePackageName) {
		this(packagePropertiesNode, PackagePropertiesNode.asPackageFragmentsList(relativePackageName).iterator());
	}
	
	private IterableToDescendant(final PackagePropertiesNode packagePropertiesNode, final Iterator<String> packageFragmentsIterator) {
		this.packagePropertiesNode = packagePropertiesNode;
		this.packageFragmentsIterator = packageFragmentsIterator;
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
