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
package org.guvnor.asset.management.client.editors.conf;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class RepositoryConfigurationViewImpl extends Composite implements RepositoryConfigurationPresenter.RepositoryConfigurationView {

    interface Binder
            extends UiBinder<Widget, RepositoryConfigurationViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @Inject
    private PlaceManager placeManager;

    private RepositoryConfigurationPresenter presenter;

    @UiField
    public ListBox chooseRepositoryBox;

    @UiField
    public Button configureButton;

    @UiField
    public TextBox sourceBranchText;

    @UiField
    public TextBox releaseBranchText;

    @UiField
    public TextBox devBranchText;

    @UiField
    public TextBox versionText;

    @UiField
    public TextBox currentVersionText;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );

    public RepositoryConfigurationViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final RepositoryConfigurationPresenter presenter ) {
        this.presenter = presenter;

        configureButton.setText( constants.Configure_Repository() );
        sourceBranchText.setText( "master" );
        devBranchText.setText( "dev" );
        releaseBranchText.setText( "release" );
        currentVersionText.setReadOnly( true );
        chooseRepositoryBox.addChangeHandler( new ChangeHandler() {

            @Override
            public void onChange( ChangeEvent event ) {
                String value = chooseRepositoryBox.getValue();
                GWT.log( value );

                presenter.loadRepositoryStructure( value );
            }
        } );
        presenter.loadRepositories();
    }

    @UiHandler("configureButton")
    public void configureButton( ClickEvent e ) {

        presenter.configureRepository( chooseRepositoryBox.getValue(), sourceBranchText.getText(), devBranchText.getText(), releaseBranchText.getText(), versionText.getText() );

    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public Button getConfigureButton() {
        return configureButton;
    }

    @Override
    public TextBox getReleaseBranchText() {
        return releaseBranchText;
    }

    @Override
    public TextBox getDevBranchText() {
        return devBranchText;
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
