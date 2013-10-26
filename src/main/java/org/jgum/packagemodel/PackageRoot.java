package org.jgum.packagemodel;

import java.util.ArrayList;
import java.util.List;

import org.jgum.JGum;
import org.jgum.graph.NodeCreationListener;

import com.google.common.collect.FluentIterable;

/**
 * The root node in a hierarchy of packages.
 * @author sergioc
 *
 */
public class PackageRoot extends PackageNode {

	private List<NodeCreationListener<PackageNode>> packageNodeCreationListeners;
	
	public PackageRoot(JGum context) {
		super(context);
		packageNodeCreationListeners = new ArrayList<>();
	}
	
	public PackageNode getNode(Package pakkage) {
		return getNode(pakkage.getName());
	}
	
	public PackageNode getOrCreateNode(Package pakkage) {
		return getOrCreateNode(pakkage.getName());
	}
	
	public PackageNode getOrCreateNode(String relativePackageName) {
		return super.getOrCreateNode(relativePackageName);
	}
	
	public Object get(Package pakkage, Object key) {
		return get(pakkage.getName(), key);
	}
	
	public FluentIterable<PackageNode> pathToDescendant(Package pakkage) {
		return topDownPath(pakkage.getName());
	}
	
	void notifyCreationListeners(PackageNode node) {
		for(NodeCreationListener<PackageNode> listener : packageNodeCreationListeners) {
			listener.onNodeCreation(node);
		}
	}
	
}
