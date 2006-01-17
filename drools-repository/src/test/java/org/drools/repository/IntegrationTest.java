package org.drools.repository;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * This integration test aims to do a full suite of scenarios.
 * Including concurrent editing of rules, simulating multiple rules in parallel.
 * 
 * Note that I am using stateful repository instances as it makes it easier to test.
 * I can control when the session is closed etc in a single threaded test run.
 * 
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class IntegrationTest extends TestCase {

    
    
    /**
     * This will all execute as one JUnit test. 
     * Any failure will cause the test to stop, but this is not a unit test,
     * so that is acceptable.
     */
    public void testBootstrap() {
        runVersioningTests();
//        runAttachmentTests();
//        runConcurrentTests();
//        runLocalPersistTests();
        
    }


    /**
     * These tests show how it all hangs together.
     * You can see versioning in action, save history etc.
     */
    private void runVersioningTests() {
        RepositoryManager repo = RepositoryFactory.getStatefulRepository();
        
        // In the beginning there was a rule
        RuleDef rule = new RuleDef("Integration rule 1", "rule { when bob likes cheese then print 'yeah' }");
        rule.setDocumentation("This is an initial rule");
        rule.addTag("CHEESE RULE");
        rule.setMetaData(new MetaData("a rule", "michael", "cheese", "something", "drl"));
        repo.save(rule);
        
        //now lets load that rule back
        List list = repo.findRulesByTag("CHEESE RULE");
        assertEquals(1, list.size());
        rule = (RuleDef) list.get(0);
        assertEquals("This is an initial rule", rule.getDocumentation());

        
        // lets create a ruleset
        MetaData meta = new MetaData();
        meta.setContributor("Michael");
        meta.setDescription("A test ruleset");
        
        RuleSetDef ruleSet = new RuleSetDef("Integration ruleset 1", meta);
        RuleSetVersionInfo info = ruleSet.getVersionInfoWorking();
        info.setStatus("draft");
        
        //time to save it
        repo.save(ruleSet);
        
        //now lets finish this session
        repo.close();
        
        //check we can load it up
        repo = RepositoryFactory.getStatefulRepository();
        assertTrue(repo.listRuleSets().size() > 0);
        ruleSet = repo.loadRuleSet("Integration ruleset 1", 1);
        assertEquals("draft", ruleSet.getVersionInfoWorking().getStatus());
        
        //now lets work "disconnected" for a while
        repo.close();
        
        
        //lets add the rule to it that we loaded before, and some other goodies
        ruleSet.addRule(rule);
        ruleSet.addRule(new RuleDef("Integration rule 2", "Some content"));
        ruleSet.addFunction(new FunctionDef("this is a function", "blah blah blah"));
        ruleSet.addApplicationData(new ApplicationDataDef("blah", "String"));
        ruleSet.addImport(new ImportDef("java.lang.String"));
        
        repo = RepositoryFactory.getStatefulRepository();
        repo.save(ruleSet);        
        repo.close();

        //now lets load it again, and work on it disconnected
        repo = RepositoryFactory.getStatefulRepository();
        ruleSet = repo.loadRuleSet("Integration ruleset 1", 1);
        repo.close();
        
        //check its OK first
        assertEquals("this is a function", ((FunctionDef) ruleSet.getFunctions().iterator().next()).getFunctionContent());
        assertEquals(2, ruleSet.getRules().size());
        
        //now lets go and modify some rules
        for ( Iterator iter = ruleSet.getRules().iterator(); iter.hasNext(); ) {
            RuleDef myRule = (RuleDef) iter.next();
            myRule.setContent("CHANGED RULE TEXT");   
            //this should cause us to have some rule history saved.
            //this has nothing to do with ruleset versioning.
        } 
        
        //connect and save
        repo = RepositoryFactory.getStatefulRepository();
        repo.save(ruleSet);
        ruleSet = repo.loadRuleSet("Integration ruleset 1", 1);

        
        //just to prove the save history, lets check the history of a rule
        rule = ruleSet.findRuleByName("Integration rule 2");
        List history = repo.listRuleSaveHistory(rule);
        assertEquals(1, history.size());
        RuleDef historicalRule = (RuleDef) history.get(0);
        String oldContent = historicalRule.getContent();        
        assertTrue(! oldContent.equals(rule.getContent()));

        //lets also check that the correct number of rules are on the ruleset, history is seperate
        assertEquals(2, ruleSet.getRules().size());
        
        //now lets create a new major version of the ruleset.
        ruleSet.createNewVersion("New version", "pending");
        repo.save(ruleSet);
        repo.close();
        
        //now we will have 2 versions of the ruleset, lets load up the latest.
        repo = RepositoryFactory.getStatefulRepository();
        ruleSet = repo.loadRuleSet("Integration ruleset 1", 2);
        assertEquals(2, ruleSet.getVersionHistory().size());
        assertEquals("New version", ruleSet.getVersionInfoWorking().getVersionComment());
        assertEquals("pending", ruleSet.getVersionInfoWorking().getStatus());        
        
        
       //lets add a rule to it (making 3 rules in total)
        ruleSet.addRule(new RuleDef("Integration rule 3", "content"));
         
        repo.save(ruleSet);
        
        //lets load up the old version, check that there is still only 2 rules
        ruleSet = repo.loadRuleSet("Integration ruleset 1", 1);
        assertEquals(2, ruleSet.getRules().size());        
        assertEquals("draft", ruleSet.getVersionInfoWorking().getStatus());        
        
        //ooh look, the new one has 3, and they are all different rules !
        assertEquals(3, repo.loadRuleSet("Integration ruleset 1", 2).getRules().size());
        
        repo.close();
    }
    
    
    /** 
     * Lets create 3 rulesets, each with 500 rules.
     * Each RuleSet will have several functions, applicationdata, imports and so on.
     * Rules will also have tags, a
     *  */
    public void initialDataSetup() {
        create500CheeseRules();
        
        
    }


    private void create500CheeseRules() {
        RepositoryManager repo = RepositoryFactory.getStatefulRepository();
        
        //lets build some rules, stand alone
        for (int i = 0; i < 500; i ++) {
            RuleDef rule = new RuleDef("cheese-rule-" + i, "The rule body here " + i);
            rule.addTag("FIRST").addTag("NUM" + i);
            repo.save(rule);
        }
        
        repo.close();
    }
    
}
