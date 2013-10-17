package org.jgum.packagemodel;

import com.google.common.collect.TreeTraverser;

public class PackageTraverser extends TreeTraverser<PackagePropertiesNode> {

	@Override
	public Iterable<PackagePropertiesNode> children(PackagePropertiesNode packagePropertiesNode) {
		return packagePropertiesNode.getSubpackages();
	}

}
