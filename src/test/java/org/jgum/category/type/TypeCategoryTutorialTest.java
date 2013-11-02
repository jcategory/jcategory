package org.jgum.category.type;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.apache.log4j.or.ObjectRenderer;
import org.jgum.JGum;
import org.jgum.category.type.TypeCategoryRoot.Any;
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
		assertEquals(asList(Cat.class, FourLegged.class, HasLegs.class, Any.class, Furry.class, Animal.class, Object.class), jgum.forClass(Cat.class).bottomUpLabels().toList());
	}
	
	@Test
	public void testMultiInheritanceInterfacesFirst() {
		JGum jgum = new JGum();
		TypeCategory<?> animalCategory = jgum.forClass(Animal.class);
		animalCategory.setProperty(ObjectRenderer.class, AnimalRenderer.class);
		TypeCategory<?> hasLegsCategory = jgum.forClass(HasLegs.class);
		hasLegsCategory.setProperty(ObjectRenderer.class, HasLegsRenderer.class);
		TypeCategory<?> catCategory = jgum.forClass(Cat.class);
		assertEquals(HasLegsRenderer.class, catCategory.getProperty(ObjectRenderer.class).get());
		TypeCategory<?> fishCategory = jgum.forClass(Fish.class);
		assertEquals(AnimalRenderer.class, fishCategory.getProperty(ObjectRenderer.class).get());
	}
	
	@Test
	public void testMultiInheritanceClassesFirst() {
		Function<TypeCategory<?>, Iterable<TypeCategory<?>>> linearizationFunction = 
				new BottomUpTypeTraversalPolicy(SearchStrategy.PRE_ORDER, Priority.CLASSES_FIRST, InterfaceOrder.DECLARATION);
		JGum jgum = new JGum(linearizationFunction);
		TypeCategory<?> animalCategory = jgum.forClass(Animal.class);
		animalCategory.setProperty(ObjectRenderer.class, AnimalRenderer.class);
		TypeCategory<?> hasLegsCategory = jgum.forClass(HasLegs.class);
		hasLegsCategory.setProperty(ObjectRenderer.class, HasLegsRenderer.class);
		TypeCategory<?> catCategory = jgum.forClass(Cat.class);
		assertEquals(AnimalRenderer.class, catCategory.getProperty(ObjectRenderer.class).get());
	}
	
	
	public interface A {}
	public class B implements A {}
	public interface C extends A {}
	public class D extends B implements C {}
	
	@Test
	public void testDiamondInheritance() {
		JGum jgum = new JGum();
		assertEquals(asList(D.class, C.class, A.class, Any.class, B.class, Object.class), jgum.forClass(D.class).bottomUpLabels().toList());
	}
	
}
