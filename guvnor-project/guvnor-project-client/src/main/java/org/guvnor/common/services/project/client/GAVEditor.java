/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.project.client;

import java.util.ArrayList;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.GAV;

public class GAVEditor
        implements GAVEditorView.Presenter,
                   IsWidget {

    private final GAVEditorView view;
    private GAV gav;
    private ArrayList<GroupIdChangeHandler> groupIdChangeHandlers = new ArrayList<GroupIdChangeHandler>();
    private ArrayList<ArtifactIdChangeHandler> artifactIdChangeHandlers = new ArrayList<ArtifactIdChangeHandler>();
    private ArrayList<VersionChangeHandler> versionChangeHandlers = new ArrayList<VersionChangeHandler>();

    @Inject
    public GAVEditor( GAVEditorView view ) {
        this.view = view;
        view.setPresenter( this );
    }

    @Override
    public void setGAV( GAV gav ) {
        this.gav = gav;
        view.setGroupId( gav.getGroupId() );
        view.setArtifactId( gav.getArtifactId() );
        view.setVersion( gav.getVersion() );
    }

    @Override
    public void onGroupIdChange( String groupId ) {
        gav.setGroupId( groupId );
        for ( GroupIdChangeHandler handler : groupIdChangeHandlers ) {
            handler.onChange( groupId );
        }
    }

    @Override
    public void onArtifactIdChange( String artifactId ) {
        gav.setArtifactId( artifactId );
        for ( ArtifactIdChangeHandler handler : artifactIdChangeHandlers ) {
            handler.onChange( artifactId );
        }
    }

    @Override
    public void onVersionChange( String version ) {
        gav.setVersion( version );
        for ( VersionChangeHandler handler : versionChangeHandlers ) {
            handler.onChange( version );
        }
    }

    @Override
    public void addGroupIdChangeHandler( GroupIdChangeHandler changeHandler ) {
        groupIdChangeHandlers.add( changeHandler );
    }

    @Override
    public void addArtifactIdChangeHandler( ArtifactIdChangeHandler changeHandler ) {
        artifactIdChangeHandlers.add( changeHandler );
    }

    @Override
    public void addVersionChangeHandler( VersionChangeHandler changeHandler ) {
        versionChangeHandlers.add( changeHandler );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void setReadOnly() {
        view.setReadOnly();
    }

    @Override
    public void disableGroupID( String reason ) {
        view.disableGroupID( reason );
    }

    @Override
    public void disableVersion( String reason ) {
        view.disableVersion( reason );
    }

    @Override
    public void disableArtifactID( String reason ) {
        view.disableArtifactID( reason );
    }

    @Override
    public void enableGroupID() {
        view.enableGroupID();
    }

    @Override
    public void enableVersion() {
        view.enableVersion();
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
