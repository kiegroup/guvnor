/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
