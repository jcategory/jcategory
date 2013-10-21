package org.jgum;

import org.jgum.classmodel.ClassHierarchyPropertiesTest;
import org.jgum.packagemodel.PackageHierarchyPropertiesTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({PackageHierarchyPropertiesTest.class, ClassHierarchyPropertiesTest.class})
public class JGumTestSuite {
}
