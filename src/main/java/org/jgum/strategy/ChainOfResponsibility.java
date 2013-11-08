package org.jgum.strategy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class implementing the chain of responsibility pattern.
 * The class is initialized by an iterable of objects implementing a particular method.
 * The eval method invokes a given method on each member of the list until finds one that does not throw a NoMyResponsibilityException.
 * In that case, it return the result of the method invocation.
 * Any other exception (different to NoMyResponsibilityException) is propagated to the caller.
 * @author sergioc
 *
 * @param <T>
 */
public class ChainOfResponsibility<T> {

	private Iterable<T> responsibilityChain;
	
	public ChainOfResponsibility(Iterable<T> responsibilityChain) {
		this.responsibilityChain = responsibilityChain;
	}
	
	public Object eval(Method method, Object... args) throws Throwable {
		for(T object : responsibilityChain) {
			try {
				return method.invoke(object, args);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch(InvocationTargetException e) {
				if( !(e.getTargetException() instanceof NoMyResponsibilityException))
					throw e.getTargetException();
			}
		}
		throw new NoMyResponsibilityException();
	}

}
