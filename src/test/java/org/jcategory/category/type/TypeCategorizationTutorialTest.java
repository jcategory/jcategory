package org.jcategory.category.type;

import static java.util.Arrays.asList;
import static org.jcategory.category.Key.key;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.function.Function;

import org.apache.log4j.or.ObjectRenderer;
import org.jcategory.JCategory;
import org.jcategory.category.Key;
import org.jcategory.category.type.TypeCategoryRoot.Any;
import org.jcategory.testutil.animalhierarchy.Animal;
import org.jcategory.testutil.animalhierarchy.AnimalRenderer;
import org.jcategory.testutil.animalhierarchy.Cat;
import org.jcategory.testutil.animalhierarchy.Fish;
import org.jcategory.testutil.animalhierarchy.FourLegged;
import org.jcategory.testutil.animalhierarchy.Furry;
import org.jcategory.testutil.animalhierarchy.HasLegs;
import org.jcategory.testutil.animalhierarchy.HasLegsRenderer;
import org.jcategory.traversal.RedundancyCheck;
import org.jcategory.traversal.SearchStrategy;
import org.junit.Test;

public class TypeCategorizationTutorialTest {

	public class Fruit  {}
	public class Orange extends Fruit{}
	
	public class FruitRenderer implements ObjectRenderer {
		@Override
		public String doRender(Object fruit) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static final Key OBJECT_RENDERER_KEY = key(ObjectRenderer.class);
	
	@Test
	public void testTypeCategoryInheritance() {
		JCategory context = new JCategory();
		TypeCategory<Fruit> fruitCategory = context.forClass(Fruit.class); //type category for Fruit
		TypeCategory<Orange> orangeCategory = context.forClass(Orange.class); //type category for Orange
		fruitCategory.setProperty(OBJECT_RENDERER_KEY, FruitRenderer.class); //ObjectRenderer property set to FruitRenderer for Fruit
		assertEquals(FruitRenderer.class, fruitCategory.getProperty(OBJECT_RENDERER_KEY).get()); //ObjectRenderer property is FruitRenderer for Fruit
		assertEquals(FruitRenderer.class, orangeCategory.getProperty(OBJECT_RENDERER_KEY).get()); //ObjectRenderer property is also FruitRenderer for Orange
		assertFalse(context.forClass(Object.class).getProperty(OBJECT_RENDERER_KEY).isPresent()); //ObjectRenderer property has not been set for Object
	}
	
	
	@Test
	public void testLabelsInterfacesFirst() {
		JCategory context = new JCategory();
		assertEquals(asList(Cat.class, Animal.class, Object.class, Furry.class, FourLegged.class, HasLegs.class, Any.class), context.forClass(Cat.class).bottomUpLabels());
	}
	
	@Test
	public void testLabelsClassesFirst() {
		Function<TypeCategory<?>, List<TypeCategory<?>>> linearizationFunction = 
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.CLASSES_FIRST, InterfaceOrder.DECLARATION, RedundancyCheck.KEEP_LAST);
		JCategory context = new JCategory(linearizationFunction);
		assertEquals(asList(Cat.class, Animal.class, Object.class, Furry.class, FourLegged.class, HasLegs.class, Any.class), context.forClass(Cat.class).bottomUpLabels());
	}
	
	@Test
	public void testMultiInheritanceInterfacesFirst() {
		//configuring the JCategory context
		Function<TypeCategory<?>, List<TypeCategory<?>>> linearizationFunction = 
				new BottomUpTypeTraversalPolicy( 
						SearchStrategy.PRE_ORDER, //pre-order search
						Priority.INTERFACES_FIRST, //look first at interfaces, then classes
						InterfaceOrder.REVERSE, //traverse the interfaces from right to left (reverse declaration order)
						RedundancyCheck.KEEP_LAST); //if a category appears more than once in the linearization, keep the last found and discard the previous ones.
		JCategory context = new JCategory(linearizationFunction);
		
		//instantiating categories
		TypeCategory<Animal> animalCategory = context.forClass(Animal.class); //type category for Animal
		TypeCategory<HasLegs> hasLegsCategory = context.forClass(HasLegs.class); //type category for HasLegs
		TypeCategory<Cat> catCategory = context.forClass(Cat.class); //type category for Cat
		TypeCategory<Fish> fishCategory = context.forClass(Fish.class); //type category for Fish
		
		//setting properties
		animalCategory.setProperty(OBJECT_RENDERER_KEY, AnimalRenderer.class); //ObjectRenderer property is AnimalRenderer for Animal
		hasLegsCategory.setProperty(OBJECT_RENDERER_KEY, HasLegsRenderer.class); //ObjectRenderer property is HasLegsRenderer for HasLegs
		
		//testing
		assertEquals(HasLegsRenderer.class, catCategory.getProperty(OBJECT_RENDERER_KEY).get()); //ObjectRenderer property is HasLegsRenderer for Cat
		assertEquals(AnimalRenderer.class, fishCategory.getProperty(OBJECT_RENDERER_KEY).get()); //ObjectRenderer property is AnimalRenderer for Fish
	}
	
	@Test
	public void testMultiInheritanceClassesFirst() {
		//configuring the JCategory context
		Function<TypeCategory<?>, List<TypeCategory<?>>> linearizationFunction = 
				new BottomUpTypeTraversalPolicy(
						SearchStrategy.PRE_ORDER, //pre-order search
						Priority.CLASSES_FIRST, //look first at classes, then interfaces
						InterfaceOrder.DECLARATION, //traverse the interfaces from left to right (declaration order)
						RedundancyCheck.KEEP_LAST); //if a category appears more than once in the linearization, keep the last found and discard the previous ones.
		JCategory context = new JCategory(linearizationFunction);
		
		//instantiating categories
		TypeCategory<Animal> animalCategory = context.forClass(Animal.class);
		TypeCategory<HasLegs> hasLegsCategory = context.forClass(HasLegs.class);
		TypeCategory<Cat> catCategory = context.forClass(Cat.class);
		
		//setting properties
		animalCategory.setProperty(OBJECT_RENDERER_KEY, AnimalRenderer.class);
		hasLegsCategory.setProperty(OBJECT_RENDERER_KEY, HasLegsRenderer.class);
		
		//testing
		assertEquals(AnimalRenderer.class, catCategory.getProperty(OBJECT_RENDERER_KEY).get()); //ObjectRenderer property is AnimalRenderer for Cat
	}
	
	
	public interface A {}
	public interface B extends A {}
	public interface C extends A {}
	public interface D extends B, C {}
	
	@Test
	public void testDiamondInheritance() {
		JCategory context = new JCategory();
		assertEquals(asList(D.class, B.class, C.class, A.class, Any.class), context.forClass(D.class).bottomUpLabels());
	}
	
}
