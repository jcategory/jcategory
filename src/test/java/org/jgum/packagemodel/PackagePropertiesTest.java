package org.jgum.packagemodel;


import static org.jgum.graph.PropertyIterable.properties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

import org.jgum.JGum;
import org.jgum.graph.SearchStrategy;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class PackagePropertiesTest {

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
	
	private PackageRoot newPackagePropertiesRoot() {
		JGum jgum = new JGum();
		PackageRoot root = jgum.forPackageRoot();
		root.put(rootProperty, rootProperty);
		jgum.forPackage(packageP1).put(rootProperty, rootProperty);
		jgum.forPackage(packageP1).put(p1Property, p1Property);
		jgum.forPackage(packageP2).put(p2Property, p2Property);
		jgum.forPackage(packageP3).put(p3Property, p3Property);
		jgum.forPackage(packageP4).put(p4Property, p4Property);
		jgum.forPackage(packageP5).put(p5Property, p5Property);
		jgum.forPackage(packageP6).put(p6Property, p6Property);
		jgum.forPackage(packageP8).put(p8Property, p8Property);
		return root;
	}
	
	@Test
	public void testPackageName() {
		JGum jGum = new JGum();
		assertEquals("", jGum.forPackageRoot().getPackageName());
		assertEquals("p1", jGum.forPackage("p1").getPackageName());
		assertEquals("p1.p2", jGum.forPackage("p1.p2").getPackageName());
	}
	
	@Test
	public void testPathToDescendant() {
		PackageRoot root = newPackagePropertiesRoot();
		FluentIterable<PackageNode> fit = root.topDownPath(packageP3);
		assertEquals(4, fit.size());
		Iterator<PackageNode> it = fit.iterator();
		assertEquals("", it.next().getValue());
		assertEquals("p1", it.next().getValue());
		assertEquals("p2", it.next().getValue());
		assertEquals("p3", it.next().getValue());
		//will not include nodes that do not exist already in the package node
		assertEquals(5, root.topDownPath("p1.p2.px.py").size()); //the returned path includes the root (default) package node, existing nodes p1 and p2, and the new nodes px and py.
	}
	
	@Test
	public void testPropertiesInPath() {
		PackageRoot root = newPackagePropertiesRoot();

		assertNull(root.get("wrongProperty"));
		assertNull(root.get(packageP1, "wrongProperty"));
		assertNull(root.get(packageP2, "wrongProperty"));

		assertEquals(rootProperty, root.topDownPathProperties(packageP1, rootProperty).first().get());
		assertEquals(p1Property, root.topDownPathProperties(packageP1, p1Property).first().get());
		assertEquals(Optional.absent(), root.topDownPathProperties(packageP1, p2Property).first());
		assertEquals(p1Property, root.topDownPathProperties(packageP2, p1Property).first().get());
		assertEquals(p2Property, root.topDownPathProperties(packageP2, p2Property).first().get());
		
		Iterator<String> propertiesIt = root.<String>topDownPathProperties(p6Property).iterator();
		assertTrue(propertiesIt.hasNext());
		assertEquals(p6Property, propertiesIt.next());
		assertFalse(propertiesIt.hasNext());
		
		assertEquals(rootProperty, root.bottomUpPathProperties(rootProperty).first().get());
		assertEquals(p1Property, root.getDescendant(packageP1).bottomUpPathProperties(p1Property).first().get());
		assertEquals(p2Property, root.getDescendant(packageP2).bottomUpPathProperties(p2Property).first().get());
		assertEquals(p1Property, root.getDescendant(packageP2).bottomUpPathProperties(p1Property).first().get());
		assertEquals(p8Property, root.getDescendant(packageP8).bottomUpPathProperties(p8Property).first().get());
		
		//another way to write the same as above
		assertEquals(rootProperty, root.bottomUpPathProperties(rootProperty).first().get());
		assertEquals(p1Property, properties(root.getDescendant(packageP1).bottomUpPath(), p1Property).first().get());
		assertEquals(p2Property, properties(root.getDescendant(packageP2).bottomUpPath(), p2Property).first().get());
		assertEquals(p1Property, properties(root.getDescendant(packageP2).bottomUpPath(), p1Property).first().get()); //the property is not defined in p2, so it should inherit from p1
		assertEquals(p8Property, properties(root.getDescendant(packageP8).bottomUpPath(), p8Property).first().get());
		


		//now let's override one property in one subpackage
		root.getDescendant(packageP2).put(p1Property, p2Property);
		assertEquals(p2Property, properties(root.getDescendant(packageP2).bottomUpPath(), p1Property).first().get());
		
		//overriding the same property
		root.getDescendant(packageP2).put(p1Property, p2Property);
		
		try {
			//attempting to override the same property without allowing overrides
			root.getDescendant(packageP2).put(p1Property, p2Property, false);
			fail("Expected exception not thrown when overridding package property");
		} catch(Exception e) {
			//expected
		}
	}

	@Test
	public void testAllDescendantsPreOrder() {
		PackageNode root = newPackagePropertiesRoot().getChild(packageP1);
		List<PackageNode> preOrderList = Lists.newArrayList(root.path(new TopDownPackageTraversalPolicy(SearchStrategy.PRE_ORDER)));
		assertEquals(p1Property, preOrderList.get(0).get(p1Property));
		assertEquals(p2Property, preOrderList.get(1).get(p2Property));
		assertEquals(p3Property, preOrderList.get(2).get(p3Property));
		assertEquals(p4Property, preOrderList.get(3).get(p4Property));
		assertEquals(p5Property, preOrderList.get(4).get(p5Property));
		assertEquals(p6Property, preOrderList.get(5).get(p6Property));
	}
	
	@Test
	public void testAllDescendantsPostOrder() {
		PackageNode root = newPackagePropertiesRoot().getChild(packageP1);
		List<PackageNode> postOrderList = Lists.newArrayList(root.path(new TopDownPackageTraversalPolicy(SearchStrategy.POST_ORDER)));
		assertEquals(p3Property, postOrderList.get(0).get(p3Property));
		assertEquals(p5Property, postOrderList.get(1).get(p5Property));
		assertEquals(p4Property, postOrderList.get(2).get(p4Property));
		assertEquals(p6Property, postOrderList.get(3).get(p6Property));
		assertEquals(p2Property, postOrderList.get(4).get(p2Property));
		assertEquals(p1Property, postOrderList.get(5).get(p1Property));
	}
	
	@Test
	public void testAllDescendantsBreadthFirst() {
		PackageNode root = newPackagePropertiesRoot().getChild(packageP1);
		List<PackageNode> breadthFirstList = Lists.newArrayList(root.path(new TopDownPackageTraversalPolicy(SearchStrategy.BREADTH_FIRST)));
		assertEquals(p1Property, breadthFirstList.get(0).get(p1Property));
		assertEquals(p2Property, breadthFirstList.get(1).get(p2Property));
		assertEquals(p3Property, breadthFirstList.get(2).get(p3Property));
		assertEquals(p4Property, breadthFirstList.get(3).get(p4Property));
		assertEquals(p6Property, breadthFirstList.get(4).get(p6Property));
		assertEquals(p5Property, breadthFirstList.get(5).get(p5Property));
	}
	
}
