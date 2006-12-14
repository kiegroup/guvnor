package org.drools.brms.client;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.model.ActionAssertFact;
import org.drools.brms.client.modeldriven.model.ActionFieldValue;
import org.drools.brms.client.modeldriven.model.ActionRetractFact;
import org.drools.brms.client.modeldriven.model.ActionSetField;
import org.drools.brms.client.modeldriven.model.CompositeFactPattern;
import org.drools.brms.client.modeldriven.model.Constraint;
import org.drools.brms.client.modeldriven.model.DSLSentence;
import org.drools.brms.client.modeldriven.model.DSLSentenceFragment;
import org.drools.brms.client.modeldriven.model.FactPattern;
import org.drools.brms.client.modeldriven.model.IAction;
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
        
        DSLSentence sen = new DSLSentence();
        sen.elements = new DSLSentenceFragment[2];
        sen.elements[0] = new DSLSentenceFragment("This is a dsl expression", false);
        sen.elements[1] = new DSLSentenceFragment("(something)", true);
        com.addDSLCondition( sen );

        
        sen = new DSLSentence();
        sen.elements = new DSLSentenceFragment[3];
        sen.elements[0] = new DSLSentenceFragment("Send an email to [", false);
        sen.elements[1] = new DSLSentenceFragment("(someone)", true);
        sen.elements[2] = new DSLSentenceFragment("]", false);
        com.addDSLAction( sen );
        
        sen = new DSLSentence();
        sen.elements = new DSLSentenceFragment[1];
        sen.elements[0] = new DSLSentenceFragment("do nothing", false);        
        com.addDSLAction( sen );
        
        
        
        return com;
    }


    private RuleModel getDummyData() {
        RuleModel model = new RuleModel();
        
        model.lhs = new IPattern[3];
        
        FactPattern p1 = new FactPattern();
        FactPattern p2 = new FactPattern();
        CompositeFactPattern p3 = new CompositeFactPattern();
        
        
        model.lhs[0] = p1;
        model.lhs[1] = p2;
        model.lhs[2] = p3;
        
        DSLSentence dsl = new DSLSentence();
        dsl.elements = new DSLSentenceFragment[2];
        dsl.elements[0] = new DSLSentenceFragment("There is a Storm alert of type", false);
        dsl.elements[1] = new DSLSentenceFragment("(code here)", true);
        
        model.addLhsItem( dsl );
        
        dsl = new DSLSentence();
        dsl.elements = new DSLSentenceFragment[2];
        dsl.elements[0] = new DSLSentenceFragment("- severity rating is not more than", false);
        dsl.elements[1] = new DSLSentenceFragment("(code here)", true);
        
        model.addLhsItem( dsl );
            
        
        
        
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
  
        
        
        p2.factType = "Vehicle";
        p2.boundName = "car1";
        p2.constraints = new Constraint[1];
        p2.constraints[0] = new Constraint();
        p2.constraints[0].fieldName = "type";
        p2.constraints[0].operator = "!=";
        
        p3.type = "not";
        p3.patterns = new FactPattern[1];
        FactPattern i1 = new FactPattern("Vehicle");
        i1.constraints = new Constraint[1];
        i1.constraints[0] = new Constraint();
        i1.constraints[0].fieldName = "type";
        i1.constraints[0].operator = "==";
        
        p3.patterns[0] = i1;
        
        ActionSetField set = new ActionSetField();
        set.variable = "car1";
        set.fieldValues = new ActionFieldValue[1];
        set.fieldValues[0] = new ActionFieldValue();
        set.fieldValues[0].field = "type";
        
        ActionAssertFact fact = new ActionAssertFact();
        fact.factType = "Person";
        fact.fieldValues = new ActionFieldValue[2];
        fact.fieldValues[0] = new ActionFieldValue("name", "Mike");
        fact.fieldValues[1] = new ActionFieldValue("age", "42");
        
        ActionRetractFact retract = new ActionRetractFact("car1");
        
        model.rhs = new IAction[3];
        model.rhs[0] = set;
        model.rhs[1] = fact;
        model.rhs[2] = retract;
        
        return model;
        
    }


    public void onShow() {
    }
}
