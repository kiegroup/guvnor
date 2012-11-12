package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.IsWidget;

public interface ProjectModelScreenView
        extends IsWidget {

    interface Presenter{

        void onKBaseSelection(String name);

    }
    void setPresenter(Presenter presenter);

    void addKnowledgeBaseConfiguration(String kbaseName);

    void showForm(KnowledgeBaseConfiguration knowledgeBaseConfiguration);

}
