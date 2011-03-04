package org.drools.guvnor.client.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.Popup;
import org.drools.guvnor.client.messages.Constants;

public class PerspectiveEditorPopUpViewImpl extends Popup implements PerspectiveEditorPopUpView {

    interface PerspectiveEditorPopUpViewImplBinder
            extends
            UiBinder<Widget, PerspectiveEditorPopUpViewImpl> {

    }

    private static PerspectiveEditorPopUpViewImplBinder uiBinder = GWT.create(PerspectiveEditorPopUpViewImplBinder.class);

    private Presenter presenter;

    private Widget content;

    @UiField
    TextBox nameTextBox;

    @UiField
    TextBox urlTextBox;

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    public PerspectiveEditorPopUpViewImpl() {
        setTitle(Constants.INSTANCE.PerspectivesConfiguration());
        content = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget getContent() {
        return content;
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setName(String name) {
        nameTextBox.setText(name);
    }

    public String getName() {
        return nameTextBox.getText();
    }

    public void setUrl(String url) {
        urlTextBox.setText(url);
    }

    public String getUrl() {
        return urlTextBox.getText();
    }

    public void showNameCanNotBeEmptyWarning() {
        ErrorPopup.showMessage(Constants.INSTANCE.NameCanNotBeEmpty());
    }

    public void showUrlCanNotBeEmptyWarning() {
        ErrorPopup.showMessage(Constants.INSTANCE.UrlCanNotBeEmpty());
    }

    @UiHandler("saveButton")
    public void okClick(ClickEvent event) {
        presenter.onSave();
    }

    @UiHandler("cancelButton")
    public void handleClick(ClickEvent event) {
        presenter.onCancel();
    }
}
