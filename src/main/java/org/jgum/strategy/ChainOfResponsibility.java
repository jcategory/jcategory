package org.jgum.strategy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
