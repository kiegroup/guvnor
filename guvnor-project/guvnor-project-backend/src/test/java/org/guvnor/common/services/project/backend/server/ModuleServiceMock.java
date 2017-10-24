package org.guvnor.common.services.project.backend.server;

import java.util.Set;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.structure.repositories.Branch;
import org.uberfire.backend.vfs.Path;

/**
 * Created by tonirikkola on 23.10.2017.
 */
public class ModuleServiceMock
        implements ModuleService<MockModule> {

    @Override
    public MockModule resolveModule(Path resource) {
        return null;
    }

    @Override
    public Module resolveParentModule(Path resource) {
        return null;
    }

    @Override
    public Module resolveToParentModule(Path resource) {
        return null;
    }

    @Override
    public Set<Package> resolvePackages(Module module) {
        return null;
    }

    @Override
    public Set<Package> resolvePackages(Package pkg) {
        return null;
    }

    @Override
    public Package resolveDefaultPackage(Module module) {
        return null;
    }

    @Override
    public Package resolveDefaultWorkspacePackage(Module module) {
        return null;
    }

    @Override
    public Package resolveParentPackage(Package pkg) {
        return null;
    }

    @Override
    public Path resolveDefaultPath(Package pkg, String resourceType) {
        return null;
    }

    @Override
    public boolean isPom(Path resource) {
        return false;
    }

    @Override
    public Package resolvePackage(Path resource) {
        return null;
    }

    @Override
    public Set<Module> getAllModules(Branch branch) {
        return null;
    }

    @Override
    public MockModule newModule(Path repositoryRoot, POM pom, String baseURL) {
        return null;
    }

    @Override
    public MockModule newModule(Path repositoryRoot, POM pom, String baseURL, DeploymentMode mode) {
        return null;
    }

    @Override
    public Package newPackage(Package pkg, String packageName) {
        return null;
    }

    @Override
    public Path rename(Path pathToPomXML, String newName, String comment) {
        return null;
    }

    @Override
    public void delete(Path pathToPomXML, String comment) {

    }

    @Override
    public void copy(Path pathToPomXML, String newName, String comment) {

    }

    @Override
    public void reImport(Path pathToPomXML) {

    }
}
