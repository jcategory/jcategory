package org.jgum.category.name;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgum.category.DuplicatesDetection;
import org.jgum.category.SearchStrategy;
import org.jgum.category.TraversalPolicy;

import com.google.common.base.Function;

/**
 * A class facilitating the definition of top-down traversal policies in a tree of package nodes.
 * @author sergioc
 *
 */
public class TopDownNameTraversalPolicy extends TraversalPolicy<NameCategory> {

	/**
	 * 
	 * @return a function mapping a NameCategory to its children.
	 */
	public static Function<NameCategory, List<NameCategory>> childrenPackagesFunction() {
		return new Function<NameCategory, List<NameCategory>>() {
			@Override
			public List<NameCategory> apply(NameCategory nameCategory) {
				return nameCategory.getChildren();
			}
		};
	}
	
	/**
	 * 
	 * @param comparator determines how children should be ordered.
	 * @return a function mapping a NameCategory to its children, ordering the children according to the given comparator.
	 */
	public static Function<NameCategory, List<NameCategory>> childrenPackagesFunction(final Comparator<NameCategory> comparator) {
		return new Function<NameCategory, List<NameCategory>>() {
			@Override
			public List<NameCategory> apply(NameCategory nameCategory) {
				List<NameCategory> subPackages = nameCategory.getChildren();
				Collections.sort(subPackages, comparator);
				return subPackages;
			}
		};
	}
	
	/**
	 * 
	 * @param searchStrategy how the nodes in the top-down path should be traversed.
	 */
	public TopDownNameTraversalPolicy(SearchStrategy searchStrategy) {
		super(searchStrategy, childrenPackagesFunction(), DuplicatesDetection.IGNORE);
	}
	
	/**
	 * 
	 * @param searchStrategy how the nodes in the top-down path should be traversed.
	 * @param comparator determines how children packages should be ordered.
	 */
	public TopDownNameTraversalPolicy(SearchStrategy searchStrategy, Comparator<NameCategory> comparator) {
		super(searchStrategy, childrenPackagesFunction(comparator), DuplicatesDetection.IGNORE);
	}

}
