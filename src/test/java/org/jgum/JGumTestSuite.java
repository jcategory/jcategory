package org.jgum;

import org.jgum.category.named.NamedCategoryTest;
import org.jgum.category.named.NamedCategoryTutorialTest;
import org.jgum.category.type.TypeCategoryTest;
import org.jgum.category.type.TypeCategoryTutorialTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({NamedCategoryTest.class, NamedCategoryTutorialTest.class, TypeCategoryTest.class, TypeCategoryTutorialTest.class})
public class JGumTestSuite {
}
