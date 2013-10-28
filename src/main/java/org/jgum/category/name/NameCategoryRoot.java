package org.jgum.category.name;

import java.util.ArrayList;
import java.util.List;

import org.jgum.category.CategoryCreationListener;

import com.google.common.collect.FluentIterable;

/**
 * The root node in a hierarchy of packages.
 * @author sergioc
 *
 */
public class NameCategoryRoot extends NameCategory {

	private List<CategoryCreationListener<NameCategory>> packageNodeCreationListeners;
	
	public NameCategoryRoot(NameHierarchy nameHierarchy) {
		super(nameHierarchy);
		packageNodeCreationListeners = new ArrayList<>();
	}
	
	public NameCategory getNode(Package pakkage) {
		return getNode(pakkage.getName());
	}
	
	public NameCategory getOrCreateNode(Package pakkage) {
		return getOrCreateNode(pakkage.getName());
	}
	
	public NameCategory getOrCreateNode(String relativePackageName) {
		return super.getOrCreateNode(relativePackageName);
	}
	
	public Object get(Package pakkage, Object key) {
		return get(pakkage.getName(), key);
	}
	
	public FluentIterable<NameCategory> pathToDescendant(Package pakkage) {
		return topDownPath(pakkage.getName());
	}
	
	void notifyCreationListeners(NameCategory node) {
		for(CategoryCreationListener<NameCategory> listener : packageNodeCreationListeners) {
			listener.onNodeCreation(node);
		}
	}
	
}
