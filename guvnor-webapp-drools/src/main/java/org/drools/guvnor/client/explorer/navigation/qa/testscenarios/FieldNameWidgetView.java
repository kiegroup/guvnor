package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

public interface FieldNameWidgetView {

    interface Presenter {

        void onClick();

    }

    void setPresenter(FieldNameWidgetView.Presenter presenter);

    void setTitle(String title);

    void openNewFieldSelector();
}
