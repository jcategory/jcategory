package org.jgum.category.name;

import com.google.common.collect.TreeTraverser;

public class NameTraverser extends TreeTraverser<NameCategory> {

	@Override
	public Iterable<NameCategory> children(NameCategory nameCategory) {
		return nameCategory.getChildren();
	}

}
