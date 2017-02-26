package org.jcategory.strategy;

import static org.jcategory.category.Key.key;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import org.jcategory.ChainOfResponsibilityExhaustedException;
import org.jcategory.JCategory;
import org.jcategory.category.Key;
import org.junit.Test;

public class StrategyTest {

	interface DoSomething {
		
		public String doIt1();
		public String doIt2();
		public String doIt3();
		public String doIt4();
	}
	
	class DoSomethingChild1 implements DoSomething {
		@Override
		public String doIt1() {
			return "DoSomethingChild1.doIt1()";
		}

		@Override
		public String doIt2() {
			return "DoSomethingChild1.doIt2()";
		}
		
		@Override
		public String doIt3() {
			return "DoSomethingChild1.doIt3()";
		}
		
		@Override
		public String doIt4() {
			throw new NoMyResponsibilityException();
		}
	}
	
	class DoSomethingChild2 implements DoSomething {
		@Override
		public String doIt1() {
			return "DoSomethingChild2.doIt1()";
		}

		@Override
		public String doIt2() {
			throw new NoMyResponsibilityException();
		}
		
		@Override
		public String doIt3() {
			throw new RuntimeException();
		}
		
		@Override
		public String doIt4() {
			throw new NoMyResponsibilityException();
		}
	}
	
	private JCategory context() {
		JCategory context = new JCategory();
		Key key = key(DoSomething.class);
		context.forClass(ArrayList.class).setProperty(key, new DoSomethingChild2());
		context.forClass(Collection.class).setProperty(key, new DoSomethingChild1());
		return context;
	}
	
	@Test
	public void testNoDelegation() {
		JCategory context = context();
		DoSomething doSomething = context.forClass(ArrayList.class).getStrategy(DoSomething.class);
		assertEquals("DoSomethingChild2.doIt1()", doSomething.doIt1());
	}
	
	@Test
	public void testDelegation() {
		JCategory context = context();
		DoSomething doSomething = context.forClass(ArrayList.class).getStrategy(DoSomething.class);
		assertEquals("DoSomethingChild1.doIt2()", doSomething.doIt2());
	}
	
	@Test
	public void testDelegationWithException() {
		JCategory context = context();
		DoSomething doSomething = context.forClass(ArrayList.class).getStrategy(DoSomething.class, NoMyResponsibilityException.class);
		assertEquals("DoSomethingChild1.doIt2()", doSomething.doIt2());
	}
	
	@Test
	public void testRuntimeException() {
		JCategory context = context();
		DoSomething doSomething = context.forClass(ArrayList.class).getStrategy(DoSomething.class);
		try {
			doSomething.doIt3();
			fail();
		} catch(RuntimeException e) {
			assertEquals(RuntimeException.class, e.getClass());
		}
	}
	
	@Test
	public void testUnsupportedOperationException() {
		JCategory context = context();
		DoSomething doSomething = context.forClass(ArrayList.class).getStrategy(DoSomething.class);
		try {
			doSomething.doIt4();
			fail();
		} catch(ChainOfResponsibilityExhaustedException e) {}
	}

}
