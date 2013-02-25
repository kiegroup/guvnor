package org.kie.guvnor.testscenario.client;

public interface FieldNameWidgetView {

    interface Presenter {

        void onClick();

    }

    void setPresenter(FieldNameWidgetView.Presenter presenter);

    void setTitle(String title);

    void openNewFieldSelector();
}
