package org.drools.guvnor.server.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.builder.ResourceType;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.rpc.AnalysisFactUsage;
import org.drools.guvnor.client.rpc.AnalysisFieldUsage;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.AnalysisReportLine;
import org.drools.guvnor.client.rpc.Cause;
import org.drools.io.ResourceFactory;
import org.drools.verifier.DefaultVerifierConfiguration;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

import com.google.gwt.user.client.rpc.SerializableException;

public class VerifierRunner {

    public AnalysisReport verify(String drl,
                                 String scope) throws DroolsParserException,
                                              SerializableException {

        VerifierConfiguration conf = new DefaultVerifierConfiguration();
        conf.getVerifyingScopes().clear();
        conf.getVerifyingScopes().add( scope );
        conf.setAcceptRulesWithoutVerifiyingScope( true );
        Verifier verifier = VerifierBuilderFactory.newVerifierBuilder().newVerifier( conf );
        verifier.addResourcesToVerify( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                                       ResourceType.DRL );
        verifier.fireAnalysis();
        VerifierReport res = verifier.getResult();

        AnalysisReport report = new AnalysisReport();
        report.errors = doLines( res.getBySeverity( Severity.ERROR ) );
        report.warnings = doLines( res.getBySeverity( Severity.WARNING ) );
        report.notes = doLines( res.getBySeverity( Severity.NOTE ) );
        report.factUsages = doFactUsage( res.getVerifierData() );
        return report;
    }

    private AnalysisFactUsage[] doFactUsage(VerifierData verifierData) {

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
    private String intern(String ruleName,
                          Map<String, String> interned) {
        if ( interned.containsKey( ruleName ) ) {
            return interned.get( ruleName );
        } else {
            interned.put( ruleName,
                          ruleName );
            return ruleName;
        }
    }

    private AnalysisReportLine[] doLines(Collection<VerifierMessageBase> msgs) {
        List<AnalysisReportLine> lines = new ArrayList<AnalysisReportLine>();
        for ( VerifierMessageBase m : msgs ) {
            AnalysisReportLine line = new AnalysisReportLine();
            line.description = m.getMessage();
            if ( m.getFaulty() != null ) {
                line.reason = m.getFaulty().toString();
            }
            line.causes = doCauses( m.getCauses() );
            lines.add( line );
        }
        return lines.toArray( new AnalysisReportLine[lines.size()] );
    }

    private Cause[] doCauses(Collection<org.drools.verifier.report.components.Cause> causes) {
        ArrayList<Cause> results = new ArrayList<Cause>();

        for ( org.drools.verifier.report.components.Cause cause : causes ) {
            Cause result = new Cause( cause.toString(),
                                      doCauses( cause.getCauses() ) );
            results.add( result );
        }

        return results.toArray( new Cause[results.size()] );
    }

}
