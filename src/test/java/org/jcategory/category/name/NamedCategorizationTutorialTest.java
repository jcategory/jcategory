package org.jcategory.category.name;

import static org.jcategory.category.Key.key;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jcategory.JCategory;
import org.jcategory.category.Key;
import org.junit.Test;

public class NamedCategorizationTutorialTest {

	@Test
	public void testNamedCategoryInheritance() {
		final Key LEVEL = key("level"); //the property identifier
		JCategory context = new JCategory();
		NameCategory parent = context.forPackage(JCategory.class.getPackage()); //named category for "org.jum"
		NameCategory child = context.forName(JCategory.class.getName()); //named category for "org.jum.JCategory"
		parent.setProperty(LEVEL, "WARN"); //"level" property set to "WARN" for "org.jum"
		assertEquals("WARN", parent.getProperty(LEVEL).get()); //"level" property is "WARN" for "org.jum"
		assertEquals("WARN", child.getProperty(LEVEL).get()); //"level" property is also "WARN" for "org.jcategory.JCategory"
		assertFalse(context.forName("org").getProperty(LEVEL).isPresent()); //"level" property has not been set for "org"
	}
	
}
