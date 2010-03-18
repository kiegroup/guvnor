package org.drools.guvnor.server.util;

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
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

/**
 * 
 * @author Toni Rikkola
 */
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
                Collection<VerifierRule> cr = verifierData.getRulesByFieldId( f.getGuid() );
                List<String> ruleNames = new ArrayList<String>();
                for ( VerifierRule verifierRule : cr ) {
                    ruleNames.add( intern( verifierRule.getRuleName(),
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
