package org.drools.guvnor.server;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.drools.builder.ResourceType;
import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.server.factory.ConstraintsFactory;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.VerificationService;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;
import org.drools.guvnor.server.security.PackageNameType;
import org.drools.guvnor.server.security.PackageUUIDType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.VerifierRunner;
import org.drools.io.ResourceFactory;
import org.drools.repository.PackageItem;
import org.drools.verifier.DefaultVerifierConfiguration;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierConfigurationImpl;
import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author rikkola
 *
 */
@Name("org.drools.guvnor.client.rpc.VerificationService")
@AutoCreate
public class VerificationServiceImplementation extends RemoteServiceServlet
    implements
    VerificationService {

    private static final long          serialVersionUID = -1618598780198053452L;

    private static final LoggingHelper log              = LoggingHelper.getLogger( ServiceImplementation.class );

    private ServiceImplementation      service          = RepositoryServiceServlet.getService();

    private Verifier                   defaultVerifier  = VerifierBuilderFactory.newVerifierBuilder().newVerifier();

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public AnalysisReport analysePackage(String packageUUID) throws SerializationException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( packageUUID ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        PackageItem packageItem = service.getRulesRepository().loadPackageByUUID( packageUUID );

        VerifierRunner runner = new VerifierRunner( defaultVerifier );

        AnalysisReport report = runner.verify( packageItem,
                                               new ScopesAgendaFilter( true,
                                                                       ScopesAgendaFilter.VERIFYING_SCOPE_KNOWLEDGE_PACKAGE ) );

        defaultVerifier.flushKnowledgeSession();

        return report;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public AnalysisReport verifyAsset(RuleAsset asset,
                                      Set<String> activeWorkingSets) throws SerializationException {
        return this.performAssetVerification( asset,
                                              true,
                                              activeWorkingSets );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public AnalysisReport verifyAssetWithoutVerifiersRules(RuleAsset asset,
                                                           Set<String> activeWorkingSets) throws SerializationException {
        return this.performAssetVerification( asset,
                                              false,
                                              activeWorkingSets );
    }

    private AnalysisReport performAssetVerification(RuleAsset asset,
                                                    boolean useVerifierDefaultConfig,
                                                    Set<String> activeWorkingSets) throws SerializationException {
        long startTime = System.currentTimeMillis();

        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( asset.metaData.packageName ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        PackageItem packageItem = service.getRulesRepository().loadPackage( asset.metaData.packageName );

        ScopesAgendaFilter verificationScope;

        if ( isAssetDecisionTable( asset ) ) {
            verificationScope = new ScopesAgendaFilter( true,
                                                        ScopesAgendaFilter.VERIFYING_SCOPE_DECISION_TABLE );
        } else {
            verificationScope = new ScopesAgendaFilter( true,
                                                        ScopesAgendaFilter.VERIFYING_SCOPE_SINGLE_RULE );
        }

        List<String> constraintRules = applyWorkingSets( activeWorkingSets );

        VerifierRunner runner;

        if ( useVerifierDefaultConfig ) {
            runner = new VerifierRunner( defaultVerifier );
        } else {
            runner = new VerifierRunner( getWorkingSetVerifier( constraintRules ) );
        }

        log.debug( "constraints rules: " + constraintRules );

        try {
            AnalysisReport report = runner.verify( packageItem,
                                                   verificationScope );

            defaultVerifier.flushKnowledgeSession();

            log.debug( "Asset verification took: " + (System.currentTimeMillis() - startTime) );

            return report;

        } catch ( Throwable t ) {
            throw new SerializationException( t.getMessage() );
        }
    }

    private boolean isAssetDecisionTable(RuleAsset asset) {
        return AssetFormats.DECISION_TABLE_GUIDED.equals( asset.metaData.format ) || AssetFormats.DECISION_SPREADSHEET_XLS.equals( asset.metaData.format );
    }

    private List<String> applyWorkingSets(Set<String> activeWorkingSets) throws SerializationException {
        if (activeWorkingSets == null){
            return new LinkedList<String>();
        }
        
        RuleAsset[] workingSets = service.loadRuleAssets( activeWorkingSets.toArray(new String[activeWorkingSets.size()]) );
        List<String> constraintRules = new LinkedList<String>();
        if ( workingSets != null ) {
            for ( RuleAsset workingSet : workingSets ) {
                WorkingSetConfigData wsConfig = (WorkingSetConfigData) workingSet.content;
                if ( wsConfig.constraints != null ) {
                    for ( ConstraintConfiguration config : wsConfig.constraints ) {
                        constraintRules.add( ConstraintsFactory.getInstance().getVerifierRule( config ) );
                    }
                }
            }
        }
        return constraintRules;
    }

    private Verifier getWorkingSetVerifier(Collection<String> additionalVerifierRules) {
        VerifierConfiguration configuration = new DefaultVerifierConfiguration();
        configuration = new VerifierConfigurationImpl();

        if ( additionalVerifierRules != null ) {
            for ( String rule : additionalVerifierRules ) {
                configuration.getVerifyingResources().put( ResourceFactory.newByteArrayResource( rule.getBytes() ),
                                                           ResourceType.DRL );
            }
        }

        return VerifierBuilderFactory.newVerifierBuilder().newVerifier( configuration );
    }
}
