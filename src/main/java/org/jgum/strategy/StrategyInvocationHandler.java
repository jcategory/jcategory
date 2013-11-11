package org.jgum.strategy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.jgum.category.Category;
import org.jgum.category.CategoryProperty.PropertyIterable;

/**
 * An invocation handler that delegates a proxied method to an object implementing the chain of responsibility pattern.
 * The objects in such chain are all the objects found in a category hierarchy (in the order specified by the default linearization function) identified by a given property (passed by in the constructor).
 * @see ReflectiveChainOfResponsibility
 * @author sergioc
 */
public class StrategyInvocationHandler implements InvocationHandler {

	private Category category;
	private final Object property;
	
	public StrategyInvocationHandler(Category category, Object property) {
		this.category = category;
		this.property = property;
	} 
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		PropertyIterable<?> implementors = new PropertyIterable<>(category, property);
		ChainOfResponsibility<?> chainOfResponsibility = new ReflectiveChainOfResponsibility<>(implementors, method, args);
		return chainOfResponsibility.apply();
	}

}
