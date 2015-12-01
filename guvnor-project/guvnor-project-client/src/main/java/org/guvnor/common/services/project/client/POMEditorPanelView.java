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

package org.guvnor.common.services.project.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public interface POMEditorPanelView extends HasBusyIndicator,
                                            IsWidget {

    interface Presenter {

        void addNameChangeHandler( final NameChangeHandler changeHandler );

        void addGroupIdChangeHandler( final GroupIdChangeHandler changeHandler );

        void addArtifactIdChangeHandler( final ArtifactIdChangeHandler changeHandler );

        void addVersionChangeHandler( final VersionChangeHandler changeHandler );

        void onNameChange( final String name );

        void onDescriptionChange( final String description );

        void onOpenProjectContext();

        void disableGroupID( final String reason );

        void disableVersion( final String reason );

        POM getPom();

        void setValidName( final boolean isValid );

        void setValidGroupID( final boolean isValid );

        void setValidArtifactID( final boolean isValid );

        void setValidVersion( final boolean isValid );

    }

    void setPresenter( final Presenter presenter );

    String getTitleWidget();

    void setTitleText( final String titleText );

    void setProjectModelTitleText();

    void showSaveSuccessful( final String fileName );

    void setName( final String projectName );

    void setDescription( final String projectDescription );

    void setArtifactID( final String artifactID );

    void showParentGAV();

    void hideParentGAV();

    void setParentGAV( GAV gav );

    void setGAV( GAV gav );

    void addGroupIdChangeHandler( final GroupIdChangeHandler changeHandler );

    void addArtifactIdChangeHandler( final ArtifactIdChangeHandler changeHandler );

    void addVersionChangeHandler( final VersionChangeHandler changeHandler );

    void setReadOnly();

    void disableGroupID( final String reason );

    void disableVersion( final String reason );

    void enableGroupID();

    void enableVersion();

    void setValidName( final boolean isValid );

    void setValidGroupID( final boolean isValid );

    void setValidArtifactID( final boolean isValid );

    void setValidVersion( final boolean isValid );

}
