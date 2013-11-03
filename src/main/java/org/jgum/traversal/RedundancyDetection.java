package org.jgum.traversal;

/**
 * Determines if in a list of categories in a given linearization already visited categories should be filtered.
 * This flag is useful when a linearization algorithm does not warranty that any category will be visited at most once.
 * @author sergioc
 *
 */
public enum RedundancyDetection {
	/**
	 * keep the first redundant category, redundant categories coming afterwards are ignored.
	 */
	KEEP_FIRST, 
	
	/**
	 * keep the last redundant category, previous redundant categories are ignored.
	 */
	KEEP_LAST, 
	
	/**
	 * Ignore redundancy checks. 
	 * This flag may improve performance and should be used if the linearization function does not answer redundant categories (e.g., in single-inheritance categorizations).
	 */
	IGNORE; 
}
