package org.jgum.category.name;

import org.jgum.JGum;
import org.jgum.category.CategoryCreationListener;
import org.jgum.category.CategoryCreationListenersManager;

/**
 * A package tree.
 * @author sergioc
 *
 */
public class NameHierarchy {

	private NameCategoryRoot nameCategoryRoot;
	private final JGum context;
	private final CategoryCreationListenersManager listenersManager;
	
	private final BottomUpNameTraversalPolicy bottomUpNameTraversalPolicy; //bottom up package traversing strategy for this context.
	private final TopDownNameTraversalPolicy topDownNameTraversalPolicy; //top down package traversing strategy for this context.
	
	public NameHierarchy(JGum context) {
		this.context = context;
		bottomUpNameTraversalPolicy = context.getBottomUpNameTraversalPolicy();
		topDownNameTraversalPolicy = context.getTopDownNameTraversalPolicy();
		listenersManager = new CategoryCreationListenersManager();
	}
	
	public NameCategoryRoot getRoot() {
		if(nameCategoryRoot == null) {
			nameCategoryRoot = new NameCategoryRoot(this);
			notifyCreationListeners(nameCategoryRoot);
		}
		return nameCategoryRoot;
	}
	
	public JGum getContext() {
		return context;
	}
	
	public void addNodeCreationListener(CategoryCreationListener<NameCategory> creationListener) {
		listenersManager.addNodeCreationListener(creationListener);
	}
	
	void notifyCreationListeners(NameCategory node) {
		listenersManager.notifyCreationListeners(node);
	}
	
	/**
	 * 
	 * @return the bottom up package traversing strategy for this context.
	 */
	public BottomUpNameTraversalPolicy getBottomUpNameTraversalPolicy() {
		return bottomUpNameTraversalPolicy;
	}

	/**
	 * 
	 * @return the top down package traversing strategy for this context.
	 */
	public TopDownNameTraversalPolicy getTopDownNameTraversalPolicy() {
		return topDownNameTraversalPolicy;
	}

}
