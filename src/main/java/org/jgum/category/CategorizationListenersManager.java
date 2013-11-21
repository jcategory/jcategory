package org.jgum.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A utility class for managing category creation listeners.
 * @author sergioc
 *
 */
public class CategorizationListenersManager {

	private final List<CategorizationListener<?>> categorizationListeners;
	
	public CategorizationListenersManager() {
		this((List)Collections.emptyList());
	}
	
	public CategorizationListenersManager(List<CategorizationListener<?>> categorizationListeners) {
		this.categorizationListeners = new ArrayList<>(categorizationListeners);
	}
	
	public void add(CategorizationListener<?> creationListener) {
		categorizationListeners.add(creationListener);
	}
	
	public void remove(CategorizationListener<?> creationListener) {
		categorizationListeners.remove(creationListener);
	}
	
	public void notifyCategorizationListeners(Category category) {
		for(CategorizationListener<?> listener : categorizationListeners) {
			((CategorizationListener)listener).onCategorization(category);
		}
	}

}
