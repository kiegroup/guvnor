package org.kie.guvnor.projecteditor.client.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class GAVEditorViewImpl
        extends Composite
        implements GAVEditorView {

    interface Binder
            extends UiBinder<Widget, GAVEditorViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    TextBox groupIdTextBox;

    @UiField
    TextBox artifactIdTextBox;

    @UiField
    TextBox versionIdTextBox;

    private Presenter presenter;

    public GAVEditorViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setGroupId(String id) {
        groupIdTextBox.setText(id);
    }

    @UiHandler("groupIdTextBox")
    public void onGroupIdChange(KeyUpEvent event) {
        presenter.onGroupIdChange(groupIdTextBox.getText());
    }

    @Override
    public void setArtifactId(String id) {
        artifactIdTextBox.setText(id);
    }

    @UiHandler("artifactIdTextBox")
    public void onArtifactIdChange(KeyUpEvent event) {
        presenter.onArtifactIdChange(artifactIdTextBox.getText());
    }

    @Override
    public void setVersionId(String versionId) {
        versionIdTextBox.setText(versionId);
    }

    @UiHandler("versionIdTextBox")
    public void onVersionIdChange(KeyUpEvent event) {
        presenter.onVersionIdChange(versionIdTextBox.getText());
    }
}
