package org.jgum.packagemodel;

import org.jgum.JGum;
import org.jgum.graph.NodeCreationListener;
import org.jgum.graph.NodeCreationListenersManager;

/**
 * A package tree.
 * @author sergioc
 *
 */
public class PackageTree {

	private PackageRoot packageRoot;
	private final JGum context;
	private final NodeCreationListenersManager listenersManager;
	
	public PackageTree(JGum context) {
		this.context = context;
		listenersManager = new NodeCreationListenersManager();
	}
	
	public PackageRoot getRoot() {
		if(packageRoot == null) {
			packageRoot = new PackageRoot(context);
			notifyCreationListeners(packageRoot);
		}
		return packageRoot;
	}
	
	public JGum getContext() {
		return context;
	}
	
	public void addNodeCreationListener(NodeCreationListener<PackageNode> creationListener) {
		listenersManager.addNodeCreationListener(creationListener);
	}
	
	void notifyCreationListeners(PackageNode node) {
		listenersManager.notifyCreationListeners(node);
	}

}
