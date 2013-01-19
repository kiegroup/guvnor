package org.kie.guvnor.projecteditor.client.forms;

import javax.inject.Inject;

public class DependencySelectorPopup {

    private final DependencySelectorPopupView view;

    @Inject
    public DependencySelectorPopup(DependencySelectorPopupView view) {
        this.view = view;
    }


    public void show() {
        view.show();
    }
}
