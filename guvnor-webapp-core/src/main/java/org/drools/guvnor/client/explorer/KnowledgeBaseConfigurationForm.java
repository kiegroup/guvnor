package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import javax.inject.Inject;

public class KnowledgeBaseConfigurationForm
        implements IsWidget {

    private final KnowledgeBaseConfigurationFormView view;

    @Inject
    public KnowledgeBaseConfigurationForm(KnowledgeBaseConfigurationFormView view) {
        this.view = view;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setConfig(KnowledgeBaseConfiguration knowledgeBaseConfiguration) {
        view.setName(knowledgeBaseConfiguration.getName());
        view.setNamespace(knowledgeBaseConfiguration.getNamespace());
    }
}
