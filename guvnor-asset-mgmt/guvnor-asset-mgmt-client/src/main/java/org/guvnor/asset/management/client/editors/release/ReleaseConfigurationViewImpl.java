/*
 * Copyright 2012 JBoss Inc
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
package org.guvnor.asset.management.client.editors.release;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class ReleaseConfigurationViewImpl extends Composite implements ReleaseConfigurationPresenter.ReleaseConfigurationView {

    interface Binder
            extends UiBinder<Widget, ReleaseConfigurationViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @Inject
    private PlaceManager placeManager;

    private ReleaseConfigurationPresenter presenter;

    @UiField
    public ListBox chooseRepositoryBox;

    @UiField
    public ListBox chooseBranchBox;

    @UiField
    public Button releaseButton;

    @UiField
    public TextBox userNameText;

    @UiField
    public PasswordTextBox passwordText;

    @UiField
    public TextBox serverURLText;

    @UiField
    public CheckBox deployToRuntimeCheck;

    @UiField
    public TextBox versionText;

    @UiField
    public TextBox currentVersionText;

    @UiField
    public FluidRow deployToRuntimeRow;

    @UiField
    public FluidRow usernameRow;

    @UiField
    public FluidRow passwordRow;

    @UiField
    public FluidRow serverURLRow;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );

    public ReleaseConfigurationViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final ReleaseConfigurationPresenter presenter ) {
        this.presenter = presenter;
        currentVersionText.setReadOnly( true );
        presenter.loadServerSetting();
        chooseRepositoryBox.addChangeHandler( new ChangeHandler() {

            @Override
            public void onChange( ChangeEvent event ) {
                String value = chooseRepositoryBox.getValue();

                presenter.loadBranches( value );
                presenter.loadRepositoryStructure( value );

            }
        } );

        presenter.loadRepositories();

    }

    public void showHideDeployToRuntimeSection( boolean show ) {
        if ( show ) {
            deployToRuntimeRow.setVisible( true );
            usernameRow.setVisible( true );
            passwordRow.setVisible( true );
            serverURLRow.setVisible( true );

            // by default deploy to runtime inputs are disabled
            userNameText.setEnabled( false );
            passwordText.setEnabled( false );
            serverURLText.setEnabled( false );

            deployToRuntimeCheck.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange( ValueChangeEvent<Boolean> event ) {
                    if ( event.getValue() ) {
                        userNameText.setEnabled( true );
                        passwordText.setEnabled( true );
                        serverURLText.setEnabled( true );
                    } else {
                        userNameText.setEnabled( false );
                        passwordText.setEnabled( false );
                        serverURLText.setEnabled( false );
                    }
                }
            } );
        } else {
            deployToRuntimeRow.setVisible( false );
            usernameRow.setVisible( false );
            passwordRow.setVisible( false );
            serverURLRow.setVisible( false );
        }
    }

    @UiHandler("releaseButton")
    public void releaseButton( ClickEvent e ) {

        presenter.releaseProject( chooseRepositoryBox.getValue(), chooseBranchBox.getValue(),
                userNameText.getText(), passwordText.getText(), serverURLText.getText(), deployToRuntimeCheck.getValue(), versionText.getText() );
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    public ListBox getChooseBranchBox() {
        return chooseBranchBox;
    }

    @Override
    public ListBox getChooseRepositoryBox() {
        return chooseRepositoryBox;
    }

    @Override
    public TextBox getCurrentVersionText() {
        return currentVersionText;
    }

    @Override
    public TextBox getVersionText() {
        return versionText;
    }

}
