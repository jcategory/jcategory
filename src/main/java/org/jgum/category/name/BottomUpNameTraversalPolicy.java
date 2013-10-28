package org.jgum.category.name;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;

import org.jgum.category.DuplicatesDetection;
import org.jgum.category.SearchStrategy;
import org.jgum.category.TraversalPolicy;

import com.google.common.base.Function;

/**
 * A class facilitating the definition of bottom-up traversal policies in a tree of package nodes.
 * @author sergioc
 *
 */
public class BottomUpNameTraversalPolicy extends TraversalPolicy<NameCategory> {

	/**
	 *
	 * @return a function mapping a NameCategory to its parent.
	 */
	public static Function<NameCategory, List<NameCategory>> parentPackageFunction() {
		return new Function<NameCategory, List<NameCategory>>() {
			@Override
			public List<NameCategory> apply(NameCategory nameCategory) {
				NameCategory parentPackageNode = nameCategory.getParent();
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
	public BottomUpNameTraversalPolicy(SearchStrategy searchStrategy) {
		super(searchStrategy, parentPackageFunction(), DuplicatesDetection.IGNORE);
	}
	
}
