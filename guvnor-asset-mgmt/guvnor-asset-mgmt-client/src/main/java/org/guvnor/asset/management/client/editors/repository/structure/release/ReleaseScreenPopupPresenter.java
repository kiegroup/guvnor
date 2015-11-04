/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guvnor.asset.management.client.editors.repository.structure.release;

import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.user.client.Command;
import org.guvnor.asset.management.client.i18n.Constants;


public class ReleaseScreenPopupPresenter {
     
    private ReleaseScreenPopupViewImpl view;
    private Command callbackCommand;

    public ReleaseScreenPopupPresenter(ReleaseScreenPopupViewImpl view, Command callbackCommand) {
        this.view = view;
        this.callbackCommand = callbackCommand;
    }
    
    
    
    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            if ( isEmpty( view.getVersionText().getText() ) ) {
                view.getVersionTextGroup().setType( ControlGroupType.ERROR );
                view.getVersionTextHelpInline().setText( Constants.INSTANCE.FieldMandatory0( "Version" ) );

                return;
            }
            if ( isSnapshot( view.getVersionText().getText() ) ) {
                view.getVersionTextGroup().setType( ControlGroupType.ERROR );
                view.getVersionTextHelpInline().setText( Constants.INSTANCE.SnapshotNotAvailableForRelease( "-SNAPSHOT" ) );

                return;
            }
            if( !view.getSourceBranchText().getText().startsWith("release")){
                view.getSourceBranchTextGroup().setType( ControlGroupType.ERROR );
                view.getSourceBranchTextHelpInline().setText( Constants.INSTANCE.ReleaseCanOnlyBeDoneFromAReleaseBranch());
                return;
            }
            if ( view.getDeployToRuntimeCheck().getValue() ) {

                if ( isEmpty( view.getUserNameText().getText() ) ) {
                    view.getUserNameTextGroup().setType( ControlGroupType.ERROR );
                    view.getUserNameTextHelpInline().setText( Constants.INSTANCE.FieldMandatory0( "Username" ) );

                    return;
                }

                if ( isEmpty( view.getPasswordText().getText() ) ) {
                    view.getPasswordTextGroup().setType( ControlGroupType.ERROR );
                    view.getPasswordTextHelpInline().setText( Constants.INSTANCE.FieldMandatory0( "Password" ) );

                    return;
                }

                if ( isEmpty( view.getServerURLText().getText() ) ) {
                    view.getServerURLTextGroup().setType( ControlGroupType.ERROR );
                    view.getServerURLTextHelpInline().setText( Constants.INSTANCE.FieldMandatory0( "ServerURL" ) );

                    return;
                }

            }

            if ( callbackCommand != null ) {
                callbackCommand.execute();
            }
            view.hide();
        }

        private boolean isEmpty( String value ) {
            return value == null || value.isEmpty() || value.trim().isEmpty();
        }

        private boolean isSnapshot( String value ) {
            return value != null && value.trim().endsWith( "-SNAPSHOT" );
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            view.hide();
        }
    };

    public Command getOkCommand() {
        return okCommand;
    }

    public Command getCancelCommand() {
        return cancelCommand;
    }
    
    
}
