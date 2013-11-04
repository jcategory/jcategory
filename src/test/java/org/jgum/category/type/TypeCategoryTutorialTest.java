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

public class TypeCategoryTutorialTest {

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
		TypeCategory<?> fruitCategory = jgum.forClass(Fruit.class); //type category for Fruit.class
		TypeCategory<?> orangeCategory = jgum.forClass(Orange.class); //type category for Orange.class
		fruitCategory.setProperty(ObjectRenderer.class, FruitRenderer.class); //ObjectRenderer.class property set to FruitRenderer.class for Fruit.class
		assertEquals(FruitRenderer.class, fruitCategory.getProperty(ObjectRenderer.class).get()); //ObjectRenderer.class property is FruitRenderer.class for Fruit.class
		assertEquals(FruitRenderer.class, orangeCategory.getProperty(ObjectRenderer.class).get()); //ObjectRenderer.class property is also FruitRenderer.class for Orange.class
		assertFalse(jgum.forClass(Object.class).getProperty(ObjectRenderer.class).isPresent()); //ObjectRenderer.class property has not been set for Object.class
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
	public void testLabels() {
		JGum jgum = new JGum();
		assertEquals(asList(Cat.class, FourLegged.class, HasLegs.class, Furry.class, Animal.class, Object.class, Any.class), jgum.forClass(Cat.class).bottomUpLabels());
	}
	
	@Test
	public void testMultiInheritanceInterfacesFirst() {
		JGum jgum = new JGum();
		TypeCategory<?> animalCategory = jgum.forClass(Animal.class); //type category for Animal.class
		animalCategory.setProperty(ObjectRenderer.class, AnimalRenderer.class); //ObjectRenderer.class property is AnimalRenderer.class for Animal.class
		TypeCategory<?> hasLegsCategory = jgum.forClass(HasLegs.class); //type category for HasLegs.class
		hasLegsCategory.setProperty(ObjectRenderer.class, HasLegsRenderer.class); //ObjectRenderer.class property is HasLegsRenderer.class for HasLegs.class
		TypeCategory<?> catCategory = jgum.forClass(Cat.class); //type category for Cat.class
		assertEquals(HasLegsRenderer.class, catCategory.getProperty(ObjectRenderer.class).get()); //ObjectRenderer.class property is HasLegsRenderer.class for Cat.class
		TypeCategory<?> fishCategory = jgum.forClass(Fish.class); //type category for Fish.class
		assertEquals(AnimalRenderer.class, fishCategory.getProperty(ObjectRenderer.class).get()); //ObjectRenderer.class property is AnimalRenderer.class for Fish.class
	}
	
	@Test
	public void testMultiInheritanceClassesFirst() {
		Function<TypeCategory<?>, List<TypeCategory<?>>> linearizationFunction = 
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.CLASSES_FIRST, InterfaceOrder.DECLARATION, RedundancyCheck.KEEP_LAST);
		JGum jgum = new JGum(linearizationFunction);
		TypeCategory<?> animalCategory = jgum.forClass(Animal.class);
		animalCategory.setProperty(ObjectRenderer.class, AnimalRenderer.class);
		TypeCategory<?> hasLegsCategory = jgum.forClass(HasLegs.class);
		hasLegsCategory.setProperty(ObjectRenderer.class, HasLegsRenderer.class);
		TypeCategory<?> catCategory = jgum.forClass(Cat.class);
		assertEquals(AnimalRenderer.class, catCategory.getProperty(ObjectRenderer.class).get());
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
