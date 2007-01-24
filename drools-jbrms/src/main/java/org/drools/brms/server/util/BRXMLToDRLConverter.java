package org.drools.brms.server.util;

import org.drools.brms.client.modeldriven.brxml.CompositeFactPattern;
import org.drools.brms.client.modeldriven.brxml.DSLSentence;
import org.drools.brms.client.modeldriven.brxml.FactPattern;
import org.drools.brms.client.modeldriven.brxml.IPattern;
import org.drools.brms.client.modeldriven.brxml.RuleAttribute;
import org.drools.brms.client.modeldriven.brxml.RuleModel;

/** 
 * This class will convert BRXML to DRL.
 * It will work off the RuleModel object graph, primarily.
 * 
 * @author Michael Neale
 * @written-when-jetlagged-in-hong-kong-oh-how-I-hate-jetlag
 */
public class BRXMLToDRLConverter {
    
    public String toDRL(RuleModel model,
                        String ruleName) {
        StringBuffer buf = new StringBuffer();
        buf.append( "rule \"" + ruleName + "\"" );
        
        render( model.attributes, buf );
        buf.append( "\n\twhen" );
        render( model.lhs, buf );
        
        return buf.toString();
    }

    private void render(IPattern[] lhs, StringBuffer buf) {
        for ( int i = 0; i < lhs.length; i++ ) {
            IPattern cond = lhs[i];
            if (cond instanceof DSLSentence) {
                render((DSLSentence) cond, buf);
            } else if (cond instanceof FactPattern) {
                render((FactPattern) cond, buf);
            } else if (cond instanceof CompositeFactPattern) {
                render((CompositeFactPattern) cond, buf);
            }
        }
        
    }

    private void render(CompositeFactPattern pattern,
                        StringBuffer buf) {
        // TODO Auto-generated method stub
        
    }

    private void render(FactPattern pattern,
                        StringBuffer buf) {
        buf.append( "\n\t\t" );
        
        if (!nil(pattern.boundName)) {
            buf.append( pattern.boundName );
            buf.append( " : " );
        }
        
        buf.append( pattern.factType );
        buf.append( '(' );
        
        
    }

    private boolean nil(String s) {
        if (s== null) return false;
        if (s.equals( "" )) {
            return false;
        }
        return true;
    }

    private void render(DSLSentence sentence,
                        StringBuffer buf) {
        // TODO Auto-generated method stub
        
    }

    private void render(RuleAttribute[] attributes, StringBuffer buf) {
        
        for ( int i = 0; i < attributes.length; i++ ) {
            buf.append( "\n\t" );
            RuleAttribute at = attributes[i];
            String name = at.attributeName;
            buf.append( name );
            buf.append( ' ' );
            
            if (name.equals( "agenda-group" ) 
                    || name.equals( "activation-group" )
                    || name.equals( "date-effective" )
                    || name.equals( "date-expires" )) {
                    buf.append( '"' );
                    buf.append( at.value );
                    buf.append( '"' );
            } else if (name.equals( "no-loop" )) {
                buf.append( "true" );
            } else {
                buf.append( at.value );
            }
        }
        
    }

}
