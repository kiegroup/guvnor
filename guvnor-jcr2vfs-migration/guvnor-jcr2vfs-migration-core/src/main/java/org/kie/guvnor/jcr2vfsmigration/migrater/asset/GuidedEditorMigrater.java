package org.kie.guvnor.jcr2vfsmigration.migrater.asset;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.kie.guvnor.datamodel.model.IAction;
import org.kie.guvnor.datamodel.model.IPattern;
import org.kie.guvnor.guided.rule.model.RuleAttribute;
import org.kie.guvnor.guided.rule.model.RuleMetadata;
import org.kie.guvnor.guided.rule.model.RuleModel;
import org.kie.guvnor.guided.rule.service.GuidedRuleEditorService;
import org.kie.guvnor.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class GuidedEditorMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(GuidedEditorMigrater.class);

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    protected GuidedRuleEditorService guidedRuleEditorService;

    @Inject
    protected MigrationPathManager migrationPathManager;

    public void migrate(Module jcrModule, Asset jcrAsset, final String checkinComment, final Date lastModified, String lastContributor) {
        if (!AssetFormats.BUSINESS_RULE.equals(jcrAsset.getFormat())) {
            throw new IllegalArgumentException("The jcrAsset (" + jcrAsset
                    + ") has the wrong format (" + jcrAsset.getFormat() + ").");
        }
        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAsset);
        RuleModel vfsRuleModel = convertRuleModel(
                (org.drools.ide.common.client.modeldriven.brl.RuleModel) jcrAsset.getContent());
        guidedRuleEditorService.save(path, vfsRuleModel, checkinComment);
    }

    private RuleModel convertRuleModel(
            org.drools.ide.common.client.modeldriven.brl.RuleModel jcrRuleModel) {
        RuleModel vfsRuleModel = new RuleModel();
        vfsRuleModel.setNegated(jcrRuleModel.isNegated());
        vfsRuleModel.name = jcrRuleModel.name;
        vfsRuleModel.modelVersion = jcrRuleModel.modelVersion;
        vfsRuleModel.parentName = jcrRuleModel.parentName;

        RuleAttribute[] ruleAttribute = new RuleAttribute[jcrRuleModel.attributes.length];
        for(int i = 0; i< jcrRuleModel.attributes.length; i++) {
            ruleAttribute[i] = convertRuleAttribute(jcrRuleModel.attributes[i]);
        }
        vfsRuleModel.attributes = ruleAttribute;

        RuleMetadata[] ruleMetadata = new RuleMetadata[jcrRuleModel.metadataList.length];
        for(int i = 0; i< jcrRuleModel.metadataList.length; i++) {
            ruleMetadata[i] = convertRuleMetadata(jcrRuleModel.metadataList[i]);
        }
        vfsRuleModel.metadataList = ruleMetadata;

        IPattern[] iPattern = new IPattern[jcrRuleModel.lhs.length];
        for(int i = 0; i< jcrRuleModel.lhs.length; i++) {
            iPattern[i] = convertIPattern(jcrRuleModel.lhs[i]);
        }
        vfsRuleModel.lhs = iPattern;

        IAction[] iAction = new IAction[jcrRuleModel.rhs.length];
        for(int i = 0; i< jcrRuleModel.rhs.length; i++) {
            iAction[i] = convertIAction(jcrRuleModel.rhs[i]);
        }
        vfsRuleModel.rhs = iAction;

        return vfsRuleModel;
    }

    private RuleAttribute convertRuleAttribute(org.drools.ide.common.client.modeldriven.brl.RuleAttribute r) {
        RuleAttribute ruleAttribute = new RuleAttribute();
        ruleAttribute.setAttributeName(r.attributeName);
        ruleAttribute.setValue(r.value);
        return ruleAttribute;
    }

    private RuleMetadata convertRuleMetadata(org.drools.ide.common.client.modeldriven.brl.RuleMetadata m) {
        RuleMetadata ruleMetadata = new RuleMetadata();
        ruleMetadata.setAttributeName(m.attributeName);
        ruleMetadata.setValue(m.value);
        return ruleMetadata;
    }

    private IPattern convertIPattern(org.drools.ide.common.client.modeldriven.brl.IPattern m) {
/*        IPattern iPattern = new IPattern();
        return iPattern;  */
        return null;
    }

    private IAction convertIAction(org.drools.ide.common.client.modeldriven.brl.IAction m) {

        return null;
    }
}
