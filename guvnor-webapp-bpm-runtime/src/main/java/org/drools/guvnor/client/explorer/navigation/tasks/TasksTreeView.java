package org.drools.guvnor.client.explorer.navigation.tasks;

import com.google.gwt.user.client.ui.IsWidget;

public interface TasksTreeView extends IsWidget {

    interface Presenter {

        void onPersonalTasksSelected();

        void onGroupTasksSelected();
    }

    void setPresenter(Presenter presenter);
}
