package org.jgum.category.type;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.apache.log4j.or.ObjectRenderer;
import org.jgum.JGum;
import org.jgum.category.type.TypeCategoryRoot.Any;
import org.jgum.testutil.animalhierarchy.Animal;
import org.jgum.testutil.animalhierarchy.AnimalRenderer;
import org.jgum.testutil.animalhierarchy.Cat;
import org.jgum.testutil.animalhierarchy.Fish;
import org.jgum.testutil.animalhierarchy.FourLegged;
import org.jgum.testutil.animalhierarchy.Furry;
import org.jgum.testutil.animalhierarchy.HasLegs;
import org.jgum.testutil.animalhierarchy.HasLegsRenderer;
import org.jgum.traversal.RedundancyCheck;
import org.jgum.traversal.SearchStrategy;
import org.junit.Test;

import java.util.function.Function;

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
	
	public static final Object OBJECT_RENDERER_KEY = ObjectRenderer.class;
	
	@Test
	public void testTypeCategoryInheritance() {
		JGum jgum = new JGum();
		TypeCategory<Fruit> fruitCategory = jgum.forClass(Fruit.class); //type category for Fruit
		TypeCategory<Orange> orangeCategory = jgum.forClass(Orange.class); //type category for Orange
		fruitCategory.setProperty(OBJECT_RENDERER_KEY, FruitRenderer.class); //ObjectRenderer property set to FruitRenderer for Fruit
		assertEquals(FruitRenderer.class, fruitCategory.getProperty(OBJECT_RENDERER_KEY).get()); //ObjectRenderer property is FruitRenderer for Fruit
		assertEquals(FruitRenderer.class, orangeCategory.getProperty(OBJECT_RENDERER_KEY).get()); //ObjectRenderer property is also FruitRenderer for Orange
		assertFalse(jgum.forClass(Object.class).getProperty(OBJECT_RENDERER_KEY).isPresent()); //ObjectRenderer property has not been set for Object
	}
	
	
	@Test
	public void testLabelsInterfacesFirst() {
		JGum jgum = new JGum();
		assertEquals(asList(Cat.class, Animal.class, Object.class, Furry.class, FourLegged.class, HasLegs.class, Any.class), jgum.forClass(Cat.class).bottomUpLabels());
	}
	
	@Test
	public void testLabelsClassesFirst() {
		Function<TypeCategory<?>, List<TypeCategory<?>>> linearizationFunction = 
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.CLASSES_FIRST, InterfaceOrder.DECLARATION, RedundancyCheck.KEEP_LAST);
		JGum jgum = new JGum(linearizationFunction);
		assertEquals(asList(Cat.class, Animal.class, Object.class, Furry.class, FourLegged.class, HasLegs.class, Any.class), jgum.forClass(Cat.class).bottomUpLabels());
	}
	
	@Test
	public void testMultiInheritanceInterfacesFirst() {
		//configuring the JGum context
		Function<TypeCategory<?>, List<TypeCategory<?>>> linearizationFunction = 
				new BottomUpTypeTraversalPolicy( 
						SearchStrategy.PRE_ORDER, //pre-order search
						Priority.INTERFACES_FIRST, //look first at interfaces, then classes
						InterfaceOrder.REVERSE, //traverse the interfaces from right to left (reverse declaration order)
						RedundancyCheck.KEEP_LAST); //if a category appears more than once in the linearization, keep the last found and discard the previous ones.
		JGum jgum = new JGum(linearizationFunction);
		
		//instantiating categories
		TypeCategory<Animal> animalCategory = jgum.forClass(Animal.class); //type category for Animal
		TypeCategory<HasLegs> hasLegsCategory = jgum.forClass(HasLegs.class); //type category for HasLegs
		TypeCategory<Cat> catCategory = jgum.forClass(Cat.class); //type category for Cat
		TypeCategory<Fish> fishCategory = jgum.forClass(Fish.class); //type category for Fish
		
		//setting properties
		animalCategory.setProperty(OBJECT_RENDERER_KEY, AnimalRenderer.class); //ObjectRenderer property is AnimalRenderer for Animal
		hasLegsCategory.setProperty(OBJECT_RENDERER_KEY, HasLegsRenderer.class); //ObjectRenderer property is HasLegsRenderer for HasLegs
		
		//testing
		assertEquals(HasLegsRenderer.class, catCategory.getProperty(OBJECT_RENDERER_KEY).get()); //ObjectRenderer property is HasLegsRenderer for Cat
		assertEquals(AnimalRenderer.class, fishCategory.getProperty(OBJECT_RENDERER_KEY).get()); //ObjectRenderer property is AnimalRenderer for Fish
	}
	
	@Test
	public void testMultiInheritanceClassesFirst() {
		//configuring the JGum context
		Function<TypeCategory<?>, List<TypeCategory<?>>> linearizationFunction = 
				new BottomUpTypeTraversalPolicy(
						SearchStrategy.PRE_ORDER, //pre-order search
						Priority.CLASSES_FIRST, //look first at classes, then interfaces
						InterfaceOrder.DECLARATION, //traverse the interfaces from left to right (declaration order)
						RedundancyCheck.KEEP_LAST); //if a category appears more than once in the linearization, keep the last found and discard the previous ones.
		JGum jgum = new JGum(linearizationFunction);
		
		//instantiating categories
		TypeCategory<Animal> animalCategory = jgum.forClass(Animal.class);
		TypeCategory<HasLegs> hasLegsCategory = jgum.forClass(HasLegs.class);
		TypeCategory<Cat> catCategory = jgum.forClass(Cat.class);
		
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
		JGum jgum = new JGum();
		assertEquals(asList(D.class, B.class, C.class, A.class, Any.class), jgum.forClass(D.class).bottomUpLabels());
	}
	
}
