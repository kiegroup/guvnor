package org.kie.guvnor.jcr2vfsmigration.migrater.asset;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.kie.guvnor.factmodel.model.AnnotationMetaModel;
import org.kie.guvnor.factmodel.model.FactModels;
import org.kie.guvnor.factmodel.model.FactMetaModel;
import org.kie.guvnor.factmodel.model.FieldMetaModel;
import org.kie.guvnor.factmodel.service.FactModelService;
import org.kie.guvnor.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class FactModelsMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(FactModelsMigrater.class);

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    protected FactModelService vfsFactModelService;

    @Inject
    protected MigrationPathManager migrationPathManager;

    public void migrate(Module jcrModule, Asset jcrAsset, final String checkinComment, final Date lastModified, String lastContributor) {
        if (!AssetFormats.DRL_MODEL.equals(jcrAsset.getFormat())) {
            throw new IllegalArgumentException("The jcrAsset (" + jcrAsset
                    + ") has the wrong format (" + jcrAsset.getFormat() + ").");
        }
        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAsset);
        FactModels vfsFactModels = convertFactModels(
                (org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels) jcrAsset.getContent());
        
        Metadata m = null;       
        vfsFactModelService.save(path, vfsFactModels, m, checkinComment); //, lastModified, lastContributor); TODO: Some sort of migration service
    }

    private FactModels convertFactModels(
            org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels jcrFactModels) {
        FactModels vfsFactModels = new FactModels();
        List<FactMetaModel> vfsModels = vfsFactModels.getModels();
        for (org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel jcrFactMetaModel
                : jcrFactModels.models) {
            vfsModels.add(convertFactMetaModel(jcrFactMetaModel));
        }
        return vfsFactModels;
    }

    private FactMetaModel convertFactMetaModel(
            org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel jcrFactMetaModel) {
        FactMetaModel vfsFactMetaModel = new FactMetaModel();
        vfsFactMetaModel.setName(jcrFactMetaModel.getName());
        vfsFactMetaModel.setSuperType(jcrFactMetaModel.getSuperType());
        List<FieldMetaModel> vfsFields = vfsFactMetaModel.getFields();
        for (org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel jcrFieldMetaModel
                : jcrFactMetaModel.getFields()) {
            vfsFields.add(convertFieldMetaModel(jcrFieldMetaModel));
        }
        vfsFactMetaModel.setFields(vfsFields);
        List<AnnotationMetaModel> vfsAnnotations = vfsFactMetaModel.getAnnotations();
        for (org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel jcrAnnotationMetaModel
                : jcrFactMetaModel.getAnnotations()) {
            vfsAnnotations.add(convertAnnotationMetaModel(jcrAnnotationMetaModel));
        }
        vfsFactMetaModel.setAnnotations(vfsAnnotations);
        return vfsFactMetaModel;
    }

    private FieldMetaModel convertFieldMetaModel(
            org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel jcrFieldMetaModel) {
        FieldMetaModel vfsFieldMetaModel = new FieldMetaModel();
        vfsFieldMetaModel.name = jcrFieldMetaModel.name;
        vfsFieldMetaModel.type = jcrFieldMetaModel.type;
        return vfsFieldMetaModel;
    }

    private AnnotationMetaModel convertAnnotationMetaModel(
            org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel jcrAnnotationMetaModel) {
        AnnotationMetaModel vfsAnnotationMetaModel = new AnnotationMetaModel();
        vfsAnnotationMetaModel.name = jcrAnnotationMetaModel.name;
        Map<String, String> vfsValues = vfsAnnotationMetaModel.values;
        for (Map.Entry<String, String> jcrValueEntry : jcrAnnotationMetaModel.values.entrySet()) {
            vfsValues.put(jcrValueEntry.getKey(), jcrValueEntry.getValue());
        }
        return vfsAnnotationMetaModel;
    }

}
