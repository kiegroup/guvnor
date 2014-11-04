package org.guvnor.asset.management.client.editors.repository.structure.promote;

import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;
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
    ControlGroup targetBranchListBoxGroup;

    @UiField
    ListBox targetBranchListBox;

    @UiField
    HelpInline targetBranchListBoxHelpInline;

    

    private Command callbackCommand;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {

            if (targetBranchListBox.getValue().equals(Constants.INSTANCE.Select_A_Branch())
                    || targetBranchListBox.getValue().equals(sourceBranchText.getText())) {
                targetBranchListBoxGroup.setType(ControlGroupType.ERROR);
                targetBranchListBoxHelpInline.setText(Constants.INSTANCE.FieldMandatory0("Target Branch"));

                return;
            }

            if (callbackCommand != null) {
                callbackCommand.execute();
            }
            hide();
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
        setTitle(Constants.INSTANCE.Promote_Assets());
        setBackdrop(BackdropType.STATIC);
        setKeyboard(true);
        setAnimation(true);
        setDynamicSafe(true);

        add(uiBinder.createAndBindUi(this));
        add(footer);
    }

    public void configure(String repositoryAlias, String branch, Collection<String> branches, Command command) {
        this.callbackCommand = command;
        this.sourceBranchText.setText(branch);
        this.repositoryText.setText(repositoryAlias);
        this.sourceBranchText.setReadOnly(true);
        this.repositoryText.setReadOnly(true);
        targetBranchListBox.clear();
        this.targetBranchListBox.addItem( Constants.INSTANCE.Select_A_Branch() );
        
        for ( String b : branches ) {
                targetBranchListBox.addItem( b, b );
                
        }
    }

    public String getTargetBranch() {
        return this.targetBranchListBox.getValue();
    }

   


}
