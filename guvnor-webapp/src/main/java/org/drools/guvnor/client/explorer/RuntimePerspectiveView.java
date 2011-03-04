package org.drools.guvnor.client.explorer;


public interface RuntimePerspectiveView extends PerspectiveView {

    interface Presenter {
    }

    void setPresenter(Presenter presenter);
}
