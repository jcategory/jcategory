package org.jgum.category.name;

import org.jgum.category.Categorization;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * A package tree.
 * @author sergioc
 *
 */
public class NameCategorization extends Categorization<NameCategory> {

	private NameCategory nameCategoryRoot;
	
	public NameCategorization(Function<? extends NameCategory, FluentIterable<? extends NameCategory>> bottomUpLinearization, 
			Function<? extends NameCategory, FluentIterable<? extends NameCategory>> topDownLinearization) {
		super((Function)bottomUpLinearization, (Function)topDownLinearization);
	}
	
	@Override
	public NameCategory getRoot() {
		if(nameCategoryRoot == null) {
			nameCategoryRoot = new NameCategory(this);
			notifyCreationListeners(nameCategoryRoot);
		}
		return nameCategoryRoot;
	}

	@Override
	protected void notifyCreationListeners(NameCategory newCategory) {
		super.notifyCreationListeners(newCategory);
	}
	
}
