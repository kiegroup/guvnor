/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.asset.management.client.editors.project.structure;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.FluidRow;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.editors.project.structure.widgets.ProjectModulesView;
import org.guvnor.asset.management.client.editors.project.structure.widgets.ProjectStructureDataView;
import org.guvnor.asset.management.model.ProjectStructureModel;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.kie.uberfire.client.common.BusyPopup;

@ApplicationScoped
public class ProjectStructureViewImpl
        extends Composite
        implements ProjectStructureView {

    private ProjectStructurePresenter presenter;

    interface ProjectStructureViewImplBinder
            extends
            UiBinder<Widget, ProjectStructureViewImpl> {

    }

    private static ProjectStructureViewImplBinder uiBinder = GWT.create( ProjectStructureViewImplBinder.class );

    @UiField
    FluidRow dataViewContainer;

    @UiField(provided = true)
    ProjectStructureDataView dataView;

    @UiField
    FluidRow modulesViewContainer;

    @UiField(provided = true)
    ProjectModulesView modulesView;

    @Inject
    public ProjectStructureViewImpl( ProjectStructureDataView dataView,
            ProjectModulesView modulesView ) {

        this.dataView = dataView;
        this.modulesView = modulesView;

        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void setPresenter( ProjectStructurePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public ProjectStructureDataView getDataView() {
        return dataView;
    }

    @Override
    public ProjectModulesView getModulesView() {
        return modulesView;
    }

    @Override
    public void setModulesViewVisible( boolean visible ) {
        modulesViewContainer.setVisible( visible );
    }

    @Override
    public void setModel( ProjectStructureModel model ) {
        if ( model == null ) {
            return;
        }

        if ( model.getPathToPOM() != null ) {
            getDataView().setGroupId( model.getPOM().getGav().getGroupId() );
            getDataView().setArtifactId( model.getPOM().getGav().getArtifactId() );
            getDataView().setVersion( model.getPOM().getGav().getVersion() );
        } else if ( model.isSingleProject() ) {
            Project project = model.getOrphanProjects().get( 0 );
            POM pom = model.getOrphanProjectsPOM().get( project.getSignatureId() );
            if ( pom != null && pom.getGav() != null ) {
                getDataView().setSingleProjectGroupId( pom.getGav().getGroupId() );
                getDataView().setSingleProjectArtifactId( pom.getGav().getArtifactId() );
                getDataView().setSingleProjectVersion( pom.getGav().getVersion() );
            }
        }
    }

    @Override
    public void clear() {
        getDataView().clear();
    }
}
