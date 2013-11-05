package org.jgum.category.type;

/**
 * Determines if classes should be visited before interfaces or vice versa.
 * @author sergioc
 *
 */
public enum Priority {
	/**
	 * Classes should be visited before interfaces.
	 */
	CLASSES_FIRST, 
	/**
	 * Interfaces should be visited before classes.
	 */
	INTERFACES_FIRST;
}
