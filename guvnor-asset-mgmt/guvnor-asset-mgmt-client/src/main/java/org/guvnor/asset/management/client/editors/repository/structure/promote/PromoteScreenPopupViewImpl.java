package org.guvnor.asset.management.client.editors.repository.structure.promote;

import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.uberfire.client.common.popups.KieBaseModal;
import org.kie.uberfire.client.common.popups.footers.ModalFooterOKCancelButtons;


public class PromoteScreenPopupViewImpl extends KieBaseModal {

    interface PromoteScreenPopupWidgetBinder
            extends
            UiBinder<Widget, PromoteScreenPopupViewImpl> {

    }

    private PromoteScreenPopupWidgetBinder uiBinder = GWT.create(PromoteScreenPopupWidgetBinder.class);

    @Inject
    private User identity;

    @UiField
    ControlGroup targetBranchTextGroup;

    @UiField
    TextBox targetBranchText;

    @UiField
    HelpInline targetBranchTextHelpInline;

    

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

    public PromoteScreenPopupViewImpl() {
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

    public String getTargetBranch() {
        return this.targetBranchText.getText();
    }

   


}
