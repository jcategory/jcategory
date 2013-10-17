package org.jgum.packagemodel;

import org.jgum.Path;


public class PackageHierarchyRoot extends PackagePropertiesNode {

	public PackageHierarchyRoot() {
	}
	
	public PackagePropertiesNode getDescendant(Package pakkage) {
		return getDescendant(pakkage.getName());
	}
	
	public PackagePropertiesNode getOrCreateDescendant(Package pakkage) {
		return getOrCreateDescendant(pakkage.getName());
	}
	
	public Object get(Package pakkage, Object key) {
		return get(pakkage.getName(), key);
	}
	
	public Path<PackagePropertiesNode> pathToDescendant(Package pakkage) {
		return pathToDescendant(pakkage.getName());
	}
	
}
