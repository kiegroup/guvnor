/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.guided.rule.backend.server.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.kie.guvnor.datamodel.model.DSLSentence;
import org.kie.guvnor.guided.rule.backend.server.util.upgrade.RuleModelUpgradeHelper1;
import org.kie.guvnor.guided.rule.backend.server.util.upgrade.RuleModelUpgradeHelper2;
import org.kie.guvnor.guided.rule.backend.server.util.upgrade.RuleModelUpgradeHelper3;
import org.kie.guvnor.guided.rule.model.ActionFieldValue;
import org.kie.guvnor.guided.rule.model.ActionGlobalCollectionAdd;
import org.kie.guvnor.guided.rule.model.ActionInsertFact;
import org.kie.guvnor.guided.rule.model.ActionInsertLogicalFact;
import org.kie.guvnor.guided.rule.model.ActionRetractFact;
import org.kie.guvnor.guided.rule.model.ActionSetField;
import org.kie.guvnor.guided.rule.model.ActionUpdateField;
import org.kie.guvnor.guided.rule.model.CompositeFactPattern;
import org.kie.guvnor.guided.rule.model.CompositeFieldConstraint;
import org.kie.guvnor.guided.rule.model.ConnectiveConstraint;
import org.kie.guvnor.guided.rule.model.ExpressionCollection;
import org.kie.guvnor.guided.rule.model.ExpressionCollectionIndex;
import org.kie.guvnor.guided.rule.model.ExpressionField;
import org.kie.guvnor.guided.rule.model.ExpressionFormLine;
import org.kie.guvnor.guided.rule.model.ExpressionGlobalVariable;
import org.kie.guvnor.guided.rule.model.ExpressionMethod;
import org.kie.guvnor.guided.rule.model.ExpressionText;
import org.kie.guvnor.guided.rule.model.ExpressionVariable;
import org.kie.guvnor.guided.rule.model.FactPattern;
import org.kie.guvnor.guided.rule.model.FreeFormLine;
import org.kie.guvnor.guided.rule.model.FromAccumulateCompositeFactPattern;
import org.kie.guvnor.guided.rule.model.FromCollectCompositeFactPattern;
import org.kie.guvnor.guided.rule.model.FromCompositeFactPattern;
import org.kie.guvnor.guided.rule.model.RuleAttribute;
import org.kie.guvnor.guided.rule.model.RuleMetadata;
import org.kie.guvnor.guided.rule.model.RuleModel;
import org.kie.guvnor.guided.rule.model.SingleFieldConstraint;

import java.util.List;

/**
 * This class persists the rule model to XML and back. This is the 'brl' xml
 * format (Business Rule Language).
 */
public class BRXMLPersistence
        implements BRLPersistence {

    protected XStream xt;

    private static final RuleModelUpgradeHelper1 upgrader1 = new RuleModelUpgradeHelper1();
    private static final RuleModelUpgradeHelper2 upgrader2 = new RuleModelUpgradeHelper2();
    private static final RuleModelUpgradeHelper3 upgrader3 = new RuleModelUpgradeHelper3();

    private static final BRLPersistence INSTANCE = new BRXMLPersistence();

    protected BRXMLPersistence() {
        this.xt = new XStream( new DomDriver() );

        this.xt.alias( "rule",
                       RuleModel.class );
        this.xt.alias( "fact",
                       FactPattern.class );
        this.xt.alias( "retract",
                       ActionRetractFact.class );
        this.xt.alias( "assert",
                       ActionInsertFact.class );
        this.xt.alias( "modify",
                       ActionUpdateField.class );
        this.xt.alias( "setField",
                       ActionSetField.class );
        this.xt.alias( "dslSentence",
                       DSLSentence.class );
        this.xt.alias( "compositePattern",
                       CompositeFactPattern.class );
        this.xt.alias( "fromCompositePattern",
                       FromCompositeFactPattern.class );
        this.xt.alias( "fromCollectCompositePattern",
                       FromCollectCompositeFactPattern.class );
        this.xt.alias( "fromAccumulateCompositePattern",
                       FromAccumulateCompositeFactPattern.class );
        this.xt.alias( "metadata",
                       RuleMetadata.class );
        this.xt.alias( "attribute",
                       RuleAttribute.class );

        this.xt.alias( "fieldValue",
                       ActionFieldValue.class );
        this.xt.alias( "connectiveConstraint",
                       ConnectiveConstraint.class );
        this.xt.alias( "fieldConstraint",
                       SingleFieldConstraint.class );

        this.xt.alias( "compositeConstraint",
                       CompositeFieldConstraint.class );

        this.xt.alias( "assertLogical",
                       ActionInsertLogicalFact.class );
        this.xt.alias( "freeForm",
                       FreeFormLine.class );

        this.xt.alias( "addToGlobal",
                       ActionGlobalCollectionAdd.class );
        //Begin ExpressionFormLine
        this.xt.alias( "expression",
                       ExpressionFormLine.class );

        this.xt.alias( "field",
                       ExpressionField.class );

        this.xt.alias( "method",
                       ExpressionMethod.class );

        this.xt.alias( "collection",
                       ExpressionCollection.class );

        this.xt.alias( "collectionIndex",
                       ExpressionCollectionIndex.class );

        this.xt.alias( "text",
                       ExpressionText.class );

        this.xt.alias( "global",
                       ExpressionGlobalVariable.class );

        this.xt.alias( "variable",
                       ExpressionVariable.class );
        //end ExpressionFormLine

        //See https://issues.jboss.org/browse/GUVNOR-1115
        this.xt.aliasPackage( "org.drools.guvnor.client",
                              "org.drools.ide.common.client" );

        this.xt.aliasPackage( "org.drools.guvnor.client.modeldriven.brl",
                              "org.kie.guvnor.datamodel.model" );

        //Legacy DSLSentences have a collection of String values whereas newer persisted models
        //have a collection of DSLVariableValues. See https://issues.jboss.org/browse/GUVNOR-1872
        this.xt.registerLocalConverter( DSLSentence.class,
                                        "values",
                                        new DSLVariableValuesConverter( this.xt.getMapper() ) );

    }

    public static BRLPersistence getInstance() {
        return INSTANCE;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.ide.common.server.util.BRLPersistence#toXML(org.drools.guvnor
     * .client.modeldriven.brl.RuleModel)
     */
    public String marshal( final RuleModel model ) {
        return this.xt.toXML( model );
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.ide.common.server.util.BRLPersistence#toModel(java.lang.String
     * )
     */
    public RuleModel unmarshal( final String xml ) {
        if ( xml == null || xml.trim().length() == 0 ) {
            return createEmptyModel();
        }
        RuleModel rm = (RuleModel) this.xt.fromXML( xml );

        //Upgrade model changes to legacy artifacts
        upgrader1.upgrade( rm );
        upgrader2.upgrade( rm );
        upgrader3.upgrade( rm );
        return rm;
    }

    public RuleModel unmarshalUsingDSL( final String str, final List<String> globals, final String... dsls ) {
        throw new UnsupportedOperationException();
    }

    protected RuleModel createEmptyModel() {
        return new RuleModel();
    }

}
