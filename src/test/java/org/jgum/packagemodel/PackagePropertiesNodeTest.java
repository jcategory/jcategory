package org.jgum.packagemodel;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.jgum.path.SearchStrategy;
import org.junit.Test;

import com.google.common.collect.Lists;

public class PackagePropertiesNodeTest {

	String packageP1 = "p1";
	String packageP2 = "p1.p2";
	String packageP3 = "p1.p2.p3";
	String packageP4 = "p1.p2.p4";
	String packageP5 = "p1.p2.p4.p5";
	String packageP6 = "p1.p2.p6";
	String packageP8 = "p7.p8";
	String rootProperty = "rootProperty";
	String rootPropertyValue = "rootPropertyValue";
	String p1Property = "p1Property";
	String p1PropertyValue = "p1PropertyValue";
	String p2Property = "p2Property";
	String p2PropertyValue = "p2PropertyValue";
	String p3Property = "p3Property";
	String p3PropertyValue = "p3PropertyValue";
	String p4Property = "p4Property";
	String p4PropertyValue = "p4PropertyValue";
	String p5Property = "p5Property";
	String p5PropertyValue = "p5PropertyValue";
	String p6Property = "p6Property";
	String p6PropertyValue = "p6PropertyValue";
	String p8Property = "p8Property";
	String p8PropertyValue = "p8PropertyValue";
	
	private PackageHierarchyRoot newPackagePropertiesRoot() {
		PackageHierarchyRoot root = new PackageHierarchyRoot();
		root.put(rootProperty, rootPropertyValue);
		root.getOrCreateDescendant(packageP1).put(p1Property, p1PropertyValue);
		root.getOrCreateDescendant(packageP2).put(p2Property, p2PropertyValue);
		root.getOrCreateDescendant(packageP3).put(p3Property, p3PropertyValue);
		root.getOrCreateDescendant(packageP4).put(p4Property, p4PropertyValue);
		root.getOrCreateDescendant(packageP5).put(p5Property, p5PropertyValue);
		root.getOrCreateDescendant(packageP6).put(p6Property, p6PropertyValue);
		root.getOrCreateDescendant(packageP8).put(p8Property, p8PropertyValue);
		return root;
	}
	
	@Test
	public void testPackageName() {
		PackagePropertiesNode root = new PackageHierarchyRoot();
		assertEquals("", root.getPackageName());
		PackagePropertiesNode fragment1 = new PackagePropertiesNode("p1", root);
		assertEquals("p1", fragment1.getPackageName());
		PackagePropertiesNode fragment2 = new PackagePropertiesNode("p2", fragment1);
		assertEquals("p1.p2", fragment2.getPackageName());
	}
	
	@Test
	public void testPropertiesInPath() {
		PackageHierarchyRoot root = newPackagePropertiesRoot();

		assertNull(root.get("wrongProperty"));
		assertNull(root.get(packageP1, "wrongProperty"));
		assertNull(root.get(packageP2, "wrongProperty"));

		assertEquals(rootPropertyValue, root.pathToDescendant(packageP1).first(rootProperty));
		assertEquals(p1PropertyValue, root.pathToDescendant(packageP1).first(p1Property));
		assertNull(root.pathToDescendant(packageP1).first(p2Property));
		assertEquals(p1PropertyValue, root.pathToDescendant(packageP2).first(p1Property));
		assertEquals(p2PropertyValue, root.pathToDescendant(packageP2).first(p2Property));
		
		assertEquals(rootPropertyValue, root.pathToRoot().first(rootProperty));
		assertEquals(p1PropertyValue, root.getDescendant(packageP1).pathToRoot().first(p1Property));
		assertEquals(p2PropertyValue, root.getDescendant(packageP2).pathToRoot().first(p2Property));
		assertEquals(p1PropertyValue, root.getDescendant(packageP2).pathToRoot().first(p1Property)); //the property is not defined in p2, so it should inherit from p1
		assertEquals(p8PropertyValue, root.getDescendant(packageP8).pathToRoot().first(p8Property));
		
		//now let's override one property in one subpackage
		root.getDescendant(packageP2).put(p1Property, p2PropertyValue);
		assertEquals(p2PropertyValue, root.getDescendant(packageP2).pathToRoot().first(p1Property));
		
		//overriding the same property
		root.getDescendant(packageP2).put(p1Property, p2PropertyValue);
		
		try {
			//attempting to override the same property without allowing overrides
			root.getDescendant(packageP2).put(p1Property, p2PropertyValue, false);
			fail("Expected exception not thrown when overridding package property");
		} catch(Exception e) {
			//expected
		}
	}

	@Test
	public void testAllDescendantsPreOrder() {
		PackagePropertiesNode root = newPackagePropertiesRoot().getChild(packageP1);
		List<PackagePropertiesNode> preOrderList = Lists.newArrayList(root.allDescendants(SearchStrategy.PRE_ORDER));
		assertEquals(p1PropertyValue, preOrderList.get(0).get(p1Property));
		assertEquals(p2PropertyValue, preOrderList.get(1).get(p2Property));
		assertEquals(p3PropertyValue, preOrderList.get(2).get(p3Property));
		assertEquals(p4PropertyValue, preOrderList.get(3).get(p4Property));
		assertEquals(p5PropertyValue, preOrderList.get(4).get(p5Property));
		assertEquals(p6PropertyValue, preOrderList.get(5).get(p6Property));
	}
	
	@Test
	public void testAllDescendantsPostOrder() {
		PackagePropertiesNode root = newPackagePropertiesRoot().getChild(packageP1);
		List<PackagePropertiesNode> postOrderList = Lists.newArrayList(root.allDescendants(SearchStrategy.POST_ORDER));
		assertEquals(p3PropertyValue, postOrderList.get(0).get(p3Property));
		assertEquals(p5PropertyValue, postOrderList.get(1).get(p5Property));
		assertEquals(p4PropertyValue, postOrderList.get(2).get(p4Property));
		assertEquals(p6PropertyValue, postOrderList.get(3).get(p6Property));
		assertEquals(p2PropertyValue, postOrderList.get(4).get(p2Property));
		assertEquals(p1PropertyValue, postOrderList.get(5).get(p1Property));
	}
	
	@Test
	public void testAllDescendantsBreadthFirst() {
		PackagePropertiesNode root = newPackagePropertiesRoot().getChild(packageP1);
		List<PackagePropertiesNode> breadthFirstList = Lists.newArrayList(root.allDescendants(SearchStrategy.BREADTH_FIRST));
		assertEquals(p1PropertyValue, breadthFirstList.get(0).get(p1Property));
		assertEquals(p2PropertyValue, breadthFirstList.get(1).get(p2Property));
		assertEquals(p3PropertyValue, breadthFirstList.get(2).get(p3Property));
		assertEquals(p4PropertyValue, breadthFirstList.get(3).get(p4Property));
		assertEquals(p6PropertyValue, breadthFirstList.get(4).get(p6Property));
		assertEquals(p5PropertyValue, breadthFirstList.get(5).get(p5Property));
	}
	
}
