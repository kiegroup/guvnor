package org.kie.guvnor.projecteditor.client.forms;

public interface DependencySelectorPopupView {

    interface Presenter {
        void onPathSelection(String pathToDependency);
    }

    void show();

    void setPresenter(Presenter presenter);
}
