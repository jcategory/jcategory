package org.jcategory.category.type;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.jcategory.category.Key.key;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.RandomAccess;

import org.jcategory.JCategory;
import org.jcategory.category.CategorizationListener;
import org.jcategory.category.CategoryProperty;
import org.jcategory.category.Key;
import org.jcategory.category.name.NameCategory;
import org.jcategory.category.type.TypeCategoryRoot.Any;
import org.jcategory.testutil.CounterCreationListener;
import org.jcategory.traversal.RedundancyCheck;
import org.jcategory.traversal.SearchStrategy;
import org.junit.Test;

public class TypeCategorizationTest {

	@Test
	public void addingTypeCategories() {
		JCategory context = new JCategory();
		TypeCategorization hierarchy = context.getTypeCategorization();
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
		JCategory context = new JCategory();
		assertEquals(Any.class, context.forClass(Object.class).<TypeCategory<Object>>getSuper().get().getLabel());
		assertEquals(Optional.empty(), context.forTypeRoot().getSuper());
	}
	
	@Test
	public void propertyInTypeRootTest() {
		JCategory context = new JCategory();
		TypeCategoryRoot hierarchyRoot = context.getTypeCategorization().getRoot();
		Key key = key();
		hierarchyRoot.setProperty(key, "x");
		CategoryProperty cp = new CategoryProperty(context.forClass(Object.class), key);
		assertEquals("x", cp.get());
	}
	
	@Test
	public void knownSubClassesTest() {
		JCategory context = new JCategory();
		context.forClass(ArrayList.class);
		List subclasses;
		subclasses = context.forTypeRoot().getKnownSubClasses();
		assertEquals(4, subclasses.size());
		subclasses = context.forClass(Object.class).getKnownSubClasses();
		assertEquals(3, subclasses.size());
		subclasses = context.forClass(List.class).getKnownSubClasses();
		assertEquals(2, subclasses.size());
	}
	
	@Test
	public void knownSubInterfacesTest() {
		JCategory context = new JCategory();
		context.forClass(ArrayList.class);
		List subclasses;
		subclasses = context.forTypeRoot().getKnownSubInterfaces();
		assertEquals(6, subclasses.size());
		subclasses = context.forClass(Collection.class).getKnownSubInterfaces();
		assertEquals(1, subclasses.size());
		subclasses = context.forClass(Object.class).getKnownSubInterfaces();
		assertEquals(0, subclasses.size());
	}
	
	@Test
	public void bottomUpLinearizationTest() {
		JCategory context = new JCategory();
		TypeCategorization hierarchy = context.getTypeCategorization();
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
		assertEquals(asList(AbstractList.class, AbstractCollection.class, Object.class), NameCategory.<Class<?>>labels(ancestorsPath));
		
		List<InterfaceCategory<? super ArrayList>> interfacesPath = arrayListNode.getAncestorInterfaces();
		assertEquals(asList(List.class, Collection.class, Iterable.class, RandomAccess.class, Cloneable.class, Serializable.class), NameCategory.<Class<?>>labels(interfacesPath));
	}
	
	@Test
	public void topDownLinearizationTest() {
		JCategory context = new JCategory();
		TypeCategorization hierarchy = context.getTypeCategorization();
		hierarchy.getOrCreateTypeCategory(ArrayList.class);
		List<TypeCategory<?>> rootTopDownPath;
		rootTopDownPath = hierarchy.getRoot().topDownCategories();
		//System.out.println(rootTopDownPath);
		assertEquals(asList(Any.class, java.lang.Object.class, java.lang.Iterable.class, java.util.RandomAccess.class, java.lang.Cloneable.class, java.io.Serializable.class, 
				java.util.AbstractCollection.class, java.util.Collection.class, java.util.ArrayList.class,
				java.util.AbstractList.class, java.util.List.class), NameCategory.<Class<?>>labels(rootTopDownPath));
	}

	@Test
	public void attachProperties() {
		JCategory context = new JCategory();
		TypeCategorization hierarchy = context.getTypeCategorization();
		Key key = key(); //the property identifier
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
		context = new JCategory();
		hierarchy = context.getTypeCategorization();
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
		JCategory context = new JCategory();
		CounterCreationListener listener = new CounterCreationListener();
		context.getTypeCategorization().addCategorizationListener((CategorizationListener)listener);
		context.forClass(ArrayList.class);
		//there are 10 classes and interfaces in the class hierarchy of ArrayList + the Any class located at the root of the class and interface hierarchy.
		assertEquals(11, listener.getCounter()); 
	}

	@Test
	public void testSetQuantifiedOneBound() {
		Key key = key();
		Object value = "y";
		
		JCategory context = new JCategory();
		TypeCategory iterableCategory = context.forClass(Iterable.class);
		TypeCategory collectionCategory = context.forClass(Collection.class);
		TypeCategory listCategory = context.forClass(List.class);
		TypeCategory objectCategory = context.forClass(Object.class);
		TypeCategory abstractCollectionCategory = context.forClass(AbstractCollection.class);
		TypeCategory abstractListCategory = context.forClass(AbstractList.class);
		TypeCategory arrayListCategory = context.forClass(ArrayList.class);
		TypeCategory hashSetCategory = context.forClass(HashSet.class);
		
		context.getTypeCategorization().setQuantified(Arrays.<Class<?>>asList(List.class), key, value);
		
		assertEquals(emptyList(), iterableCategory.getLocalProperty(key));
		assertEquals(emptyList(), collectionCategory.getLocalProperty(key));
		assertEquals(value, listCategory.getLocalProperty(key).get(0));
		assertEquals(emptyList(), objectCategory.getLocalProperty(key));
		assertEquals(emptyList(), abstractCollectionCategory.getLocalProperty(key));
		assertEquals(value, abstractListCategory.getLocalProperty(key).get(0));
		assertEquals(value, arrayListCategory.getLocalProperty(key).get(0));
		assertEquals(emptyList(), hashSetCategory.getLocalProperty(key));
		
		context.getTypeCategorization().removeQuantified(Arrays.<Class<?>>asList(List.class), key);
		
		assertEquals(emptyList(), iterableCategory.getLocalProperty(key));
		assertEquals(emptyList(), collectionCategory.getLocalProperty(key));
		assertEquals(emptyList(), listCategory.getLocalProperty(key));
		assertEquals(emptyList(), objectCategory.getLocalProperty(key));
		assertEquals(emptyList(), abstractCollectionCategory.getLocalProperty(key));
		assertEquals(emptyList(), abstractListCategory.getLocalProperty(key));
		assertEquals(emptyList(), arrayListCategory.getLocalProperty(key));
		assertEquals(emptyList(), hashSetCategory.getLocalProperty(key));
	}
	
	@Test
	public void testSetQuantifiedMultipleBounds() {
		Key key = key();
		Object value = "y";
		
		JCategory context = new JCategory();
		TypeCategory iterableCategory = context.forClass(Iterable.class);
		TypeCategory collectionCategory = context.forClass(Collection.class);
		TypeCategory listCategory = context.forClass(List.class);
		TypeCategory objectCategory = context.forClass(Object.class);
		TypeCategory abstractCollectionCategory = context.forClass(AbstractCollection.class);
		TypeCategory abstractListCategory = context.forClass(AbstractList.class);
		TypeCategory arrayListCategory = context.forClass(ArrayList.class);
		TypeCategory hashSetCategory = context.forClass(HashSet.class);
		
		context.getTypeCategorization().setQuantified(Arrays.<Class<?>>asList(AbstractCollection.class, List.class), key, value);
		
		assertEquals(emptyList(), iterableCategory.getLocalProperty(key));
		assertEquals(emptyList(), collectionCategory.getLocalProperty(key));
		assertEquals(emptyList(), listCategory.getLocalProperty(key));
		assertEquals(emptyList(), objectCategory.getLocalProperty(key));
		assertEquals(emptyList(), abstractCollectionCategory.getLocalProperty(key));
		assertEquals(value, abstractListCategory.getLocalProperty(key).get(0));
		assertEquals(value, arrayListCategory.getLocalProperty(key).get(0));
		assertEquals(emptyList(), hashSetCategory.getLocalProperty(key));
		
		context.getTypeCategorization().removeQuantified(Arrays.<Class<?>>asList(AbstractCollection.class, List.class), key);
		
		assertEquals(emptyList(), iterableCategory.getLocalProperty(key));
		assertEquals(emptyList(), collectionCategory.getLocalProperty(key));
		assertEquals(emptyList(), listCategory.getLocalProperty(key));
		assertEquals(emptyList(), objectCategory.getLocalProperty(key));
		assertEquals(emptyList(), abstractCollectionCategory.getLocalProperty(key));
		assertEquals(emptyList(), abstractListCategory.getLocalProperty(key));
		assertEquals(emptyList(), arrayListCategory.getLocalProperty(key));
		assertEquals(emptyList(), hashSetCategory.getLocalProperty(key));
	}
	
}
