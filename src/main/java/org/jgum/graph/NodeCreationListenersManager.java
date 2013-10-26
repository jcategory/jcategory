package org.jgum.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for controlling node creation listeners.
 * @author sergioc
 *
 */
public class NodeCreationListenersManager {

	private final List<NodeCreationListener<?>> nodeCreationListeners;
	
	public NodeCreationListenersManager() {
		nodeCreationListeners = new ArrayList<>();
	}
	
	public void addNodeCreationListener(NodeCreationListener<?> creationListener) {
		nodeCreationListeners.add(creationListener);
	}
	
	public void notifyCreationListeners(Node<?> node) {
		for(NodeCreationListener<?> listener : nodeCreationListeners) {
			((NodeCreationListener)listener).onNodeCreation(node);
		}
	}
}
