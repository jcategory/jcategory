package org.jgum.classmodel;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import org.jgum.JGum;
import org.jgum.graph.DuplicatesDetection;
import org.jgum.graph.Node;
import org.jgum.graph.SearchStrategy;
import org.junit.Test;

import com.google.common.collect.FluentIterable;

public class ClassPropertiesTest {

	
	@Test
	public void addingNodes() {
		JGum jgum = new JGum();
		AnyClassRoot hierarchyRoot = jgum.forAnyClassRoot();
		assertNotNull(hierarchyRoot.getNode(Object.class));
		assertNull(hierarchyRoot.getNode(ArrayList.class));
		ClassNode<ArrayList> arrayListNode = (ClassNode<ArrayList>)hierarchyRoot.getOrCreateNode(ArrayList.class);
		assertNotNull(arrayListNode);
		assertEquals(0, hierarchyRoot.getAncestorClasses().size());
		assertEquals(0, hierarchyRoot.getAncestorInterfaces().size());
	}
	
	@Test
	public void noPropertyInTypeRootTest() {
		JGum jgum = new JGum();
		AnyClassRoot hierarchyRoot = jgum.forAnyClassRoot();
		try {
			hierarchyRoot.put("x", "x");
			fail();
		} catch(UnsupportedOperationException e){}
		try {
			hierarchyRoot.put("x", "x", true);
			fail();
		} catch(UnsupportedOperationException e){}
	}
	
	@Test
	public void bottomUpPathTest() {
		JGum jgum = new JGum();
		AnyClassRoot hierarchyRoot = jgum.forAnyClassRoot();
		ClassNode<ArrayList> arrayListNode = (ClassNode<ArrayList>)hierarchyRoot.getOrCreateNode(ArrayList.class);
		
		FluentIterable<TypeNode<?>> arrayListBottomUpPath;
		
		arrayListBottomUpPath = arrayListNode.path(
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, DuplicatesDetection.IGNORE, Priority.INTERFACES_FIRST, InterfaceOrder.REVERSE));
		assertNotNull(arrayListBottomUpPath);
		//System.out.println(arrayListBottomUpPath);
		List<Class<?>> classes;
		classes = Node.<Class<?>>pathValues(arrayListBottomUpPath).toList();
		assertEquals(asList(ArrayList.class, Serializable.class, Cloneable.class, RandomAccess.class, List.class, Collection.class, Iterable.class, 
				AbstractList.class, List.class, Collection.class, Iterable.class, 
				AbstractCollection.class, Collection.class, Iterable.class, Object.class), classes);

		arrayListBottomUpPath = arrayListNode.path(
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, DuplicatesDetection.IGNORE, Priority.CLASSES_FIRST, InterfaceOrder.DIRECT));
		assertNotNull(arrayListBottomUpPath);
		//System.out.println(arrayListBottomUpPath);
		classes = Node.<Class<?>>pathValues(arrayListBottomUpPath).toList();
		assertEquals(asList(ArrayList.class, AbstractList.class, AbstractCollection.class, Object.class, 
				Collection.class, Iterable.class, List.class, Collection.class, Iterable.class, 
				List.class, Collection.class, Iterable.class, RandomAccess.class, Cloneable.class, Serializable.class
				), classes);
		
		arrayListBottomUpPath = arrayListNode.path(
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, DuplicatesDetection.ENFORCE, Priority.INTERFACES_FIRST, InterfaceOrder.REVERSE));
		assertNotNull(arrayListBottomUpPath);
		//System.out.println(arrayListBottomUpPath);
		classes = Node.<Class<?>>pathValues(arrayListBottomUpPath).toList();
		assertEquals(asList(ArrayList.class, Serializable.class, Cloneable.class, RandomAccess.class, List.class, Collection.class, Iterable.class, 
				AbstractList.class, AbstractCollection.class, Object.class), classes);
		
		FluentIterable<ClassNode<? super ArrayList>> ancestorsPath = arrayListNode.getAncestorClasses();
		assertEquals(asList(AbstractList.class, AbstractCollection.class, Object.class), Node.<Class<?>>pathValues(ancestorsPath).toList());
		
		FluentIterable<InterfaceNode<? super ArrayList>> interfacesPath = arrayListNode.getAncestorInterfaces();
		assertEquals(asList(Serializable.class, Cloneable.class, RandomAccess.class, List.class, Collection.class, Iterable.class), Node.<Class<?>>pathValues(interfacesPath).toList());
	}
	
	@Test
	public void topDownPathTest() {
		JGum jgum = new JGum();
		AnyClassRoot hierarchyRoot = jgum.forAnyClassRoot();
		hierarchyRoot.getOrCreateNode(ArrayList.class);
		FluentIterable<TypeNode<?>> rootTopDownPath;
		rootTopDownPath = hierarchyRoot.topDownPath();
		assertEquals(asList(java.lang.Iterable.class, java.util.RandomAccess.class, java.lang.Cloneable.class, java.io.Serializable.class, 
				java.lang.Object.class, java.util.Collection.class, java.util.ArrayList.class, java.util.AbstractCollection.class, 
				java.util.List.class, java.util.AbstractList.class), Node.<Class<?>>pathValues(rootTopDownPath).toList());
		//System.out.println(rootTopDownPath);
	}

	@Test
	public void attachProperties() {
		JGum jgum = new JGum();
		AnyClassRoot hierarchyRoot = jgum.forAnyClassRoot();
		String key = "key"; //the property name
		String v1 = "v1";
		String v2 = "v2";
		String v3 = "v3";
		String v4 = "v4";
		TypeNode<?> arrayListNode = hierarchyRoot.getOrCreateNode(ArrayList.class);  //creates a node for the ArrayList class. Nodes for all super classes and interfaces of ArrayList are also created.
		//Adding properties to different classes and interfaces in the hierarchy graph:
		arrayListNode.put(key, v1);
		hierarchyRoot.getNode(List.class).put(key, v2);
		hierarchyRoot.getNode(AbstractCollection.class).put(key, v3);
		hierarchyRoot.getNode(Object.class).put(key, v4);
		//Verifying the properties
		Iterator<?> propertiesIt = arrayListNode.bottomUpPathProperties(key).iterator();
		assertEquals(v1, propertiesIt.next());
		assertEquals(v2, propertiesIt.next());
		assertEquals(v3, propertiesIt.next());
		assertEquals(v4, propertiesIt.next());
		
		//Repeating the same example as above, but inserting the properties in a different order
		jgum = new JGum();
		hierarchyRoot = jgum.forAnyClassRoot();
		arrayListNode = hierarchyRoot.getOrCreateNode(ArrayList.class);
		//Properties added in an arbitrary order:
		hierarchyRoot.getNode(List.class).put(key, v2);
		hierarchyRoot.getNode(Object.class).put(key, v4);
		arrayListNode.put(key, v1);
		hierarchyRoot.getNode(AbstractCollection.class).put(key, v3);
		//but the result is the same:
		propertiesIt = arrayListNode.bottomUpPathProperties(key).iterator();
		assertEquals(v1, propertiesIt.next());
		assertEquals(v2, propertiesIt.next());
		assertEquals(v3, propertiesIt.next());
		assertEquals(v4, propertiesIt.next());
	}

}
