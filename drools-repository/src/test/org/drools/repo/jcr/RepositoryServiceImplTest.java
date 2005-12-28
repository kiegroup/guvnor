package org.drools.repo.jcr;

import junit.framework.TestCase;

import org.drools.metamodel.RuleDefinition;
import org.drools.repo.RepositoryService;
import org.drools.repo.RepositoryServiceFactory;

public class RepositoryServiceImplTest extends TestCase
{
    
    private static RepositoryServiceImpl instance;
    private long startTime;
    




    public void testAddRemoveNewRuleAndRuleSet() {
        
        RuleDefinition rule = new RuleDefinition("nami", "rule blah {}");                                
                
        getRepo().addNewRule("rb", "rs", rule);
        getRepo().save();
        RuleDefinition rule2 = getRepo().retrieveRule("rb", "rs", "nami");
        getRepo().removeRule("rb", "rs", rule.getRuleName());        
        getRepo().removeRuleSet("rb", "rs");
        
        assertEquals(rule.getFragment(), rule2.getFragment());       
        getRepo().save();
        
    }
    
    public void testLargeNumberOfRules() {
        startTimer();
        //setup 5000 rules in ruleset 1
        RepositoryService repo = getRepo();
        for (int i = 0; i < 5000; i ++) {            
            RuleDefinition rule = new RuleDefinition("rule" + i, "MyRule_" + i);
            repo.addNewRule("rulebase", "ruleset1", rule);       
            if (i % 10 == 0) repo.save();
        }
        System.out.println("Time for first 5000: " + lapTime());
        repo.save();

        //ruleset2
        for (int i = 0; i < 5000; i ++) {
            RuleDefinition rule = new RuleDefinition("rule" + i, "MyRule_" + i);
            repo.addNewRule("rulebase", "ruleset2", rule);
            if (i % 10 == 0) repo.save();
        }
        System.out.println("Time for second 5000: " + lapTime());
        
        repo.save();
        //rulebase2
        for (int i = 0; i < 5000; i ++) {
            RuleDefinition rule = new RuleDefinition("rule" + i, "MyRule_" + i);
            repo.addNewRule("rulebase2", "ruleset", rule);
            if (i % 10 == 0) repo.save();
        }
        System.out.println("Time for third 5000: " + lapTime());        
        repo.save();
        
        startTimer();
        
        RuleDefinition rule1 = repo.retrieveRule("rulebase", "ruleset1", "rule1000");
        System.out.println("Retrieve 1 :" + lapTime());
        RuleDefinition rule2 = repo.retrieveRule("rulebase", "ruleset2", "rule1000");
        System.out.println("Retrieve 2 :" + lapTime());
        RuleDefinition rule3 = repo.retrieveRule("rulebase2", "ruleset", "rule1000");
        System.out.println("Retrieve 3 :" + lapTime());
        
        repo.removeRuleSet("rulebase", "ruleset1");
        repo.removeRuleSet("rulebase", "ruleset2");
        repo.removeRuleSet("rulebase2", "ruleset");        
        repo.save();
        System.out.println("Remove all:" + lapTime());        
        
        assertEquals("MyRule_1000", rule1.getFragment());
        assertEquals("MyRule_1000", rule2.getFragment());
        assertEquals("MyRule_1000", rule3.getFragment());
        
        
    }
    
    private long lapTime(){
        long elapsed = System.currentTimeMillis() - startTime;
        startTime = System.currentTimeMillis();
        return elapsed;
    }

    private void startTimer(){
        this.startTime = System.currentTimeMillis();
        
    }

    public void testCalcPath() {
        String result = getRepo().calcPath(new String[] {"one", "two", "three"});
        assertEquals("drools:one/drools:two/drools:three", result);        
    }
    
    private RepositoryServiceImpl getRepo() {
        if (instance == null) { 
            instance = (RepositoryServiceImpl) RepositoryServiceFactory.getRepositoryService( );
            instance.createNewRepo( );
            instance.save();
        }
        return instance;
    }    

}
