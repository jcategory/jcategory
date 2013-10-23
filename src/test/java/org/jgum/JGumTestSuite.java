package org.jgum;

import org.jgum.classmodel.ClassPropertiesTest;
import org.jgum.packagemodel.PackagePropertiesTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({PackagePropertiesTest.class, ClassPropertiesTest.class})
public class JGumTestSuite {
}
