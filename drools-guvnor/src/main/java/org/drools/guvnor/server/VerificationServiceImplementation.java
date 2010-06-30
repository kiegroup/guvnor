package org.drools.guvnor.server;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.server.factory.ConstraintsFactory;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.VerificationService;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;
import org.drools.guvnor.server.security.PackageNameType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.VerifierRunner;
import org.drools.repository.PackageItem;
import org.drools.verifier.VerifierConfiguration;
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
        ServiceImplementation service = RepositoryServiceServlet.getService();

        PackageItem packageItem = service.getRulesRepository().loadPackage( asset.metaData.packageName );

        VerifierRunner runner = new VerifierRunner();

        runner.setUseDefaultConfig( useVerifierDefaultConfig );

        RuleAsset[] workingSets = service.loadRuleAssets( activeWorkingSets );
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

        log.debug( "constraints rules: " + constraintRules );

        try {
            AnalysisReport report;
            if ( AssetFormats.DECISION_TABLE_GUIDED.equals( asset.metaData.format ) || AssetFormats.DECISION_SPREADSHEET_XLS.equals( asset.metaData.format ) ) {
                report = runner.verify( packageItem,
                                        VerifierConfiguration.VERIFYING_SCOPE_DECISION_TABLE,
                                        constraintRules );
            } else {
                report = runner.verify( packageItem,
                                        VerifierConfiguration.VERIFYING_SCOPE_SINGLE_RULE,
                                        constraintRules );
            }

            log.debug( "Asset verification took: " + (System.currentTimeMillis() - startTime) );

            return report;
        } catch ( Throwable t ) {
            throw new SerializationException( t.getMessage() );
        }
    }
}
