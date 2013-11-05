package org.jgum.category.named;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jgum.JGum;
import org.junit.Test;

public class NamedCategorizationTutorialTest {

	@Test
	public void testNamedCategoryInheritance() {
		final String LEVEL = "level";
		JGum jgum = new JGum();
		NamedCategory parent = jgum.forPackage(JGum.class.getPackage()); //named category for "org.jum"
		NamedCategory child = jgum.forName(JGum.class.getName()); //named category for "org.jum.JGum"
		parent.setProperty(LEVEL, "WARN"); //"level" property set to "WARN" for "org.jum"
		assertEquals("WARN", parent.getProperty(LEVEL).get()); //"level" property is "WARN" for "org.jum"
		assertEquals("WARN", child.getProperty(LEVEL).get()); //"level" property is also "WARN" for "org.jum.jGum"
		assertFalse(jgum.forName("org").getProperty(LEVEL).isPresent()); //"level" property has not been set for "org"
	}
	
}
