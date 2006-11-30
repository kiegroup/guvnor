package org.drools.brms.client;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.model.ConnectiveConstraint;
import org.drools.brms.client.modeldriven.model.Constraint;
import org.drools.brms.client.modeldriven.model.FactPattern;
import org.drools.brms.client.modeldriven.model.IPattern;
import org.drools.brms.client.modeldriven.model.RuleModel;
import org.drools.brms.client.modeldriven.ui.RuleModeller;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is the package management feature. 
 * For managing packages (namespaces, imports etc) for rule assets.
 * 
 */
public class Packages extends JBRMSFeature {


    public static ComponentInfo init() {
        return new ComponentInfo( "Packages",
                                  "This is where you configure packages of rules." + "You select rules to belong to packages, and what version they are. A rule can "
                                          + "appear in more then one package, and possibly even different versions of the rule." ) {
            public JBRMSFeature createInstance() {
                return new Packages();
            }

            public Image getImage() {
                return new Image( "images/package.gif" );
            }
        };
    }


    public Packages() {
        VerticalPanel panel = new VerticalPanel();
        
        panel.add( new RuleModeller(getDummySuggestionEngine(), getDummyData() ) );
        
        panel.setSpacing( 8 );
        initWidget( panel );
    }


    private SuggestionCompletionEngine getDummySuggestionEngine() {
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();
        
        com.addFact( "Person", new String[] {"age", "name"}  );
        com.addFact("Vehicle", new String[] {"type", "make"} );
        com.addOperators( "Person", "name", new String[] {"==", "!="});
        com.addOperators( "Person", "age", new String[] {"==", "!=", "<", ">", "<=", ">="});
        com.addOperators( "Vehicle", "age", new String[] {"==", "!=", "<", ">"});
        com.addOperators( "Vehicle", "type", new String[] {"==", "!=", "<", ">"});

        com.addConnectiveOperators( "Person", "name", new String[] {"|=", "!="});

        
        com.addConnectiveOperators( "Vehicle", "make", new String[] {"|="});
        return com;
    }


    private RuleModel getDummyData() {
        RuleModel model = new RuleModel();
        
        model.lhs = new IPattern[2];
        
        FactPattern p1 = new FactPattern();
        FactPattern p2 = new FactPattern();
        
        model.lhs[0] = p1;
        model.lhs[1] = p2;
        
        p1.factType = "Person";
        p1.constraints = new Constraint[2];
        p1.constraints[0] = new Constraint();
        p1.constraints[1] = new Constraint();
        p1.constraints[0].fieldName = "age";
        p1.constraints[0].operator = "<";
        p1.constraints[0].value = "42";

        p1.constraints[1].fieldName = "name";
        p1.constraints[1].operator = "==";
        p1.constraints[1].value = "Bob";
        p1.constraints[1].fieldBinding = "n";
        p1.constraints[1].connectives = new ConnectiveConstraint[2];
        p1.constraints[1].connectives[0] = new ConnectiveConstraint("|=", "Michael");
        p1.constraints[1].connectives[1] = new ConnectiveConstraint("|=", "Mark");

        
        p2.factType = "Vehicle";
        p2.boundName = "car1";
        p2.constraints = new Constraint[1];
        p2.constraints[0] = new Constraint();
        p2.constraints[0].fieldName = "type";
        p2.constraints[0].operator = "!=";
        
        
        
        return model;
        
    }


    public void onShow() {
    }
}
