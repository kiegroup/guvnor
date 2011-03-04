package org.drools.guvnor.server;

import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.RulesRepository;

import javax.jcr.Session;

import static org.mockito.Mockito.mock;

public class RepositorySetUpper {

    private boolean usingMockRepository = false;
    private RulesRepository repository;

    protected void setUpMock() {
        if (repository != null) {
            throw new IllegalStateException("Repository is already set. Please call this method before any other set up method.");
        }

        usingMockRepository = true;
    }

    public boolean isUsingMockRepository() {
        return usingMockRepository;
    }

    protected RulesRepository getRulesRepository() {
        if (repository == null) {
            createRepository();
        }
        return repository;
    }

    private void createRepository() {
        if (usingMockRepository) {
            createMockRepository();
        } else {
            createRealRepository();
        }
    }

    private void createMockRepository() {
        repository = mock(RulesRepository.class);
    }

    private void createRealRepository() {
        repository = new RulesRepository(getSession());
    }

    protected Session getSession() {
        return TestEnvironmentSessionHelper.getSession(true);
    }

    public void tearDown() {
        repository = null;
    }
}
