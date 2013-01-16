package org.drools.guvnor.server;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.drools.guvnor.client.rpc.ModuleService;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.jboss.solder.core.Veto;

import javax.inject.Inject;

@Veto
public class ModuleServiceServlet
        extends RemoteServiceServlet
        implements ModuleService {

    @Inject
    private RepositoryModuleService moduleService;

    @Override
    public org.drools.guvnor.client.rpc.Module[] listModules(java.lang.String p0) {
        return moduleService.listModules(p0);
    }

    @Override
    public org.drools.guvnor.client.rpc.Module[] listModules() {
        return moduleService.listModules();
    }

    @Override
    public org.drools.guvnor.client.rpc.Module[] listArchivedModules() {
        return moduleService.listArchivedModules();
    }

    @Override
    public org.drools.guvnor.client.rpc.Module loadGlobalModule() {
        return moduleService.loadGlobalModule();
    }

    @Override
    public SnapshotInfo loadSnapshotInfo(String moduleName, String snapshotName) {
        return moduleService.loadSnapshotInfo(moduleName, snapshotName);
    }

    @Override
    public java.lang.String createModule(java.lang.String p0,
                                         java.lang.String p1,
                                         java.lang.String p2) throws com.google.gwt.user.client.rpc.SerializationException {
        return moduleService.createModule(p0,
                p1, p2);
    }

    @Override
    public java.lang.String createSubModule(java.lang.String p0,
                                            java.lang.String p1,
                                            java.lang.String p2) throws com.google.gwt.user.client.rpc.SerializationException {
        return moduleService.createSubModule(p0,
                p1,
                p2);
    }

    @Override
    public org.drools.guvnor.client.rpc.Module loadModule(java.lang.String p0) {
        return moduleService.loadModule(p0);
    }

    @Override
    public void saveModule(org.drools.guvnor.client.rpc.Module p0) throws com.google.gwt.user.client.rpc.SerializationException {
        moduleService.saveModule(p0);
    }

    @Override
    public void createModuleSnapshot(String moduleName, String snapshotName, boolean replaceExisting, String comment, String buildMode, String statusOperator, String statusValue, boolean enableStatusSelector, String categoryOperator, String category, boolean enableCategorySelector, String customSelector) throws SerializationException {
        moduleService.createModuleSnapshot(moduleName, snapshotName, replaceExisting, comment, buildMode, statusOperator, statusValue, enableStatusSelector, categoryOperator, category, enableCategorySelector, customSelector);
}

    @Override
    public void createModuleSnapshot(String moduleName, String snapshotName, boolean replaceExisting, String comment, boolean checkIsBinaryUpToDate, String buildMode, String statusOperator, String statusValue, boolean enableStatusSelector, String categoryOperator, String category, boolean enableCategorySelector, String customSelector) throws SerializationException {
        moduleService.createModuleSnapshot(moduleName, snapshotName, replaceExisting, comment, buildMode, statusOperator, statusValue, enableStatusSelector, categoryOperator, category, enableCategorySelector, customSelector);
 }

    @Override
    public void copyOrRemoveSnapshot(java.lang.String p0,
                                     java.lang.String p1,
                                     boolean p2,
                                     java.lang.String p3) throws com.google.gwt.user.client.rpc.SerializationException {
        moduleService.copyOrRemoveSnapshot(p0,
                p1,
                p2,
                p3);
    }

    @Override
    public org.drools.guvnor.client.rpc.BuilderResult buildPackage(java.lang.String p0,
                                                                   boolean p1,
                                                                   java.lang.String p2,
                                                                   java.lang.String p3,
                                                                   java.lang.String p4,
                                                                   boolean p5,
                                                                   java.lang.String p6,
                                                                   java.lang.String p7,
                                                                   boolean p8,
                                                                   java.lang.String p9) throws com.google.gwt.user.client.rpc.SerializationException {
        return moduleService.buildPackage(p0,
                p1,
                p2,
                p3,
                p4,
                p5,
                p6,
                p7,
                p8,
                p9);
    }

    @Override
    public java.lang.String buildModuleSource(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return moduleService.buildModuleSource(p0);
    }

    @Override
    public String copyModule(java.lang.String p0,
                             java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return moduleService.copyModule(p0,
                p1);
    }

    @Override
    public void removeModule(java.lang.String p0) {
        moduleService.removeModule(p0);
    }

    @Override
    public java.lang.String renameModule(java.lang.String p0,
                                         java.lang.String p1) {
        return moduleService.renameModule(p0,
                p1);
    }

    @Override
    public void rebuildSnapshots() throws com.google.gwt.user.client.rpc.SerializationException {
        moduleService.rebuildSnapshots();
    }

    @Override
    public void rebuildPackages() throws com.google.gwt.user.client.rpc.SerializationException {
        moduleService.rebuildPackages();
    }

    @Override
    public java.lang.String[] listRulesInPackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return moduleService.listRulesInPackage(p0);
    }

    @Override
    public java.lang.String[] listImagesInModule(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return moduleService.listImagesInModule(p0);
    }

    @Override
    public org.drools.guvnor.client.rpc.SnapshotInfo[] listSnapshots(java.lang.String p0) {
        return moduleService.listSnapshots(p0);
    }

    @Override
    public java.lang.String[] listTypesInPackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return moduleService.listTypesInPackage(p0);
    }

    @Override
    public void installSampleRepository() throws com.google.gwt.user.client.rpc.SerializationException {
        moduleService.installSampleRepository();
    }

    @Override
    public org.drools.guvnor.client.rpc.SnapshotDiffs compareSnapshots(java.lang.String p0,
                                                                       java.lang.String p1,
                                                                       java.lang.String p2) {
        return moduleService.compareSnapshots(p0,
                p1,
                p2);
    }

    @Override
    public org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse compareSnapshots(org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest p0) {
        return moduleService.compareSnapshots(p0);
    }

    @Override
    public void updateDependency(java.lang.String p0,
                                 java.lang.String p1) {
        moduleService.updateDependency(p0,
                p1);
    }

    @Override
    public java.lang.String[] getDependencies(java.lang.String p0) {
        return moduleService.getDependencies(p0);
    }

}
