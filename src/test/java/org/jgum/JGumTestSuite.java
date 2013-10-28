package org.jgum;

import org.jgum.category.name.NameCategoryTest;
import org.jgum.category.type.TypeCategoryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({NameCategoryTest.class, TypeCategoryTest.class})
public class JGumTestSuite {
}
