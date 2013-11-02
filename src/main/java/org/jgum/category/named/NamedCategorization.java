package org.jgum.category.named;

import org.jgum.category.Categorization;

import com.google.common.base.Function;

/**
 * A package tree.
 * @author sergioc
 *
 */
public class NamedCategorization extends Categorization<NamedCategory> {

	private NamedCategory nameCategoryRoot;
	
	public NamedCategorization(Function<? extends NamedCategory, Iterable<? extends NamedCategory>> bottomUpLinearization, 
			Function<? extends NamedCategory, Iterable<? extends NamedCategory>> topDownLinearization) {
		super((Function)bottomUpLinearization, (Function)topDownLinearization);
	}
	
	@Override
	public NamedCategory getRoot() {
		if(nameCategoryRoot == null) {
			nameCategoryRoot = new NamedCategory(this);
			notifyCreationListeners(nameCategoryRoot);
		}
		return nameCategoryRoot;
	}

	@Override
	protected void notifyCreationListeners(NamedCategory newCategory) {
		super.notifyCreationListeners(newCategory);
	}
	
}
