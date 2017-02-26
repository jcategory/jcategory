package org.jcategory.strategy;

import static org.jcategory.category.Key.key;
import static org.junit.Assert.assertEquals;

import org.apache.log4j.or.ObjectRenderer;
import org.jcategory.JCategory;
import org.jcategory.category.Key;
import org.jcategory.category.type.TypeCategory;
import org.jcategory.testutil.animalhierarchy.Animal;
import org.jcategory.testutil.animalhierarchy.AnimalRenderer;
import org.jcategory.testutil.animalhierarchy.Cat;
import org.jcategory.testutil.animalhierarchy.HasLegs;
import org.jcategory.testutil.animalhierarchy.HasLegsRenderer;
import org.junit.Test;

public class StrategyTutorialTest {

	class DelegationAnimalRenderer implements ObjectRenderer {
		@Override
		public String doRender(Object animal) {
			throw new NoMyResponsibilityException();
		}
	}

	public static final Key OBJECT_RENDERER_KEY = key(ObjectRenderer.class);
	
	@Test
	public void noDelegationTest() {
		JCategory context = new JCategory();
		//instantiating categories
		TypeCategory<Animal> animalCategory = context.forClass(Animal.class); //type category for Animal
		TypeCategory<HasLegs> hasLegsCategory = context.forClass(HasLegs.class); //type category for HasLegs
		TypeCategory<Cat> catCategory = context.forClass(Cat.class); //type category for Cat
		
		//setting properties
		animalCategory.setProperty(OBJECT_RENDERER_KEY, new AnimalRenderer()); //ObjectRenderer property is an instance of AnimalRenderer for Animal
		hasLegsCategory.setProperty(OBJECT_RENDERER_KEY, new HasLegsRenderer()); //ObjectRenderer property is an instance of HasLegsRenderer for HasLegs
		
		//testing
		ObjectRenderer renderer = catCategory.getStrategy(ObjectRenderer.class);
		assertEquals("animal", renderer.doRender(new Cat()));
	}
	
	@Test
	public void delegationTest() {
		JCategory context = new JCategory();
		//instantiating categories
		TypeCategory<Animal> animalCategory = context.forClass(Animal.class); //type category for Animal
		TypeCategory<HasLegs> hasLegsCategory = context.forClass(HasLegs.class); //type category for HasLegs
		TypeCategory<Cat> catCategory = context.forClass(Cat.class); //type category for Cat
		
		//setting properties
		animalCategory.setProperty(OBJECT_RENDERER_KEY, new DelegationAnimalRenderer()); //ObjectRenderer property is an instance of DelegationAnimalRenderer for Animal
		hasLegsCategory.setProperty(OBJECT_RENDERER_KEY, new HasLegsRenderer()); //ObjectRenderer property is an instance of HasLegsRenderer for HasLegs
		
		//testing
		ObjectRenderer renderer = catCategory.getStrategy(ObjectRenderer.class);
		assertEquals("has-legs", renderer.doRender(new Cat()));
	}
	
}
