package org.drools.repository.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.drools.repository.test");
        //$JUnit-BEGIN$
        suite.addTestSuite(StateItemTestCase.class);
        suite.addTestSuite(RulePackageItemTestCase.class);
        suite.addTestSuite(DslItemTestCase.class);
        suite.addTestSuite(RuleItemTestCase.class);
        suite.addTestSuite(TagItemTestCase.class);
        suite.addTestSuite(RulesRepositoryTestCase.class);
        //$JUnit-END$
        return suite;
    }

}
