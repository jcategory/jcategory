package org.jgum;

import org.jgum.category.AdHocCategorizationTutorialTest;
import org.jgum.category.named.NamedCategorizationTest;
import org.jgum.category.named.NamedCategorizationTutorialTest;
import org.jgum.category.type.TypeCategorizationTest;
import org.jgum.category.type.TypeCategorizationTutorialTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AdHocCategorizationTutorialTest.class, NamedCategorizationTest.class, NamedCategorizationTutorialTest.class, TypeCategorizationTest.class, TypeCategorizationTutorialTest.class})
public class JGumTestSuite {
}
