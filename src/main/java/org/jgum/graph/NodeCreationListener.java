package org.jgum.graph;

/**
 * A listener notified when a new node has been created in a context.
 * @author sergioc
 *
 * @param <T> the type of the node
 */
public interface NodeCreationListener<T extends Node<?>> {

	/**
	 * callback method invoked when a node has been created in a context.
	 * @param node the created node.
	 */
	public void onNodeCreation(T node);
	
}
