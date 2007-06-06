package org.drools.brms.client.modeldriven;

import java.util.HashMap;
import java.util.Map;

/**
 * This contains some simple mappings between operators, conditional elements and the human readable
 * equivalent.
 * 
 * Yes, I am making the presumption that programmers are not human, 
 * but I think they (we) are cool with that.
 * 
 * @author Michael Neale
 */
public class HumanReadable {
    
    public static Map operatorDisplayMap = new HashMap();
    public static Map ceDisplayMap = new HashMap();
    public static Map actionDisplayMap = new HashMap();
    public static final String[] CONDITIONAL_ELEMENTS = new String[] {"not", "exists", "or"};
    
    static {
        operatorDisplayMap.put( "==", "is equal to" );
        operatorDisplayMap.put( "!=", "is not equal to" );
        operatorDisplayMap.put( "<", "is less than" );
        operatorDisplayMap.put( "<=", "less than or equal to" );
        operatorDisplayMap.put( ">", "greater than" );
        operatorDisplayMap.put( ">=", "greater than or equal to" );
        
        operatorDisplayMap.put( "|| ==", "or equal to" );
        operatorDisplayMap.put( "|| !=", "or not equal to" );
        operatorDisplayMap.put( "&& !=", "and not equal to" );
        operatorDisplayMap.put( "&& >", "and greater than" );
        operatorDisplayMap.put( "&& <", "and less than" );
        operatorDisplayMap.put( "|| >", "or greater than" );
        operatorDisplayMap.put( "|| <", "or less than" );
        
        operatorDisplayMap.put( "|| >=", "or greater than (or equal to)" );
        operatorDisplayMap.put( "|| <=", "or less than (or equal to)" );
        operatorDisplayMap.put( "&& >=", "and greater than (or equal to)" );
        operatorDisplayMap.put( "&& <=", "or less than (or equal to)" );
        operatorDisplayMap.put( "&& contains", "and contains" );
        operatorDisplayMap.put( "|| contains", "or contains" );
        operatorDisplayMap.put( "&& matches", "and matches" );
        operatorDisplayMap.put( "|| matches", "or matches" );
        operatorDisplayMap.put( "|| excludes", "or excludes" );
        operatorDisplayMap.put( "&& excludes", "and excludes" );
    
        
        ceDisplayMap.put( "not", "There is no" );
        ceDisplayMap.put( "exists", "There exists" );
        ceDisplayMap.put( "or", "Any of" );
        
        actionDisplayMap.put( "assert", "Assert" );
        actionDisplayMap.put( "assertLogical", "Logically assert" );
        actionDisplayMap.put( "retract", "Retract" );
        actionDisplayMap.put( "set", "Set" );
        actionDisplayMap.put( "modify", "Modify" );
    
    }
    

    
    
    public static String getOperatorDisplayName(String op) {
        return lookup(op, operatorDisplayMap);
    }
    
    public static String getCEDisplayName(String ce) {
        return lookup( ce, ceDisplayMap );
    }

    private static String lookup(String ce, Map map) {
        if (map.containsKey(ce)) {
            return (String) map.get(ce);
        } else {
            return ce;
        }
    }
    
    public static String getActionDisplayName(String action) {
        return lookup(action, actionDisplayMap);
    }    
    
}
