package org.jgum;

import org.jgum.category.AdHocCategorizationTutorialTest;
import org.jgum.category.named.NamedCategorizationTest;
import org.jgum.category.named.NamedCategorizationTutorialTest;
import org.jgum.category.type.TypeCategorizationTest;
import org.jgum.category.type.TypeCategorizationTutorialTest;
import org.jgum.strategy.StrategyTest;
import org.jgum.strategy.StrategyTutorialTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AdHocCategorizationTutorialTest.class, NamedCategorizationTest.class, NamedCategorizationTutorialTest.class, TypeCategorizationTest.class, TypeCategorizationTutorialTest.class, 
	StrategyTest.class, StrategyTutorialTest.class})
public class JGumTestSuite {
}
