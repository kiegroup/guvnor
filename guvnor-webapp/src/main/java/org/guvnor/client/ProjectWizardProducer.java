package org.guvnor.client;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectWizard;
import org.uberfire.client.callbacks.Callback;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * This is help class, which produces mock instance of ProjectWizard
 *
 * Produced mock is needed because of dependency on guvnor-asset-mgmt, where is
 * needed ProjectWizard instance to be injected into RepositoryStructurePresenter
 *
 * But guvnor-webapp uses from guvnor-asset-mgmt only one class CreateRepositoryWizard,
 * which doesn't need any ProjectWizard instance
 */
@ApplicationScoped
public class ProjectWizardProducer {

    private ProjectWizard wizard = new ProjectWizard() {

        @Override
        public void setContent(String projectName, String groupId, String version) {

        }

        @Override
        public void start() {

        }

        @Override
        public void start(Callback<Project> callback, boolean openEditor) {

        }
    };

    @Produces
    public ProjectWizard getProjectWizard() {
        return wizard;
    }
}
