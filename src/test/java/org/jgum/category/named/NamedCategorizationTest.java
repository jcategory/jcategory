package org.jgum.category.named;

import static org.jgum.category.CategoryProperty.properties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

import org.jgum.JGum;
import org.jgum.category.CategorizationListener;
import org.jgum.category.CategoryProperty;
import org.jgum.testutil.CounterCreationListener;
import org.jgum.traversal.RedundancyCheck;
import org.jgum.traversal.SearchStrategy;
import org.jgum.traversal.TraversalPolicy;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class NamedCategorizationTest {

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
	
	private NamedCategory newPackagePropertiesRoot() {
		JGum jgum = new JGum();
		NamedCategory root = jgum.forNameRoot();
		root.setProperty(rootProperty, rootProperty);
		jgum.forName(packageP1).setProperty(rootProperty, rootProperty);
		jgum.forName(packageP1).setProperty(p1Property, p1Property);
		jgum.forName(packageP2).setProperty(p2Property, p2Property);
		jgum.forName(packageP3).setProperty(p3Property, p3Property);
		jgum.forName(packageP4).setProperty(p4Property, p4Property);
		jgum.forName(packageP5).setProperty(p5Property, p5Property);
		jgum.forName(packageP6).setProperty(p6Property, p6Property);
		jgum.forName(packageP8).setProperty(p8Property, p8Property);
		return root;
	}
	
	@Test
	public void superMethodTest() {
		JGum jgum = new JGum();
		assertEquals("", jgum.forName("org").<NamedCategory>getSuper().get().getLabel());
		assertEquals(Optional.absent(), jgum.forNameRoot().getSuper());
	}
	
	@Test
	public void testPackageName() {
		JGum jGum = new JGum();
		assertEquals("", jGum.forNameRoot().getLabel());
		assertEquals("p1", jGum.forName("p1").getLabel());
		assertEquals("p1.p2", jGum.forName("p1.p2").getLabel());
	}
	
	@Test
	public void testPathToDescendant() {
		NamedCategory root = newPackagePropertiesRoot();
		
		assertEquals("", root.topDownPath("").get(0).getLabel());
		assertEquals(packageP3, root.getCategory(packageP3).topDownPath("").get(0).getLabel());
		
		List<NamedCategory> topDownPathp3 = root.topDownPath(packageP3);
		assertEquals(4, topDownPathp3.size());
		Iterator<NamedCategory> it = topDownPathp3.iterator();
		assertEquals("", it.next().getLabel());
		assertEquals("p1", it.next().getSimpleName());
		assertEquals("p2", it.next().getSimpleName());
		assertEquals("p3", it.next().getSimpleName());
		//will not include nodes that do not exist already in the package node
		assertEquals(5, root.topDownPath("p1.p2.px.py").size()); //the returned path includes the root (default) package node, existing nodes p1 and p2, and the new nodes px and py.
	}
	
	@Test
	public void testCategoryProperty() {
		NamedCategory root = newPackagePropertiesRoot();
		assertEquals(rootProperty, new CategoryProperty(root.getCategory(packageP1), rootProperty).get());
		assertEquals(p1Property, new CategoryProperty(root.getCategory(packageP1), p1Property).get());
		
		assertFalse(new CategoryProperty(root.getCategory(packageP1), p2Property).isPresent());
		assertEquals(p1Property, new CategoryProperty(root.getCategory(packageP2), p1Property).get());
		assertEquals(p2Property, new CategoryProperty(root.getCategory(packageP2), p2Property).get());
	}
	
	@Test
	public void testPropertiesInPath() {
		NamedCategory root = newPackagePropertiesRoot();
		assertEquals(Optional.absent(), root.getLocalProperty("wrongProperty"));
		assertEquals(Optional.absent(), root.getCategory(packageP1).getLocalProperty("wrongProperty"));
		assertEquals(Optional.absent(), root.getCategory(packageP2).getLocalProperty("wrongProperty"));

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
		root.getCategory(packageP2).setProperty(p1Property, p2Property);
		assertEquals(p2Property, properties(root.getCategory(packageP2).bottomUpLinearization(), p1Property).first().get());
		
		//overriding the same property
		root.getCategory(packageP2).setProperty(p1Property, p2Property);
		
		try {
			//attempting to override the same property without allowing overrides
			root.getCategory(packageP2).setProperty(p1Property, p2Property, false);
			fail("Expected exception not thrown when overridding package property");
		} catch(Exception e) {
			//expected
		}
	}

	@Test
	public void testAllDescendantsPreOrder() {
		NamedCategory root = newPackagePropertiesRoot().getCategory(packageP1);
		List<NamedCategory> preOrderList = Lists.newArrayList(root.linearize(TraversalPolicy.topDownTraversalPolicy(SearchStrategy.PRE_ORDER, RedundancyCheck.IGNORE)));
		assertEquals(p1Property, preOrderList.get(0).getLocalProperty(p1Property).get());
		assertEquals(p2Property, preOrderList.get(1).getLocalProperty(p2Property).get());
		assertEquals(p3Property, preOrderList.get(2).getLocalProperty(p3Property).get());
		assertEquals(p4Property, preOrderList.get(3).getLocalProperty(p4Property).get());
		assertEquals(p5Property, preOrderList.get(4).getLocalProperty(p5Property).get());
		assertEquals(p6Property, preOrderList.get(5).getLocalProperty(p6Property).get());
	}
	
	@Test
	public void testAllDescendantsPostOrder() {
		NamedCategory root = newPackagePropertiesRoot().getCategory(packageP1);
		List<NamedCategory> postOrderList = Lists.newArrayList(root.linearize(TraversalPolicy.topDownTraversalPolicy(SearchStrategy.POST_ORDER, RedundancyCheck.IGNORE)));
		assertEquals(p3Property, postOrderList.get(0).getLocalProperty(p3Property).get());
		assertEquals(p5Property, postOrderList.get(1).getLocalProperty(p5Property).get());
		assertEquals(p4Property, postOrderList.get(2).getLocalProperty(p4Property).get());
		assertEquals(p6Property, postOrderList.get(3).getLocalProperty(p6Property).get());
		assertEquals(p2Property, postOrderList.get(4).getLocalProperty(p2Property).get());
		assertEquals(p1Property, postOrderList.get(5).getLocalProperty(p1Property).get());
	}
	
	@Test
	public void testAllDescendantsBreadthFirst() {
		NamedCategory root = newPackagePropertiesRoot().getCategory(packageP1);
		List<NamedCategory> breadthFirstList = Lists.newArrayList(root.linearize(TraversalPolicy.topDownTraversalPolicy(SearchStrategy.BREADTH_FIRST, RedundancyCheck.IGNORE)));
		assertEquals(p1Property, breadthFirstList.get(0).getLocalProperty(p1Property).get());
		assertEquals(p2Property, breadthFirstList.get(1).getLocalProperty(p2Property).get());
		assertEquals(p3Property, breadthFirstList.get(2).getLocalProperty(p3Property).get());
		assertEquals(p4Property, breadthFirstList.get(3).getLocalProperty(p4Property).get());
		assertEquals(p6Property, breadthFirstList.get(4).getLocalProperty(p6Property).get());
		assertEquals(p5Property, breadthFirstList.get(5).getLocalProperty(p5Property).get());
	}

	@Test
	public void testListener() {
		JGum jgum = new JGum();
		CounterCreationListener listener = new CounterCreationListener();
		jgum.getNamedCategorization().addCategorizationListener((CategorizationListener)listener);
		NamedCategory namedCategory = jgum.forName("x.y.z");
		assertEquals(4, listener.getCounter()); //added 3 packages + the root (empty) package
		namedCategory.getOrCreateCategory(""); //will return the sender node since the relative package is the empty package
		assertEquals(4, listener.getCounter());
		jgum.forName("x.y.a.b"); //will trigger the creation of two additional packages
		assertEquals(6, listener.getCounter());
	}
}
