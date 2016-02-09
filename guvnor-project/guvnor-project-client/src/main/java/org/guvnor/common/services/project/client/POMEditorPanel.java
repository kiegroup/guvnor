/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.client;

import java.util.ArrayList;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
public class POMEditorPanel
        implements POMEditorPanelView.Presenter,
                   IsWidget {

    private ArrayList<NameChangeHandler> nameChangeHandlers = new ArrayList<NameChangeHandler>();
    private POMEditorPanelView view;
    private SyncBeanManager iocManager;
    private POM model;

    public POMEditorPanel() {
    }

    @Inject
    public POMEditorPanel( final POMEditorPanelView view,
                           final SyncBeanManager iocManager ) {
        this.view = view;
        this.iocManager = iocManager;
        view.setPresenter( this );
    }

    public void setPOM( POM model,
                        boolean isReadOnly ) {
        if ( isReadOnly ) {
            view.setReadOnly();
        }

        this.model = model;

        view.setName( model.getName() );
        view.setDescription( model.getDescription() );
        if ( model.hasParent() ) {
            view.setParentGAV( model.getParent() );
            view.showParentGAV();
            view.disableGroupID( "" );
            view.enableArtifactID();
            view.disableVersion( "" );
        } else {
            view.hideParentGAV();
            view.enableGroupID();
            view.enableArtifactID();
            view.enableVersion();
        }
        view.setGAV( model.getGav() );
        view.addArtifactIdChangeHandler( new ArtifactIdChangeHandler() {
            @Override
            public void onChange( String newArtifactId ) {
                setTitle( newArtifactId );
            }
        } );
        setTitle( model.getGav().getArtifactId() );
    }

    public void setArtifactID( final String artifactID ) {
        view.setArtifactID( artifactID );
    }

    private void setTitle( final String titleText ) {
        if ( titleText == null || titleText.isEmpty() ) {
            view.setProjectModelTitleText();
        } else {
            view.setTitleText( titleText );
        }
    }

    @Override
    public void addNameChangeHandler( final NameChangeHandler changeHandler ) {
        nameChangeHandlers.add( changeHandler );
    }

    @Override
    public void addGroupIdChangeHandler( final GroupIdChangeHandler changeHandler ) {
        this.view.addGroupIdChangeHandler( changeHandler );
    }

    @Override
    public void addArtifactIdChangeHandler( final ArtifactIdChangeHandler changeHandler ) {
        this.view.addArtifactIdChangeHandler( changeHandler );
    }

    @Override
    public void addVersionChangeHandler( final VersionChangeHandler changeHandler ) {
        this.view.addVersionChangeHandler( changeHandler );
    }

    @Override
    public void onNameChange( final String name ) {
        this.model.setName( name );
        for ( NameChangeHandler changeHandler : nameChangeHandlers ) {
            changeHandler.onChange( name );
        }
    }

    @Override
    public void onDescriptionChange( final String description ) {
        this.model.setDescription( description );
    }

    @Override
    public void onOpenProjectContext() {
        IOCBeanDef<PlaceManager> placeManagerIOCBeanDef = iocManager.lookupBean( PlaceManager.class );
        placeManagerIOCBeanDef.getInstance().goTo( "repositoryStructureScreen" );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void disableGroupID( final String reason ) {
        view.disableGroupID( reason );
    }

    @Override
    public void disableVersion( final String reason ) {
        view.disableVersion( reason );
    }

    @Override
    public POM getPom() {
        return model;
    }

    @Override
    public void setValidName( final boolean isValid ) {
        view.setValidName( isValid );
    }

    @Override
    public void setValidGroupID( final boolean isValid ) {
        view.setValidGroupID( isValid );
    }

    @Override
    public void setValidArtifactID( final boolean isValid ) {
        view.setValidArtifactID( isValid );
    }

    @Override
    public void setValidVersion( final boolean isValid ) {
        view.setValidVersion( isValid );
    }

}