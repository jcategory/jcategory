package org.jgum.packagemodel;


import static org.jgum.graph.PropertyIterable.properties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

import org.jgum.JGum;
import org.jgum.graph.SearchStrategy;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class PackageHierarchyPropertiesTest {

	String packageP1 = "p1";
	String packageP2 = "p1.p2";
	String packageP3 = "p1.p2.p3";
	String packageP4 = "p1.p2.p4";
	String packageP5 = "p1.p2.p4.p5";
	String packageP6 = "p1.p2.p6";
	String packageP8 = "p7.p8";
	String rootProperty = "rootProperty";
	String p1Property = "p1Property";
	String p2Property = "p2Property";
	String p3Property = "p3Property";
	String p4Property = "p4Property";
	String p5Property = "p5Property";
	String p6Property = "p6Property";
	String p8Property = "p8Property";
	
	private PackageHierarchyRoot newPackagePropertiesRoot() {
		PackageHierarchyRoot root = new PackageHierarchyRoot(new JGum());
		root.put(rootProperty, rootProperty);
		root.getOrCreateDescendant(packageP1).put(p1Property, p1Property);
		root.getOrCreateDescendant(packageP2).put(p2Property, p2Property);
		root.getOrCreateDescendant(packageP3).put(p3Property, p3Property);
		root.getOrCreateDescendant(packageP4).put(p4Property, p4Property);
		root.getOrCreateDescendant(packageP5).put(p5Property, p5Property);
		root.getOrCreateDescendant(packageP6).put(p6Property, p6Property);
		root.getOrCreateDescendant(packageP8).put(p8Property, p8Property);
		return root;
	}
	
	@Test
	public void testPackageName() {
		JGum jGum = new JGum();
		PackageNode root = new PackageHierarchyRoot(jGum);
		assertEquals("", root.getPackageName());
		PackageNode fragment1 = new PackageNode(jGum, "p1", root);
		assertEquals("p1", fragment1.getPackageName());
		PackageNode fragment2 = new PackageNode(jGum, "p2", fragment1);
		assertEquals("p1.p2", fragment2.getPackageName());
	}
	
	@Test
	public void testPathToDescendant() {
		PackageHierarchyRoot root = newPackagePropertiesRoot();
		FluentIterable<PackageNode> fit = root.pathToDescendant(packageP3);
		assertEquals(4, fit.size());
		Iterator<PackageNode> it = fit.iterator();
		assertEquals("", it.next().getPackageFragment());
		assertEquals("p1", it.next().getPackageFragment());
		assertEquals("p2", it.next().getPackageFragment());
		assertEquals("p3", it.next().getPackageFragment());
		assertEquals(3, root.pathToDescendant("p1.p2.px").size());
	}
	
	@Test
	public void testPropertiesInPath() {
		PackageHierarchyRoot root = newPackagePropertiesRoot();

		assertNull(root.get("wrongProperty"));
		assertNull(root.get(packageP1, "wrongProperty"));
		assertNull(root.get(packageP2, "wrongProperty"));

		assertEquals(rootProperty, properties(root.pathToDescendant(packageP1), rootProperty).first().get());
		assertEquals(p1Property, properties(root.pathToDescendant(packageP1), p1Property).first().get());
		assertEquals(Optional.absent(), properties(root.pathToDescendant(packageP1), p2Property).first());
		assertEquals(p1Property, properties(root.pathToDescendant(packageP2), p1Property).first().get());
		assertEquals(p2Property, properties(root.pathToDescendant(packageP2), p2Property).first().get());
		
		assertEquals(rootProperty, properties(root.pathToRoot(), rootProperty).first().get());
		assertEquals(p1Property, properties(root.getDescendant(packageP1).pathToRoot(), p1Property).first().get());
		assertEquals(p2Property, properties(root.getDescendant(packageP2).pathToRoot(), p2Property).first().get());
		assertEquals(p1Property, properties(root.getDescendant(packageP2).pathToRoot(), p1Property).first().get()); //the property is not defined in p2, so it should inherit from p1
		assertEquals(p8Property, properties(root.getDescendant(packageP8).pathToRoot(), p8Property).first().get());
		
		//another way to write the same as above
		assertEquals(rootProperty, root.propertyInHierarchy(rootProperty).first().get());
		assertEquals(p1Property, root.getDescendant(packageP1).propertyInHierarchy(p1Property).first().get());
		assertEquals(p2Property, root.getDescendant(packageP2).propertyInHierarchy(p2Property).first().get());
		assertEquals(p1Property, root.getDescendant(packageP2).propertyInHierarchy(p1Property).first().get());
		assertEquals(p8Property, root.getDescendant(packageP8).propertyInHierarchy(p8Property).first().get());

		//now let's override one property in one subpackage
		root.getDescendant(packageP2).put(p1Property, p2Property);
		assertEquals(p2Property, properties(root.getDescendant(packageP2).pathToRoot(), p1Property).first().get());
		
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
		List<PackageNode> preOrderList = Lists.newArrayList(root.allDescendants(new TopDownPackageTraversalPolicy(SearchStrategy.PRE_ORDER)));
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
		List<PackageNode> postOrderList = Lists.newArrayList(root.allDescendants(new TopDownPackageTraversalPolicy(SearchStrategy.POST_ORDER)));
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
		List<PackageNode> breadthFirstList = Lists.newArrayList(root.allDescendants(new TopDownPackageTraversalPolicy(SearchStrategy.BREADTH_FIRST)));
		assertEquals(p1Property, breadthFirstList.get(0).get(p1Property));
		assertEquals(p2Property, breadthFirstList.get(1).get(p2Property));
		assertEquals(p3Property, breadthFirstList.get(2).get(p3Property));
		assertEquals(p4Property, breadthFirstList.get(3).get(p4Property));
		assertEquals(p6Property, breadthFirstList.get(4).get(p6Property));
		assertEquals(p5Property, breadthFirstList.get(5).get(p5Property));
	}
	
}
