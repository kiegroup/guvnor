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
package org.drools.ide.common.client.modeldriven.dt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLConditionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLRuleModel;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.junit.Test;

/**
 * Tests relating to the extended function of Fact\Field bindings
 */
public class BRLRuleModelTest {

    @Test
    public void testOnlyDecisionTableColumns() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( ins );

        BRLRuleModel model = new BRLRuleModel( dt );

        assertNotNull( model.getAllVariables() );
        assertEquals( 3,
                      model.getAllVariables().size() );
        assertTrue( model.getAllVariables().contains( "$p1" ) );
        assertTrue( model.getAllVariables().contains( "$c1" ) );
        assertTrue( model.getAllVariables().contains( "$ins" ) );
    }

    @Test
    public void testDecisionTableColumnsWithLHS() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        BRLConditionColumn brlCondition = new BRLConditionColumn();
        FactPattern fp = new FactPattern( "Driver" );
        fp.setBoundName( "$brl1" );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldBinding( "$sfc1" );
        sfc1.setOperator( "==" );
        sfc1.setFactType( "Driver" );
        sfc1.setFieldName( "name" );
        sfc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );

        fp.addConstraint( sfc1 );
        brlCondition.getDefinition().add( fp );
        dt.getConditions().add( brlCondition );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( ins );

        BRLRuleModel model = new BRLRuleModel( dt );

        assertNotNull( model.getAllVariables() );
        assertEquals( 5,
                      model.getAllVariables().size() );
        assertTrue( model.getAllVariables().contains( "$p1" ) );
        assertTrue( model.getAllVariables().contains( "$c1" ) );
        assertTrue( model.getAllVariables().contains( "$ins" ) );
        assertTrue( model.getAllVariables().contains( "$brl1" ) );
        assertTrue( model.getAllVariables().contains( "$sfc1" ) );
    }

    @Test
    public void testDecisionTableColumnsWithLHSBoundFacts() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        BRLConditionColumn brlCondition = new BRLConditionColumn();
        FactPattern fp = new FactPattern( "Driver" );
        fp.setBoundName( "$brl1" );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldBinding( "$sfc1" );
        sfc1.setOperator( "==" );
        sfc1.setFactType( "Driver" );
        sfc1.setFieldName( "name" );
        sfc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );

        fp.addConstraint( sfc1 );
        brlCondition.getDefinition().add( fp );
        dt.getConditions().add( brlCondition );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( ins );

        BRLRuleModel model = new BRLRuleModel( dt );

        assertNotNull( model.getLHSBoundFacts() );
        assertEquals( 2,
                      model.getLHSBoundFacts().size() );
        assertTrue( model.getLHSBoundFacts().contains( "$p1" ) );
        assertTrue( model.getLHSBoundFacts().contains( "$brl1" ) );

        assertNotNull( model.getLHSBindingType( "$p1" ) );
        assertEquals( "Driver",
                      model.getLHSBindingType( "$p1" ) );
        assertNotNull( model.getLHSBindingType( "$brl1" ) );
        assertEquals( "Driver",
                      model.getLHSBindingType( "$brl1" ) );

        FactPattern r1 = model.getLHSBoundFact( "$p1" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof BRLRuleModel.Pattern52FactPatternAdaptor );
        BRLRuleModel.Pattern52FactPatternAdaptor raif1 = (BRLRuleModel.Pattern52FactPatternAdaptor) r1;
        assertEquals( "Driver",
                      raif1.getFactType() );

        FactPattern r2 = model.getLHSBoundFact( "$brl1" );
        assertNotNull( r2 );
        assertEquals( "Driver",
                      r2.getFactType() );
    }

    @Test
    public void testDecisionTableColumnsWithLHSBoundFields() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        BRLConditionColumn brlCondition = new BRLConditionColumn();
        FactPattern fp = new FactPattern( "Driver" );
        fp.setBoundName( "$brl1" );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldBinding( "$sfc1" );
        sfc1.setOperator( "==" );
        sfc1.setFactType( "Driver" );
        sfc1.setFieldName( "name" );
        sfc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );

        fp.addConstraint( sfc1 );
        brlCondition.getDefinition().add( fp );
        dt.getConditions().add( brlCondition );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( ins );

        BRLRuleModel model = new BRLRuleModel( dt );

        FieldConstraint fcr1 = model.getLHSBoundField( "$sfc1" );
        assertNotNull( fcr1 );
        assertTrue( fcr1 instanceof SingleFieldConstraint );
        SingleFieldConstraint fcr1sfc = (SingleFieldConstraint) fcr1;
        assertEquals( "name",
                      fcr1sfc.getFieldName() );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      fcr1sfc.getFieldType() );
    }

    @Test
    public void testDecisionTableColumnsWithRHS() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( ins );

        BRLActionColumn brlAction = new BRLActionColumn();
        ActionInsertFact aif = new ActionInsertFact( "Person" );
        aif.setBoundName( "$aif" );
        aif.addFieldValue( new ActionFieldValue( "rating",
                                                 null,
                                                 SuggestionCompletionEngine.TYPE_STRING ) );
        aif.fieldValues[0].nature = BaseSingleFieldConstraint.TYPE_LITERAL;

        brlAction.getDefinition().add( aif );
        dt.getActionCols().add( brlAction );

        BRLRuleModel model = new BRLRuleModel( dt );

        assertNotNull( model.getAllVariables() );
        assertEquals( 4,
                      model.getAllVariables().size() );
        assertTrue( model.getAllVariables().contains( "$p1" ) );
        assertTrue( model.getAllVariables().contains( "$c1" ) );
        assertTrue( model.getAllVariables().contains( "$ins" ) );
        assertTrue( model.getAllVariables().contains( "$aif" ) );

        assertNotNull( model.getRHSBoundFacts() );
        assertEquals( 2,
                      model.getRHSBoundFacts().size() );
        assertTrue( model.getRHSBoundFacts().contains( "$ins" ) );
        assertTrue( model.getRHSBoundFacts().contains( "$aif" ) );

        ActionInsertFact r1 = model.getRHSBoundFact( "$ins" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof BRLRuleModel.ActionInsertFactCol52ActionInsertFactAdaptor );
        BRLRuleModel.ActionInsertFactCol52ActionInsertFactAdaptor raif1 = (BRLRuleModel.ActionInsertFactCol52ActionInsertFactAdaptor) r1;
        assertEquals( "Person",
                      raif1.factType );
        assertEquals( "rating",
                      raif1.fieldValues[0].field );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      raif1.fieldValues[0].type );
        assertNull( raif1.fieldValues[0].value );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif1.fieldValues[0].nature );

        ActionInsertFact r2 = model.getRHSBoundFact( "$aif" );
        assertNotNull( r2 );
        assertTrue( r2 instanceof ActionInsertFact );
        ActionInsertFact raif2 = (ActionInsertFact) r2;
        assertEquals( "Person",
                      raif2.factType );
        assertEquals( "rating",
                      raif2.fieldValues[0].field );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      raif2.fieldValues[0].type );
        assertNull( raif2.fieldValues[0].value );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif2.fieldValues[0].nature );

    }

    @Test
    public void testDecisionTableColumnsWithRHSBoundFacts() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( ins );

        ActionInsertFactCol52 ins2 = new ActionInsertFactCol52();
        ins2.setInsertLogical( true );
        ins2.setBoundName( "$ins2" );
        ins2.setFactField( "rating2" );
        ins2.setFactType( "Person2" );
        ins2.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( ins2 );

        BRLActionColumn brlAction = new BRLActionColumn();
        ActionInsertFact aif = new ActionInsertFact( "Person" );
        aif.setBoundName( "$aif" );
        aif.addFieldValue( new ActionFieldValue( "rating",
                                                 null,
                                                 SuggestionCompletionEngine.TYPE_STRING ) );
        aif.fieldValues[0].nature = BaseSingleFieldConstraint.TYPE_LITERAL;

        brlAction.getDefinition().add( aif );
        dt.getActionCols().add( brlAction );

        BRLRuleModel model = new BRLRuleModel( dt );

        assertNotNull( model.getRHSBoundFacts() );
        assertEquals( 3,
                      model.getRHSBoundFacts().size() );
        assertTrue( model.getRHSBoundFacts().contains( "$ins" ) );
        assertTrue( model.getRHSBoundFacts().contains( "$ins2" ) );
        assertTrue( model.getRHSBoundFacts().contains( "$aif" ) );

        ActionInsertFact r1 = model.getRHSBoundFact( "$ins" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof BRLRuleModel.ActionInsertFactCol52ActionInsertFactAdaptor );
        BRLRuleModel.ActionInsertFactCol52ActionInsertFactAdaptor raif1 = (BRLRuleModel.ActionInsertFactCol52ActionInsertFactAdaptor) r1;
        assertEquals( "Person",
                      raif1.factType );
        assertEquals( "rating",
                      raif1.fieldValues[0].field );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      raif1.fieldValues[0].type );
        assertNull( raif1.fieldValues[0].value );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif1.fieldValues[0].nature );

        ActionInsertFact r2 = model.getRHSBoundFact( "$ins2" );
        assertNotNull( r2 );
        assertTrue( r2 instanceof BRLRuleModel.ActionInsertFactCol52ActionInsertLogicalFactAdaptor );
        BRLRuleModel.ActionInsertFactCol52ActionInsertLogicalFactAdaptor raif2 = (BRLRuleModel.ActionInsertFactCol52ActionInsertLogicalFactAdaptor) r2;
        assertEquals( "Person2",
                      raif2.factType );
        assertEquals( "rating2",
                      raif2.fieldValues[0].field );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      raif2.fieldValues[0].type );
        assertNull( raif2.fieldValues[0].value );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif2.fieldValues[0].nature );

        ActionInsertFact r3 = model.getRHSBoundFact( "$aif" );
        assertNotNull( r3 );
        assertTrue( r3 instanceof ActionInsertFact );
        ActionInsertFact raif3 = (ActionInsertFact) r3;
        assertEquals( "Person",
                      raif3.factType );
        assertEquals( "rating",
                      raif3.fieldValues[0].field );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      raif3.fieldValues[0].type );
        assertNull( raif3.fieldValues[0].value );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif3.fieldValues[0].nature );

    }

    @Test
    public void testRuleModelLHSBoundFacts() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        //Setup Decision Table columns
        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        //Setup RuleModel columns (new BRLConditionColumn being added)
        BRLRuleModel model = new BRLRuleModel( dt );
        FactPattern fp = new FactPattern( "Driver" );
        fp.setBoundName( "$brl1" );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldBinding( "$sfc1" );
        sfc1.setOperator( "==" );
        sfc1.setFactType( "Driver" );
        sfc1.setFieldName( "name" );
        sfc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );

        fp.addConstraint( sfc1 );
        model.addLhsItem( fp );

        //Checks
        assertNotNull( model.getLHSBoundFacts() );
        assertEquals( 2,
                      model.getLHSBoundFacts().size() );
        assertTrue( model.getLHSBoundFacts().contains( "$p1" ) );
        assertTrue( model.getLHSBoundFacts().contains( "$brl1" ) );

        assertNotNull( model.getLHSBindingType( "$p1" ) );
        assertEquals( "Driver",
                      model.getLHSBindingType( "$p1" ) );
        assertNotNull( model.getLHSBindingType( "$brl1" ) );
        assertEquals( "Driver",
                      model.getLHSBindingType( "$brl1" ) );

        FactPattern r1 = model.getLHSBoundFact( "$p1" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof BRLRuleModel.Pattern52FactPatternAdaptor );
        BRLRuleModel.Pattern52FactPatternAdaptor raif1 = (BRLRuleModel.Pattern52FactPatternAdaptor) r1;
        assertEquals( "Driver",
                      raif1.getFactType() );

        FactPattern r2 = model.getLHSBoundFact( "$brl1" );
        assertNotNull( r2 );
        assertEquals( "Driver",
                      r2.getFactType() );
    }

    @Test
    public void testRuleModelLHSBoundFacts_NoDuplicates() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        //Setup Decision Table columns (with existing BRLConditionColumn)
        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        BRLConditionColumn brlCondition = new BRLConditionColumn();
        FactPattern fp1 = new FactPattern( "Driver" );
        fp1.setBoundName( "$brl1" );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldBinding( "$sfc1" );
        sfc1.setOperator( "==" );
        sfc1.setFactType( "Driver" );
        sfc1.setFieldName( "name" );
        sfc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );

        fp1.addConstraint( sfc1 );
        brlCondition.getDefinition().add( fp1 );
        dt.getConditions().add( brlCondition );

        //Setup RuleModel columns (existing BRLConditionColumn being edited)
        BRLRuleModel model = new BRLRuleModel( dt );
        FactPattern fp2 = new FactPattern( "Driver" );
        fp2.setBoundName( "$brl1" );

        SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFieldBinding( "$sfc1" );
        sfc2.setOperator( "==" );
        sfc2.setFactType( "Driver" );
        sfc2.setFieldName( "name" );
        sfc2.setFieldType( SuggestionCompletionEngine.TYPE_STRING );

        fp2.addConstraint( sfc2 );
        model.addLhsItem( fp2 );

        //Checks
        assertNotNull( model.getLHSBoundFacts() );
        assertEquals( 2,
                      model.getLHSBoundFacts().size() );
        assertTrue( model.getLHSBoundFacts().contains( "$p1" ) );
        assertTrue( model.getLHSBoundFacts().contains( "$brl1" ) );

        assertNotNull( model.getLHSBindingType( "$p1" ) );
        assertEquals( "Driver",
                      model.getLHSBindingType( "$p1" ) );
        assertNotNull( model.getLHSBindingType( "$brl1" ) );
        assertEquals( "Driver",
                      model.getLHSBindingType( "$brl1" ) );

        FactPattern r1 = model.getLHSBoundFact( "$p1" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof BRLRuleModel.Pattern52FactPatternAdaptor );
        BRLRuleModel.Pattern52FactPatternAdaptor raif1 = (BRLRuleModel.Pattern52FactPatternAdaptor) r1;
        assertEquals( "Driver",
                      raif1.getFactType() );

        FactPattern r2 = model.getLHSBoundFact( "$brl1" );
        assertNotNull( r2 );
        assertEquals( "Driver",
                      r2.getFactType() );
    }

    @Test
    public void testRuleModelRHSBoundFacts() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        //Setup Decision Table columns
        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( ins );

        //Setup RuleModel columns (new BRLActionColumn being added)
        BRLRuleModel model = new BRLRuleModel( dt );
        ActionInsertFact aif = new ActionInsertFact( "Person" );
        aif.setBoundName( "$aif" );
        aif.addFieldValue( new ActionFieldValue( "rating",
                                                 null,
                                                 SuggestionCompletionEngine.TYPE_STRING ) );
        aif.fieldValues[0].nature = BaseSingleFieldConstraint.TYPE_LITERAL;
        model.addRhsItem( aif );

        //Checks
        assertNotNull( model.getRHSBoundFacts() );
        assertEquals( 2,
                      model.getRHSBoundFacts().size() );
        assertTrue( model.getRHSBoundFacts().contains( "$ins" ) );
        assertTrue( model.getRHSBoundFacts().contains( "$aif" ) );

        ActionInsertFact r1 = model.getRHSBoundFact( "$ins" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof BRLRuleModel.ActionInsertFactCol52ActionInsertFactAdaptor );
        BRLRuleModel.ActionInsertFactCol52ActionInsertFactAdaptor raif1 = (BRLRuleModel.ActionInsertFactCol52ActionInsertFactAdaptor) r1;
        assertEquals( "Person",
                      raif1.factType );
        assertEquals( "rating",
                      raif1.fieldValues[0].field );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      raif1.fieldValues[0].type );
        assertNull( raif1.fieldValues[0].value );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif1.fieldValues[0].nature );

        ActionInsertFact r2 = model.getRHSBoundFact( "$aif" );
        assertNotNull( r2 );
        assertTrue( r2 instanceof ActionInsertFact );
        ActionInsertFact raif2 = (ActionInsertFact) r2;
        assertEquals( "Person",
                      raif2.factType );
        assertEquals( "rating",
                      raif2.fieldValues[0].field );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      raif2.fieldValues[0].type );
        assertNull( raif2.fieldValues[0].value );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif2.fieldValues[0].nature );
    }

    @Test
    public void testRuleModelRHSBoundFacts_NoDuplicates() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        //Setup Decision Table columns (with existing BRLActionColumn)
        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( ins );

        BRLActionColumn brlAction = new BRLActionColumn();
        ActionInsertFact aif1 = new ActionInsertFact( "Person" );
        aif1.setBoundName( "$aif" );
        aif1.addFieldValue( new ActionFieldValue( "rating",
                                                  null,
                                                  SuggestionCompletionEngine.TYPE_STRING ) );
        aif1.fieldValues[0].nature = BaseSingleFieldConstraint.TYPE_LITERAL;

        brlAction.getDefinition().add( aif1 );
        dt.getActionCols().add( brlAction );

        //Setup RuleModel columns (existing BRLActionColumn being edited)
        BRLRuleModel model = new BRLRuleModel( dt );
        ActionInsertFact aif2 = new ActionInsertFact( "Person" );
        aif2.setBoundName( "$aif" );
        aif2.addFieldValue( new ActionFieldValue( "rating",
                                                  null,
                                                  SuggestionCompletionEngine.TYPE_STRING ) );
        aif2.fieldValues[0].nature = BaseSingleFieldConstraint.TYPE_LITERAL;
        model.addRhsItem( aif2 );

        //Checks
        assertNotNull( model.getRHSBoundFacts() );
        assertEquals( 2,
                      model.getRHSBoundFacts().size() );
        assertTrue( model.getRHSBoundFacts().contains( "$ins" ) );
        assertTrue( model.getRHSBoundFacts().contains( "$aif" ) );

        ActionInsertFact r1 = model.getRHSBoundFact( "$ins" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof BRLRuleModel.ActionInsertFactCol52ActionInsertFactAdaptor );
        BRLRuleModel.ActionInsertFactCol52ActionInsertFactAdaptor raif1 = (BRLRuleModel.ActionInsertFactCol52ActionInsertFactAdaptor) r1;
        assertEquals( "Person",
                      raif1.factType );
        assertEquals( "rating",
                      raif1.fieldValues[0].field );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      raif1.fieldValues[0].type );
        assertNull( raif1.fieldValues[0].value );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif1.fieldValues[0].nature );

        ActionInsertFact r2 = model.getRHSBoundFact( "$aif" );
        assertNotNull( r2 );
        assertTrue( r2 instanceof ActionInsertFact );
        ActionInsertFact raif2 = (ActionInsertFact) r2;
        assertEquals( "Person",
                      raif2.factType );
        assertEquals( "rating",
                      raif2.fieldValues[0].field );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      raif2.fieldValues[0].type );
        assertNull( raif2.fieldValues[0].value );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif2.fieldValues[0].nature );
    }

}
