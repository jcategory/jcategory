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
import java.util.List;
import java.util.RandomAccess;

import org.jgum.JGum;
import org.jgum.category.CategorizationListener;
import org.jgum.category.CategoryProperty;
import org.jgum.category.Key;
import org.jgum.category.named.NamedCategory;
import org.jgum.category.type.TypeCategoryRoot.Any;
import org.jgum.testutil.CounterCreationListener;
import org.jgum.traversal.RedundancyCheck;
import org.jgum.traversal.SearchStrategy;
import org.junit.Test;

import com.google.common.base.Optional;

public class TypeCategorizationTest {

	@Test
	public void addingTypeCategories() {
		JGum jgum = new JGum();
		TypeCategorization hierarchy = jgum.getTypeCategorization();
		TypeCategoryRoot hierarchyRoot = hierarchy.getRoot();
		assertNull(hierarchy.getTypeCategory(Object.class));
		assertNull(hierarchy.getTypeCategory(ArrayList.class));
		ClassCategory<ArrayList> arrayListNode = (ClassCategory<ArrayList>)hierarchy.getOrCreateTypeCategory(ArrayList.class);
		assertNotNull(arrayListNode);
		assertEquals(0, hierarchy.getTypeCategory(Object.class).getAncestorClasses().size());
		assertEquals(0, hierarchy.getTypeCategory(Object.class).getAncestorInterfaces().size());
		assertEquals(0, hierarchyRoot.getAncestorInterfaces().size());
	}
	
	@Test
	public void superMethodTest() {
		JGum jgum = new JGum();
		assertEquals(Any.class, jgum.forClass(Object.class).<TypeCategory<Object>>getSuper().get().getLabel());
		assertEquals(Optional.absent(), jgum.forTypeRoot().getSuper());
	}
	
	@Test
	public void propertyInTypeRootTest() {
		JGum jgum = new JGum();
		TypeCategoryRoot hierarchyRoot = jgum.getTypeCategorization().getRoot();
		hierarchyRoot.setProperty(new Key("x"), "x");
		CategoryProperty cp = new CategoryProperty(jgum.forClass(Object.class), new Key("x"));
		assertEquals("x", cp.get());
	}
	
	@Test
	public void bottomUpLinearizationTest() {
		JGum jgum = new JGum();
		TypeCategorization hierarchy = jgum.getTypeCategorization();
		ClassCategory<ArrayList> arrayListNode = (ClassCategory<ArrayList>)hierarchy.getOrCreateTypeCategory(ArrayList.class);
		
		List<Class<?>> classes;
		
		classes = arrayListNode.linearizeLabels(
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.INTERFACES_FIRST, InterfaceOrder.REVERSE, RedundancyCheck.KEEP_LAST));
		assertEquals(asList(ArrayList.class, Serializable.class, Cloneable.class, RandomAccess.class,
				AbstractList.class, List.class, AbstractCollection.class, Collection.class, Iterable.class, Object.class, Any.class), classes);
		
		classes = arrayListNode.linearizeLabels(
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.INTERFACES_FIRST, InterfaceOrder.REVERSE, RedundancyCheck.KEEP_FIRST));
		assertEquals(asList(ArrayList.class, Serializable.class, Any.class, Cloneable.class, RandomAccess.class, List.class, Collection.class, Iterable.class, 
				AbstractList.class, AbstractCollection.class, Object.class), classes);
		
		classes = arrayListNode.linearizeLabels(
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.INTERFACES_FIRST, InterfaceOrder.REVERSE, RedundancyCheck.IGNORE));
		assertEquals(asList(ArrayList.class, Serializable.class, Any.class, Cloneable.class, Any.class, RandomAccess.class, Any.class,
				List.class, Collection.class, Iterable.class, Any.class,
				AbstractList.class, List.class, Collection.class, Iterable.class, Any.class,
				AbstractCollection.class, Collection.class, Iterable.class, Any.class, 
				Object.class, Any.class), classes);

		classes = arrayListNode.linearizeLabels(
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.CLASSES_FIRST, InterfaceOrder.DECLARATION, RedundancyCheck.IGNORE));
		assertEquals(asList(ArrayList.class, AbstractList.class, AbstractCollection.class, Object.class, Any.class,
				Collection.class, Iterable.class, Any.class, 
				List.class, Collection.class, Iterable.class, Any.class,
				List.class, Collection.class, Iterable.class, Any.class, 
				RandomAccess.class, Any.class, Cloneable.class, Any.class, Serializable.class, Any.class
				), classes);
		
		List<ClassCategory<? super ArrayList>> ancestorsPath = arrayListNode.getAncestorClasses();
		assertEquals(asList(AbstractList.class, AbstractCollection.class, Object.class), NamedCategory.<Class<?>>labels(ancestorsPath));
		
		List<InterfaceCategory<? super ArrayList>> interfacesPath = arrayListNode.getAncestorInterfaces();
		assertEquals(asList(List.class, Collection.class, Iterable.class, RandomAccess.class, Cloneable.class, Serializable.class), NamedCategory.<Class<?>>labels(interfacesPath));
	}
	
	@Test
	public void topDownLinearizationTest() {
		JGum jgum = new JGum();
		TypeCategorization hierarchy = jgum.getTypeCategorization();
		hierarchy.getOrCreateTypeCategory(ArrayList.class);
		List<TypeCategory<?>> rootTopDownPath;
		rootTopDownPath = hierarchy.getRoot().topDownCategories();
		//System.out.println(rootTopDownPath);
		assertEquals(asList(Any.class, java.lang.Object.class, java.lang.Iterable.class, java.util.RandomAccess.class, java.lang.Cloneable.class, java.io.Serializable.class, 
				java.util.AbstractCollection.class, java.util.Collection.class, java.util.ArrayList.class,
				java.util.AbstractList.class, java.util.List.class), NamedCategory.<Class<?>>labels(rootTopDownPath));
	}

	@Test
	public void attachProperties() {
		JGum jgum = new JGum();
		TypeCategorization hierarchy = jgum.getTypeCategorization();
		Key key = new Key("key"); //the property identifier
		String v1 = "v1";
		String v2 = "v2";
		String v3 = "v3";
		String v4 = "v4";
		TypeCategory<?> arrayListNode = hierarchy.getOrCreateTypeCategory(ArrayList.class);  //creates a node for the ArrayList class. Nodes for all super classes and interfaces of ArrayList are also created.
		//Adding properties to different classes and interfaces in the hierarchy graph:
		arrayListNode.setProperty(key, v1);
		hierarchy.getTypeCategory(AbstractCollection.class).setProperty(key, v2);
		hierarchy.getTypeCategory(Object.class).setProperty(key, v3);
		hierarchy.getTypeCategory(List.class).setProperty(key, v4);
		//Verifying the properties
		List<?> properties = arrayListNode.bottomUpProperties(key);
		assertEquals(v1, properties.get(0));
		assertEquals(v2, properties.get(1));
		assertEquals(v3, properties.get(2));
		assertEquals(v4, properties.get(3));
		
		//Repeating the same example as above, but inserting the properties in a different order
		jgum = new JGum();
		hierarchy = jgum.getTypeCategorization();
		arrayListNode = hierarchy.getOrCreateTypeCategory(ArrayList.class);
		//Properties added in an arbitrary order:
		hierarchy.getTypeCategory(List.class).setProperty(key, v4);
		hierarchy.getTypeCategory(Object.class).setProperty(key, v3);
		arrayListNode.setProperty(key, v1);
		hierarchy.getTypeCategory(AbstractCollection.class).setProperty(key, v2);
		//but the result is the same:
		properties = arrayListNode.bottomUpProperties(key);
		assertEquals(v1, properties.get(0));
		assertEquals(v2, properties.get(1));
		assertEquals(v3, properties.get(2));
		assertEquals(v4, properties.get(3));
	}
	
	@Test
	public void testListener() {
		JGum jgum = new JGum();
		CounterCreationListener listener = new CounterCreationListener();
		jgum.getTypeCategorization().addCategorizationListener((CategorizationListener)listener);
		jgum.forClass(ArrayList.class);
		//there are 10 classes and interfaces in the class hierarchy of ArrayList + the Any class located at the root of the class and interface hierarchy.
		assertEquals(11, listener.getCounter()); 
	}

}
