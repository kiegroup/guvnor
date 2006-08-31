package org.drools.repository.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.UnsupportedRepositoryOperationException;

import org.apache.jackrabbit.core.TransientRepository;
import org.drools.repository.RuleItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;

import junit.framework.TestCase;

public class ScalabilityTest extends TestCase {

    private static final int NUM = 5000;
    
    
    public void xxtestRun() throws Exception {
        
        RulesRepository repo = new RulesRepository(false);   

        
        long start = System.currentTimeMillis();
        setupData( repo );
        System.out.println("time to add, version and tag 5000: " + (System.currentTimeMillis() - start));
        List list = listACat(repo);
        System.out.println("list size is: " + list.size());
        
        start = System.currentTimeMillis();
        RuleItem item = (RuleItem) list.get( 0 );
        item.updateDescription( "this is a description" );
        System.out.println("time to update and version: " + (System.currentTimeMillis() - start));        
    }

    private List listACat(RulesRepository repo) {
        long start = System.currentTimeMillis();
        List results = repo.findRulesByTag( "HR/CAT_1" );
        System.out.println("Time for listing a cat: " + (System.currentTimeMillis() - start));
        return results;
    }

    private void setupData(RulesRepository repo) throws Exception {
        

        int count = 1;
        
        String prefix = "HR/";
        String cat = prefix + "CAT_1";
        for (int i=1; i <= NUM; i++ ) {

            if (i > 2500) {
                prefix = "FINANCE/";
            }
            
            if (count == 100) {
                count = 1;
                cat = prefix + "CAT_" + i;
                System.err.println("changing CAT");
                System.gc();
                
            } else {
                count++;
            }            
            
            String ruleName = "rule_" + i + "_" + System.currentTimeMillis();
            System.out.println("ADDING rule: " + ruleName);
                        
            
            RuleItem item = repo.addRule( ruleName, "Foo(bar == " + i + ")", "panic(" + i + ");" );
            //item.addCategory( cat );

        }
        

    }
    
    static void hacked() throws Exception {
        Repository repository = new TransientRepository();
        Session session = repository.login(
                                   new SimpleCredentials("username", "password".toCharArray()));
        
    }

    
    
}
