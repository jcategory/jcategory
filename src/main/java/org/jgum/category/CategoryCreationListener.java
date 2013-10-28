package org.jgum.category;

/**
 * A listener notified when a new node has been created in a context.
 * @author sergioc
 *
 * @param <T> the type of the node
 */
public interface CategoryCreationListener<T extends Category<?>> {

	/**
	 * callback method invoked when a node has been created in a context.
	 * @param node the created node.
	 */
	public void onNodeCreation(T node);
	
}
