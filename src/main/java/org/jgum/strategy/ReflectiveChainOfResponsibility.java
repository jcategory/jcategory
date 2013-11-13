package org.jgum.strategy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * An implementation of the chain of responsibility pattern, where each command is defined by a method and its arguments passed by in the constructor.
 * @see ChainOfResponsibility
 * @author sergioc
 * 
 * @param <T> the type of objects in the chain of responsibility.
 */
public class ReflectiveChainOfResponsibility<T> extends ChainOfResponsibility<T> {

	private final Method method;
	private final Object args[];
	
	/**
	 * Creates an empty chain of responsibility.
	 * @param command the method to be invoked on each command object.
	 * @param args the arguments of the method.
	 */
	public ReflectiveChainOfResponsibility(Method command, Object args[]) {
		this.method = command;
		this.args = args;
	}
	
	/**
	 * Creates an empty chain of responsibility.
	 * @param exceptionClass instances of this exception class denote that a processing object delegates to the next object in the responsibility chain.
	 * @param command the method to be invoked on each command object.
	 * @param args the arguments of the method.
	 */
	public ReflectiveChainOfResponsibility(Class<? extends RuntimeException> exceptionClass, Method command, Object args[]) {
		super(exceptionClass);
		this.method = command;
		this.args = args;
	}
	
	/**
	 * @param responsibilityChain the processing objects.
	 * @param command the method to be invoked on each command object.
	 * @param args the arguments of the method.
	 */
	public ReflectiveChainOfResponsibility(List<T> responsibilityChain, Method command, Object args[]) {
		super(responsibilityChain);
		this.method = command;
		this.args = args;
	}
	
	/**
	 * @param responsibilityChain the processing objects.
	 * @param exceptionClass instances of this exception class denote that a processing object delegates to the next object in the responsibility chain.
	 * @param command the method to be invoked on each command object.
	 * @param args the arguments of the method.
	 */
	public ReflectiveChainOfResponsibility(List<T> responsibilityChain, Class<? extends RuntimeException> exceptionClass, Method command, Object args[]) {
		super(responsibilityChain, exceptionClass);
		this.method = command;
		this.args = args;
	}
	
	@Override
	protected Object delegate(T processingObject) {
		try {
			return method.invoke(processingObject, args);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch(InvocationTargetException e) {
			if((e.getTargetException() instanceof RuntimeException))
				throw ((RuntimeException)e.getTargetException());
			else
				throw new RuntimeException(e.getTargetException());
		}
	}
	
}
