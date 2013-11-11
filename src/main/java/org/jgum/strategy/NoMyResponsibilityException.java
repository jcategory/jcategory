package org.jgum.strategy;

/**
 * Exception thrown by a processing object in a chain of responsibility to denote that it cannot manages a specific command.
 * It indicates that the command should be delegated to the next object in the chain.
 * @author sergioc
 *
 */
public class NoMyResponsibilityException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}
