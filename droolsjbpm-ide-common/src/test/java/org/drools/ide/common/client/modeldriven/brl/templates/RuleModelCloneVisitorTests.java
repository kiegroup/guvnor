/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.ide.common.client.modeldriven.brl.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.ActionUpdateField;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.CEPWindow;
import org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;
import org.drools.ide.common.client.modeldriven.brl.DSLVariableValue;
import org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine;
import org.drools.ide.common.client.modeldriven.brl.ExpressionText;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FreeFormLine;
import org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleAttribute;
import org.drools.ide.common.client.modeldriven.brl.RuleMetadata;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;
import org.junit.Test;

/**
 * Tests for cloning of RuleModel
 */
public class RuleModelCloneVisitorTests {

    @Test
    public void testRuleModel() {
        RuleModel model = new RuleModel();
        model.modelVersion = "1";
        model.name = "ruleModelName";
        model.parentName = "ruleModelParentName";

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertNotSame( model,
                       clone );

        assertEquals( "1",
                      clone.modelVersion );
        assertEquals( "ruleModelName",
                      clone.name );
        assertEquals( "ruleModelParentName",
                      clone.parentName );
    }

    @Test
    public void testRuleAttributes() {

        RuleModel model = new RuleModel();
        model.attributes = new RuleAttribute[2];
        model.attributes[0] = new RuleAttribute( "attr0",
                                                 "attr0Value" );
        model.attributes[1] = new RuleAttribute( "attr1",
                                                 "attr1Value" );

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 2,
                      clone.attributes.length );

        assertNotSame( model.attributes[0],
                       clone.attributes[0] );
        assertEquals( "attr0",
                      clone.attributes[0].attributeName );
        assertEquals( "attr0Value",
                      clone.attributes[0].value );

        assertNotSame( model.attributes[1],
                       clone.attributes[1] );
        assertEquals( "attr1",
                      clone.attributes[1].attributeName );
        assertEquals( "attr1Value",
                      clone.attributes[1].value );
    }

    @Test
    public void testRuleMetadata() {

        RuleModel model = new RuleModel();
        model.metadataList = new RuleMetadata[2];
        model.metadataList[0] = new RuleMetadata( "md0",
                                                  "md0Value" );
        model.metadataList[1] = new RuleMetadata( "md1",
                                                  "md1Value" );

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 2,
                      clone.metadataList.length );

        assertNotSame( model.metadataList[0],
                       clone.metadataList[0] );
        assertEquals( "md0",
                      clone.metadataList[0].attributeName );
        assertEquals( "md0Value",
                      clone.metadataList[0].value );

        assertNotSame( model.metadataList[1],
                       clone.metadataList[1] );
        assertEquals( "md1",
                      clone.metadataList[1].attributeName );
        assertEquals( "md1Value",
                      clone.metadataList[1].value );
    }

    @Test
    public void testActionInsertFact() {
        RuleModel model = new RuleModel();

        model.rhs = new IAction[2];
        ActionInsertFact aif0 = new ActionInsertFact( "AIF0" );
        aif0.setBoundName( "$t0" );
        ActionFieldValue aif0f0 = new ActionFieldValue( "AIF0F0",
                                                        "AIF0F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        aif0f0.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        aif0.addFieldValue( aif0f0 );
        ActionFieldValue aif0f1 = new ActionFieldValue( "AIF0F1",
                                                        "AIF0F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        aif0f0.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        aif0.addFieldValue( aif0f1 );
        model.rhs[0] = aif0;

        ActionInsertFact aif1 = new ActionInsertFact( "AIF1" );
        aif1.setBoundName( "$t1" );
        ActionFieldValue aif1f0 = new ActionFieldValue( "AIF1F0",
                                                        "AIF1F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        aif1f0.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        aif1.addFieldValue( aif1f0 );
        ActionFieldValue aif1f1 = new ActionFieldValue( "AIF1F1",
                                                        "AIF1F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        aif1f1.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        aif1.addFieldValue( aif1f1 );
        model.rhs[1] = aif1;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 2,
                      clone.rhs.length );

        assertNotSame( model.rhs[0],
                       clone.rhs[0] );
        assertNotNull( clone.rhs[0] );
        assertTrue( clone.rhs[0] instanceof ActionInsertFact );
        ActionInsertFact aif0Clone = (ActionInsertFact) clone.rhs[0];
        assertEquals( "AIF0",
                      aif0Clone.factType );
        assertEquals( "$t0",
                      aif0Clone.getBoundName() );
        assertEquals( 2,
                      aif0Clone.fieldValues.length );

        assertNotSame( aif0.fieldValues[0],
                       aif0Clone.fieldValues[0] );
        assertNotNull( aif0Clone.fieldValues[0] );
        assertTrue( aif0Clone.fieldValues[0] instanceof ActionFieldValue );
        ActionFieldValue aif0f0Clone = (ActionFieldValue) aif0Clone.fieldValues[0];
        assertEquals( aif0f0.field,
                      aif0f0Clone.field );
        assertEquals( aif0f0.nature,
                      aif0f0Clone.nature );
        assertEquals( aif0f0.type,
                      aif0f0Clone.type );
        assertEquals( aif0f0.value,
                      aif0f0Clone.value );

        assertNotSame( aif0.fieldValues[1],
                       aif0Clone.fieldValues[1] );
        assertNotNull( aif0Clone.fieldValues[1] );
        assertTrue( aif0Clone.fieldValues[1] instanceof ActionFieldValue );
        ActionFieldValue aif0f1Clone = (ActionFieldValue) aif0Clone.fieldValues[1];
        assertEquals( aif0f1.field,
                      aif0f1Clone.field );
        assertEquals( aif0f1.nature,
                      aif0f1Clone.nature );
        assertEquals( aif0f1.type,
                      aif0f1Clone.type );
        assertEquals( aif0f1.value,
                      aif0f1Clone.value );

        assertNotSame( model.rhs[1],
                       clone.rhs[1] );
        assertNotNull( clone.rhs[1] );
        assertTrue( clone.rhs[1] instanceof ActionInsertFact );
        ActionInsertFact aif1Clone = (ActionInsertFact) clone.rhs[1];
        assertEquals( "AIF1",
                      aif1Clone.factType );
        assertEquals( "$t1",
                      aif1Clone.getBoundName() );
        assertEquals( 2,
                      aif1Clone.fieldValues.length );

        assertNotSame( aif1.fieldValues[0],
                       aif1Clone.fieldValues[0] );
        assertNotNull( aif1Clone.fieldValues[0] );
        assertTrue( aif1Clone.fieldValues[0] instanceof ActionFieldValue );
        ActionFieldValue aif1f0Clone = (ActionFieldValue) aif1Clone.fieldValues[0];
        assertEquals( aif1f0.field,
                      aif1f0Clone.field );
        assertEquals( aif1f0.nature,
                      aif1f0Clone.nature );
        assertEquals( aif1f0.type,
                      aif1f0Clone.type );
        assertEquals( aif1f0.value,
                      aif1f0Clone.value );

        assertNotSame( aif1.fieldValues[1],
                       aif1Clone.fieldValues[1] );
        assertNotNull( aif1Clone.fieldValues[1] );
        assertTrue( aif1Clone.fieldValues[1] instanceof ActionFieldValue );
        ActionFieldValue aif1f1Clone = (ActionFieldValue) aif1Clone.fieldValues[1];
        assertEquals( aif1f1.field,
                      aif1f1Clone.field );
        assertEquals( aif1f1.nature,
                      aif1f1Clone.nature );
        assertEquals( aif1f1.type,
                      aif1f1Clone.type );
        assertEquals( aif1f1.value,
                      aif1f1Clone.value );
    }

    @Test
    public void testActionSetField() {
        RuleModel model = new RuleModel();

        model.rhs = new IAction[2];
        ActionSetField asf0 = new ActionSetField( "ASF0" );
        asf0.variable = "$t0";
        ActionFieldValue asf0f0 = new ActionFieldValue( "ASF0F0",
                                                        "ASF0F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        asf0f0.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        asf0.addFieldValue( asf0f0 );
        ActionFieldValue asf0f1 = new ActionFieldValue( "ASF0F1",
                                                        "ASF0F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        asf0f0.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        asf0.addFieldValue( asf0f1 );
        model.rhs[0] = asf0;

        ActionSetField asf1 = new ActionSetField( "ASF1" );
        asf1.variable = "$t1";
        ActionFieldValue asf1f0 = new ActionFieldValue( "ASF1F0",
                                                        "ASF1F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        asf1f0.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        asf1.addFieldValue( asf1f0 );
        ActionFieldValue asf1f1 = new ActionFieldValue( "ASF1F1",
                                                        "ASF1F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        asf1f1.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        asf1.addFieldValue( asf1f1 );
        model.rhs[1] = asf1;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 2,
                      clone.rhs.length );

        assertNotSame( model.rhs[0],
                       clone.rhs[0] );
        assertNotNull( clone.rhs[0] );
        assertTrue( clone.rhs[0] instanceof ActionSetField );
        ActionSetField asf0Clone = (ActionSetField) clone.rhs[0];
        assertEquals( "$t0",
                      asf0Clone.variable );
        assertEquals( 2,
                      asf0Clone.fieldValues.length );

        assertNotSame( asf0.fieldValues[0],
                       asf0Clone.fieldValues[0] );
        assertNotNull( asf0Clone.fieldValues[0] );
        assertTrue( asf0Clone.fieldValues[0] instanceof ActionFieldValue );
        ActionFieldValue asf0f0Clone = (ActionFieldValue) asf0Clone.fieldValues[0];
        assertEquals( asf0f0.field,
                      asf0f0Clone.field );
        assertEquals( asf0f0.nature,
                      asf0f0Clone.nature );
        assertEquals( asf0f0.type,
                      asf0f0Clone.type );
        assertEquals( asf0f0.value,
                      asf0f0Clone.value );

        assertNotSame( asf0.fieldValues[1],
                       asf0Clone.fieldValues[1] );
        assertNotNull( asf0Clone.fieldValues[1] );
        assertTrue( asf0Clone.fieldValues[1] instanceof ActionFieldValue );
        ActionFieldValue asf0f1Clone = (ActionFieldValue) asf0Clone.fieldValues[1];
        assertEquals( asf0f1.field,
                      asf0f1Clone.field );
        assertEquals( asf0f1.nature,
                      asf0f1Clone.nature );
        assertEquals( asf0f1.type,
                      asf0f1Clone.type );
        assertEquals( asf0f1.value,
                      asf0f1Clone.value );

        assertNotSame( model.rhs[1],
                       clone.rhs[1] );
        assertNotNull( clone.rhs[1] );
        assertTrue( clone.rhs[1] instanceof ActionSetField );
        ActionSetField asf1Clone = (ActionSetField) clone.rhs[1];
        assertEquals( "$t1",
                      asf1Clone.variable );
        assertEquals( 2,
                      asf1Clone.fieldValues.length );

        assertNotSame( asf1.fieldValues[0],
                       asf1Clone.fieldValues[0] );
        assertNotNull( asf1Clone.fieldValues[0] );
        assertTrue( asf1Clone.fieldValues[0] instanceof ActionFieldValue );
        ActionFieldValue asf1f0Clone = (ActionFieldValue) asf1Clone.fieldValues[0];
        assertEquals( asf1f0.field,
                      asf1f0Clone.field );
        assertEquals( asf1f0.nature,
                      asf1f0Clone.nature );
        assertEquals( asf1f0.type,
                      asf1f0Clone.type );
        assertEquals( asf1f0.value,
                      asf1f0Clone.value );

        assertNotSame( asf1.fieldValues[1],
                       asf1Clone.fieldValues[1] );
        assertNotNull( asf1Clone.fieldValues[1] );
        assertTrue( asf1Clone.fieldValues[1] instanceof ActionFieldValue );
        ActionFieldValue asf1f1Clone = (ActionFieldValue) asf1Clone.fieldValues[1];
        assertEquals( asf1f1.field,
                      asf1f1Clone.field );
        assertEquals( asf1f1.nature,
                      asf1f1Clone.nature );
        assertEquals( asf1f1.type,
                      asf1f1Clone.type );
        assertEquals( asf1f1.value,
                      asf1f1Clone.value );
    }

    @Test
    public void testActionUpdateField() {
        RuleModel model = new RuleModel();

        model.rhs = new IAction[2];
        ActionUpdateField auf0 = new ActionUpdateField();
        auf0.variable = "$t0";
        ActionFieldValue auf0f0 = new ActionFieldValue( "AUF0F0",
                                                        "AUF0F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        auf0f0.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        auf0.addFieldValue( auf0f0 );
        ActionFieldValue auf0f1 = new ActionFieldValue( "AUF0F1",
                                                        "AUF0F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        auf0f0.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        auf0.addFieldValue( auf0f1 );
        model.rhs[0] = auf0;

        ActionUpdateField auf1 = new ActionUpdateField();
        auf1.variable = "$t1";
        ActionFieldValue auf1f0 = new ActionFieldValue( "AUF1F0",
                                                        "AUF1F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        auf1f0.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        auf1.addFieldValue( auf1f0 );
        ActionFieldValue auf1f1 = new ActionFieldValue( "AUF1F1",
                                                        "AUF1F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        auf1f1.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        auf1.addFieldValue( auf1f1 );
        model.rhs[1] = auf1;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 2,
                      clone.rhs.length );

        assertNotSame( model.rhs[0],
                       clone.rhs[0] );
        assertNotNull( clone.rhs[0] );
        assertTrue( clone.rhs[0] instanceof ActionUpdateField );
        ActionUpdateField auf0Clone = (ActionUpdateField) clone.rhs[0];
        assertEquals( "$t0",
                      auf0Clone.variable );
        assertEquals( 2,
                      auf0Clone.fieldValues.length );

        assertNotSame( auf0.fieldValues[0],
                       auf0Clone.fieldValues[0] );
        assertNotNull( auf0Clone.fieldValues[0] );
        assertTrue( auf0Clone.fieldValues[0] instanceof ActionFieldValue );
        ActionFieldValue auf0f0Clone = (ActionFieldValue) auf0Clone.fieldValues[0];
        assertEquals( auf0f0.field,
                      auf0f0Clone.field );
        assertEquals( auf0f0.nature,
                      auf0f0Clone.nature );
        assertEquals( auf0f0.type,
                      auf0f0Clone.type );
        assertEquals( auf0f0.value,
                      auf0f0Clone.value );

        assertNotSame( auf0.fieldValues[1],
                       auf0Clone.fieldValues[1] );
        assertNotNull( auf0Clone.fieldValues[1] );
        assertTrue( auf0Clone.fieldValues[1] instanceof ActionFieldValue );
        ActionFieldValue auf0f1Clone = (ActionFieldValue) auf0Clone.fieldValues[1];
        assertEquals( auf0f1.field,
                      auf0f1Clone.field );
        assertEquals( auf0f1.nature,
                      auf0f1Clone.nature );
        assertEquals( auf0f1.type,
                      auf0f1Clone.type );
        assertEquals( auf0f1.value,
                      auf0f1Clone.value );

        assertNotSame( model.rhs[1],
                       clone.rhs[1] );
        assertNotNull( clone.rhs[1] );
        assertTrue( clone.rhs[1] instanceof ActionUpdateField );
        ActionUpdateField auf1Clone = (ActionUpdateField) clone.rhs[1];
        assertEquals( "$t1",
                      auf1Clone.variable );
        assertEquals( 2,
                      auf1Clone.fieldValues.length );

        assertNotSame( auf1.fieldValues[0],
                       auf1Clone.fieldValues[0] );
        assertNotNull( auf1Clone.fieldValues[0] );
        assertTrue( auf1Clone.fieldValues[0] instanceof ActionFieldValue );
        ActionFieldValue auf1f0Clone = (ActionFieldValue) auf1Clone.fieldValues[0];
        assertEquals( auf1f0.field,
                      auf1f0Clone.field );
        assertEquals( auf1f0.nature,
                      auf1f0Clone.nature );
        assertEquals( auf1f0.type,
                      auf1f0Clone.type );
        assertEquals( auf1f0.value,
                      auf1f0Clone.value );

        assertNotSame( auf1.fieldValues[1],
                       auf1Clone.fieldValues[1] );
        assertNotNull( auf1Clone.fieldValues[1] );
        assertTrue( auf1Clone.fieldValues[1] instanceof ActionFieldValue );
        ActionFieldValue auf1f1Clone = (ActionFieldValue) auf1Clone.fieldValues[1];
        assertEquals( auf1f1.field,
                      auf1f1Clone.field );
        assertEquals( auf1f1.nature,
                      auf1f1Clone.nature );
        assertEquals( auf1f1.type,
                      auf1f1Clone.type );
        assertEquals( auf1f1.value,
                      auf1f1Clone.value );
    }

    @Test
    public void testDSLSentence() {
        RuleModel model = new RuleModel();

        model.lhs = new IPattern[2];
        DSLSentence dsl0 = new DSLSentence();
        dsl0.setDefinition( "DSL Sentence 0" );
        dsl0.getValues().add( new DSLVariableValue( "dsl0v0" ) );
        dsl0.getValues().add( new DSLVariableValue( "dsl0v1" ) );
        model.lhs[0] = dsl0;
        DSLSentence dsl1 = new DSLSentence();
        dsl1.setDefinition( "DSL Sentence 1" );
        dsl1.getValues().add( new DSLVariableValue( "dsl1v0" ) );
        dsl1.getValues().add( new DSLVariableValue( "dsl1v1" ) );
        model.lhs[1] = dsl1;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 2,
                      clone.lhs.length );

        assertNotSame( model.lhs[0],
                       clone.lhs[0] );
        assertNotNull( clone.lhs[0] );
        assertTrue( clone.lhs[0] instanceof DSLSentence );
        DSLSentence dsl0Clone = (DSLSentence) clone.lhs[0];
        assertEquals( dsl0.getDefinition(),
                      dsl0Clone.getDefinition() );

        assertEquals( 2,
                      dsl0Clone.getValues().size() );
        assertNotSame( dsl0.getValues().get( 0 ),
                       dsl0Clone.getValues().get( 0 ) );
        assertNotNull( dsl0Clone.getValues().get( 0 ) );
        assertTrue( dsl0Clone.getValues().get( 0 ) instanceof DSLVariableValue );
        DSLVariableValue dsl0v0Clone = (DSLVariableValue) dsl0Clone.getValues().get( 0 );
        assertEquals( dsl0.getValues().get( 0 ).getValue(),
                      dsl0v0Clone.getValue() );

        assertNotSame( dsl0.getValues().get( 1 ),
                       dsl0Clone.getValues().get( 1 ) );
        assertNotNull( dsl0Clone.getValues().get( 1 ) );
        assertTrue( dsl0Clone.getValues().get( 1 ) instanceof DSLVariableValue );
        DSLVariableValue dsl0v1Clone = (DSLVariableValue) dsl0Clone.getValues().get( 1 );
        assertEquals( dsl0.getValues().get( 1 ).getValue(),
                      dsl0v1Clone.getValue() );

        assertNotSame( model.lhs[1],
                       clone.lhs[1] );
        assertNotNull( clone.lhs[1] );
        assertTrue( clone.lhs[1] instanceof DSLSentence );
        DSLSentence dsl1Clone = (DSLSentence) clone.lhs[1];
        assertEquals( dsl1.getDefinition(),
                      dsl1Clone.getDefinition() );

        assertEquals( 2,
                      dsl1Clone.getValues().size() );
        assertNotSame( dsl1.getValues().get( 0 ),
                       dsl0Clone.getValues().get( 0 ) );
        assertNotNull( dsl1Clone.getValues().get( 0 ) );
        assertTrue( dsl1Clone.getValues().get( 0 ) instanceof DSLVariableValue );
        DSLVariableValue dsl1v0Clone = (DSLVariableValue) dsl1Clone.getValues().get( 0 );
        assertEquals( dsl1.getValues().get( 0 ).getValue(),
                      dsl1v0Clone.getValue() );

        assertNotSame( dsl1.getValues().get( 1 ),
                       dsl1Clone.getValues().get( 1 ) );
        assertNotNull( dsl1Clone.getValues().get( 1 ) );
        assertTrue( dsl1Clone.getValues().get( 1 ) instanceof DSLVariableValue );
        DSLVariableValue dsl1v1Clone = (DSLVariableValue) dsl1Clone.getValues().get( 1 );
        assertEquals( dsl1.getValues().get( 1 ).getValue(),
                      dsl1v1Clone.getValue() );
    }

    @Test
    public void testFactPattern_Basics() {
        RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];
        FactPattern fp0 = new FactPattern();
        fp0.setBoundName( "$t0" );
        fp0.setFactType( "FT0" );
        fp0.setNegated( true );
        model.lhs[0] = fp0;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 1,
                      clone.lhs.length );
        assertNotSame( model.lhs[0],
                       clone.lhs[0] );
        assertNotNull( clone.lhs[0] );
        assertTrue( clone.lhs[0] instanceof FactPattern );
        FactPattern fp0Clone = (FactPattern) clone.lhs[0];
        assertEquals( fp0.getBoundName(),
                      fp0Clone.getBoundName() );
        assertEquals( fp0.getFactType(),
                      fp0Clone.getFactType() );
        assertEquals( fp0.isNegated(),
                      fp0Clone.isNegated() );
    }

    @Test
    public void testFactPattern_CEPWindow() {
        RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];
        FactPattern fp0 = new FactPattern();
        fp0.setBoundName( "$t0" );
        fp0.setFactType( "FT0" );
        fp0.setNegated( true );
        CEPWindow cepWindow0 = new CEPWindow();
        cepWindow0.setOperator( SuggestionCompletionEngine.getCEPWindowOperators().get( 0 ) );
        cepWindow0.setParameter( "cepWindow0P0",
                                 "cepWindow0P0Value" );
        cepWindow0.setParameter( "cepWindow0P1",
                                 "cepWindow0P1Value" );
        fp0.setWindow( cepWindow0 );
        model.lhs[0] = fp0;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 1,
                      clone.lhs.length );
        assertNotSame( model.lhs[0],
                       clone.lhs[0] );
        assertNotNull( clone.lhs[0] );
        assertTrue( clone.lhs[0] instanceof FactPattern );
        FactPattern fp0Clone = (FactPattern) clone.lhs[0];
        assertEquals( fp0.getBoundName(),
                      fp0Clone.getBoundName() );
        assertEquals( fp0.getFactType(),
                      fp0Clone.getFactType() );
        assertEquals( fp0.isNegated(),
                      fp0Clone.isNegated() );

        assertNotSame( fp0.getWindow(),
                       fp0Clone.getWindow() );
        assertNotNull( fp0Clone.getWindow() );
        assertTrue( fp0Clone.getWindow() instanceof CEPWindow );
        CEPWindow cepWindow0Clone = (CEPWindow) fp0Clone.getWindow();
        assertEquals( cepWindow0.getOperator(),
                      cepWindow0Clone.getOperator() );
        assertEquals( cepWindow0.getParameters().size(),
                      cepWindow0Clone.getParameters().size() );
        assertNotNull( cepWindow0Clone.getParameter( "cepWindow0P0" ) );
        assertEquals( cepWindow0.getParameter( "cepWindow0P0" ),
                      cepWindow0Clone.getParameter( "cepWindow0P0" ) );
        assertNotNull( cepWindow0Clone.getParameter( "cepWindow0P1" ) );
        assertEquals( cepWindow0.getParameter( "cepWindow0P1" ),
                      cepWindow0Clone.getParameter( "cepWindow0P1" ) );
    }

    @Test
    public void testFactPattern_SingleFieldConstraints() {
        RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];
        FactPattern fp0 = new FactPattern();
        fp0.setBoundName( "$t0" );
        fp0.setFactType( "FT0" );

        SingleFieldConstraint sfc0 = new SingleFieldConstraint();
        sfc0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        sfc0.setFieldBinding( "$sfc0" );
        sfc0.setFieldName( "sfc0" );
        sfc0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc0.setOperator( "==" );
        sfc0.setParameter( "sfc0p0",
                           "sfc0p0Value" );
        sfc0.setValue( "sfc0Value" );
        fp0.addConstraint( sfc0 );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        sfc1.setFieldBinding( "$sfc1" );
        sfc1.setFieldName( "sfc1" );
        sfc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1.setOperator( "==" );
        sfc1.setParameter( "sfc1p0",
                           "sfc1p0Value" );
        sfc1.setValue( "sfc1Value" );
        fp0.addConstraint( sfc1 );

        model.lhs[0] = fp0;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 1,
                      clone.lhs.length );
        assertNotSame( model.lhs[0],
                       clone.lhs[0] );
        assertNotNull( clone.lhs[0] );
        assertTrue( clone.lhs[0] instanceof FactPattern );
        FactPattern fp0Clone = (FactPattern) clone.lhs[0];
        assertEquals( fp0.getBoundName(),
                      fp0Clone.getBoundName() );
        assertEquals( fp0.getFactType(),
                      fp0Clone.getFactType() );

        assertEquals( 2,
                      fp0Clone.constraintList.constraints.length );

        assertNotSame( fp0.constraintList.constraints[0],
                       fp0Clone.constraintList.constraints[0] );
        assertNotNull( fp0Clone.constraintList.constraints[0] );
        assertTrue( fp0Clone.constraintList.constraints[0] instanceof SingleFieldConstraint );
        SingleFieldConstraint sfc0Clone = (SingleFieldConstraint) fp0Clone.constraintList.constraints[0];
        assertEquals( sfc0.getConstraintValueType(),
                      sfc0Clone.getConstraintValueType() );
        assertEquals( sfc0.getFieldBinding(),
                      sfc0Clone.getFieldBinding() );
        assertEquals( sfc0.getFieldName(),
                      sfc0Clone.getFieldName() );
        assertEquals( sfc0.getFieldType(),
                      sfc0Clone.getFieldType() );
        assertEquals( sfc0.getOperator(),
                      sfc0Clone.getOperator() );
        assertNotNull( sfc0Clone.getParameter( "sfc0p0" ) );
        assertEquals( sfc0.getParameter( "sfc0p0" ),
                      sfc0Clone.getParameter( "sfc0p0" ) );
        assertEquals( sfc0.getValue(),
                      sfc0Clone.getValue() );

        assertNotSame( fp0.constraintList.constraints[1],
                       fp0Clone.constraintList.constraints[1] );
        assertNotNull( fp0Clone.constraintList.constraints[1] );
        assertTrue( fp0Clone.constraintList.constraints[1] instanceof SingleFieldConstraint );
        SingleFieldConstraint sfc1Clone = (SingleFieldConstraint) fp0Clone.constraintList.constraints[1];
        assertEquals( sfc1.getConstraintValueType(),
                      sfc1Clone.getConstraintValueType() );
        assertEquals( sfc1.getFieldBinding(),
                      sfc1Clone.getFieldBinding() );
        assertEquals( sfc1.getFieldName(),
                      sfc1Clone.getFieldName() );
        assertEquals( sfc1.getFieldType(),
                      sfc1Clone.getFieldType() );
        assertEquals( sfc1.getOperator(),
                      sfc1Clone.getOperator() );
        assertNotNull( sfc1Clone.getParameter( "sfc1p0" ) );
        assertEquals( sfc1.getParameter( "sfc1p0" ),
                      sfc1Clone.getParameter( "sfc1p0" ) );
        assertEquals( sfc1.getValue(),
                      sfc1Clone.getValue() );
    }

    @Test
    public void testFactPattern_ConnectiveConstraints() {
        RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];
        FactPattern fp0 = new FactPattern();
        fp0.setBoundName( "$t0" );
        fp0.setFactType( "FT0" );

        SingleFieldConstraint sfc0 = new SingleFieldConstraint();
        sfc0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        sfc0.setFieldBinding( "$sfc0" );
        sfc0.setFieldName( "sfc0" );
        sfc0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc0.setOperator( "==" );
        sfc0.setParameter( "sfc0p0",
                           "sfc0p0Value" );
        sfc0.setValue( "sfc0Value" );
        fp0.addConstraint( sfc0 );

        sfc0.addNewConnective();
        ConnectiveConstraint sfc0cc0 = sfc0.connectives[0];
        sfc0cc0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        sfc0cc0.setFieldName( "sfc0" );
        sfc0cc0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc0cc0.setOperator( "|| ==" );
        sfc0cc0.setParameter( "sfc0cc0p0",
                              "sfc0cc0p0Value" );
        sfc0cc0.setValue( "sfc0cc0Value" );

        model.lhs[0] = fp0;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 1,
                      clone.lhs.length );
        assertNotSame( model.lhs[0],
                       clone.lhs[0] );
        assertNotNull( clone.lhs[0] );
        assertTrue( clone.lhs[0] instanceof FactPattern );
        FactPattern fp0Clone = (FactPattern) clone.lhs[0];
        assertEquals( fp0.getBoundName(),
                      fp0Clone.getBoundName() );
        assertEquals( fp0.getFactType(),
                      fp0Clone.getFactType() );

        assertEquals( 1,
                      fp0Clone.constraintList.constraints.length );

        assertNotSame( fp0.constraintList.constraints[0],
                       fp0Clone.constraintList.constraints[0] );
        assertNotNull( fp0Clone.constraintList.constraints[0] );
        assertTrue( fp0Clone.constraintList.constraints[0] instanceof SingleFieldConstraint );
        SingleFieldConstraint sfc0Clone = (SingleFieldConstraint) fp0Clone.constraintList.constraints[0];
        assertEquals( sfc0.getConstraintValueType(),
                      sfc0Clone.getConstraintValueType() );
        assertEquals( sfc0.getFieldBinding(),
                      sfc0Clone.getFieldBinding() );
        assertEquals( sfc0.getFieldName(),
                      sfc0Clone.getFieldName() );
        assertEquals( sfc0.getFieldType(),
                      sfc0Clone.getFieldType() );
        assertEquals( sfc0.getOperator(),
                      sfc0Clone.getOperator() );
        assertNotNull( sfc0Clone.getParameter( "sfc0p0" ) );
        assertEquals( sfc0.getParameter( "sfc0p0" ),
                      sfc0Clone.getParameter( "sfc0p0" ) );
        assertEquals( sfc0.getValue(),
                      sfc0Clone.getValue() );

        assertEquals( sfc0.connectives.length,
                      sfc0Clone.connectives.length );
        assertNotSame( sfc0.connectives[0],
                       sfc0Clone.connectives[0] );
        assertNotNull( sfc0Clone.connectives[0] );
        assertTrue( sfc0Clone.connectives[0] instanceof ConnectiveConstraint );
        ConnectiveConstraint sfc0cc0Clone = (ConnectiveConstraint) sfc0Clone.connectives[0];
        assertEquals( sfc0cc0.getConstraintValueType(),
                      sfc0cc0Clone.getConstraintValueType() );
        assertEquals( sfc0cc0.getFieldName(),
                      sfc0cc0Clone.getFieldName() );
        assertEquals( sfc0cc0.getFieldType(),
                      sfc0cc0Clone.getFieldType() );
        assertEquals( sfc0cc0.getOperator(),
                      sfc0cc0Clone.getOperator() );
        assertNotNull( sfc0cc0Clone.getParameter( "sfc0cc0p0" ) );
        assertEquals( sfc0cc0.getParameter( "sfc0cc0p0" ),
                      sfc0cc0Clone.getParameter( "sfc0cc0p0" ) );
        assertEquals( sfc0cc0.getValue(),
                      sfc0cc0Clone.getValue() );
    }

    @Test
    public void testFactPattern_CompositeFieldConstraints() {
        RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];
        FactPattern fp0 = new FactPattern();
        fp0.setBoundName( "$t0" );
        fp0.setFactType( "FT0" );

        CompositeFieldConstraint cfc0 = new CompositeFieldConstraint();
        cfc0.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_OR;

        SingleFieldConstraint sfc0 = new SingleFieldConstraint();
        sfc0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        sfc0.setFieldBinding( "$sfc0" );
        sfc0.setFieldName( "sfc0" );
        sfc0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc0.setOperator( "==" );
        sfc0.setParameter( "sfc0p0",
                           "sfc0p0Value" );
        sfc0.setValue( "sfc0Value" );
        cfc0.addConstraint( sfc0 );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        sfc1.setFieldBinding( "$sfc1" );
        sfc1.setFieldName( "sfc1" );
        sfc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1.setOperator( "==" );
        sfc1.setParameter( "sfc1p0",
                           "sfc1p0Value" );
        sfc1.setValue( "sfc1Value" );
        cfc0.addConstraint( sfc1 );

        fp0.addConstraint( cfc0 );

        model.lhs[0] = fp0;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 1,
                      clone.lhs.length );
        assertNotSame( model.lhs[0],
                       clone.lhs[0] );
        assertNotNull( clone.lhs[0] );
        assertTrue( clone.lhs[0] instanceof FactPattern );
        FactPattern fp0Clone = (FactPattern) clone.lhs[0];
        assertEquals( fp0.getBoundName(),
                      fp0Clone.getBoundName() );
        assertEquals( fp0.getFactType(),
                      fp0Clone.getFactType() );

        assertEquals( 1,
                      fp0Clone.constraintList.constraints.length );

        assertNotSame( fp0.constraintList.constraints[0],
                       fp0Clone.constraintList.constraints[0] );
        assertNotNull( fp0Clone.constraintList.constraints[0] );
        assertTrue( fp0Clone.constraintList.constraints[0] instanceof CompositeFieldConstraint );
        CompositeFieldConstraint cfc0Clone = (CompositeFieldConstraint) fp0Clone.constraintList.constraints[0];
        assertEquals( cfc0.compositeJunctionType,
                      cfc0Clone.compositeJunctionType );

        assertNotSame( cfc0.constraints[0],
                       cfc0Clone.constraints[0] );
        assertNotNull( cfc0Clone.constraints[0] );
        assertTrue( cfc0Clone.constraints[0] instanceof SingleFieldConstraint );
        SingleFieldConstraint sfc0Clone = (SingleFieldConstraint) cfc0Clone.constraints[0];
        assertEquals( sfc0.getFieldBinding(),
                      sfc0Clone.getFieldBinding() );
        assertEquals( sfc0.getFieldName(),
                      sfc0Clone.getFieldName() );
        assertEquals( sfc0.getFieldType(),
                      sfc0Clone.getFieldType() );
        assertEquals( sfc0.getOperator(),
                      sfc0Clone.getOperator() );
        assertNotNull( sfc0Clone.getParameter( "sfc0p0" ) );
        assertEquals( sfc0.getParameter( "sfc0p0" ),
                      sfc0Clone.getParameter( "sfc0p0" ) );
        assertEquals( sfc0.getValue(),
                      sfc0Clone.getValue() );

        assertNotSame( cfc0.constraints[1],
                       cfc0Clone.constraints[1] );
        assertNotNull( cfc0Clone.constraints[1] );
        assertTrue( cfc0Clone.constraints[1] instanceof SingleFieldConstraint );
        SingleFieldConstraint sfc1Clone = (SingleFieldConstraint) cfc0Clone.constraints[1];
        assertEquals( sfc1.getConstraintValueType(),
                      sfc1Clone.getConstraintValueType() );
        assertEquals( sfc1.getFieldBinding(),
                      sfc1Clone.getFieldBinding() );
        assertEquals( sfc1.getFieldName(),
                      sfc1Clone.getFieldName() );
        assertEquals( sfc1.getFieldType(),
                      sfc1Clone.getFieldType() );
        assertEquals( sfc1.getOperator(),
                      sfc1Clone.getOperator() );
        assertNotNull( sfc1Clone.getParameter( "sfc1p0" ) );
        assertEquals( sfc1.getParameter( "sfc1p0" ),
                      sfc1Clone.getParameter( "sfc1p0" ) );
        assertEquals( sfc1.getValue(),
                      sfc1Clone.getValue() );
    }

    @Test
    public void testFactPattern_ExpressionFormLine() {
        RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];
        FactPattern fp0 = new FactPattern();
        fp0.setBoundName( "$t0" );
        fp0.setFactType( "FT0" );

        SingleFieldConstraint sfc0 = new SingleFieldConstraint();
        ExpressionFormLine efl0 = new ExpressionFormLine();
        efl0.setBinding( "$efl0" );
        ExpressionText efl0p0 = new ExpressionText( "efl0p0" );
        efl0.appendPart( efl0p0 );
        sfc0.setExpressionValue( efl0 );
        fp0.addConstraint( sfc0 );

        model.lhs[0] = fp0;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 1,
                      clone.lhs.length );
        assertNotSame( model.lhs[0],
                       clone.lhs[0] );
        assertNotNull( clone.lhs[0] );
        assertTrue( clone.lhs[0] instanceof FactPattern );
        FactPattern fp0Clone = (FactPattern) clone.lhs[0];
        assertEquals( fp0.getBoundName(),
                      fp0Clone.getBoundName() );
        assertEquals( fp0.getFactType(),
                      fp0Clone.getFactType() );

        assertEquals( 1,
                      fp0Clone.constraintList.constraints.length );

        assertNotSame( fp0.constraintList.constraints[0],
                       fp0Clone.constraintList.constraints[0] );
        assertNotNull( fp0Clone.constraintList.constraints[0] );
        assertTrue( fp0Clone.constraintList.constraints[0] instanceof SingleFieldConstraint );
        SingleFieldConstraint sfc0Clone = (SingleFieldConstraint) fp0Clone.constraintList.constraints[0];

        assertNotSame( sfc0.getExpressionValue(),
                       sfc0Clone.getExpressionValue() );
        assertNotNull( sfc0Clone.getExpressionValue() );
        assertTrue( sfc0Clone.getExpressionValue() instanceof ExpressionFormLine );
        ExpressionFormLine efl0Clone = (ExpressionFormLine) sfc0Clone.getExpressionValue();

        assertEquals( 1,
                      sfc0Clone.getExpressionValue().getParts().size() );
        assertTrue( sfc0Clone.getExpressionValue().getParts().get( 0 ) instanceof ExpressionText );
        ExpressionText efl0p0Clone = (ExpressionText) sfc0Clone.getExpressionValue().getParts().get( 0 );
        assertEquals( efl0p0.getClassType(),
                      efl0p0Clone.getClassType() );
        assertEquals( efl0p0.getName(),
                      efl0p0Clone.getName() );
        assertEquals( efl0p0.getGenericType(),
                      efl0p0Clone.getGenericType() );
        assertEquals( efl0p0.getParametricType(),
                      efl0p0Clone.getParametricType() );

        assertEquals( efl0.getBinding(),
                      efl0Clone.getBinding() );
        assertEquals( efl0.getClassType(),
                      efl0Clone.getClassType() );
        assertEquals( efl0.getFieldName(),
                      efl0Clone.getFieldName() );
        assertEquals( efl0.getGenericType(),
                      efl0Clone.getGenericType() );
        assertEquals( efl0.getParametricType(),
                      efl0Clone.getParametricType() );
        assertEquals( efl0.getCurrentName(),
                      efl0Clone.getCurrentName() );
    }

    @Test
    public void testFactPattern_SingleFieldConstraintEBLeftSide() {
        RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];
        FactPattern fp0 = new FactPattern();
        fp0.setBoundName( "$t0" );
        fp0.setFactType( "FT0" );

        SingleFieldConstraintEBLeftSide sfc0 = new SingleFieldConstraintEBLeftSide();

        ExpressionFormLine eflhs0 = new ExpressionFormLine();
        eflhs0.setBinding( "$eflhs0" );
        ExpressionText eflhs0p0 = new ExpressionText( "eflhs0p0" );
        eflhs0.appendPart( eflhs0p0 );
        sfc0.setExpressionLeftSide( eflhs0 );

        ExpressionFormLine efl0 = new ExpressionFormLine();
        efl0.setBinding( "$efl0" );
        ExpressionText efl0p0 = new ExpressionText( "efl0p0" );
        efl0.appendPart( efl0p0 );
        sfc0.setExpressionValue( efl0 );

        fp0.addConstraint( sfc0 );

        model.lhs[0] = fp0;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 1,
                      clone.lhs.length );
        assertNotSame( model.lhs[0],
                       clone.lhs[0] );
        assertNotNull( clone.lhs[0] );
        assertTrue( clone.lhs[0] instanceof FactPattern );
        FactPattern fp0Clone = (FactPattern) clone.lhs[0];
        assertEquals( fp0.getBoundName(),
                      fp0Clone.getBoundName() );
        assertEquals( fp0.getFactType(),
                      fp0Clone.getFactType() );

        assertEquals( 1,
                      fp0Clone.constraintList.constraints.length );

        assertNotSame( fp0.constraintList.constraints[0],
                       fp0Clone.constraintList.constraints[0] );
        assertNotNull( fp0Clone.constraintList.constraints[0] );
        assertTrue( fp0Clone.constraintList.constraints[0] instanceof SingleFieldConstraintEBLeftSide );
        SingleFieldConstraintEBLeftSide sfc0Clone = (SingleFieldConstraintEBLeftSide) fp0Clone.constraintList.constraints[0];

        assertNotSame( sfc0.getExpressionLeftSide(),
                       sfc0Clone.getExpressionLeftSide() );
        assertNotNull( sfc0Clone.getExpressionLeftSide() );
        assertTrue( sfc0Clone.getExpressionLeftSide() instanceof ExpressionFormLine );
        ExpressionFormLine eflhs0Clone = (ExpressionFormLine) sfc0Clone.getExpressionLeftSide();

        assertEquals( 1,
                      sfc0Clone.getExpressionLeftSide().getParts().size() );
        assertTrue( sfc0Clone.getExpressionLeftSide().getParts().get( 0 ) instanceof ExpressionText );
        ExpressionText eflhs0p0Clone = (ExpressionText) sfc0Clone.getExpressionLeftSide().getParts().get( 0 );
        assertEquals( eflhs0p0.getClassType(),
                      eflhs0p0Clone.getClassType() );
        assertEquals( eflhs0p0.getName(),
                      eflhs0p0Clone.getName() );
        assertEquals( eflhs0p0.getGenericType(),
                      eflhs0p0Clone.getGenericType() );
        assertEquals( eflhs0p0.getParametricType(),
                      eflhs0p0Clone.getParametricType() );

        assertEquals( eflhs0.getBinding(),
                      eflhs0Clone.getBinding() );
        assertEquals( eflhs0.getClassType(),
                      eflhs0Clone.getClassType() );
        assertEquals( eflhs0.getFieldName(),
                      eflhs0Clone.getFieldName() );
        assertEquals( eflhs0.getGenericType(),
                      eflhs0Clone.getGenericType() );
        assertEquals( eflhs0.getParametricType(),
                      eflhs0Clone.getParametricType() );
        assertEquals( eflhs0.getCurrentName(),
                      eflhs0Clone.getCurrentName() );

        assertNotSame( sfc0.getExpressionValue(),
                       sfc0Clone.getExpressionValue() );
        assertNotNull( sfc0Clone.getExpressionValue() );
        assertTrue( sfc0Clone.getExpressionValue() instanceof ExpressionFormLine );
        ExpressionFormLine efl0Clone = (ExpressionFormLine) sfc0Clone.getExpressionValue();

        assertEquals( 1,
                      sfc0Clone.getExpressionValue().getParts().size() );
        assertTrue( sfc0Clone.getExpressionValue().getParts().get( 0 ) instanceof ExpressionText );
        ExpressionText efl0p0Clone = (ExpressionText) sfc0Clone.getExpressionValue().getParts().get( 0 );
        assertEquals( efl0p0.getClassType(),
                      efl0p0Clone.getClassType() );
        assertEquals( efl0p0.getName(),
                      efl0p0Clone.getName() );
        assertEquals( efl0p0.getGenericType(),
                      efl0p0Clone.getGenericType() );
        assertEquals( efl0p0.getParametricType(),
                      efl0p0Clone.getParametricType() );

        assertEquals( efl0.getBinding(),
                      efl0Clone.getBinding() );
        assertEquals( efl0.getClassType(),
                      efl0Clone.getClassType() );
        assertEquals( efl0.getFieldName(),
                      efl0Clone.getFieldName() );
        assertEquals( efl0.getGenericType(),
                      efl0Clone.getGenericType() );
        assertEquals( efl0.getParametricType(),
                      efl0Clone.getParametricType() );
        assertEquals( efl0.getCurrentName(),
                      efl0Clone.getCurrentName() );
    }

    @Test
    public void testCompositeFactPatterns() {
        RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];
        CompositeFactPattern cfp0 = new CompositeFactPattern();
        cfp0.type = CompositeFactPattern.COMPOSITE_TYPE_OR;

        FactPattern fp0 = new FactPattern();
        fp0.setBoundName( "$t0" );
        fp0.setFactType( "FT0" );
        fp0.setNegated( true );

        SingleFieldConstraint fp0sfc0 = new SingleFieldConstraint();
        fp0sfc0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        fp0sfc0.setFieldBinding( "$fp0sfc0" );
        fp0sfc0.setFieldName( "fp0sfc0" );
        fp0sfc0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        fp0sfc0.setOperator( "==" );
        fp0sfc0.setParameter( "fp0sfc0p0",
                              "fp0sfc0p0Value" );
        fp0sfc0.setValue( "fp0sfc0Value" );
        fp0.addConstraint( fp0sfc0 );

        FactPattern fp1 = new FactPattern();
        fp1.setBoundName( "$t1" );
        fp1.setFactType( "FT1" );
        fp1.setNegated( true );

        SingleFieldConstraint fp1sfc0 = new SingleFieldConstraint();
        fp1sfc0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        fp1sfc0.setFieldBinding( "$fp1sfc0" );
        fp1sfc0.setFieldName( "fp1sfc0" );
        fp1sfc0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        fp1sfc0.setOperator( "==" );
        fp1sfc0.setParameter( "fp1sfc0p0",
                              "fp1sfc0p0Value" );
        fp1sfc0.setValue( "fp1sfc0Value" );
        fp1.addConstraint( fp1sfc0 );

        cfp0.addFactPattern( fp0 );
        cfp0.addFactPattern( fp1 );

        model.lhs[0] = cfp0;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 1,
                      clone.lhs.length );
        assertNotSame( model.lhs[0],
                       clone.lhs[0] );
        assertNotNull( clone.lhs[0] );
        assertTrue( clone.lhs[0] instanceof CompositeFactPattern );
        CompositeFactPattern cfp0Clone = (CompositeFactPattern) clone.lhs[0];
        assertEquals( cfp0.type,
                      cfp0Clone.type );
        assertEquals( cfp0.getPatterns().length,
                      cfp0Clone.getPatterns().length );

        assertNotSame( cfp0.getPatterns()[0],
                       cfp0Clone.getPatterns()[0] );
        assertNotNull( cfp0Clone.getPatterns()[0] );
        assertTrue( cfp0Clone.getPatterns()[0] instanceof FactPattern );
        FactPattern fp0Clone = (FactPattern) cfp0Clone.getPatterns()[0];
        assertEquals( fp0.getBoundName(),
                      fp0Clone.getBoundName() );
        assertEquals( fp0.getFactType(),
                      fp0Clone.getFactType() );

        assertEquals( fp0.constraintList.constraints.length,
                      fp0Clone.constraintList.constraints.length );
        assertNotSame( fp0.constraintList.constraints[0],
                       fp0Clone.constraintList.constraints[0] );
        assertNotNull( fp0Clone.constraintList.constraints[0] );
        assertTrue( fp0Clone.constraintList.constraints[0] instanceof SingleFieldConstraint );
        SingleFieldConstraint fp0sfc0Clone = (SingleFieldConstraint) fp0Clone.constraintList.constraints[0];
        assertEquals( fp0sfc0.getFieldBinding(),
                      fp0sfc0Clone.getFieldBinding() );
        assertEquals( fp0sfc0.getFieldName(),
                      fp0sfc0Clone.getFieldName() );
        assertEquals( fp0sfc0.getFieldType(),
                      fp0sfc0Clone.getFieldType() );
        assertEquals( fp0sfc0.getOperator(),
                      fp0sfc0Clone.getOperator() );
        assertNotNull( fp0sfc0Clone.getParameter( "fp0sfc0p0" ) );
        assertEquals( fp0sfc0.getParameter( "fp0sfc0p0" ),
                      fp0sfc0Clone.getParameter( "fp0sfc0p0" ) );
        assertEquals( fp0sfc0.getValue(),
                      fp0sfc0Clone.getValue() );

        assertNotSame( cfp0.getPatterns()[1],
                       cfp0Clone.getPatterns()[1] );
        assertNotNull( cfp0Clone.getPatterns()[1] );
        assertTrue( cfp0Clone.getPatterns()[1] instanceof FactPattern );
        FactPattern fp1Clone = (FactPattern) cfp0Clone.getPatterns()[1];
        assertEquals( fp1.getBoundName(),
                      fp1Clone.getBoundName() );
        assertEquals( fp1.getFactType(),
                      fp1Clone.getFactType() );

        assertEquals( fp1.constraintList.constraints.length,
                      fp1Clone.constraintList.constraints.length );
        assertNotSame( fp1.constraintList.constraints[0],
                       fp1Clone.constraintList.constraints[0] );
        assertNotNull( fp1Clone.constraintList.constraints[0] );
        assertTrue( fp1Clone.constraintList.constraints[0] instanceof SingleFieldConstraint );
        SingleFieldConstraint fp1sfc0Clone = (SingleFieldConstraint) fp1Clone.constraintList.constraints[0];
        assertEquals( fp1sfc0.getFieldBinding(),
                      fp1sfc0Clone.getFieldBinding() );
        assertEquals( fp1sfc0.getFieldName(),
                      fp1sfc0Clone.getFieldName() );
        assertEquals( fp1sfc0.getFieldType(),
                      fp1sfc0Clone.getFieldType() );
        assertEquals( fp1sfc0.getOperator(),
                      fp1sfc0Clone.getOperator() );
        assertNotNull( fp1sfc0Clone.getParameter( "fp1sfc0p0" ) );
        assertEquals( fp1sfc0.getParameter( "fp1sfc0p0" ),
                      fp1sfc0Clone.getParameter( "fp1sfc0p0" ) );
        assertEquals( fp1sfc0.getValue(),
                      fp1sfc0Clone.getValue() );
    }

    @Test
    public void testFreeFormLine() {
        RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];
        FreeFormLine ffl0 = new FreeFormLine();
        ffl0.text = "ffl0";
        model.lhs[0] = ffl0;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 1,
                      clone.lhs.length );
        assertNotSame( model.lhs[0],
                       clone.lhs[0] );
        assertNotNull( clone.lhs[0] );
        assertTrue( clone.lhs[0] instanceof FreeFormLine );
        FreeFormLine ffl0Clone = (FreeFormLine) clone.lhs[0];
        assertEquals( ffl0.text,
                      ffl0Clone.text );
    }

    @Test
    public void testFromAccumulateCompositeFactPattern() {
        RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];
        FromAccumulateCompositeFactPattern fp0 = new FromAccumulateCompositeFactPattern();
        fp0.setActionCode( "fp0.actionCode" );
        ExpressionFormLine efl0 = new ExpressionFormLine();
        efl0.setBinding( "$efl0" );
        ExpressionText efl0p0 = new ExpressionText( "efl0p0" );
        efl0.appendPart( efl0p0 );
        fp0.setExpression( efl0 );
        FactPattern fp0FactPattern = new FactPattern( "fp0FactPattern" );
        fp0.setFactPattern( fp0FactPattern );
        fp0.setFunction( "fp0.function" );
        fp0.setInitCode( "fp0.initCode" );
        fp0.setResultCode( "fp0.resultCode" );
        fp0.setReverseCode( "fp0.reverseCode" );
        FactPattern fp0SourcePattern = new FactPattern( "fp0SourcePattern" );
        fp0.setSourcePattern( fp0SourcePattern );

        model.lhs[0] = fp0;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 1,
                      clone.lhs.length );
        assertNotSame( model.lhs[0],
                       clone.lhs[0] );
        assertNotNull( clone.lhs[0] );
        assertTrue( clone.lhs[0] instanceof FromAccumulateCompositeFactPattern );
        FromAccumulateCompositeFactPattern fp0Clone = (FromAccumulateCompositeFactPattern) clone.lhs[0];
        assertEquals( fp0.getActionCode(),
                      fp0Clone.getActionCode() );
        assertEquals( fp0.getFunction(),
                      fp0Clone.getFunction() );
        assertEquals( fp0.getInitCode(),
                      fp0Clone.getInitCode() );
        assertEquals( fp0.getResultCode(),
                      fp0Clone.getResultCode() );
        assertEquals( fp0.getReverseCode(),
                      fp0Clone.getReverseCode() );

        assertNotSame( fp0.getExpression(),
                       fp0Clone.getExpression() );
        assertNotNull( fp0Clone.getExpression() );
        assertTrue( fp0Clone.getExpression() instanceof ExpressionFormLine );
        ExpressionFormLine efl0Clone = (ExpressionFormLine) fp0Clone.getExpression();

        assertEquals( 1,
                      fp0Clone.getExpression().getParts().size() );
        assertTrue( fp0Clone.getExpression().getParts().get( 0 ) instanceof ExpressionText );
        ExpressionText efl0p0Clone = (ExpressionText) fp0Clone.getExpression().getParts().get( 0 );
        assertEquals( efl0p0.getClassType(),
                      efl0p0Clone.getClassType() );
        assertEquals( efl0p0.getName(),
                      efl0p0Clone.getName() );
        assertEquals( efl0p0.getGenericType(),
                      efl0p0Clone.getGenericType() );
        assertEquals( efl0p0.getParametricType(),
                      efl0p0Clone.getParametricType() );

        assertEquals( efl0.getBinding(),
                      efl0Clone.getBinding() );
        assertEquals( efl0.getClassType(),
                      efl0Clone.getClassType() );
        assertEquals( efl0.getFieldName(),
                      efl0Clone.getFieldName() );
        assertEquals( efl0.getGenericType(),
                      efl0Clone.getGenericType() );
        assertEquals( efl0.getParametricType(),
                      efl0Clone.getParametricType() );
        assertEquals( efl0.getCurrentName(),
                      efl0Clone.getCurrentName() );

        assertNotSame( fp0.getFactPattern(),
                       fp0Clone.getFactPattern() );
        assertNotNull( fp0Clone.getFactPattern() );
        assertTrue( fp0Clone.getFactPattern() instanceof FactPattern );
        FactPattern fp0FactPatternClone = (FactPattern) fp0Clone.getFactPattern();
        assertEquals( fp0FactPattern.getBoundName(),
                      fp0FactPatternClone.getBoundName() );
        assertEquals( fp0FactPattern.getFactType(),
                      fp0FactPatternClone.getFactType() );

        assertNotSame( fp0.getSourcePattern(),
                       fp0Clone.getSourcePattern() );
        assertNotNull( fp0Clone.getSourcePattern() );
        assertTrue( fp0Clone.getSourcePattern() instanceof FactPattern );
        FactPattern fp0SourcePatternClone = (FactPattern) fp0Clone.getSourcePattern();
        assertEquals( fp0SourcePattern.getBoundName(),
                      fp0SourcePatternClone.getBoundName() );
        assertEquals( fp0SourcePattern.getFactType(),
                      fp0SourcePatternClone.getFactType() );
    }

    @Test
    public void testFromCollectCompositeFactPattern() {
        RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];
        FromCollectCompositeFactPattern fp0 = new FromCollectCompositeFactPattern();
        ExpressionFormLine efl0 = new ExpressionFormLine();
        efl0.setBinding( "$efl0" );
        ExpressionText efl0p0 = new ExpressionText( "efl0p0" );
        efl0.appendPart( efl0p0 );
        fp0.setExpression( efl0 );
        FactPattern fp0FactPattern = new FactPattern( "fp0FactPattern" );
        fp0.setFactPattern( fp0FactPattern );
        FactPattern fp0RightPattern = new FactPattern( "fp0RightPattern" );
        fp0.setRightPattern( fp0RightPattern );

        model.lhs[0] = fp0;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 1,
                      clone.lhs.length );
        assertNotSame( model.lhs[0],
                       clone.lhs[0] );
        assertNotNull( clone.lhs[0] );
        assertTrue( clone.lhs[0] instanceof FromCollectCompositeFactPattern );
        FromCollectCompositeFactPattern fp0Clone = (FromCollectCompositeFactPattern) clone.lhs[0];

        assertNotSame( fp0.getExpression(),
                       fp0Clone.getExpression() );
        assertNotNull( fp0Clone.getExpression() );
        assertTrue( fp0Clone.getExpression() instanceof ExpressionFormLine );
        ExpressionFormLine efl0Clone = (ExpressionFormLine) fp0Clone.getExpression();

        assertEquals( 1,
                      fp0Clone.getExpression().getParts().size() );
        assertTrue( fp0Clone.getExpression().getParts().get( 0 ) instanceof ExpressionText );
        ExpressionText efl0p0Clone = (ExpressionText) fp0Clone.getExpression().getParts().get( 0 );
        assertEquals( efl0p0.getClassType(),
                      efl0p0Clone.getClassType() );
        assertEquals( efl0p0.getName(),
                      efl0p0Clone.getName() );
        assertEquals( efl0p0.getGenericType(),
                      efl0p0Clone.getGenericType() );
        assertEquals( efl0p0.getParametricType(),
                      efl0p0Clone.getParametricType() );

        assertEquals( efl0.getBinding(),
                      efl0Clone.getBinding() );
        assertEquals( efl0.getClassType(),
                      efl0Clone.getClassType() );
        assertEquals( efl0.getFieldName(),
                      efl0Clone.getFieldName() );
        assertEquals( efl0.getGenericType(),
                      efl0Clone.getGenericType() );
        assertEquals( efl0.getParametricType(),
                      efl0Clone.getParametricType() );
        assertEquals( efl0.getCurrentName(),
                      efl0Clone.getCurrentName() );

        assertNotSame( fp0.getFactPattern(),
                       fp0Clone.getFactPattern() );
        assertNotNull( fp0Clone.getFactPattern() );
        assertTrue( fp0Clone.getFactPattern() instanceof FactPattern );
        FactPattern fp0FactPatternClone = (FactPattern) fp0Clone.getFactPattern();
        assertEquals( fp0FactPattern.getBoundName(),
                      fp0FactPatternClone.getBoundName() );
        assertEquals( fp0FactPattern.getFactType(),
                      fp0FactPatternClone.getFactType() );

        assertNotSame( fp0.getRightPattern(),
                       fp0Clone.getRightPattern() );
        assertNotNull( fp0Clone.getRightPattern() );
        assertTrue( fp0Clone.getRightPattern() instanceof FactPattern );
        FactPattern fp0RightPatternClone = (FactPattern) fp0Clone.getRightPattern();
        assertEquals( fp0RightPattern.getBoundName(),
                      fp0RightPatternClone.getBoundName() );
        assertEquals( fp0RightPattern.getFactType(),
                      fp0RightPatternClone.getFactType() );
    }

    @Test
    public void testFromCompositeFactPattern() {
        RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];
        FromCompositeFactPattern fp0 = new FromCompositeFactPattern();
        ExpressionFormLine efl0 = new ExpressionFormLine();
        efl0.setBinding( "$efl0" );
        ExpressionText efl0p0 = new ExpressionText( "efl0p0" );
        efl0.appendPart( efl0p0 );
        fp0.setExpression( efl0 );
        FactPattern fp0FactPattern = new FactPattern( "fp0FactPattern" );
        fp0.setFactPattern( fp0FactPattern );

        model.lhs[0] = fp0;

        RuleModelCloneVisitor cloneVisitor = new RuleModelCloneVisitor();
        RuleModel clone = cloneVisitor.visitRuleModel( model );

        assertEquals( 1,
                      clone.lhs.length );
        assertNotSame( model.lhs[0],
                       clone.lhs[0] );
        assertNotNull( clone.lhs[0] );
        assertTrue( clone.lhs[0] instanceof FromCompositeFactPattern );
        FromCompositeFactPattern fp0Clone = (FromCompositeFactPattern) clone.lhs[0];

        assertNotSame( fp0.getExpression(),
                       fp0Clone.getExpression() );
        assertNotNull( fp0Clone.getExpression() );
        assertTrue( fp0Clone.getExpression() instanceof ExpressionFormLine );
        ExpressionFormLine efl0Clone = (ExpressionFormLine) fp0Clone.getExpression();

        assertEquals( 1,
                      fp0Clone.getExpression().getParts().size() );
        assertTrue( fp0Clone.getExpression().getParts().get( 0 ) instanceof ExpressionText );
        ExpressionText efl0p0Clone = (ExpressionText) fp0Clone.getExpression().getParts().get( 0 );
        assertEquals( efl0p0.getClassType(),
                      efl0p0Clone.getClassType() );
        assertEquals( efl0p0.getName(),
                      efl0p0Clone.getName() );
        assertEquals( efl0p0.getGenericType(),
                      efl0p0Clone.getGenericType() );
        assertEquals( efl0p0.getParametricType(),
                      efl0p0Clone.getParametricType() );

        assertEquals( efl0.getBinding(),
                      efl0Clone.getBinding() );
        assertEquals( efl0.getClassType(),
                      efl0Clone.getClassType() );
        assertEquals( efl0.getFieldName(),
                      efl0Clone.getFieldName() );
        assertEquals( efl0.getGenericType(),
                      efl0Clone.getGenericType() );
        assertEquals( efl0.getParametricType(),
                      efl0Clone.getParametricType() );
        assertEquals( efl0.getCurrentName(),
                      efl0Clone.getCurrentName() );

        assertNotSame( fp0.getFactPattern(),
                       fp0Clone.getFactPattern() );
        assertNotNull( fp0Clone.getFactPattern() );
        assertTrue( fp0Clone.getFactPattern() instanceof FactPattern );
        FactPattern fp0FactPatternClone = (FactPattern) fp0Clone.getFactPattern();
        assertEquals( fp0FactPattern.getBoundName(),
                      fp0FactPatternClone.getBoundName() );
        assertEquals( fp0FactPattern.getFactType(),
                      fp0FactPatternClone.getFactType() );
    }

}
