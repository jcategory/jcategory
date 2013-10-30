package org.jgum.category;

/**
 * Determines if a list of categories in a given linearization should filter nodes that have already been visited.
 * This flag is useful when a linearization algorithm does not warranty that any category will be visited at most once.
 * @author sergioc
 *
 */
public enum DuplicatesDetection {
	ENFORCE, IGNORE; 
}
