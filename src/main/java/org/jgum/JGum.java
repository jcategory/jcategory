package org.jgum;

import org.jgum.classmodel.ClassHierarchyRoot;
import org.jgum.packagemodel.PackagePropertiesNode;
import org.jgum.packagemodel.PackageHierarchyRoot;

public class JGum {

	private PackageHierarchyRoot packageHierarchyRoot;
	private ClassHierarchyRoot classHierarchyRoot;
	
	public JGum() {
		packageHierarchyRoot = new PackageHierarchyRoot();
		classHierarchyRoot = new ClassHierarchyRoot();
	}

	public PackagePropertiesNode getPackageHierarchyRoot() {
		return packageHierarchyRoot;
	}

	public ClassHierarchyRoot getClassHierarchyRoot() {
		return classHierarchyRoot;
	}
	
}
