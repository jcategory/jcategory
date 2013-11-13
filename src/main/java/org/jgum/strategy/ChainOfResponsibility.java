package org.jgum.strategy;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * Class implementing the chain of responsibility pattern.
 * The class encapsulates a list of objects (the processing objects) in charge of managing a desired command.
 * </p>
 * <p>
 * A processing object delegates to the next object in the chain by means of throwing an instance of the exception class passed by in the constructor. 
 * If no exception is given at the constructor, a NoMyResponsibilityException exception is assumed as the indicator for delegation.
 * </p>
 * 
 * @author sergioc
 *
 * @param <T> the type of objects in the chain of responsibility.
 */
public abstract class ChainOfResponsibility<T> {

	/**
	 * The default exception class used for signaling delegation.
	 */
	public static final Class<? extends RuntimeException> DEFAULT_DELEGATION_EXCEPTION = NoMyResponsibilityException.class;
	
	private final List<T> responsibilityChain;
	private final Class<? extends RuntimeException> exceptionClass;
	private final RuntimeException chainExhaustedException;
	
	/**
	 * Creates an empty chain of responsibility.
	 */
	public ChainOfResponsibility() {
		this(new ArrayList<T>());
	}
	
	/**
	 * Creates an empty chain of responsibility.
	 * @param exceptionClass instances of this exception class denote that a processing object delegates to the next object in the responsibility chain.
	 */
	public ChainOfResponsibility(Class<? extends RuntimeException> exceptionClass) {
		this(new ArrayList<T>(), exceptionClass);
	}
	
	/**
	 * Creates a chain of responsibility initialized with the given list of processing objects.
	 * @param responsibilityChain the processing objects.
	 */
	public ChainOfResponsibility(List<T> responsibilityChain) {
		this(responsibilityChain, DEFAULT_DELEGATION_EXCEPTION);
	}

	/**
	 * Creates a chain of responsibility initialized with the given list of processing objects.
	 * @param responsibilityChain the processing objects.
	 * @param exceptionClass instances of this exception class denote that a processing object delegates to the next object in the responsibility chain.
	 */
	public ChainOfResponsibility(List<T> responsibilityChain, Class<? extends RuntimeException> exceptionClass) {
		this.responsibilityChain = responsibilityChain;
		this.exceptionClass = exceptionClass;
		try {
			chainExhaustedException = exceptionClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Adds a new processing object at the end of the chain of responsibility.
	 * @param processingObject a processing object.
	 */
	public void add(T processingObject) {
		responsibilityChain.add(processingObject);
	}
	
	/**
	 * This method executes a command on each member of the responsibility chain until it finds one that can manage it.
	 * How this command execution is accomplished is implementation dependent.
	 * If a processing object throws a {@link NoMyResponsibilityException} when attempting to execute the command, the operation will be delegated to the next object in the responsibility chain.
	 * Any other exception is propagated to the caller.
	 * <p>
	 * If no object is able to process the command after exhausting the responsibility chain, a {@link NoMyResponsibilityException} is thrown.
	 * </p>
	 * @return the result of executing the command on the first object in the responsibility chain that does not throw a {@link NoMyResponsibilityException} exception.
	 */
	public Object apply() {
		for(T object : responsibilityChain) {
			try {
				return delegate(object);
			} catch (RuntimeException e) {
				if(!exceptionClass.isInstance(e))
					throw e;
			}
		}
		throw chainExhaustedException;
	}
	
	/**
	 * Subclasses should override this method to specify how a object in the responsibility chain executes the desired command.
	 * @param processingObject a processing object in the responsibility chain.
	 * @return the result of executing the command.
	 */
	protected abstract Object delegate(T processingObject);

}
