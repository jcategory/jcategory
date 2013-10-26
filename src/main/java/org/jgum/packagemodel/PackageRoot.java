package org.jgum.packagemodel;

import java.util.HashMap;
import java.util.Map;

import org.jgum.JGum;

import com.google.common.collect.FluentIterable;


public class PackageRoot extends PackageNode {

	private Map<String, PackageNode> nodeIndex;
	
	public PackageRoot(JGum context) {
		super(context);
		nodeIndex = new HashMap<>();
		nodeIndex.put("", this);
	}
	
	public PackageNode getNode(Package pakkage) {
		return getNode(pakkage.getName());
	}
	
	@Override
	public PackageNode getNode(String packageName) {
		return nodeIndex.get(packageName);
	}
	
	private void putNode(String packageName, PackageNode node) {
		nodeIndex.put(packageName, node);
	}
	
	public PackageNode getOrCreateNode(Package pakkage) {
		return getOrCreateNode(pakkage.getName());
	}
	
	@Override
	public PackageNode getOrCreateNode(String packageName) {
		PackageNode node = getNode(packageName);
		if(node == null) {
			node = super.getOrCreateNode(packageName);
			putNode(packageName, node);
		}
		return node;
	}
	
	public Object get(Package pakkage, Object key) {
		return get(pakkage.getName(), key);
	}
	
	public FluentIterable<PackageNode> pathToDescendant(Package pakkage) {
		return topDownPath(pakkage.getName());
	}
	
}
