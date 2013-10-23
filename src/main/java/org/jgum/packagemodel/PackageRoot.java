package org.jgum.packagemodel;

import org.jgum.JGum;

import com.google.common.collect.FluentIterable;


public class PackageRoot extends PackageNode {

	public PackageRoot(JGum context) {
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
		return topDownPath(pakkage.getName());
	}
	
}
