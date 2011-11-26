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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.drools.ide.common.client.modeldriven.ModelField;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.Analysis;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.junit.Test;

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

        @SuppressWarnings("unchecked")
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

        dt.setData( data );
        
        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(sce);
        List<Analysis> analysisData = analyzer.analyze(dt);

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

        ConditionCol52 ageMinimum = new ConditionCol52();
        ageMinimum.setFactField("age");
        ageMinimum.setOperator(">=");
        ageMinimum.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(ageMinimum);

        ConditionCol52 ageMaximum = new ConditionCol52();
        ageMaximum.setFactField("age");
        ageMaximum.setOperator("<=");
        ageMaximum.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(ageMaximum);

        dt.getConditionPatterns().add(driverPattern);

        @SuppressWarnings("unchecked")
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
        
        dt.setData(data);

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(sce);
        List<Analysis> analysisData = analyzer.analyze(dt);

        assertEquals(data.size(), analysisData.size());
        assertEquals(0, analysisData.get(0).getImpossibleMatchesSize());
        assertEquals(1, analysisData.get(1).getImpossibleMatchesSize());
        assertEquals(0, analysisData.get(2).getImpossibleMatchesSize());
        assertEquals(0, analysisData.get(3).getImpossibleMatchesSize());
    }

    @Test
    public void testImpossibleMatchesString() throws ParseException {
        SuggestionCompletionEngine sce = buildSuggestionCompletionEngine();

        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 driverPattern = new Pattern52();
        driverPattern.setBoundName("driverPattern");
        driverPattern.setFactType("Driver");

        ConditionCol52 name = new ConditionCol52();
        name.setFactField("name");
        name.setOperator("==");
        name.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(name);

        ConditionCol52 notName = new ConditionCol52();
        notName.setFactField("name");
        notName.setOperator("!=");
        notName.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(notName);

        ConditionCol52 nameIn = new ConditionCol52();
        nameIn.setFactField("name");
        nameIn.setOperator("in");
        nameIn.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(nameIn);

        dt.getConditionPatterns().add(driverPattern);

        @SuppressWarnings("unchecked")
        List<List<DTCellValue52>> data = Arrays.asList(
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("1")),
                        new DTCellValue52("Row 1 description"),
                        new DTCellValue52("Homer"),
                        new DTCellValue52("Bart"),
                        new DTCellValue52((String) null)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("2")),
                        new DTCellValue52("Row 2 description"),
                        new DTCellValue52("Homer"),
                        new DTCellValue52("Homer"),
                        new DTCellValue52((String) null)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("3")),
                        new DTCellValue52("Row 3 description"),
                        new DTCellValue52("Homer"),
                        new DTCellValue52((String) null),
                        new DTCellValue52((String) null)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("4")),
                        new DTCellValue52("Row 4 description"),
                        new DTCellValue52((String) null),
                        new DTCellValue52("Bart"),
                        new DTCellValue52((String) null)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("5")),
                        new DTCellValue52("Row 5 description"),
                        new DTCellValue52("Homer"),
                        new DTCellValue52((String) null),
                        new DTCellValue52("Marge,Lisa")
                )
        );

        dt.setData(data);

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(sce);
        List<Analysis> analysisData = analyzer.analyze(dt);

        assertEquals(data.size(), analysisData.size());
        assertEquals(0, analysisData.get(0).getImpossibleMatchesSize());
        assertEquals(1, analysisData.get(1).getImpossibleMatchesSize());
        assertEquals(0, analysisData.get(2).getImpossibleMatchesSize());
        assertEquals(0, analysisData.get(3).getImpossibleMatchesSize());
        assertEquals(1, analysisData.get(4).getImpossibleMatchesSize());
    }

    @Test
    public void testImpossibleMatchesDate() throws ParseException {
        SuggestionCompletionEngine sce = buildSuggestionCompletionEngine();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 driverPattern = new Pattern52();
        driverPattern.setBoundName("driverPattern");
        driverPattern.setFactType("Driver");

        ConditionCol52 dateOfBirthMinimum = new ConditionCol52();
        dateOfBirthMinimum.setFactField("dateOfBirth");
        dateOfBirthMinimum.setOperator(">=");
        dateOfBirthMinimum.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(dateOfBirthMinimum);

        ConditionCol52 dateOfBirthMaximum = new ConditionCol52();
        dateOfBirthMaximum.setFactField("dateOfBirth");
        dateOfBirthMaximum.setOperator("<=");
        dateOfBirthMaximum.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(dateOfBirthMaximum);

        dt.getConditionPatterns().add(driverPattern);

        @SuppressWarnings("unchecked")
        List<List<DTCellValue52>> data = Arrays.asList(
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("1")),
                        new DTCellValue52("Row 1 description"),
                        new DTCellValue52(dateFormat.parse("1981-01-01")),
                        new DTCellValue52(dateFormat.parse("2001-01-01"))
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("2")),
                        new DTCellValue52("Row 2 description"),
                        new DTCellValue52(dateFormat.parse("2001-01-01")),
                        new DTCellValue52(dateFormat.parse("1981-01-01"))
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("3")),
                        new DTCellValue52("Row 3 description"),
                        new DTCellValue52((Date) null),
                        new DTCellValue52(dateFormat.parse("2001-01-01"))
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("4")),
                        new DTCellValue52("Row 4 description"),
                        new DTCellValue52(dateFormat.parse("1981-01-01")),
                        new DTCellValue52((Date) null)
                )
        );

        dt.setData(data);
        
        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(sce);
        List<Analysis> analysisData = analyzer.analyze(dt);

        assertEquals(data.size(), analysisData.size());
        assertEquals(0, analysisData.get(0).getImpossibleMatchesSize());
        assertEquals(1, analysisData.get(1).getImpossibleMatchesSize());
        assertEquals(0, analysisData.get(2).getImpossibleMatchesSize());
        assertEquals(0, analysisData.get(3).getImpossibleMatchesSize());
    }

    @Test
    public void testImpossibleMatchesCombination() throws ParseException {
        SuggestionCompletionEngine sce = buildSuggestionCompletionEngine();

        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 driverPattern = new Pattern52();
        driverPattern.setBoundName("driverPattern");
        driverPattern.setFactType("Driver");

        ConditionCol52 name = new ConditionCol52();
        name.setFactField("name");
        name.setOperator("==");
        name.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(name);

        ConditionCol52 ageMinimum = new ConditionCol52();
        ageMinimum.setFactField("age");
        ageMinimum.setOperator(">=");
        ageMinimum.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(ageMinimum);

        ConditionCol52 ageMaximum = new ConditionCol52();
        ageMaximum.setFactField("age");
        ageMaximum.setOperator("<=");
        ageMaximum.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(ageMaximum);

        dt.getConditionPatterns().add(driverPattern);

        @SuppressWarnings("unchecked")
        List<List<DTCellValue52>> data = Arrays.asList(
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("1")),
                        new DTCellValue52("Row 1 description"),
                        new DTCellValue52("Homer"),
                        new DTCellValue52(new BigDecimal("20")),
                        new DTCellValue52(new BigDecimal("50"))
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("2")),
                        new DTCellValue52("Row 2 description"),
                        new DTCellValue52("Homer"),
                        new DTCellValue52(new BigDecimal("40")),
                        new DTCellValue52(new BigDecimal("30"))
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("3")),
                        new DTCellValue52("Row 3 description"),
                        new DTCellValue52((String) null),
                        new DTCellValue52(new BigDecimal("40")),
                        new DTCellValue52(new BigDecimal("30"))
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("4")),
                        new DTCellValue52("Row 4 description"),
                        new DTCellValue52("Homer"),
                        new DTCellValue52(new BigDecimal("20")),
                        new DTCellValue52((BigDecimal) null)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("5")),
                        new DTCellValue52("Row 5 description"),
                        new DTCellValue52((String) null),
                        new DTCellValue52((BigDecimal) null),
                        new DTCellValue52((BigDecimal) null)
                )
        );

        dt.setData(data);

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(sce);
        List<Analysis> analysisData = analyzer.analyze(dt);

        assertEquals(data.size(), analysisData.size());
        assertEquals(0, analysisData.get(0).getImpossibleMatchesSize());
        assertEquals(1, analysisData.get(1).getImpossibleMatchesSize());
        assertEquals(1, analysisData.get(2).getImpossibleMatchesSize());
        assertEquals(0, analysisData.get(3).getImpossibleMatchesSize());
        assertEquals(0, analysisData.get(4).getImpossibleMatchesSize());
    }

    @Test
    public void testImpossibleMatchesLimitedEntry() throws ParseException {
        SuggestionCompletionEngine sce = buildSuggestionCompletionEngine();

        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 driverPattern = new Pattern52();
        driverPattern.setBoundName("driverPattern");
        driverPattern.setFactType("Driver");

        LimitedEntryConditionCol52 child = new LimitedEntryConditionCol52();
        child.setFactField("age");
        child.setOperator("<");
        child.setValue(new DTCellValue52(new BigDecimal("18")));
        child.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(child);

        LimitedEntryConditionCol52 pensioner = new LimitedEntryConditionCol52();
        pensioner.setFactField("age");
        pensioner.setOperator(">=");
        pensioner.setValue(new DTCellValue52(new BigDecimal("65")));
        pensioner.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(pensioner);

        dt.getConditionPatterns().add(driverPattern);

        @SuppressWarnings("unchecked")
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
                        new DTCellValue52(Boolean.FALSE),
                        new DTCellValue52(Boolean.TRUE)
                )
        );

        dt.setData(data);

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(sce);
        List<Analysis> analysisData = analyzer.analyze(dt);

        assertEquals(data.size(), analysisData.size());
        assertEquals(0, analysisData.get(0).getImpossibleMatchesSize());
        assertEquals(1, analysisData.get(1).getImpossibleMatchesSize());
        assertEquals(0, analysisData.get(2).getImpossibleMatchesSize());
    }


    @Test
    public void testConflictingMatchNumeric() throws ParseException {
        SuggestionCompletionEngine sce = buildSuggestionCompletionEngine();

        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 driverPattern = new Pattern52();
        driverPattern.setBoundName("driverPattern");
        driverPattern.setFactType("Driver");

        ConditionCol52 ageMinimum = new ConditionCol52();
        ageMinimum.setFactField("age");
        ageMinimum.setOperator(">=");
        ageMinimum.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(ageMinimum);

        ConditionCol52 ageMaximum = new ConditionCol52();
        ageMaximum.setFactField("age");
        ageMaximum.setOperator("<=");
        ageMaximum.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(ageMaximum);

        dt.getConditionPatterns().add(driverPattern);

        @SuppressWarnings("unchecked")
        List<List<DTCellValue52>> data = Arrays.asList(
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("1")),
                        new DTCellValue52("Row 1 description"),
                        new DTCellValue52((BigDecimal) null),
                        new DTCellValue52(new BigDecimal("20"))
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("2")),
                        new DTCellValue52("Row 2 description"),
                        new DTCellValue52(new BigDecimal("21")),
                        new DTCellValue52(new BigDecimal("40"))
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("3")),
                        new DTCellValue52("Row 3 description"),
                        new DTCellValue52(new BigDecimal("30")),
                        new DTCellValue52(new BigDecimal("60"))
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("4")),
                        new DTCellValue52("Row 4 description"),
                        new DTCellValue52(new BigDecimal("50")),
                        new DTCellValue52((BigDecimal) null)
                )
        );

        dt.setData(data);

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(sce);
        List<Analysis> analysisData = analyzer.analyze(dt);

        assertEquals(data.size(), analysisData.size());
        assertEquals(0, analysisData.get(0).getConflictingMatchSize());
        assertEquals(1, analysisData.get(1).getConflictingMatchSize());
        assertEquals(2, analysisData.get(2).getConflictingMatchSize());
        assertEquals(1, analysisData.get(3).getConflictingMatchSize());
    }

    @Test
    public void testConflictingMatchCombination() throws ParseException {
        SuggestionCompletionEngine sce = buildSuggestionCompletionEngine();

        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 driverPattern = new Pattern52();
        driverPattern.setBoundName("driverPattern");
        driverPattern.setFactType("Driver");

        ConditionCol52 ageMinimum = new ConditionCol52();
        ageMinimum.setFactField("age");
        ageMinimum.setOperator(">=");
        ageMinimum.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(ageMinimum);

        ConditionCol52 ageMaximum = new ConditionCol52();
        ageMaximum.setFactField("age");
        ageMaximum.setOperator("<=");
        ageMaximum.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(ageMaximum);

        ConditionCol52 approved = new ConditionCol52();
        approved.setFactField("approved");
        approved.setOperator("==");
        approved.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(approved);

        dt.getConditionPatterns().add(driverPattern);

        @SuppressWarnings("unchecked")
        List<List<DTCellValue52>> data = Arrays.asList(
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("1")),
                        new DTCellValue52("Row 1 description"),
                        new DTCellValue52((BigDecimal) null),
                        new DTCellValue52(new BigDecimal("20")),
                        new DTCellValue52(Boolean.TRUE)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("2")),
                        new DTCellValue52("Row 2 description"),
                        new DTCellValue52(new BigDecimal("21")),
                        new DTCellValue52(new BigDecimal("40")),
                        new DTCellValue52(Boolean.TRUE)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("3")),
                        new DTCellValue52("Row 3 description"),
                        new DTCellValue52(new BigDecimal("41")),
                        new DTCellValue52((BigDecimal) null),
                        new DTCellValue52(Boolean.TRUE)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("4")),
                        new DTCellValue52("Row 4 description"),
                        new DTCellValue52((BigDecimal) null),
                        new DTCellValue52(new BigDecimal("25")),
                        new DTCellValue52(Boolean.FALSE)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("5")),
                        new DTCellValue52("Row 5 description"),
                        new DTCellValue52(new BigDecimal("26")),
                        new DTCellValue52(new BigDecimal("60")),
                        new DTCellValue52(Boolean.FALSE)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("6")),
                        new DTCellValue52("Row 6 description"),
                        new DTCellValue52(new BigDecimal("50")),
                        new DTCellValue52((BigDecimal) null),
                        new DTCellValue52(Boolean.FALSE)
                )
        );

        dt.setData(data);

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(sce);
        List<Analysis> analysisData = analyzer.analyze(dt);

        assertEquals(data.size(), analysisData.size());
        assertEquals(0, analysisData.get(0).getConflictingMatchSize());
        assertEquals(0, analysisData.get(1).getConflictingMatchSize());
        assertEquals(0, analysisData.get(2).getConflictingMatchSize());
        assertEquals(0, analysisData.get(3).getConflictingMatchSize());
        assertEquals(1, analysisData.get(4).getConflictingMatchSize());
        assertEquals(1, analysisData.get(5).getConflictingMatchSize());
    }

    @Test
    public void testConflictingMatchLimitedEntry() throws ParseException {
        SuggestionCompletionEngine sce = buildSuggestionCompletionEngine();

        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 driverPattern = new Pattern52();
        driverPattern.setBoundName("driverPattern");
        driverPattern.setFactType("Driver");

        LimitedEntryConditionCol52 child = new LimitedEntryConditionCol52();
        child.setFactField("age");
        child.setOperator("<");
        child.setValue(new DTCellValue52(new BigDecimal("18")));
        child.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(child);

        LimitedEntryConditionCol52 adult = new LimitedEntryConditionCol52();
        adult.setFactField("age");
        adult.setOperator(">=");
        adult.setValue(new DTCellValue52(new BigDecimal("18")));
        adult.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(adult);

        LimitedEntryConditionCol52 pensioner = new LimitedEntryConditionCol52();
        pensioner.setFactField("age");
        pensioner.setOperator(">=");
        pensioner.setValue(new DTCellValue52(new BigDecimal("65")));
        pensioner.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        driverPattern.getConditions().add(pensioner);

        dt.getConditionPatterns().add(driverPattern);

        @SuppressWarnings("unchecked")
        List<List<DTCellValue52>> data = Arrays.asList(
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("1")),
                        new DTCellValue52("Row 1 description"),
                        new DTCellValue52(Boolean.TRUE),
                        new DTCellValue52(Boolean.FALSE),
                        new DTCellValue52(Boolean.FALSE)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("2")),
                        new DTCellValue52("Row 2 description"),
                        new DTCellValue52(Boolean.FALSE),
                        new DTCellValue52(Boolean.TRUE),
                        new DTCellValue52(Boolean.FALSE)
                ),
                Arrays.asList(
                        new DTCellValue52(new BigDecimal("3")),
                        new DTCellValue52("Row 3 description"),
                        new DTCellValue52(Boolean.FALSE),
                        new DTCellValue52(Boolean.FALSE),
                        new DTCellValue52(Boolean.TRUE)
                )
        );

        dt.setData(data);

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(sce);
        List<Analysis> analysisData = analyzer.analyze(dt);

        assertEquals(data.size(), analysisData.size());
        assertEquals(0, analysisData.get(0).getConflictingMatchSize());
        assertEquals(1, analysisData.get(1).getConflictingMatchSize());
        assertEquals(1, analysisData.get(2).getConflictingMatchSize());
    }

    @SuppressWarnings("serial")
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
                                        Date.class.getName(),
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
