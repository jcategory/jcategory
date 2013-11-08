package org.jgum.strategy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.jgum.category.Category;
import org.jgum.category.CategoryProperty.PropertyIterable;

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
		ChainOfResponsibility<?> chainOfResponsibility = new ChainOfResponsibility<>(implementors);
		return chainOfResponsibility.eval(method, args);
	}

}
