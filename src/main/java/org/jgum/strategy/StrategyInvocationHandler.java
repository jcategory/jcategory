package org.jgum.strategy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import org.jgum.category.Category;
import org.jgum.category.CategoryProperty.PropertyIterable;

import com.google.common.collect.Lists;

/**
 * An invocation handler that delegates a proxied method to an object implementing the chain of responsibility pattern.
 * The objects in such chain are all the objects found in a category hierarchy (in the order specified by the default linearization function) identified by a given property (passed by in the constructor).
 * @see ReflectiveChainOfResponsibility
 * @author sergioc
 */
public class StrategyInvocationHandler implements InvocationHandler {

	private final Category category;
	private final Object property;
	private final Class<? extends RuntimeException> exceptionClass;
	
	/**
	 * @param category the category where the look-up of strategies start.
	 * @param property the property name of strategies in the bottom-up hierarchy.
	 * @param exceptionClass instances of this exception class denote that a processing object delegates to the next object in the responsibility chain.
	 */
	public StrategyInvocationHandler(Category category, Object property, Class<? extends RuntimeException> exceptionClass) {
		this.category = category;
		this.property = property;
		this.exceptionClass = exceptionClass;
	} 
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		List<?> implementors = Lists.newArrayList(new PropertyIterable<>(category, property));
		ChainOfResponsibility<?> chainOfResponsibility = new ReflectiveChainOfResponsibility<>(implementors, exceptionClass, method, args);
		return chainOfResponsibility.apply();
	}

}
