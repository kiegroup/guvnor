package org.drools.repo.jcr;

import java.util.List;

import junit.framework.TestCase;

import org.drools.repo.RepositoryServiceFactory;

public class RepositoryServiceImplTest extends TestCase
{
    
    
    public void testCreateNewAndDelete() {
        RepositoryServiceImpl repo = (RepositoryServiceImpl) RepositoryServiceFactory.getRepositoryService();
        repo.createNewRepo();
        
        repo.xxxxeraseAllRuleSetDRL();
        
        repo.xxxxsaveNewRuleset("My Ruleset", "vkahijkhfhdsjkfhdsh");
        
        List rulesets = repo.xxxxfindAllRuleSetDRL();
                
        System.out.println(rulesets.size());
        
        
    }

    
    
    
}
