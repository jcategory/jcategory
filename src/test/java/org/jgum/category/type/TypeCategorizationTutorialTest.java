package org.jgum.category.type;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.apache.log4j.or.ObjectRenderer;
import org.jgum.JGum;
import org.jgum.category.type.TypeCategoryRoot.Any;
import org.jgum.traversal.RedundancyCheck;
import org.jgum.traversal.SearchStrategy;
import org.junit.Test;

import com.google.common.base.Function;

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
	
	@Test
	public void testTypeCategoryInheritance() {
		JGum jgum = new JGum();
		TypeCategory<Fruit> fruitCategory = jgum.forClass(Fruit.class); //type category for Fruit
		TypeCategory<Orange> orangeCategory = jgum.forClass(Orange.class); //type category for Orange
		fruitCategory.setProperty(ObjectRenderer.class, FruitRenderer.class); //ObjectRenderer property set to FruitRenderer for Fruit
		assertEquals(FruitRenderer.class, fruitCategory.getProperty(ObjectRenderer.class).get()); //ObjectRenderer property is FruitRenderer for Fruit
		assertEquals(FruitRenderer.class, orangeCategory.getProperty(ObjectRenderer.class).get()); //ObjectRenderer property is also FruitRenderer for Orange
		assertFalse(jgum.forClass(Object.class).getProperty(ObjectRenderer.class).isPresent()); //ObjectRenderer property has not been set for Object
	}
	
	
	
	public class Animal {}
	public interface HasLegs {}
	public interface FourLegged extends HasLegs {}
	public interface Furry {}
	public class Cat extends Animal implements Furry, FourLegged {}
	public class Fish extends Animal {}
	
	public class AnimalRenderer implements ObjectRenderer {
		@Override
		public String doRender(Object animal) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public class HasLegsRenderer implements ObjectRenderer {
		@Override
		public String doRender(Object somethingWithLegs) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	@Test
	public void testLabelsInterfacesFirst() {
		JGum jgum = new JGum();
		assertEquals(asList(Cat.class, FourLegged.class, HasLegs.class, Furry.class, Animal.class, Object.class, Any.class), jgum.forClass(Cat.class).bottomUpLabels());
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
		//JGum jgum = new JGum();
		
		//instantiating categories
		TypeCategory<Animal> animalCategory = jgum.forClass(Animal.class); //type category for Animal
		TypeCategory<HasLegs> hasLegsCategory = jgum.forClass(HasLegs.class); //type category for HasLegs
		TypeCategory<Cat> catCategory = jgum.forClass(Cat.class); //type category for Cat
		TypeCategory<Fish> fishCategory = jgum.forClass(Fish.class); //type category for Fish
		
		//setting properties
		animalCategory.setProperty(ObjectRenderer.class, AnimalRenderer.class); //ObjectRenderer property is AnimalRenderer for Animal
		hasLegsCategory.setProperty(ObjectRenderer.class, HasLegsRenderer.class); //ObjectRenderer property is HasLegsRenderer for HasLegs
		
		//testing
		assertEquals(HasLegsRenderer.class, catCategory.getProperty(ObjectRenderer.class).get()); //ObjectRenderer property is HasLegsRenderer for Cat
		assertEquals(AnimalRenderer.class, fishCategory.getProperty(ObjectRenderer.class).get()); //ObjectRenderer property is AnimalRenderer for Fish
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
		animalCategory.setProperty(ObjectRenderer.class, AnimalRenderer.class);
		hasLegsCategory.setProperty(ObjectRenderer.class, HasLegsRenderer.class);
		
		//testing
		assertEquals(AnimalRenderer.class, catCategory.getProperty(ObjectRenderer.class).get()); //ObjectRenderer property is AnimalRenderer for Cat
	}
	
	
	public interface A {}
	public interface B extends A {}
	public interface C extends A {}
	public interface D extends B, C {}
	
	@Test
	public void testDiamondInheritance() {
		JGum jgum = new JGum();
		assertEquals(asList(D.class, C.class, B.class, A.class, Any.class), jgum.forClass(D.class).bottomUpLabels());
	}
	
}
