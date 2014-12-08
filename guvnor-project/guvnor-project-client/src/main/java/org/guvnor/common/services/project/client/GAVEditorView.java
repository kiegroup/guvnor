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
