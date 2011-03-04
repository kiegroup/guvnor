package org.drools.guvnor.server;

import org.jboss.seam.mock.AbstractSeamTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class GuvnorTest extends AbstractSeamTest {

    private RepositorySetUpper repositorySetUpper = new RepositorySetUpper();

    @Before
    public void begin() {
        try {
            super.startSeam();
            super.setupClass();
        } catch (Exception e) {
            e.printStackTrace(); // TODO: Generated code -Rikkola-
        }

        super.begin();
    }

    @After
    public void end() {
        super.end();

        try {
            super.cleanupClass();
            super.stopSeam();
        } catch (Exception e) {
            e.printStackTrace(); // TODO: Generated code -Rikkola-
        }
    }

    protected RepositorySetUpper setUpRulesRepository() {
        return repositorySetUpper;
    }
}
