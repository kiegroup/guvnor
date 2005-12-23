package org.drools.repo;

import javax.jcr.RepositoryException;
import javax.naming.NamingException;

import org.drools.repo.jcr.JCRSessionUtil;
import org.drools.repo.jcr.RepositoryServiceImpl;

public class RepositoryServiceFactory 
{
    
    public static RepositoryService getRepositoryService() {
        JCRSessionUtil factory = new JCRSessionUtil();
        RepositoryService service = null; 
        try
        {
            service = new RepositoryServiceImpl(factory.getSession());
        }
        catch ( NamingException e )
        {
            throw new RuleRepositoryException(e);
        }
        catch ( RepositoryException e )
        {
            throw new RuleRepositoryException(e);
        }
        return service;
    }
    

}
