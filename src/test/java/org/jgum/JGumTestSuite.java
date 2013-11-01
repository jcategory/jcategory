package org.jgum;

import org.jgum.category.named.NamedCategoryTest;
import org.jgum.category.type.TypeCategoryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({NamedCategoryTest.class, TypeCategoryTest.class})
public class JGumTestSuite {
}
