package org.drools.brms.server.util;

import org.drools.brms.client.modeldriven.model.ActionAssertFact;
import org.drools.brms.client.modeldriven.model.ActionFieldList;
import org.drools.brms.client.modeldriven.model.ActionFieldValue;
import org.drools.brms.client.modeldriven.model.ActionRetractFact;
import org.drools.brms.client.modeldriven.model.ActionSetField;
import org.drools.brms.client.modeldriven.model.CompositeFactPattern;
import org.drools.brms.client.modeldriven.model.ConnectiveConstraint;
import org.drools.brms.client.modeldriven.model.Constraint;
import org.drools.brms.client.modeldriven.model.DSLSentence;
import org.drools.brms.client.modeldriven.model.DSLSentenceFragment;
import org.drools.brms.client.modeldriven.model.FactPattern;
import org.drools.brms.client.modeldriven.model.RuleAttribute;
import org.drools.brms.client.modeldriven.model.RuleModel;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * This class persists the rule model to XML and back.
 * 
 * This is the "brxml" xml format (Business Rule Language).
 * 
 * @author Michael Neale
 */
public class BRLPersistence {

    private XStream        xt;
    private static final BRLPersistence INSTANCE = new BRLPersistence();

    private BRLPersistence() {
        xt = new XStream(new DomDriver());

        xt.alias( "rule", RuleModel.class );
        xt.alias( "fact", FactPattern.class );
        xt.alias( "retract", ActionRetractFact.class );
        xt.alias( "assert", ActionAssertFact.class );
        xt.alias( "modify", ActionSetField.class );
        xt.alias( "dsl-expression", DSLSentence.class );
        xt.alias( "composite-pattern", CompositeFactPattern.class );
        xt.alias( "attribute", RuleAttribute.class );
        xt.alias( "action-field-list", ActionFieldList.class );
        xt.alias( "action-field-value", ActionFieldValue.class );
        xt.alias( "connective-constraint", ConnectiveConstraint.class );
        xt.alias( "constraint", Constraint.class );
        xt.alias( "sentence-fragment", DSLSentenceFragment.class );
        

    }

    public static BRLPersistence getInstance() {
        return INSTANCE;
    }

    public String toXML(RuleModel model) {
        return xt.toXML( model );
    }

    public RuleModel toModel(String xml) {
        return (RuleModel) xt.fromXML( xml );
    }

}
