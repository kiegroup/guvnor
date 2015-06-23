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

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.GAV;

public interface GAVEditorView
        extends IsWidget {

    interface Presenter {

        void setGAV( GAV gav );

        void onGroupIdChange( String groupId );

        void onArtifactIdChange( String artifactId );

        void onVersionChange( String version );

        void addGroupIdChangeHandler( GroupIdChangeHandler changeHandler );

        void addArtifactIdChangeHandler( ArtifactIdChangeHandler changeHandler );

        void addVersionChangeHandler( VersionChangeHandler changeHandler );

        void setReadOnly();

        void disableGroupID( String reason );

        void disableVersion( String reason );

        void disableArtifactID( String reason );

        void enableGroupID();

        void enableVersion();

        void setValidGroupID( boolean isValid );

        void setValidArtifactID( boolean isValid );

        void setValidVersion( boolean isValid );

    }

    void setPresenter( Presenter presenter );

    void setGroupId( String id );

    void setArtifactId( String id );

    void setReadOnly();

    void setVersion( String version );

    void disableGroupID( String reason );

    void disableArtifactID( String reason );

    void disableVersion( String reason );

    void enableGroupID();

    void enableVersion();

    void setValidGroupID( boolean isValid );

    void setValidArtifactID( boolean isValid );

    void setValidVersion( boolean isValid );

}
