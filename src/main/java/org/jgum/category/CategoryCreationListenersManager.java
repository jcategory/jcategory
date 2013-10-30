package org.jgum.category;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for managing category creation listeners.
 * @author sergioc
 *
 */
public class CategoryCreationListenersManager {

	private final List<CategoryCreationListener<?>> categoryCreationListeners;
	
	public CategoryCreationListenersManager() {
		categoryCreationListeners = new ArrayList<>();
	}
	
	public void addNodeCreationListener(CategoryCreationListener<?> creationListener) {
		categoryCreationListeners.add(creationListener);
	}
	
	public void notifyCreationListeners(Category<?> node) {
		for(CategoryCreationListener<?> listener : categoryCreationListeners) {
			((CategoryCreationListener)listener).onCategoryCreation(node);
		}
	}
}
