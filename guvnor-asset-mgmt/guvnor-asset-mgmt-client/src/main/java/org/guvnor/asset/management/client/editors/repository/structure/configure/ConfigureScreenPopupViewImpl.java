package org.guvnor.asset.management.client.editors.repository.structure.configure;

import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;


public class ConfigureScreenPopupViewImpl extends BaseModal {

    interface ConfigureScreenPopupWidgetBinder
            extends
            UiBinder<Widget, ConfigureScreenPopupViewImpl> {

    }

    private ConfigureScreenPopupWidgetBinder uiBinder = GWT.create(ConfigureScreenPopupWidgetBinder.class);

    @Inject
    private User identity;

    @UiField
    ControlGroup repositoryTextGroup;

    @UiField
    TextBox repositoryText;

    @UiField
    HelpInline repositoryTextHelpInline;
    
    @UiField
    ControlGroup sourceBranchTextGroup;

    @UiField
    TextBox sourceBranchText;

    @UiField
    HelpInline sourceBranchTextHelpInline;
    
    @UiField
    ControlGroup devBranchTextGroup;

    @UiField
    TextBox devBranchText;

    @UiField
    HelpInline devBranchTextHelpInline;

    @UiField
    ControlGroup releaseBranchTextGroup;

    @UiField
    TextBox releaseBranchText;

    @UiField
    HelpInline releaseBranchTextHelpInline;

    
    @UiField
    HelpInline versionTextHelpInline;

    @UiField
    ControlGroup versionTextGroup;

    @UiField
    TextBox versionText;

    private Command callbackCommand;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {

            if (isEmpty(devBranchText.getText())) {
                devBranchTextGroup.setType(ControlGroupType.ERROR);
                devBranchTextHelpInline.setText(Constants.INSTANCE.FieldMandatory0("Dev Branch"));

                return;
            }
            
            if (isEmpty(releaseBranchText.getText())) {
                releaseBranchTextGroup.setType(ControlGroupType.ERROR);
                releaseBranchTextHelpInline.setText(Constants.INSTANCE.FieldMandatory0("Release Branch"));

                return;
            }
            
            if (isEmpty(versionText.getText())) {
                versionTextGroup.setType(ControlGroupType.ERROR);
                versionTextHelpInline.setText(Constants.INSTANCE.FieldMandatory0("Version"));

                return;
            }

            if (callbackCommand != null) {
                callbackCommand.execute();
            }
            hide();
        }

        private boolean isEmpty(String value) {
            if (value == null || value.isEmpty()) {
                return true;
            }

            return false;
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            
            hide();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons(okCommand, cancelCommand);

    public ConfigureScreenPopupViewImpl() {
        setTitle(Constants.INSTANCE.Configure_Repository());
        setBackdrop(BackdropType.STATIC);
        setKeyboard(true);
        setAnimation(true);
        setDynamicSafe(true);

        add(uiBinder.createAndBindUi(this));
        add(footer);
    }

    public void configure(String repositoryAlias, String branch, String repositoryVersion, Command command) {
        this.callbackCommand = command;
        this.devBranchText.setText("dev");
        this.devBranchTextHelpInline.setText("The branch will be called (dev)-"+repositoryVersion);
        this.releaseBranchText.setText("release");
        this.releaseBranchTextHelpInline.setText("The branch will be called (release)-"+repositoryVersion);
        this.sourceBranchText.setText(branch);
        this.repositoryText.setText(repositoryAlias);
        this.sourceBranchText.setReadOnly(true);
        this.repositoryText.setReadOnly(true);
        this.versionTextHelpInline.setText("The current repository version is: "+repositoryVersion);
        this.versionText.setText(repositoryVersion);
    }

    public String getDevBranch() {
        return this.devBranchText.getText();
    }

    public String getReleaseBranch() {
        return this.releaseBranchText.getText();
    }

    public String getVersion(){
        return this.versionText.getText();
    }

}
