package org.jgum.strategy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
	 * 
	 * @param responsibilityChain the processing objects.
	 * @param command the method to be invoked on each object until finding one that does not throw a {@link NoMyResponsibilityException}.
	 * @param args the arguments of the method.
	 */
	public ReflectiveChainOfResponsibility(Iterable<T> responsibilityChain, Method command, Object args[]) {
		super(responsibilityChain);
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
