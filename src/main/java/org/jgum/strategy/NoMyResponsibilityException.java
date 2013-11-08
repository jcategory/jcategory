package org.jgum.strategy;

/**
 * Exception thrown by a strategy to denote that it cannot manage a specific operation.
 * It indicates that the operation should be delegated to the next strategy in the hierarchy.
 * @author sergioc
 *
 */
public class NoMyResponsibilityException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}
