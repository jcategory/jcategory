package org.jcategory.category.type;

/**
 * Determines the order in which interfaces are going to be visited in a bottom-up type linearization.
 * @author sergioc
 *
 */
public enum InterfaceOrder {
	/**
	 * Follow interface declaration order.
	 */
	DECLARATION, 
	/**
	 * Reverse interface declaration order.
	 */
	REVERSE;
}
