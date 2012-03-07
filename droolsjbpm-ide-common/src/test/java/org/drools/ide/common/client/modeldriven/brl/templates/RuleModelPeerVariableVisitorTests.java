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

import java.util.List;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.ActionUpdateField;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.ExpressionField;
import org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;
import org.drools.ide.common.client.modeldriven.brl.templates.RuleModelPeerVariableVisitor.ValueHolder;
import org.junit.Test;

public class RuleModelPeerVariableVisitorTests {

    @Test
    public void testActionInsertFact() {
        TemplateModel model = new TemplateModel();

        model.rhs = new IAction[2];

        //Both fields are Template Keys
        ActionInsertFact aif0 = new ActionInsertFact( "AIF0" );
        ActionFieldValue aif0f0 = new ActionFieldValue( "AIF0F0",
                                                        "AIF0F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        aif0f0.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        aif0.addFieldValue( aif0f0 );
        ActionFieldValue aif0f1 = new ActionFieldValue( "AIF0F1",
                                                        "AIF0F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        aif0f1.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        aif0.addFieldValue( aif0f1 );
        model.rhs[0] = aif0;

        //One field is a Template Key the other is a literal
        ActionInsertFact aif1 = new ActionInsertFact( "AIF1" );
        ActionFieldValue aif1f0 = new ActionFieldValue( "AIF1F0",
                                                        "AIF1F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        aif1f0.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        aif1.addFieldValue( aif1f0 );
        ActionFieldValue aif1f1 = new ActionFieldValue( "AIF1F1",
                                                        "AIF1F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        aif1f1.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        aif1.addFieldValue( aif1f1 );
        model.rhs[1] = aif1;

        //Test first pattern, with two Template Keys
        RuleModelPeerVariableVisitor visitor0 = new RuleModelPeerVariableVisitor( model,
                                                                                  "AIF0F0Value" );
        List<ValueHolder> variables0 = visitor0.getPeerVariables();

        assertNotNull( variables0 );
        assertEquals( 2,
                      variables0.size() );

        ValueHolder v0vh0 = variables0.get( 0 );
        assertNotNull( v0vh0 );
        assertEquals( "AIF0F0",
                      v0vh0.getFieldName() );
        assertEquals( "AIF0F0Value",
                      v0vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh0.getType() );

        ValueHolder v0vh1 = variables0.get( 1 );
        assertNotNull( v0vh1 );
        assertEquals( "AIF0F1",
                      v0vh1.getFieldName() );
        assertEquals( "AIF0F1Value",
                      v0vh1.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh1.getType() );

        //Test second pattern, with one Template Key and one Literal
        RuleModelPeerVariableVisitor visitor1 = new RuleModelPeerVariableVisitor( model,
                                                                                  "AIF1F0Value" );
        List<ValueHolder> variables1 = visitor1.getPeerVariables();

        assertNotNull( variables1 );
        assertEquals( 2,
                      variables1.size() );

        ValueHolder v1vh0 = variables1.get( 0 );
        assertNotNull( v1vh0 );
        assertEquals( "AIF1F0",
                      v1vh0.getFieldName() );
        assertEquals( "AIF1F0Value",
                      v1vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v1vh0.getType() );

        ValueHolder v1vh1 = variables1.get( 1 );
        assertNotNull( v1vh1 );
        assertEquals( "AIF1F1",
                      v1vh1.getFieldName() );
        assertEquals( "AIF1F1Value",
                      v1vh1.getValue() );
        assertEquals( ValueHolder.Type.VALUE,
                      v1vh1.getType() );
    }

    @Test
    public void testActionSetField() {
        TemplateModel model = new TemplateModel();

        model.rhs = new IAction[2];

        //Both fields are Template Keys
        ActionSetField asf0 = new ActionSetField( "ASF0" );
        ActionFieldValue asf0f0 = new ActionFieldValue( "ASF0F0",
                                                        "ASF0F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        asf0f0.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        asf0.addFieldValue( asf0f0 );
        ActionFieldValue asf0f1 = new ActionFieldValue( "ASF0F1",
                                                        "ASF0F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        asf0f1.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        asf0.addFieldValue( asf0f1 );
        model.rhs[0] = asf0;

        //One field is a Template Key the other is a literal
        ActionSetField asf1 = new ActionSetField( "ASF1" );
        ActionFieldValue asf1f0 = new ActionFieldValue( "ASF1F0",
                                                        "ASF1F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        asf1f0.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        asf1.addFieldValue( asf1f0 );
        ActionFieldValue asf1f1 = new ActionFieldValue( "ASF1F1",
                                                        "ASF1F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        asf1f1.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        asf1.addFieldValue( asf1f1 );
        model.rhs[1] = asf1;

        //Test first pattern, with two Template Keys
        RuleModelPeerVariableVisitor visitor0 = new RuleModelPeerVariableVisitor( model,
                                                                                  "ASF0F0Value" );
        List<ValueHolder> variables0 = visitor0.getPeerVariables();

        assertNotNull( variables0 );
        assertEquals( 2,
                      variables0.size() );

        ValueHolder v0vh0 = variables0.get( 0 );
        assertNotNull( v0vh0 );
        assertEquals( "ASF0F0",
                      v0vh0.getFieldName() );
        assertEquals( "ASF0F0Value",
                      v0vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh0.getType() );

        ValueHolder v0vh1 = variables0.get( 1 );
        assertNotNull( v0vh1 );
        assertEquals( "ASF0F1",
                      v0vh1.getFieldName() );
        assertEquals( "ASF0F1Value",
                      v0vh1.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh1.getType() );

        //Test second pattern, with one Template Key and one Literal
        RuleModelPeerVariableVisitor visitor1 = new RuleModelPeerVariableVisitor( model,
                                                                                  "ASF1F0Value" );
        List<ValueHolder> variables1 = visitor1.getPeerVariables();

        assertNotNull( variables1 );
        assertEquals( 2,
                      variables1.size() );

        ValueHolder v1vh0 = variables1.get( 0 );
        assertNotNull( v1vh0 );
        assertEquals( "ASF1F0",
                      v1vh0.getFieldName() );
        assertEquals( "ASF1F0Value",
                      v1vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v1vh0.getType() );

        ValueHolder v1vh1 = variables1.get( 1 );
        assertNotNull( v1vh1 );
        assertEquals( "ASF1F1",
                      v1vh1.getFieldName() );
        assertEquals( "ASF1F1Value",
                      v1vh1.getValue() );
        assertEquals( ValueHolder.Type.VALUE,
                      v1vh1.getType() );
    }

    @Test
    public void testActionUpdateField() {
        TemplateModel model = new TemplateModel();

        model.rhs = new IAction[2];

        //Both fields are Template Keys
        ActionUpdateField auf0 = new ActionUpdateField();
        ActionFieldValue auf0f0 = new ActionFieldValue( "AUF0F0",
                                                        "AUF0F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        auf0f0.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        auf0.addFieldValue( auf0f0 );
        ActionFieldValue auf0f1 = new ActionFieldValue( "AUF0F1",
                                                        "AUF0F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        auf0f1.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        auf0.addFieldValue( auf0f1 );
        model.rhs[0] = auf0;

        //One field is a Template Key the other is a literal
        ActionUpdateField auf1 = new ActionUpdateField();
        ActionFieldValue auf1f0 = new ActionFieldValue( "AUF1F0",
                                                        "AUF1F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        auf1f0.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        auf1.addFieldValue( auf1f0 );
        ActionFieldValue auf1f1 = new ActionFieldValue( "AUF1F1",
                                                        "AUF1F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        auf1f1.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        auf1.addFieldValue( auf1f1 );
        model.rhs[1] = auf1;

        //Test first pattern, with two Template Keys
        RuleModelPeerVariableVisitor visitor0 = new RuleModelPeerVariableVisitor( model,
                                                                                  "AUF0F0Value" );
        List<ValueHolder> variables0 = visitor0.getPeerVariables();

        assertNotNull( variables0 );
        assertEquals( 2,
                      variables0.size() );

        ValueHolder v0vh0 = variables0.get( 0 );
        assertNotNull( v0vh0 );
        assertEquals( "AUF0F0",
                      v0vh0.getFieldName() );
        assertEquals( "AUF0F0Value",
                      v0vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh0.getType() );

        ValueHolder v0vh1 = variables0.get( 1 );
        assertNotNull( v0vh1 );
        assertEquals( "AUF0F1",
                      v0vh1.getFieldName() );
        assertEquals( "AUF0F1Value",
                      v0vh1.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh1.getType() );

        //Test second pattern, with one Template Key and one Literal
        RuleModelPeerVariableVisitor visitor1 = new RuleModelPeerVariableVisitor( model,
                                                                                  "AUF1F0Value" );
        List<ValueHolder> variables1 = visitor1.getPeerVariables();

        assertNotNull( variables1 );
        assertEquals( 2,
                      variables1.size() );

        ValueHolder v1vh0 = variables1.get( 0 );
        assertNotNull( v1vh0 );
        assertEquals( "AUF1F0",
                      v1vh0.getFieldName() );
        assertEquals( "AUF1F0Value",
                      v1vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v1vh0.getType() );

        ValueHolder v1vh1 = variables1.get( 1 );
        assertNotNull( v1vh1 );
        assertEquals( "AUF1F1",
                      v1vh1.getFieldName() );
        assertEquals( "AUF1F1Value",
                      v1vh1.getValue() );
        assertEquals( ValueHolder.Type.VALUE,
                      v1vh1.getType() );
    }

    @Test
    public void testFactPattern_SingleFieldConstraints() {
        TemplateModel model = new TemplateModel();

        model.lhs = new IPattern[2];

        //Both fields are Template Keys
        FactPattern fp0 = new FactPattern();
        fp0.setFactType( "FT0" );

        SingleFieldConstraint sfc0p0 = new SingleFieldConstraint();
        sfc0p0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc0p0.setFieldBinding( "$sfc0p0" );
        sfc0p0.setFactType( "FT0" );
        sfc0p0.setFieldName( "sfc0p0" );
        sfc0p0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc0p0.setOperator( "==" );
        sfc0p0.setValue( "sfc0p0Value" );
        fp0.addConstraint( sfc0p0 );

        SingleFieldConstraint sfc1p0 = new SingleFieldConstraint();
        sfc1p0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc1p0.setFieldBinding( "$sfc1p0" );
        sfc1p0.setFactType( "FT0" );
        sfc1p0.setFieldName( "sfc1p0" );
        sfc1p0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1p0.setOperator( "==" );
        sfc1p0.setValue( "sfc1p0Value" );
        fp0.addConstraint( sfc1p0 );

        model.lhs[0] = fp0;

        //One field is a Template Key the other is a literal
        FactPattern fp1 = new FactPattern();
        fp1.setFactType( "FT1" );

        SingleFieldConstraint sfc0p1 = new SingleFieldConstraint();
        sfc0p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc0p1.setFieldBinding( "$sfc0p1" );
        sfc0p1.setFactType( "FT1" );
        sfc0p1.setFieldName( "sfc0p1" );
        sfc0p1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc0p1.setOperator( "==" );
        sfc0p1.setValue( "sfc0p1Value" );
        fp1.addConstraint( sfc0p1 );

        SingleFieldConstraint sfc1p1 = new SingleFieldConstraint();
        sfc1p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        sfc1p1.setFieldBinding( "$sfc1p1" );
        sfc1p1.setFactType( "FT1" );
        sfc1p1.setFieldName( "sfc1p1" );
        sfc1p1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1p1.setOperator( "==" );
        sfc1p1.setValue( "sfc1p1Value" );
        fp1.addConstraint( sfc1p1 );

        model.lhs[1] = fp1;

        //Test first pattern, with two Template Keys
        RuleModelPeerVariableVisitor visitor0 = new RuleModelPeerVariableVisitor( model,
                                                                                  "sfc0p0Value" );
        List<ValueHolder> variables0 = visitor0.getPeerVariables();

        assertNotNull( variables0 );
        assertEquals( 2,
                      variables0.size() );

        ValueHolder v0vh0 = variables0.get( 0 );
        assertNotNull( v0vh0 );
        assertEquals( "sfc0p0",
                      v0vh0.getFieldName() );
        assertEquals( "sfc0p0Value",
                      v0vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh0.getType() );

        ValueHolder v0vh1 = variables0.get( 1 );
        assertNotNull( v0vh1 );
        assertEquals( "sfc1p0",
                      v0vh1.getFieldName() );
        assertEquals( "sfc1p0Value",
                      v0vh1.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh1.getType() );

        //Test second pattern, with one Template Key and one Literal
        RuleModelPeerVariableVisitor visitor1 = new RuleModelPeerVariableVisitor( model,
                                                                                  "sfc0p1Value" );
        List<ValueHolder> variables1 = visitor1.getPeerVariables();

        assertNotNull( variables1 );
        assertEquals( 2,
                      variables1.size() );

        ValueHolder v1vh0 = variables1.get( 0 );
        assertNotNull( v1vh0 );
        assertEquals( "sfc0p1",
                      v1vh0.getFieldName() );
        assertEquals( "sfc0p1Value",
                      v1vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v1vh0.getType() );

        ValueHolder v1vh1 = variables1.get( 1 );
        assertNotNull( v1vh1 );
        assertEquals( "sfc1p1",
                      v1vh1.getFieldName() );
        assertEquals( "sfc1p1Value",
                      v1vh1.getValue() );
        assertEquals( ValueHolder.Type.VALUE,
                      v1vh1.getType() );
    }

    @Test
    public void testFactPattern_CompositeFieldConstraintsAndOperator() {
        TemplateModel model = new TemplateModel();

        model.lhs = new IPattern[2];

        //Both fields are Template Keys
        FactPattern fp0 = new FactPattern();
        fp0.setBoundName( "$t0" );
        fp0.setFactType( "FT0" );

        CompositeFieldConstraint cfc0 = new CompositeFieldConstraint();
        cfc0.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_AND;

        SingleFieldConstraint sfc0p0 = new SingleFieldConstraint();
        sfc0p0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc0p0.setFieldBinding( "$sfc0p0" );
        sfc0p0.setFactType( "FT0" );
        sfc0p0.setFieldName( "sfc0p0" );
        sfc0p0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc0p0.setOperator( "==" );
        sfc0p0.setValue( "sfc0p0Value" );
        cfc0.addConstraint( sfc0p0 );

        SingleFieldConstraint sfc1p0 = new SingleFieldConstraint();
        sfc1p0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc1p0.setFieldBinding( "$sfc1p0" );
        sfc1p0.setFactType( "FT0" );
        sfc1p0.setFieldName( "sfc1p0" );
        sfc1p0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1p0.setOperator( "==" );
        sfc1p0.setValue( "sfc1p0Value" );
        cfc0.addConstraint( sfc1p0 );

        fp0.addConstraint( cfc0 );

        model.lhs[0] = fp0;

        //One field is a Template Key the other is a literal
        FactPattern fp1 = new FactPattern();
        fp0.setBoundName( "$t1" );
        fp0.setFactType( "FT1" );

        CompositeFieldConstraint cfc1 = new CompositeFieldConstraint();
        cfc1.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_AND;

        SingleFieldConstraint sfc0p1 = new SingleFieldConstraint();
        sfc0p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc0p1.setFieldBinding( "$sfc0p1" );
        sfc0p1.setFactType( "FT1" );
        sfc0p1.setFieldName( "sfc0p1" );
        sfc0p1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc0p1.setOperator( "==" );
        sfc0p1.setValue( "sfc0p1Value" );
        cfc1.addConstraint( sfc0p1 );

        SingleFieldConstraint sfc1p1 = new SingleFieldConstraint();
        sfc1p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        sfc1p1.setFieldBinding( "$sfc1p1" );
        sfc1p1.setFactType( "FT1" );
        sfc1p1.setFieldName( "sfc1p1" );
        sfc1p1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1p1.setOperator( "==" );
        sfc1p1.setValue( "sfc1p1Value" );
        cfc1.addConstraint( sfc1p1 );

        fp1.addConstraint( cfc1 );

        model.lhs[1] = fp1;

        //Test first pattern, with two Template Keys
        RuleModelPeerVariableVisitor visitor0 = new RuleModelPeerVariableVisitor( model,
                                                                                  "sfc0p0Value" );
        List<ValueHolder> variables0 = visitor0.getPeerVariables();

        assertNotNull( variables0 );
        assertEquals( 2,
                      variables0.size() );

        ValueHolder v0vh0 = variables0.get( 0 );
        assertNotNull( v0vh0 );
        assertEquals( "sfc0p0",
                      v0vh0.getFieldName() );
        assertEquals( "sfc0p0Value",
                      v0vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh0.getType() );

        ValueHolder v0vh1 = variables0.get( 1 );
        assertNotNull( v0vh1 );
        assertEquals( "sfc1p0",
                      v0vh1.getFieldName() );
        assertEquals( "sfc1p0Value",
                      v0vh1.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh1.getType() );

        //Test second pattern, with one Template Key and one Literal
        RuleModelPeerVariableVisitor visitor1 = new RuleModelPeerVariableVisitor( model,
                                                                                  "sfc0p1Value" );
        List<ValueHolder> variables1 = visitor1.getPeerVariables();

        assertNotNull( variables1 );
        assertEquals( 2,
                      variables1.size() );

        ValueHolder v1vh0 = variables1.get( 0 );
        assertNotNull( v1vh0 );
        assertEquals( "sfc0p1",
                      v1vh0.getFieldName() );
        assertEquals( "sfc0p1Value",
                      v1vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v1vh0.getType() );

        ValueHolder v1vh1 = variables1.get( 1 );
        assertNotNull( v1vh1 );
        assertEquals( "sfc1p1",
                      v1vh1.getFieldName() );
        assertEquals( "sfc1p1Value",
                      v1vh1.getValue() );
        assertEquals( ValueHolder.Type.VALUE,
                      v1vh1.getType() );
    }

    @Test
    public void testFactPattern_CompositeFieldConstraintsOrOperator() {
        TemplateModel model = new TemplateModel();

        model.lhs = new IPattern[2];

        //Both fields are Template Keys
        FactPattern fp0 = new FactPattern();
        fp0.setBoundName( "$t0" );
        fp0.setFactType( "FT0" );

        CompositeFieldConstraint cfc0 = new CompositeFieldConstraint();
        cfc0.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_OR;

        SingleFieldConstraint sfc0p0 = new SingleFieldConstraint();
        sfc0p0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc0p0.setFieldBinding( "$sfc0p0" );
        sfc0p0.setFactType( "FT0" );
        sfc0p0.setFieldName( "sfc0p0" );
        sfc0p0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc0p0.setOperator( "==" );
        sfc0p0.setValue( "sfc0p0Value" );
        cfc0.addConstraint( sfc0p0 );

        SingleFieldConstraint sfc1p0 = new SingleFieldConstraint();
        sfc1p0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc1p0.setFieldBinding( "$sfc1p0" );
        sfc1p0.setFactType( "FT0" );
        sfc1p0.setFieldName( "sfc1p0" );
        sfc1p0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1p0.setOperator( "==" );
        sfc1p0.setValue( "sfc1p0Value" );
        cfc0.addConstraint( sfc1p0 );

        fp0.addConstraint( cfc0 );

        model.lhs[0] = fp0;

        //One field is a Template Key the other is a literal
        FactPattern fp1 = new FactPattern();
        fp0.setBoundName( "$t1" );
        fp0.setFactType( "FT1" );

        CompositeFieldConstraint cfc1 = new CompositeFieldConstraint();
        cfc1.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_OR;

        SingleFieldConstraint sfc0p1 = new SingleFieldConstraint();
        sfc0p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc0p1.setFieldBinding( "$sfc0p1" );
        sfc0p1.setFactType( "FT1" );
        sfc0p1.setFieldName( "sfc0p1" );
        sfc0p1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc0p1.setOperator( "==" );
        sfc0p1.setValue( "sfc0p1Value" );
        cfc1.addConstraint( sfc0p1 );

        SingleFieldConstraint sfc1p1 = new SingleFieldConstraint();
        sfc1p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        sfc1p1.setFieldBinding( "$sfc1p1" );
        sfc1p1.setFactType( "FT1" );
        sfc1p1.setFieldName( "sfc1p1" );
        sfc1p1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1p1.setOperator( "==" );
        sfc1p1.setValue( "sfc1p1Value" );
        cfc1.addConstraint( sfc1p1 );

        fp1.addConstraint( cfc1 );

        model.lhs[1] = fp1;

        //Test first pattern, with two Template Keys
        RuleModelPeerVariableVisitor visitor0 = new RuleModelPeerVariableVisitor( model,
                                                                                  "sfc0p0Value" );
        List<ValueHolder> variables0 = visitor0.getPeerVariables();

        assertNotNull( variables0 );
        assertEquals( 0,
                      variables0.size() );

        //Test second pattern, with one Template Key and one Literal
        RuleModelPeerVariableVisitor visitor1 = new RuleModelPeerVariableVisitor( model,
                                                                                  "sfc0p1Value" );
        List<ValueHolder> variables1 = visitor1.getPeerVariables();

        assertNotNull( variables1 );
        assertEquals( 0,
                      variables1.size() );
    }

    @Test
    public void testFactPattern_SingleFieldConstraintEBLeftSide() {
        TemplateModel model = new TemplateModel();

        model.lhs = new IPattern[2];

        //Both fields are Template Keys
        FactPattern fp0 = new FactPattern();
        fp0.setBoundName( "$t0" );
        fp0.setFactType( "FT0" );

        SingleFieldConstraintEBLeftSide sfc0p0 = new SingleFieldConstraintEBLeftSide();
        sfc0p0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc0p0.setOperator( "==" );
        sfc0p0.setValue( "sfc0p0Value" );
        fp0.addConstraint( sfc0p0 );

        ExpressionFormLine efl0sfc0p0 = new ExpressionFormLine();
        ExpressionField ef0sfc0p0 = new ExpressionField( "sfc0p0",
                                                         "sfc0p0class",
                                                         SuggestionCompletionEngine.TYPE_STRING );
        efl0sfc0p0.appendPart( ef0sfc0p0 );
        sfc0p0.setExpressionLeftSide( efl0sfc0p0 );

        SingleFieldConstraintEBLeftSide sfc1p0 = new SingleFieldConstraintEBLeftSide();
        sfc1p0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc1p0.setOperator( "==" );
        sfc1p0.setValue( "sfc1p0Value" );
        fp0.addConstraint( sfc1p0 );

        ExpressionFormLine efl0sfc1p0 = new ExpressionFormLine();
        ExpressionField ef0sfc1p0 = new ExpressionField( "sfc1p0",
                                                         "sfc1p0class",
                                                         SuggestionCompletionEngine.TYPE_STRING );
        efl0sfc1p0.appendPart( ef0sfc1p0 );
        sfc1p0.setExpressionLeftSide( efl0sfc1p0 );

        model.lhs[0] = fp0;

        //One field is a Template Key the other is a literal
        FactPattern fp1 = new FactPattern();
        fp1.setFactType( "FT1" );

        SingleFieldConstraintEBLeftSide sfc0p1 = new SingleFieldConstraintEBLeftSide();
        sfc0p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc0p1.setOperator( "==" );
        sfc0p1.setValue( "sfc0p1Value" );
        fp1.addConstraint( sfc0p1 );

        ExpressionFormLine efl0sfc0p1 = new ExpressionFormLine();
        ExpressionField ef0sfc0p1 = new ExpressionField( "sfc0p1",
                                                         "sfc0p1class",
                                                         SuggestionCompletionEngine.TYPE_STRING );
        efl0sfc0p1.appendPart( ef0sfc0p1 );
        sfc0p1.setExpressionLeftSide( efl0sfc0p1 );

        SingleFieldConstraintEBLeftSide sfc1p1 = new SingleFieldConstraintEBLeftSide();
        sfc1p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        sfc1p1.setOperator( "==" );
        sfc1p1.setValue( "sfc1p1Value" );
        fp1.addConstraint( sfc1p1 );

        ExpressionFormLine efl0sfc1p1 = new ExpressionFormLine();
        ExpressionField ef0sfc1p1 = new ExpressionField( "sfc1p1",
                                                         "sfc1p1class",
                                                         SuggestionCompletionEngine.TYPE_STRING );
        efl0sfc1p1.appendPart( ef0sfc1p1 );
        sfc1p1.setExpressionLeftSide( efl0sfc1p1 );

        model.lhs[1] = fp1;

        //Test first pattern, with two Template Keys
        RuleModelPeerVariableVisitor visitor0 = new RuleModelPeerVariableVisitor( model,
                                                                                  "sfc0p0Value" );
        List<ValueHolder> variables0 = visitor0.getPeerVariables();

        assertNotNull( variables0 );
        assertEquals( 2,
                      variables0.size() );

        ValueHolder v0vh0 = variables0.get( 0 );
        assertNotNull( v0vh0 );
        assertEquals( "sfc0p0",
                      v0vh0.getFieldName() );
        assertEquals( "sfc0p0Value",
                      v0vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh0.getType() );

        ValueHolder v0vh1 = variables0.get( 1 );
        assertNotNull( v0vh1 );
        assertEquals( "sfc1p0",
                      v0vh1.getFieldName() );
        assertEquals( "sfc1p0Value",
                      v0vh1.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh1.getType() );

        //Test second pattern, with one Template Key and one Literal
        RuleModelPeerVariableVisitor visitor1 = new RuleModelPeerVariableVisitor( model,
                                                                                  "sfc0p1Value" );
        List<ValueHolder> variables1 = visitor1.getPeerVariables();

        assertNotNull( variables1 );
        assertEquals( 2,
                      variables1.size() );

        ValueHolder v1vh0 = variables1.get( 0 );
        assertNotNull( v1vh0 );
        assertEquals( "sfc0p1",
                      v1vh0.getFieldName() );
        assertEquals( "sfc0p1Value",
                      v1vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v1vh0.getType() );

        ValueHolder v1vh1 = variables1.get( 1 );
        assertNotNull( v1vh1 );
        assertEquals( "sfc1p1",
                      v1vh1.getFieldName() );
        assertEquals( "sfc1p1Value",
                      v1vh1.getValue() );
        assertEquals( ValueHolder.Type.VALUE,
                      v1vh1.getType() );

    }

    @Test
    public void testCompositeFactPatterns1() {
        TemplateModel model = new TemplateModel();

        model.lhs = new IPattern[1];

        //Both fields are Template Keys
        CompositeFactPattern cfp0 = new CompositeFactPattern();
        cfp0.type = CompositeFactPattern.COMPOSITE_TYPE_OR;

        FactPattern fp0 = new FactPattern();
        fp0.setBoundName( "$t0" );
        fp0.setFactType( "FT0" );
        fp0.setNegated( true );

        SingleFieldConstraint fp0sfc0 = new SingleFieldConstraint();
        fp0sfc0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        fp0sfc0.setFieldBinding( "$fp0sfc0" );
        fp0sfc0.setFactType( "FT0" );
        fp0sfc0.setFieldName( "fp0sfc0" );
        fp0sfc0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        fp0sfc0.setOperator( "==" );
        fp0sfc0.setValue( "fp0sfc0Value" );
        fp0.addConstraint( fp0sfc0 );

        SingleFieldConstraint fp0sfc1 = new SingleFieldConstraint();
        fp0sfc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        fp0sfc1.setFieldBinding( "$fp0sfc1" );
        fp0sfc1.setFactType( "FT0" );
        fp0sfc1.setFieldName( "fp0sfc1" );
        fp0sfc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        fp0sfc1.setOperator( "==" );
        fp0sfc1.setValue( "fp0sfc1Value" );
        fp0.addConstraint( fp0sfc1 );

        FactPattern fp1 = new FactPattern();
        fp1.setBoundName( "$t1" );
        fp1.setFactType( "FT1" );
        fp1.setNegated( true );

        SingleFieldConstraint fp1sfc0 = new SingleFieldConstraint();
        fp1sfc0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        fp1sfc0.setFieldBinding( "$fp1sfc0" );
        fp1sfc0.setFactType( "FT1" );
        fp1sfc0.setFieldName( "fp1sfc0" );
        fp1sfc0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        fp1sfc0.setOperator( "==" );
        fp1sfc0.setValue( "fp1sfc0Value" );
        fp1.addConstraint( fp1sfc0 );

        cfp0.addFactPattern( fp0 );
        cfp0.addFactPattern( fp1 );

        model.lhs[0] = cfp0;

        //Test first pattern, with two Template Keys
        RuleModelPeerVariableVisitor visitor0 = new RuleModelPeerVariableVisitor( model,
                                                                                  "fp0sfc0Value" );
        List<ValueHolder> variables0 = visitor0.getPeerVariables();

        assertNotNull( variables0 );
        assertEquals( 2,
                      variables0.size() );

        ValueHolder v0vh0 = variables0.get( 0 );
        assertNotNull( v0vh0 );
        assertEquals( "fp0sfc0",
                      v0vh0.getFieldName() );
        assertEquals( "fp0sfc0Value",
                      v0vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh0.getType() );

        ValueHolder v0vh1 = variables0.get( 1 );
        assertNotNull( v0vh1 );
        assertEquals( "fp0sfc1",
                      v0vh1.getFieldName() );
        assertEquals( "fp0sfc1Value",
                      v0vh1.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh1.getType() );
    }

    @Test
    public void testCompositeFactPatterns2() {
        TemplateModel model = new TemplateModel();

        model.lhs = new IPattern[1];

        //One field is a Template Key the other is a literal
        CompositeFactPattern cfp0 = new CompositeFactPattern();
        cfp0.type = CompositeFactPattern.COMPOSITE_TYPE_OR;

        FactPattern fp0 = new FactPattern();
        fp0.setBoundName( "$t0" );
        fp0.setFactType( "FT0" );
        fp0.setNegated( true );

        SingleFieldConstraint fp0sfc0 = new SingleFieldConstraint();
        fp0sfc0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        fp0sfc0.setFieldBinding( "$fp0sfc0" );
        fp0sfc0.setFactType( "FT0" );
        fp0sfc0.setFieldName( "fp0sfc0" );
        fp0sfc0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        fp0sfc0.setOperator( "==" );
        fp0sfc0.setValue( "fp0sfc0Value" );
        fp0.addConstraint( fp0sfc0 );

        SingleFieldConstraint fp0sfc1 = new SingleFieldConstraint();
        fp0sfc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        fp0sfc1.setFieldBinding( "$fp0sfc1" );
        fp0sfc1.setFactType( "FT0" );
        fp0sfc1.setFieldName( "fp0sfc1" );
        fp0sfc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        fp0sfc1.setOperator( "==" );
        fp0sfc1.setValue( "fp0sfc1Value" );
        fp0.addConstraint( fp0sfc1 );

        FactPattern fp1 = new FactPattern();
        fp1.setBoundName( "$t1" );
        fp1.setFactType( "FT1" );
        fp1.setNegated( true );

        SingleFieldConstraint fp1sfc0 = new SingleFieldConstraint();
        fp1sfc0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        fp1sfc0.setFieldBinding( "$fp1sfc0" );
        fp1sfc0.setFactType( "FT1" );
        fp1sfc0.setFieldName( "fp1sfc0" );
        fp1sfc0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        fp1sfc0.setOperator( "==" );
        fp1sfc0.setValue( "fp1sfc0Value" );
        fp1.addConstraint( fp1sfc0 );

        cfp0.addFactPattern( fp0 );
        cfp0.addFactPattern( fp1 );

        model.lhs[0] = cfp0;

        //Test first pattern, with two Template Keys
        RuleModelPeerVariableVisitor visitor0 = new RuleModelPeerVariableVisitor( model,
                                                                                  "fp0sfc0Value" );
        List<ValueHolder> variables0 = visitor0.getPeerVariables();

        assertNotNull( variables0 );
        assertEquals( 2,
                      variables0.size() );

        ValueHolder v0vh0 = variables0.get( 0 );
        assertNotNull( v0vh0 );
        assertEquals( "fp0sfc0",
                      v0vh0.getFieldName() );
        assertEquals( "fp0sfc0Value",
                      v0vh0.getValue() );
        assertEquals( ValueHolder.Type.TEMPLATE_KEY,
                      v0vh0.getType() );

        ValueHolder v0vh1 = variables0.get( 1 );
        assertNotNull( v0vh1 );
        assertEquals( "fp0sfc1",
                      v0vh1.getFieldName() );
        assertEquals( "fp0sfc1Value",
                      v0vh1.getValue() );
        assertEquals( ValueHolder.Type.VALUE,
                      v0vh1.getType() );
    }

}
