package org.jgum.strategy;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.or.ObjectRenderer;
import org.jgum.JGum;
import org.jgum.category.Key;
import org.jgum.category.type.TypeCategory;
import org.jgum.testutil.animalhierarchy.Animal;
import org.jgum.testutil.animalhierarchy.AnimalRenderer;
import org.jgum.testutil.animalhierarchy.Cat;
import org.jgum.testutil.animalhierarchy.HasLegs;
import org.jgum.testutil.animalhierarchy.HasLegsRenderer;
import org.junit.Test;

public class StrategyTutorialTest {

	class DelegationAnimalRenderer implements ObjectRenderer {
		@Override
		public String doRender(Object animal) {
			throw new NoMyResponsibilityException();
		}
	}

	public static final Key OBJECT_RENDERER_KEY = new Key(ObjectRenderer.class);
	
	@Test
	public void noDelegationTest() {
		JGum jgum = new JGum();
		//instantiating categories
		TypeCategory<Animal> animalCategory = jgum.forClass(Animal.class); //type category for Animal
		TypeCategory<HasLegs> hasLegsCategory = jgum.forClass(HasLegs.class); //type category for HasLegs
		TypeCategory<Cat> catCategory = jgum.forClass(Cat.class); //type category for Cat
		
		//setting properties
		animalCategory.setProperty(OBJECT_RENDERER_KEY, new AnimalRenderer()); //ObjectRenderer property is an instance of AnimalRenderer for Animal
		hasLegsCategory.setProperty(OBJECT_RENDERER_KEY, new HasLegsRenderer()); //ObjectRenderer property is an instance of HasLegsRenderer for HasLegs
		
		//testing
		ObjectRenderer renderer = catCategory.getStrategy(ObjectRenderer.class);
		assertEquals("animal", renderer.doRender(new Cat()));
	}
	
	@Test
	public void delegationTest() {
		JGum jgum = new JGum();
		//instantiating categories
		TypeCategory<Animal> animalCategory = jgum.forClass(Animal.class); //type category for Animal
		TypeCategory<HasLegs> hasLegsCategory = jgum.forClass(HasLegs.class); //type category for HasLegs
		TypeCategory<Cat> catCategory = jgum.forClass(Cat.class); //type category for Cat
		
		//setting properties
		animalCategory.setProperty(OBJECT_RENDERER_KEY, new DelegationAnimalRenderer()); //ObjectRenderer property is an instance of DelegationAnimalRenderer for Animal
		hasLegsCategory.setProperty(OBJECT_RENDERER_KEY, new HasLegsRenderer()); //ObjectRenderer property is an instance of HasLegsRenderer for HasLegs
		
		//testing
		ObjectRenderer renderer = catCategory.getStrategy(ObjectRenderer.class);
		assertEquals("has-legs", renderer.doRender(new Cat()));
	}
	
}
