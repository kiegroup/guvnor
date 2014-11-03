package org.guvnor.asset.management.client.editors.repository.structure.configure;

import com.github.gwtbootstrap.client.ui.CheckBox;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
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
import org.kie.uberfire.client.common.popups.KieBaseModal;
import org.kie.uberfire.client.common.popups.footers.ModalFooterOKCancelButtons;


public class ConfigureScreenPopupViewImpl extends KieBaseModal {

    interface ConfigureScreenPopupWidgetBinder
            extends
            UiBinder<Widget, ConfigureScreenPopupViewImpl> {

    }

    private ConfigureScreenPopupWidgetBinder uiBinder = GWT.create(ConfigureScreenPopupWidgetBinder.class);

    @Inject
    private User identity;

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
        setTitle(Constants.INSTANCE.Release());
        setBackdrop(BackdropType.STATIC);
        setKeyboard(true);
        setAnimation(true);
        setDynamicSafe(true);

        add(uiBinder.createAndBindUi(this));
        add(footer);
    }

    public void configure(Command command) {
        this.callbackCommand = command;

      
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
