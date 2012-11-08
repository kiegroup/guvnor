package org.drools.guvnor.server;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.drools.guvnor.client.rpc.ModuleService;
import org.drools.guvnor.client.rpc.Path;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.jboss.solder.core.Veto;

import javax.inject.Inject;

@Veto
public class ModuleServiceServlet
        extends RemoteServiceServlet
        implements ModuleService {

    @Inject
    private ModuleService moduleService;
    
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
    public Path createModule(java.lang.String p0,
                                         java.lang.String p1,
                                         java.lang.String p2) throws com.google.gwt.user.client.rpc.SerializationException {
        return moduleService.createModule(p0,
                p1, p2);
    }

    @Override
    public Path createSubModule(java.lang.String p0,
                                            java.lang.String p1,
                                            java.lang.String p2) throws com.google.gwt.user.client.rpc.SerializationException {
        return moduleService.createSubModule(p0,
                p1,
                p2);
    }

    @Override
    public org.drools.guvnor.client.rpc.Module loadModule(Path p0) {
        return moduleService.loadModule(p0);
    }

    @Override
    public void saveModule(org.drools.guvnor.client.rpc.Module p0) throws com.google.gwt.user.client.rpc.SerializationException {
        moduleService.saveModule(p0);
    }

	public void createModuleSnapshot(java.lang.String p0, java.lang.String p1,
			boolean p2, java.lang.String p3) throws SerializationException {
		moduleService.createModuleSnapshot(p0, p1, p2, p3);
	}

	public void createModuleSnapshot(java.lang.String p0, java.lang.String p1,
			boolean p2, java.lang.String p3, boolean p4)
			throws SerializationException {
		moduleService.createModuleSnapshot(p0, p1, p2, p3, p4);
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
    public org.drools.guvnor.client.rpc.BuilderResult buildPackage(Path p0,
                                                                   boolean p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return moduleService.buildPackage(p0,
                p1);
    }
    
    @Override
    public org.drools.guvnor.client.rpc.BuilderResult buildPackage(Path p0,
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
    public java.lang.String buildModuleSource(Path p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return moduleService.buildModuleSource(p0);
    }

    @Override
    public Path copyModule(java.lang.String p0,
                             java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return moduleService.copyModule(p0,
                p1);
    }

    @Override
    public void removeModule(Path p0) {
        moduleService.removeModule(p0);
    }

    @Override
    public Path renameModule(Path p0,
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
    public java.lang.String[] listTypesInPackage(Path p0) throws com.google.gwt.user.client.rpc.SerializationException {
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
    public void updateDependency(Path p0,
                                 java.lang.String p1) {
        moduleService.updateDependency(p0,
                p1);
    }

    @Override
    public java.lang.String[] getDependencies(Path p0) {
        return moduleService.getDependencies(p0);
    }

}
