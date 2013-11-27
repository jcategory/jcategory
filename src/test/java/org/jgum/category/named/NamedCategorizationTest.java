package org.jgum.category.named;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.jgum.JGum;
import org.jgum.category.CategorizationListener;
import org.jgum.category.CategoryProperty;
import org.jgum.category.Key;
import org.jgum.testutil.CounterCreationListener;
import org.jgum.traversal.RedundancyCheck;
import org.jgum.traversal.SearchStrategy;
import org.jgum.traversal.TraversalPolicy;
import org.junit.Test;

import com.google.common.base.Function;
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
	
	//The id of arbitrary properties associated with such packages.
	//In order to keep it simple, the values of the properties are the same than the property keys.
	Key rootProperty = new Key("rootProperty");
	Key p1Property = new Key("p1Property");
	Key p2Property = new Key("p2Property");
	Key p3Property = new Key("p3Property");
	Key p4Property = new Key("p4Property");
	Key p5Property = new Key("p5Property");
	Key p6Property = new Key("p6Property");
	Key p8Property = new Key("p8Property");
	
	private NamedCategory newCustomRoot() {
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
	public void levelTest() {
		JGum jgum = new JGum();
		assertEquals(0, jgum.forNameRoot().getLevel());
		assertEquals(1, jgum.forName("org").getLevel());
		assertEquals(2, jgum.forName("org.jgum").getLevel());
	}
	
	@Test
	public void superMethodTest() {
		JGum jgum = new JGum();
		assertEquals("", jgum.forName("org").<NamedCategory>getSuper().get().getLabel());
		assertEquals(Optional.absent(), jgum.forNameRoot().getSuper());
	}
	
	@Test
	public void testCategoryLabel() {
		JGum jGum = new JGum();
		assertEquals("", jGum.forNameRoot().getLabel());
		assertEquals("p1", jGum.forName("p1").getLabel());
		assertEquals("p1.p2", jGum.forName("p1.p2").getLabel());
	}
	
	@Test
	public void testRemoveProperty() {
		NamedCategory root = newCustomRoot();
		assertTrue(root.getCategory(packageP5).containsProperty(p2Property)); 
		root.getCategory(packageP2).removeLocalProperty(p2Property);
		assertFalse(root.getCategory(packageP5).containsProperty(p2Property));
	}
	
	@Test
	public void testPathToDescendant() {
		NamedCategory root = newCustomRoot();
		
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
		NamedCategory root = newCustomRoot();
		assertEquals(rootProperty, new CategoryProperty(root.getCategory(packageP1), rootProperty).get());
		assertEquals(p1Property, new CategoryProperty(root.getCategory(packageP1), p1Property).get());
		
		assertFalse(new CategoryProperty(root.getCategory(packageP1), p2Property).isPresent());
		assertEquals(p1Property, new CategoryProperty(root.getCategory(packageP2), p1Property).get());
		assertEquals(p2Property, new CategoryProperty(root.getCategory(packageP2), p2Property).get());
	}
	
	@Test
	public void testPropertiesInPath() {
		NamedCategory root = newCustomRoot();
		Key wrongKey = new Key("wrongProperty");
		assertEquals(Optional.absent(), root.getLocalProperty(wrongKey));
		assertEquals(Optional.absent(), root.getCategory(packageP1).getLocalProperty(wrongKey));
		assertEquals(Optional.absent(), root.getCategory(packageP2).getLocalProperty(wrongKey));

		List<String> properties = root.<String>topDownProperties(p6Property);
		assertEquals(1, properties.size());
		assertEquals(p6Property, properties.get(0));

		assertEquals(rootProperty, root.bottomUpProperties(rootProperty).get(0));
		assertEquals(p1Property, root.getCategory(packageP1).bottomUpProperties(p1Property).get(0));
		assertEquals(p2Property, root.getCategory(packageP2).bottomUpProperties(p2Property).get(0));
		assertEquals(p1Property, root.getCategory(packageP2).bottomUpProperties(p1Property).get(0)); //the property is not defined in p2, so it should inherit from p1
		assertEquals(p8Property, root.getCategory(packageP8).bottomUpProperties(p8Property).get(0));
		

		//now let's override one property in one subpackage
		root.getCategory(packageP2).setProperty(p1Property, p2Property);
		assertEquals(p2Property, root.getCategory(packageP2).bottomUpProperties(p1Property).get(0));
		
		//querying all the properties in the categories obtained with a given linearization function
		properties = root.getCategory(packageP2).<String>properties(p1Property, (Function)JGum.DEFAULT_BOTTOM_UP_NAME_LINEARIZATION_FUNCTION);
		assertEquals(2, properties.size());
		assertEquals(p2Property, properties.get(0));
		assertEquals(p1Property, properties.get(1));
	}

	@Test
	public void testAllDescendantsPreOrder() {
		NamedCategory root = newCustomRoot().getCategory(packageP1);
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
		NamedCategory root = newCustomRoot().getCategory(packageP1);
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
		NamedCategory root = newCustomRoot().getCategory(packageP1);
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
