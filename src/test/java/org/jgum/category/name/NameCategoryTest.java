package org.jgum.category.name;

import static org.jgum.category.CategoryProperty.properties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

import org.jgum.JGum;
import org.jgum.category.CategoryCreationListener;
import org.jgum.category.CategoryProperty;
import org.jgum.category.SearchStrategy;
import org.jgum.testutil.CounterCreationListener;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class NameCategoryTest {

	//Modeling an arbitrary package hierarchy.
	String packageP1 = "p1";
	String packageP2 = "p1.p2";
	String packageP3 = "p1.p2.p3";
	String packageP4 = "p1.p2.p4";
	String packageP5 = "p1.p2.p4.p5";
	String packageP6 = "p1.p2.p6";
	String packageP8 = "p7.p8";
	
	//The name of arbitrary properties associated with such packages.
	//(in order to keep it simple, the values of the properties are the same than the property name)
	String rootProperty = "rootProperty";
	String p1Property = "p1Property";
	String p2Property = "p2Property";
	String p3Property = "p3Property";
	String p4Property = "p4Property";
	String p5Property = "p5Property";
	String p6Property = "p6Property";
	String p8Property = "p8Property";
	
	private NameCategory newPackagePropertiesRoot() {
		JGum jgum = new JGum();
		NameCategory root = jgum.forNameRoot();
		root.putProperty(rootProperty, rootProperty);
		jgum.forName(packageP1).putProperty(rootProperty, rootProperty);
		jgum.forName(packageP1).putProperty(p1Property, p1Property);
		jgum.forName(packageP2).putProperty(p2Property, p2Property);
		jgum.forName(packageP3).putProperty(p3Property, p3Property);
		jgum.forName(packageP4).putProperty(p4Property, p4Property);
		jgum.forName(packageP5).putProperty(p5Property, p5Property);
		jgum.forName(packageP6).putProperty(p6Property, p6Property);
		jgum.forName(packageP8).putProperty(p8Property, p8Property);
		return root;
	}
	
	@Test
	public void testPackageName() {
		JGum jGum = new JGum();
		assertEquals("", jGum.forNameRoot().getId());
		assertEquals("p1", jGum.forName("p1").getId());
		assertEquals("p1.p2", jGum.forName("p1.p2").getId());
	}
	
	@Test
	public void testPathToDescendant() {
		NameCategory root = newPackagePropertiesRoot();
		
		assertEquals("", root.topDownPath("").first().get().getId());
		assertEquals(packageP3, root.getCategory(packageP3).topDownPath("").first().get().getId());
		
		FluentIterable<NameCategory> fit = root.topDownPath(packageP3);
		assertEquals(4, fit.size());
		Iterator<NameCategory> it = fit.iterator();
		assertEquals("", it.next().getId());
		assertEquals("p1", it.next().getSimpleName());
		assertEquals("p2", it.next().getSimpleName());
		assertEquals("p3", it.next().getSimpleName());
		//will not include nodes that do not exist already in the package node
		assertEquals(5, root.topDownPath("p1.p2.px.py").size()); //the returned path includes the root (default) package node, existing nodes p1 and p2, and the new nodes px and py.
	}
	
	@Test
	public void testCategoryProperty() {
		NameCategory root = newPackagePropertiesRoot();
		assertEquals(rootProperty, new CategoryProperty(root.getCategory(packageP1), rootProperty).get().get());
		assertEquals(p1Property, new CategoryProperty(root.getCategory(packageP1), p1Property).get().get());
		assertEquals(Optional.absent(), new CategoryProperty(root.getCategory(packageP1), p2Property).get());
		assertEquals(p1Property, new CategoryProperty(root.getCategory(packageP2), p1Property).get().get());
		assertEquals(p2Property, new CategoryProperty(root.getCategory(packageP2), p2Property).get().get());
	}
	
	@Test
	public void testPropertiesInPath() {
		NameCategory root = newPackagePropertiesRoot();

		assertNull(root.getProperty("wrongProperty"));
		assertNull(root.get(packageP1, "wrongProperty"));
		assertNull(root.get(packageP2, "wrongProperty"));

		Iterator<String> propertiesIt = CategoryProperty.<String>properties(root.topDownLinearization(), p6Property).iterator();
		assertTrue(propertiesIt.hasNext());
		assertEquals(p6Property, propertiesIt.next());
		assertFalse(propertiesIt.hasNext());

		assertEquals(rootProperty, properties(root.bottomUpLinearization(), rootProperty).first().get());
		assertEquals(p1Property, properties(root.getCategory(packageP1).bottomUpLinearization(), p1Property).first().get());
		assertEquals(p2Property, properties(root.getCategory(packageP2).bottomUpLinearization(), p2Property).first().get());
		assertEquals(p1Property, properties(root.getCategory(packageP2).bottomUpLinearization(), p1Property).first().get()); //the property is not defined in p2, so it should inherit from p1
		assertEquals(p8Property, properties(root.getCategory(packageP8).bottomUpLinearization(), p8Property).first().get());
		

		//now let's override one property in one subpackage
		root.getCategory(packageP2).putProperty(p1Property, p2Property);
		assertEquals(p2Property, properties(root.getCategory(packageP2).bottomUpLinearization(), p1Property).first().get());
		
		//overriding the same property
		root.getCategory(packageP2).putProperty(p1Property, p2Property);
		
		try {
			//attempting to override the same property without allowing overrides
			root.getCategory(packageP2).putProperty(p1Property, p2Property, false);
			fail("Expected exception not thrown when overridding package property");
		} catch(Exception e) {
			//expected
		}
	}

	@Test
	public void testAllDescendantsPreOrder() {
		NameCategory root = newPackagePropertiesRoot().getCategory(packageP1);
		List<NameCategory> preOrderList = Lists.newArrayList(root.linearize(new TopDownNameTraversalPolicy(SearchStrategy.PRE_ORDER)));
		assertEquals(p1Property, preOrderList.get(0).getProperty(p1Property));
		assertEquals(p2Property, preOrderList.get(1).getProperty(p2Property));
		assertEquals(p3Property, preOrderList.get(2).getProperty(p3Property));
		assertEquals(p4Property, preOrderList.get(3).getProperty(p4Property));
		assertEquals(p5Property, preOrderList.get(4).getProperty(p5Property));
		assertEquals(p6Property, preOrderList.get(5).getProperty(p6Property));
	}
	
	@Test
	public void testAllDescendantsPostOrder() {
		NameCategory root = newPackagePropertiesRoot().getCategory(packageP1);
		List<NameCategory> postOrderList = Lists.newArrayList(root.linearize(new TopDownNameTraversalPolicy(SearchStrategy.POST_ORDER)));
		assertEquals(p3Property, postOrderList.get(0).getProperty(p3Property));
		assertEquals(p5Property, postOrderList.get(1).getProperty(p5Property));
		assertEquals(p4Property, postOrderList.get(2).getProperty(p4Property));
		assertEquals(p6Property, postOrderList.get(3).getProperty(p6Property));
		assertEquals(p2Property, postOrderList.get(4).getProperty(p2Property));
		assertEquals(p1Property, postOrderList.get(5).getProperty(p1Property));
	}
	
	@Test
	public void testAllDescendantsBreadthFirst() {
		NameCategory root = newPackagePropertiesRoot().getCategory(packageP1);
		List<NameCategory> breadthFirstList = Lists.newArrayList(root.linearize(new TopDownNameTraversalPolicy(SearchStrategy.BREADTH_FIRST)));
		assertEquals(p1Property, breadthFirstList.get(0).getProperty(p1Property));
		assertEquals(p2Property, breadthFirstList.get(1).getProperty(p2Property));
		assertEquals(p3Property, breadthFirstList.get(2).getProperty(p3Property));
		assertEquals(p4Property, breadthFirstList.get(3).getProperty(p4Property));
		assertEquals(p6Property, breadthFirstList.get(4).getProperty(p6Property));
		assertEquals(p5Property, breadthFirstList.get(5).getProperty(p5Property));
	}

	@Test
	public void testListener() {
		JGum jgum = new JGum();
		CounterCreationListener listener = new CounterCreationListener();
		jgum.getNameHierarchy().addNodeCreationListener((CategoryCreationListener)listener);
		NameCategory nameCategory = jgum.forName("x.y.z");
		assertEquals(4, listener.getCounter()); //added 3 packages + the root (empty) package
		nameCategory.getOrCreateCategory(""); //will return the sender node since the relative package is the empty package
		assertEquals(4, listener.getCounter());
		jgum.forName("x.y.a.b"); //will trigger the creation of two additional packages
		assertEquals(6, listener.getCounter());
	}
}
