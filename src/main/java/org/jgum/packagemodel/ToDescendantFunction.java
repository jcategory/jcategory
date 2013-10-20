package org.jgum.packagemodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;

public class ToDescendantFunction implements Function<PackageNode, List<PackageNode>> {

	private final PackageNode firstPackageNode;
	private final Iterable<String> packageFragmentsIterable;
	private Iterator<String> packageFragmentsIterator;
	
	public ToDescendantFunction(PackageNode packageNode, String relativePackageName) {
		this(packageNode, PackageNode.asPackageFragmentsList(relativePackageName));
	}
	
	private ToDescendantFunction(PackageNode packageNode, Iterable<String> packageFragmentsIterable) {
		this.firstPackageNode = packageNode;
		this.packageFragmentsIterable = packageFragmentsIterable;
	}
	
	@Override
	public List<PackageNode> apply(PackageNode packageNode) {
		if(packageNode.equals(firstPackageNode)) {
			packageFragmentsIterator = packageFragmentsIterable.iterator();
		}
		List<PackageNode> children = new ArrayList<>();
		PackageNode child = null;
		if(packageFragmentsIterator.hasNext()) {
			child = packageNode.getChild(packageFragmentsIterator.next());
		}
		if(child != null)
			children.add(child);
		return children;
	}
	
}
