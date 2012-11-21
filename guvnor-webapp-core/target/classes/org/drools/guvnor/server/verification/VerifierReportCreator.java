/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server.verification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.rpc.AnalysisFactUsage;
import org.drools.guvnor.client.rpc.AnalysisFieldUsage;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.AnalysisReportLine;
import org.drools.guvnor.client.rpc.Cause;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.PatternComponent;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

public class VerifierReportCreator {

    public static AnalysisReport doReport(VerifierReport report) {
        AnalysisReport result = new AnalysisReport();

        result.errors = doLines( report.getBySeverity( Severity.ERROR ) );
        result.warnings = doLines( report.getBySeverity( Severity.WARNING ) );
        result.notes = doLines( report.getBySeverity( Severity.NOTE ) );
        result.factUsages = doFactUsage( report.getVerifierData() );

        return result;
    }

    private static AnalysisFactUsage[] doFactUsage(VerifierData verifierData) {

        Map<String, String> interned = new HashMap<String, String>();

        List<AnalysisFactUsage> factUsage = new ArrayList<AnalysisFactUsage>();
        Collection<ObjectType> objectTypes = verifierData.getAll( VerifierComponentType.OBJECT_TYPE );
        for ( ObjectType c : objectTypes ) {
            AnalysisFactUsage fact = new AnalysisFactUsage();
            fact.name = c.getName();
            List<AnalysisFieldUsage> fieldUsage = new ArrayList<AnalysisFieldUsage>();
            Set<Field> flds = c.getFields();
            for ( Field f : flds ) {
                AnalysisFieldUsage fu = new AnalysisFieldUsage();
                fu.name = f.getName();
                Collection<VerifierRule> cr = verifierData.getRulesByFieldPath( f.getPath() );
                List<String> ruleNames = new ArrayList<String>();
                for ( VerifierRule verifierRule : cr ) {
                    ruleNames.add( intern( verifierRule.getName(),
                                           interned ) );
                }
                fu.rules = ruleNames.toArray( new String[ruleNames.size()] );
                fieldUsage.add( fu );
            }
            fact.fields = fieldUsage.toArray( new AnalysisFieldUsage[fieldUsage.size()] );
            factUsage.add( fact );
        }

        return factUsage.toArray( new AnalysisFactUsage[factUsage.size()] );
    }

    /**
     * Doing this to reuse refs to the one name (interning, but not putting in the VMs interned pool
     * as there could be quite a lot of rules).
     */
    private static String intern(String ruleName,
                                 Map<String, String> interned) {
        if ( interned.containsKey( ruleName ) ) {
            return interned.get( ruleName );
        } else {
            interned.put( ruleName,
                          ruleName );
            return ruleName;
        }
    }

    private static AnalysisReportLine[] doLines(Collection<VerifierMessageBase> messages) {
        List<AnalysisReportLine> lines = new ArrayList<AnalysisReportLine>();
        for ( VerifierMessageBase message : messages ) {
            AnalysisReportLine line = new AnalysisReportLine();
            line.description = message.getMessage();
            if ( message.getFaulty() != null ) {
                line.reason = message.getFaulty().toString();
                if ( message.getFaulty() instanceof PatternComponent ) {
                    line.patternOrderNumber = ((PatternComponent) message.getFaulty()).getPatternOrderNumber();
                }
            }

            line.impactedRules = message.getImpactedRules();

            line.causes = doCauses( message.getCauses() );
            lines.add( line );
        }
        return lines.toArray( new AnalysisReportLine[lines.size()] );
    }

    private static Cause[] doCauses(Collection<org.drools.verifier.report.components.Cause> causes) {
        ArrayList<Cause> results = new ArrayList<Cause>();

        for ( org.drools.verifier.report.components.Cause cause : causes ) {
            Cause result = new Cause( cause.toString(),
                                      doCauses( cause.getCauses() ) );
            results.add( result );
        }

        return results.toArray( new Cause[results.size()] );
    }

}
