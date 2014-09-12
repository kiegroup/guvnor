package org.guvnor.common.services.project.client;

import com.google.gwt.user.client.ui.IsWidget;

public interface GAVEditorView
        extends IsWidget {


    interface Presenter {

        void onGroupIdChange( String groupId );

        void onArtifactIdChange( String artifactId );

        void onVersionChange( String version );

    }

    void setPresenter( Presenter presenter );

    void setGroupId( String id );

    void setArtifactId( String id );

    void setReadOnly();

    void setVersion( String version );

    void disableGroupID(String reason);

    void disableArtifactID(String reason);

    void disableVersion(String reason);

    void enableGroupID();

    void enableVersion();
}
