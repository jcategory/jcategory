package org.jgum.packagemodel;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;

import org.jgum.graph.DuplicatesDetection;
import org.jgum.graph.SearchStrategy;
import org.jgum.graph.TraversalPolicy;

import com.google.common.base.Function;

/**
 * A class facilitating the definition of bottom-up traversal policies in a tree of package nodes.
 * @author sergioc
 *
 */
public class BottomUpPackageTraversalPolicy extends TraversalPolicy<PackageNode> {

	/**
	 *
	 * @return a function mapping a PackageNode to its parent.
	 */
	public static Function<PackageNode, List<PackageNode>> parentPackageFunction() {
		return new Function<PackageNode, List<PackageNode>>() {
			@Override
			public List<PackageNode> apply(PackageNode packageNode) {
				PackageNode parentPackageNode = packageNode.getParent();
				if(parentPackageNode != null)
					return asList(parentPackageNode);
				else
					return Collections.emptyList();
			}
		};
	}
	
	/**
	 * 
	 * @param searchStrategy how the nodes in the bottom-up path should be traversed.
	 */
	public BottomUpPackageTraversalPolicy(SearchStrategy searchStrategy) {
		super(searchStrategy, DuplicatesDetection.IGNORE, parentPackageFunction());
	}
	
}
