package org.drools.guvnor.client.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class KnowledgeBaseConfigurationFormViewImpl
        extends Composite
        implements KnowledgeBaseConfigurationFormView {

    interface KnowledgeBaseConfigurationFormViewImplBinder
            extends
            UiBinder<Widget, KnowledgeBaseConfigurationFormViewImpl> {

    }

    private static KnowledgeBaseConfigurationFormViewImplBinder uiBinder = GWT.create(KnowledgeBaseConfigurationFormViewImplBinder.class);

    @UiField
    TextBox nameTextBox;

    @UiField
    TextBox nameSpaceTextBox;

    public KnowledgeBaseConfigurationFormViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setName(String name) {
        nameTextBox.setText(name);
    }

    @Override
    public void setNamespace(String namespace) {
        nameSpaceTextBox.setText(namespace);
    }

}
