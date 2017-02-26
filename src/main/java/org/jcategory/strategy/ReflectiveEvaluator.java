package org.jcategory.strategy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.function.Function;

/**
 * A reflective evaluator that makes use of a given method and its arguments to delegate to a processing object in a responsibility chain.
 * @author sergioc
 *
 */
public class ReflectiveEvaluator implements Function {
	
	private final Method command;
	private final Object args[];
	
	/**
	 * @param command the reflective method to invoke.
	 * @param args the arguments of the reflective method.
	 */
	public ReflectiveEvaluator(Method command, Object args[]) {
		this.command = command;
		this.args = args;
	}
	
	/**
	 * @param object the object target of a reflective method invocation.
	 * @return the result of the reflective method invocation.
	 */
	@Override
	public Object apply(Object object) {
		try {
			return command.invoke(object, args);
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