package org.kie.guvnor.configresource.client.widget;

import com.google.gwt.user.client.ui.IsWidget;

public interface ImportsWidgetView
        extends IsWidget {

    interface Presenter {

        void onAddImport();

        void onRemoveImport();

    }

    void addImport(String type);

    String getSelected();

    void removeImport(String selected);

    void setupReadOnly();

    void setPresenter(Presenter presenter);

    void showPleaseSelectAnImport();
}
