/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.decisiontable.analysis;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.drools.ide.common.client.modeldriven.ModelField;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.Analysis;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.junit.Test;

import static org.junit.Assert.*;

public class DecisionTableAnalyzerTest {

    @Test
    public void testImpossibleMatchesBoolean() throws ParseException {
        SuggestionCompletionEngine sce = buildSuggestionCompletionEngine();

        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 driverPattern = new Pattern52();
        driverPattern.setBoundName("driverPattern");
        driverPattern.setFactType("Driver");

        ConditionCol52 approved = new ConditionCol52();
        approved.setFactField("approved");
        approved.setOperator("==");
        approved.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(approved);

        ConditionCol52 disapproved = new ConditionCol52();
        disapproved.setFactField("approved");
        disapproved.setOperator("!=");
        disapproved.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(disapproved);

        dt.getConditionPatterns().add(driverPattern);

        List<List<DTCellValue52>> data = Arrays.asList(
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("1")),
                        new DTCellValue52("Row 1 description"),
                        new DTCellValue52(Boolean.TRUE),
                        new DTCellValue52(Boolean.FALSE)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("2")),
                        new DTCellValue52("Row 2 description"),
                        new DTCellValue52(Boolean.TRUE),
                        new DTCellValue52(Boolean.TRUE)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("3")),
                        new DTCellValue52("Row 3 description"),
                        new DTCellValue52(Boolean.TRUE),
                        new DTCellValue52((Boolean) null)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("4")),
                        new DTCellValue52("Row 4 description"),
                        new DTCellValue52((Boolean) null),
                        new DTCellValue52(Boolean.TRUE)
                )
        );

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(sce);
        List<Analysis> analysisData = analyzer.analyze(dt, data);

        assertEquals(data.size(), analysisData.size());
        assertEquals(0, analysisData.get(0).getImpossibleMatchesSize());
        assertEquals(1, analysisData.get(1).getImpossibleMatchesSize());
        assertEquals(0, analysisData.get(2).getImpossibleMatchesSize());
        assertEquals(0, analysisData.get(3).getImpossibleMatchesSize());
    }

    @Test
    public void testImpossibleMatchesNumeric() throws ParseException {
        SuggestionCompletionEngine sce = buildSuggestionCompletionEngine();

        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 driverPattern = new Pattern52();
        driverPattern.setBoundName("driverPattern");
        driverPattern.setFactType("Driver");

        ConditionCol52 ageHigher = new ConditionCol52();
        ageHigher.setFactField("age");
        ageHigher.setOperator(">=");
        ageHigher.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(ageHigher);

        ConditionCol52 ageLower = new ConditionCol52();
        ageLower.setFactField("age");
        ageLower.setOperator("<=");
        ageLower.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(ageLower);

        dt.getConditionPatterns().add(driverPattern);

        List<List<DTCellValue52>> data = Arrays.asList(
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("1")),
                        new DTCellValue52("Row 1 description"),
                        new DTCellValue52(new BigDecimal("20")),
                        new DTCellValue52(new BigDecimal("50"))
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("2")),
                        new DTCellValue52("Row 2 description"),
                        new DTCellValue52(new BigDecimal("40")),
                        new DTCellValue52(new BigDecimal("30"))
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("3")),
                        new DTCellValue52("Row 3 description"),
                        new DTCellValue52((BigDecimal) null),
                        new DTCellValue52(new BigDecimal("50"))
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("4")),
                        new DTCellValue52("Row 4 description"),
                        new DTCellValue52(new BigDecimal("20")),
                        new DTCellValue52((BigDecimal) null)
                )
        );

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(sce);
        List<Analysis> analysisData = analyzer.analyze(dt, data);

        assertEquals(data.size(), analysisData.size());
        assertEquals(0, analysisData.get(0).getImpossibleMatchesSize());
        assertEquals(1, analysisData.get(1).getImpossibleMatchesSize());
        assertEquals(0, analysisData.get(2).getImpossibleMatchesSize());
        assertEquals(0, analysisData.get(3).getImpossibleMatchesSize());
    }

//    @Test
//    public void testImpossibleMatches() throws ParseException {
//        SuggestionCompletionEngine sce = buildSuggestionCompletionEngine();
//
//        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
//
//        Pattern52 driverPattern = new Pattern52();
//        driverPattern.setBoundName("driverPattern");
//        driverPattern.setFactType("Driver");
//
//        ConditionCol52 c1 = new ConditionCol52();
//        c1.setFactField( "name" );
//        c1.setOperator( "==" );
//        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
//        driverPattern.getConditions().add(c1);
//        dt.getConditionPatterns().add(driverPattern);
//
//        ConditionCol52 c2 = new ConditionCol52();
//        c2.setFactField( "age" );
//        c2.setOperator( "==" );
//        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
//        driverPattern.getConditions().add(c2);
//        dt.getConditionPatterns().add(driverPattern);
//
//        ConditionCol52 c3 = new ConditionCol52();
//        c3.setFactField( "dateOfBirth" );
//        c3.setOperator( "==" );
//        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
//        driverPattern.getConditions().add(c3);
//        dt.getConditionPatterns().add(driverPattern);
//
//        ConditionCol52 c4 = new ConditionCol52();
//        c4.setFactField( "approved" );
//        c4.setOperator( "==" );
//        c4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
//        driverPattern.getConditions().add( c4 );
//        dt.getConditionPatterns().add( driverPattern );
//
//        List<List<DTCellValue52>> data = new ArrayList<List<DTCellValue52>>();
//        Arrays.asList(
//                Arrays.asList(
//
//                ),
//                Arrays.asList(),
//        );
//
//        DTCellValue52 dcv1 = new DTCellValue52( 1 );
//        DTCellValue52 dcv2 = new DTCellValue52( Boolean.TRUE );
//        DTCellValue52 dcv3 = new DTCellValue52( "Michael" );
//        DTCellValue52 dcv4 = new DTCellValue52( 11 );
//        DTCellValue52 dcv5 = new DTCellValue52( new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01") );
//        DTCellValue52 dcv6 = new DTCellValue52( Boolean.TRUE );
//        DTCellValue52 dcv7 = new DTCellValue52( "Mike" );
//        DTCellValue52 dcv8 = new DTCellValue52( "Mike" );
//
//
//        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(sce);
//        List<Analysis> analysisData = analyzer.analyze(dt, data);
//
//    }

    private SuggestionCompletionEngine buildSuggestionCompletionEngine() {
        SuggestionCompletionEngine sce = new SuggestionCompletionEngine();

        sce.setFieldsForTypes(new HashMap<String, ModelField[]>() {
            {
                put("Driver",
                        new ModelField[]{
                                new ModelField("name",
                                        String.class.getName(),
                                        ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                        SuggestionCompletionEngine.TYPE_STRING),
                                new ModelField("age",
                                        Integer.class.getName(),
                                        ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                        SuggestionCompletionEngine.TYPE_NUMERIC),
                                new ModelField("dateOfBirth",
                                        Boolean.class.getName(),
                                        ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                        SuggestionCompletionEngine.TYPE_DATE),
                                new ModelField("approved",
                                        Boolean.class.getName(),
                                        ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                        SuggestionCompletionEngine.TYPE_BOOLEAN)
                        });
            }
        });
        return sce;
    }

}
