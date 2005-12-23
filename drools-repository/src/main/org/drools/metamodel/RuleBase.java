package org.drools.metamodel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A rulebase view of the repository.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class RuleBase extends Asset
{
    
    protected Collection ruleSets;
    protected Collection ruleSetFiles;

    public RuleBase(String name) {
        super.name = name;
        ruleSets = new ArrayList();
        ruleSetFiles = new ArrayList();
    }
    
    public void addRuleSet(RuleSet rs) {
        ruleSets.add(rs);
    }
    
    public void addRuleSetFile(RuleSetFile rs) {
        ruleSetFiles.add(rs);
    }
    
    
    
    
    
    

}
