package org.jgum.category.type;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import org.jgum.JGum;
import org.jgum.category.Category;
import org.jgum.category.CategoryCreationListener;
import org.jgum.category.CategoryProperty;
import org.jgum.category.type.TypeCategoryRoot.Any;
import org.jgum.testutil.CounterCreationListener;
import org.jgum.traversal.DuplicatesDetection;
import org.jgum.traversal.SearchStrategy;
import org.junit.Test;

import com.google.common.collect.FluentIterable;

public class TypeCategoryTest {

	@Test
	public void addingTypeCategories() {
		JGum jgum = new JGum();
		TypeCategorization hierarchy = jgum.getTypeHierarchy();
		TypeCategoryRoot hierarchyRoot = hierarchy.getRoot();
		assertNull(hierarchy.getTypeCategory(Object.class));
		assertNull(hierarchy.getTypeCategory(ArrayList.class));
		ClassCategory<ArrayList> arrayListNode = (ClassCategory<ArrayList>)hierarchy.getOrCreateTypeCategory(ArrayList.class);
		assertNotNull(arrayListNode);
		assertEquals(0, ((ClassCategory)hierarchy.getTypeCategory(Object.class)).getAncestorClasses().size());
		assertEquals(0, hierarchy.getTypeCategory(Object.class).getAncestorInterfaces().size());
		assertEquals(0, hierarchyRoot.getAncestorInterfaces().size());
	}
	
	@Test
	public void propertyInTypeRootTest() {
		JGum jgum = new JGum();
		TypeCategoryRoot hierarchyRoot = jgum.getTypeHierarchy().getRoot();
		hierarchyRoot.putProperty("x", "x");
		CategoryProperty cp = new CategoryProperty(jgum.forClass(Object.class), "x");
		assertEquals("x", cp.getOrThrow());
	}
	
	@Test
	public void bottomUpLinearizationTest() {
		JGum jgum = new JGum();
		TypeCategorization hierarchy = jgum.getTypeHierarchy();
		ClassCategory<ArrayList> arrayListNode = (ClassCategory<ArrayList>)hierarchy.getOrCreateTypeCategory(ArrayList.class);
		
		FluentIterable<TypeCategory<?>> arrayListBottomUpPath;
		
		arrayListBottomUpPath = arrayListNode.linearize(
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.INTERFACES_FIRST, InterfaceOrder.REVERSE, DuplicatesDetection.IGNORE));
		assertNotNull(arrayListBottomUpPath);
		//System.out.println(arrayListBottomUpPath);
		List<Class<?>> classes;
		classes = Category.<Class<?>>linearizeValues(arrayListBottomUpPath).toList();
		assertEquals(asList(ArrayList.class, Serializable.class, Any.class, Cloneable.class, Any.class, RandomAccess.class, Any.class,
				List.class, Collection.class, Iterable.class, Any.class,
				AbstractList.class, List.class, Collection.class, Iterable.class, Any.class,
				AbstractCollection.class, Collection.class, Iterable.class, Any.class, 
				Object.class, Any.class), classes);

		arrayListBottomUpPath = arrayListNode.linearize(
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.CLASSES_FIRST, InterfaceOrder.DIRECT, DuplicatesDetection.IGNORE));
		assertNotNull(arrayListBottomUpPath);
		//System.out.println(arrayListBottomUpPath);
		classes = Category.<Class<?>>linearizeValues(arrayListBottomUpPath).toList();
		assertEquals(asList(ArrayList.class, AbstractList.class, AbstractCollection.class, Object.class, Any.class,
				Collection.class, Iterable.class, Any.class, 
				List.class, Collection.class, Iterable.class, Any.class,
				List.class, Collection.class, Iterable.class, Any.class, 
				RandomAccess.class, Any.class, Cloneable.class, Any.class, Serializable.class, Any.class
				), classes);
		
		arrayListBottomUpPath = arrayListNode.linearize(
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.INTERFACES_FIRST, InterfaceOrder.REVERSE, DuplicatesDetection.ENFORCE));
		assertNotNull(arrayListBottomUpPath);
		//System.out.println(arrayListBottomUpPath);
		classes = Category.<Class<?>>linearizeValues(arrayListBottomUpPath).toList();
		assertEquals(asList(ArrayList.class, Serializable.class, Any.class, Cloneable.class, RandomAccess.class, List.class, Collection.class, Iterable.class, 
				AbstractList.class, AbstractCollection.class, Object.class), classes);
		
		FluentIterable<ClassCategory<? super ArrayList>> ancestorsPath = arrayListNode.getAncestorClasses();
		assertEquals(asList(AbstractList.class, AbstractCollection.class, Object.class), Category.<Class<?>>linearizeValues(ancestorsPath).toList());
		
		FluentIterable<InterfaceCategory<? super ArrayList>> interfacesPath = arrayListNode.getAncestorInterfaces();
		assertEquals(asList(Serializable.class, Cloneable.class, RandomAccess.class, List.class, Collection.class, Iterable.class), Category.<Class<?>>linearizeValues(interfacesPath).toList());
	}
	
	@Test
	public void topDownLinearizationTest() {
		JGum jgum = new JGum();
		TypeCategorization hierarchy = jgum.getTypeHierarchy();
		hierarchy.getOrCreateTypeCategory(ArrayList.class);
		FluentIterable<TypeCategory<?>> rootTopDownPath;
		rootTopDownPath = hierarchy.getRoot().topDownLinearization();
		//System.out.println(rootTopDownPath);
		assertEquals(asList(Any.class, java.lang.Iterable.class, java.util.RandomAccess.class, java.lang.Cloneable.class, java.io.Serializable.class, 
				java.lang.Object.class, java.util.Collection.class, java.util.ArrayList.class, java.util.AbstractCollection.class, 
				java.util.List.class, java.util.AbstractList.class), Category.<Class<?>>linearizeValues(rootTopDownPath).toList());
	}

	@Test
	public void attachProperties() {
		JGum jgum = new JGum();
		TypeCategorization hierarchy = jgum.getTypeHierarchy();
		String key = "key"; //the property name
		String v1 = "v1";
		String v2 = "v2";
		String v3 = "v3";
		String v4 = "v4";
		TypeCategory<?> arrayListNode = hierarchy.getOrCreateTypeCategory(ArrayList.class);  //creates a node for the ArrayList class. Nodes for all super classes and interfaces of ArrayList are also created.
		//Adding properties to different classes and interfaces in the hierarchy graph:
		arrayListNode.putProperty(key, v1);
		hierarchy.getTypeCategory(List.class).putProperty(key, v2);
		hierarchy.getTypeCategory(AbstractCollection.class).putProperty(key, v3);
		hierarchy.getTypeCategory(Object.class).putProperty(key, v4);
		//Verifying the properties
		Iterator<?> propertiesIt = CategoryProperty.<String>properties(arrayListNode.bottomUpLinearization(), key).iterator();
		assertEquals(v1, propertiesIt.next());
		assertEquals(v2, propertiesIt.next());
		assertEquals(v3, propertiesIt.next());
		assertEquals(v4, propertiesIt.next());
		
		//Repeating the same example as above, but inserting the properties in a different order
		jgum = new JGum();
		hierarchy = jgum.getTypeHierarchy();
		arrayListNode = hierarchy.getOrCreateTypeCategory(ArrayList.class);
		//Properties added in an arbitrary order:
		hierarchy.getTypeCategory(List.class).putProperty(key, v2);
		hierarchy.getTypeCategory(Object.class).putProperty(key, v4);
		arrayListNode.putProperty(key, v1);
		hierarchy.getTypeCategory(AbstractCollection.class).putProperty(key, v3);
		//but the result is the same:
		propertiesIt = CategoryProperty.<String>properties(arrayListNode.bottomUpLinearization(), key).iterator();
		assertEquals(v1, propertiesIt.next());
		assertEquals(v2, propertiesIt.next());
		assertEquals(v3, propertiesIt.next());
		assertEquals(v4, propertiesIt.next());
	}
	
	@Test
	public void testListener() {
		JGum jgum = new JGum();
		CounterCreationListener listener = new CounterCreationListener();
		jgum.getTypeHierarchy().addNodeCreationListener((CategoryCreationListener)listener);
		jgum.forClass(ArrayList.class);
		//there are 10 classes and interfaces in the class hierarchy of ArrayList + the Any class located at the root of the class and interface hierarchy.
		assertEquals(11, listener.getCounter()); 
	}

}
