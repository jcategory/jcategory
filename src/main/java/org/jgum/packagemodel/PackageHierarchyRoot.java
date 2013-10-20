package org.jgum.packagemodel;

import org.jgum.JGum;

import com.google.common.collect.FluentIterable;


public class PackageHierarchyRoot extends PackageNode {

	public PackageHierarchyRoot(JGum context) {
		super(context);
	}
	
	public PackageNode getDescendant(Package pakkage) {
		return getDescendant(pakkage.getName());
	}
	
	public PackageNode getOrCreateDescendant(Package pakkage) {
		return getOrCreateDescendant(pakkage.getName());
	}
	
	public Object get(Package pakkage, Object key) {
		return get(pakkage.getName(), key);
	}
	
	public FluentIterable<PackageNode> pathToDescendant(Package pakkage) {
		return pathToDescendant(pakkage.getName());
	}
	
}
