package org.jcategory.category.name;

import static java.util.Collections.emptyList;
import static org.jcategory.category.Key.key;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jcategory.JCategory;
import org.jcategory.category.CategorizationListener;
import org.jcategory.category.CategoryProperty;
import org.jcategory.category.Key;
import org.jcategory.testutil.CounterCreationListener;
import org.jcategory.traversal.RedundancyCheck;
import org.jcategory.traversal.SearchStrategy;
import org.jcategory.traversal.TraversalPolicy;
import org.junit.Test;

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
	Key rootProperty = key();
	Key p1Property = key();
	Key p2Property = key();
	Key p3Property = key();
	Key p4Property = key();
	Key p5Property = key();
	Key p6Property = key();
	Key p8Property = key();
	
	private NameCategory newCustomRoot() {
		JCategory context = new JCategory();
		NameCategory root = context.forNameRoot();
		root.setProperty(rootProperty, rootProperty);
		context.forName(packageP1).setProperty(rootProperty, rootProperty);
		context.forName(packageP1).setProperty(p1Property, p1Property);
		context.forName(packageP2).setProperty(p2Property, p2Property);
		context.forName(packageP3).setProperty(p3Property, p3Property);
		context.forName(packageP4).setProperty(p4Property, p4Property);
		context.forName(packageP5).setProperty(p5Property, p5Property);
		context.forName(packageP6).setProperty(p6Property, p6Property);
		context.forName(packageP8).setProperty(p8Property, p8Property);
		return root;
	}
	
	@Test
	public void levelTest() {
		JCategory context = new JCategory();
		assertEquals(0, context.forNameRoot().getLevel());
		assertEquals(1, context.forName("org").getLevel());
		assertEquals(2, context.forName("org.jcategory").getLevel());
	}
	
	@Test
	public void superMethodTest() {
		JCategory context = new JCategory();
		assertEquals("", context.forName("org").<NameCategory>getSuper().get().getLabel());
		assertEquals(Optional.empty(), context.forNameRoot().getSuper());
	}
	
	@Test
	public void testCategoryLabel() {
		JCategory context = new JCategory();
		assertEquals("", context.forNameRoot().getLabel());
		assertEquals("p1", context.forName("p1").getLabel());
		assertEquals("p1.p2", context.forName("p1.p2").getLabel());
	}
	
	@Test
	public void testRemoveProperty() {
		NameCategory root = newCustomRoot();
		assertTrue(root.getCategory(packageP5).containsProperty(p2Property)); 
		root.getCategory(packageP2).removeLocalProperty(p2Property);
		assertFalse(root.getCategory(packageP5).containsProperty(p2Property));
	}
	
	@Test
	public void testPathToDescendant() {
		NameCategory root = newCustomRoot();
		
		assertEquals("", root.topDownPath("").get(0).getLabel());
		assertEquals(packageP3, root.getCategory(packageP3).topDownPath("").get(0).getLabel());
		
		List<NameCategory> topDownPathp3 = root.topDownPath(packageP3);
		assertEquals(4, topDownPathp3.size());
		Iterator<NameCategory> it = topDownPathp3.iterator();
		assertEquals("", it.next().getLabel());
		assertEquals("p1", it.next().getSimpleName());
		assertEquals("p2", it.next().getSimpleName());
		assertEquals("p3", it.next().getSimpleName());
		//will not include nodes that do not exist already in the package node
		assertEquals(5, root.topDownPath("p1.p2.px.py").size()); //the returned path includes the root (default) package node, existing nodes p1 and p2, and the new nodes px and py.
	}
	
	@Test
	public void testCategoryProperty() {
		NameCategory root = newCustomRoot();
		assertEquals(rootProperty, new CategoryProperty(root.getCategory(packageP1), rootProperty).get());
		assertEquals(p1Property, new CategoryProperty(root.getCategory(packageP1), p1Property).get());
		
		assertFalse(new CategoryProperty(root.getCategory(packageP1), p2Property).isPresent());
		assertEquals(p1Property, new CategoryProperty(root.getCategory(packageP2), p1Property).get());
		assertEquals(p2Property, new CategoryProperty(root.getCategory(packageP2), p2Property).get());
	}
	
	@Test
	public void testPropertiesInPath() {
		NameCategory root = newCustomRoot();
		Key wrongKey = key();
		assertEquals(emptyList(), root.getLocalProperty(wrongKey));
		assertEquals(emptyList(), root.getCategory(packageP1).getLocalProperty(wrongKey));
		assertEquals(emptyList(), root.getCategory(packageP2).getLocalProperty(wrongKey));

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
		properties = root.getCategory(packageP2).<String>properties(p1Property, (Function) JCategory.DEFAULT_BOTTOM_UP_NAME_LINEARIZATION_FUNCTION);
		assertEquals(2, properties.size());
		assertEquals(p2Property, properties.get(0));
		assertEquals(p1Property, properties.get(1));
	}

	@Test
	public void testAllDescendantsPreOrder() {
		NameCategory root = newCustomRoot().getCategory(packageP1);
		List<NameCategory> preOrderList = Lists.newArrayList(root.linearize(TraversalPolicy.topDownTraversalPolicy(SearchStrategy.PRE_ORDER, RedundancyCheck.IGNORE)));
		assertEquals(p1Property, preOrderList.get(0).getLocalProperty(p1Property).get(0));
		assertEquals(p2Property, preOrderList.get(1).getLocalProperty(p2Property).get(0));
		assertEquals(p3Property, preOrderList.get(2).getLocalProperty(p3Property).get(0));
		assertEquals(p4Property, preOrderList.get(3).getLocalProperty(p4Property).get(0));
		assertEquals(p5Property, preOrderList.get(4).getLocalProperty(p5Property).get(0));
		assertEquals(p6Property, preOrderList.get(5).getLocalProperty(p6Property).get(0));
	}
	
	@Test
	public void testAllDescendantsPostOrder() {
		NameCategory root = newCustomRoot().getCategory(packageP1);
		List<NameCategory> postOrderList = Lists.newArrayList(root.linearize(TraversalPolicy.topDownTraversalPolicy(SearchStrategy.POST_ORDER, RedundancyCheck.IGNORE)));
		assertEquals(p3Property, postOrderList.get(0).getLocalProperty(p3Property).get(0));
		assertEquals(p5Property, postOrderList.get(1).getLocalProperty(p5Property).get(0));
		assertEquals(p4Property, postOrderList.get(2).getLocalProperty(p4Property).get(0));
		assertEquals(p6Property, postOrderList.get(3).getLocalProperty(p6Property).get(0));
		assertEquals(p2Property, postOrderList.get(4).getLocalProperty(p2Property).get(0));
		assertEquals(p1Property, postOrderList.get(5).getLocalProperty(p1Property).get(0));
	}
	
	@Test
	public void testAllDescendantsBreadthFirst() {
		NameCategory root = newCustomRoot().getCategory(packageP1);
		List<NameCategory> breadthFirstList = Lists.newArrayList(root.linearize(TraversalPolicy.topDownTraversalPolicy(SearchStrategy.BREADTH_FIRST, RedundancyCheck.IGNORE)));
		assertEquals(p1Property, breadthFirstList.get(0).getLocalProperty(p1Property).get(0));
		assertEquals(p2Property, breadthFirstList.get(1).getLocalProperty(p2Property).get(0));
		assertEquals(p3Property, breadthFirstList.get(2).getLocalProperty(p3Property).get(0));
		assertEquals(p4Property, breadthFirstList.get(3).getLocalProperty(p4Property).get(0));
		assertEquals(p6Property, breadthFirstList.get(4).getLocalProperty(p6Property).get(0));
		assertEquals(p5Property, breadthFirstList.get(5).getLocalProperty(p5Property).get(0));
	}

	@Test
	public void testListener() {
		JCategory context = new JCategory();
		CounterCreationListener listener = new CounterCreationListener();
		context.getNamedCategorization().addCategorizationListener((CategorizationListener)listener);
		NameCategory namedCategory = context.forName("x.y.z");
		assertEquals(4, listener.getCounter()); //added 3 packages + the root (empty) package
		namedCategory.getOrCreateCategory(""); //will return the sender node since the relative package is the empty package
		assertEquals(4, listener.getCounter());
		context.forName("x.y.a.b"); //will trigger the creation of two additional packages
		assertEquals(6, listener.getCounter());
	}
}
