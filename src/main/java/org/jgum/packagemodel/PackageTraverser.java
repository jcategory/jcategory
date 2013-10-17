package org.jgum.packagemodel;

import com.google.common.collect.TreeTraverser;

public class PackageTraverser extends TreeTraverser<PackageNode> {

	@Override
	public Iterable<PackageNode> children(PackageNode packageNode) {
		return packageNode.getSubpackages();
	}

}
