package org.jgum.packagemodel;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;

import org.jgum.graph.DuplicatesDetection;
import org.jgum.graph.SearchStrategy;
import org.jgum.graph.TraversalPolicy;

import com.google.common.base.Function;

public class BottomUpPackageTraversalPolicy extends TraversalPolicy<PackageNode> {

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
	
	public BottomUpPackageTraversalPolicy(SearchStrategy searchStrategy) {
		super(searchStrategy, DuplicatesDetection.IGNORE, parentPackageFunction());
	}
	
}
