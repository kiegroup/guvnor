package org.drools.repository;

import javax.jcr.Repository;
import javax.jcr.Session;

/**
 * This interface is required so different JCR implementations can provide their own configuration mechanism.
 * 
 * @author Michael Neale
 *
 */
public interface JCRRepositoryConfigurator {

    /** 
     * @return a new Repository instance. 
     * There should only be one instance of this in an application.
     * Generally, one repository (which may be bineded to JNDI) can spawn multiple sessions
     * for each user as needed.
     * Typically this would be created on application startup.
     * @param repositoryRootDirectory The directory where the data is stored. If empty, the repository will be generated 
     * there the first time it is used. If it is null, then a default location will be used (it won't fail).
     */
    public abstract Repository getJCRRepository(String repositoryRootDirectory);

    /**
     * Attempts to setup the repository.  If the work that it tries to do has already been done, it 
     * will return without modifying the repository.
     * This will register any node types, and setup bootstrap nodes as needed.
     * This will not erase any data.
     * 
     * @throws RulesRepositoryException     
     */
    public abstract void setupRulesRepository(Session session) throws RulesRepositoryException;

}