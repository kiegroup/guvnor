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

        void addNameChangeHandler( NameChangeHandler changeHandler );

        void addGroupIdChangeHandler( GroupIdChangeHandler changeHandler );

        void addArtifactIdChangeHandler( ArtifactIdChangeHandler changeHandler );

        void addVersionChangeHandler( VersionChangeHandler changeHandler );

        void onNameChange( String name );

        void onDescriptionChange( String description );

        void onOpenProjectContext();

        void disableGroupID( String reason );

        void disableVersion( String reason );

        POM getPom();

        void setValidName( boolean isValid );

        void setValidGroupID( boolean isValid );

        void setValidArtifactID( boolean isValid );

        void setValidVersion( boolean isValid );

    }

    void setPresenter( Presenter presenter );

    String getTitleWidget();

    void setTitleText( String titleText );

    void setProjectModelTitleText();

    void showSaveSuccessful( String fileName );

    void setName( String projectName );

    void setDescription( String projectDescription );

    void showParentGAV();

    void hideParentGAV();

    void setParentGAV( GAV gav );

    void setGAV( GAV gav );

    void addGroupIdChangeHandler( GroupIdChangeHandler changeHandler );

    void addArtifactIdChangeHandler( ArtifactIdChangeHandler changeHandler );

    void addVersionChangeHandler( VersionChangeHandler changeHandler );

    void setReadOnly();

    void disableGroupID( String reason );

    void disableVersion( String reason );

    void enableGroupID();

    void enableVersion();

    void setValidName( boolean isValid );

    void setValidGroupID( boolean isValid );

    void setValidArtifactID( boolean isValid );

    void setValidVersion( boolean isValid );

}
