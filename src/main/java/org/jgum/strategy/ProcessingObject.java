package org.jgum.strategy;

/**
 * A processing object in a chain of responsibility.
 * @author sergioc
 *
 */
public interface ProcessingObject {

	public Object apply();
	
}
