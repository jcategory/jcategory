package org.jgum.category;

/**
 * A listener notified when a new category has been created in a hierarchy.
 * @author sergioc
 *
 * @param <T> the type of the node
 */
public interface CategoryCreationListener<T extends Category> {

	/**
	 * callback method invoked when a category has been created in a hierarchy.
	 * @param node the created node.
	 */
	public void onCategoryCreation(T category);
	
}
