package org.jgum.category.named;

import java.util.Collections;
import java.util.List;

import org.jgum.category.Categorization;
import org.jgum.category.CategorizationListener;
import org.jgum.category.CategorizationListenersManager;

import com.google.common.base.Function;

/**
 * A package tree.
 * @author sergioc
 *
 */
public class NamedCategorization extends Categorization<NamedCategory> {

	private NamedCategory nameCategoryRoot;
	private final CategorizationListenersManager listenersManager; //category listeners notified when a new category is created.
	
	public NamedCategorization(Function<NamedCategory, List<NamedCategory>> bottomUpLinearizationFunction, 
			Function<NamedCategory, List<NamedCategory>> topDownLinearizationFunction) {
		this(bottomUpLinearizationFunction, topDownLinearizationFunction, (List)Collections.emptyList());
	}
	
	public NamedCategorization(Function<NamedCategory, List<NamedCategory>> bottomUpLinearizationFunction, 
			Function<NamedCategory, List<NamedCategory>> topDownLinearizationFunction,
			List<? extends CategorizationListener<NamedCategory>> categorizationListeners) {
		super((Function)bottomUpLinearizationFunction, (Function)topDownLinearizationFunction);
		listenersManager = new CategorizationListenersManager((List)categorizationListeners);
	}
	
	public NamedCategory getRoot() {
		if(nameCategoryRoot == null) {
			nameCategoryRoot = new NamedCategory(this);
			notifyCategorizationListeners(nameCategoryRoot);
		}
		return nameCategoryRoot;
	}

	
	protected void notifyCategorizationListeners(NamedCategory newCategory) {
		listenersManager.notifyCategorizationListeners(newCategory);
	}

	public void addCategorizationListener(CategorizationListener<NamedCategory> creationListener) {
		listenersManager.addCategorizationListener(creationListener);
	}
	
}
