package org.kie.guvnor.jcr2vfsmigration.migrater.asset;

import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
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

import com.google.gwt.user.client.rpc.SerializationException;

@ApplicationScoped
public class FactModelsMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(FactModelsMigrater.class);

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    protected FactModelService vfsFactModelService;

    @Inject
    protected MigrationPathManager migrationPathManager;


    public void migrate(Module jcrModule, AssetItem jcrAssetItem) {      
        if (!AssetFormats.DRL_MODEL.equals(jcrAssetItem.getFormat())) {
            throw new IllegalArgumentException("The jcrAsset (" + jcrAssetItem.getName()
                    + ") has the wrong format (" + jcrAssetItem.getFormat() + ").");
        }
        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAssetItem);
        
        try {
            Asset jcrAsset = jcrRepositoryAssetService.loadRuleAsset(jcrAssetItem.getUUID());
            FactModels vfsFactModels = convertFactModels(
                    (org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels) jcrAsset.getContent());
            
            Metadata m = null;       
            vfsFactModelService.save(path, vfsFactModels, m, jcrAssetItem.getCheckinComment()); //, lastModified, lastContributor); TODO: Some sort of migration service
        } catch (SerializationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
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
