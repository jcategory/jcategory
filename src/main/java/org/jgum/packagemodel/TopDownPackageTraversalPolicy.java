package org.jgum.packagemodel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgum.graph.DuplicatesDetection;
import org.jgum.graph.SearchStrategy;
import org.jgum.graph.TraversalPolicy;

import com.google.common.base.Function;

/**
 * A class facilitating the definition of top-down traversal policies in a tree of package nodes.
 * @author sergioc
 *
 */
public class TopDownPackageTraversalPolicy extends TraversalPolicy<PackageNode> {

	/**
	 * 
	 * @return a function mapping a PackageNode to its children.
	 */
	public static Function<PackageNode, List<PackageNode>> childrenPackagesFunction() {
		return new Function<PackageNode, List<PackageNode>>() {
			@Override
			public List<PackageNode> apply(PackageNode packageNode) {
				return packageNode.getChildren();
			}
		};
	}
	
	/**
	 * 
	 * @param comparator determines how children should be ordered.
	 * @return a function mapping a PackageNode to its children, ordering the children according to the given comparator.
	 */
	public static Function<PackageNode, List<PackageNode>> childrenPackagesFunction(final Comparator<PackageNode> comparator) {
		return new Function<PackageNode, List<PackageNode>>() {
			@Override
			public List<PackageNode> apply(PackageNode packageNode) {
				List<PackageNode> subPackages = packageNode.getChildren();
				Collections.sort(subPackages, comparator);
				return subPackages;
			}
		};
	}
	
	/**
	 * 
	 * @param searchStrategy how the nodes in the top-down path should be traversed.
	 */
	public TopDownPackageTraversalPolicy(SearchStrategy searchStrategy) {
		super(searchStrategy, DuplicatesDetection.IGNORE, childrenPackagesFunction());
	}
	
	/**
	 * 
	 * @param searchStrategy how the nodes in the top-down path should be traversed.
	 * @param comparator determines how children packages should be ordered.
	 */
	public TopDownPackageTraversalPolicy(SearchStrategy searchStrategy, Comparator<PackageNode> comparator) {
		super(searchStrategy, DuplicatesDetection.IGNORE, childrenPackagesFunction(comparator));
	}

}
