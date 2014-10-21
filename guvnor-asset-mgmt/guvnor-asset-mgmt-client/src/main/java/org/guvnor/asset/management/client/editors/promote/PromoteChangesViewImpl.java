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
package org.guvnor.asset.management.client.editors.promote;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class PromoteChangesViewImpl extends Composite implements PromoteChangesPresenter.PromoteChangesView {

    interface Binder
            extends UiBinder<Widget, PromoteChangesViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @Inject
    private PlaceManager placeManager;

    private PromoteChangesPresenter presenter;

    @UiField
    public ListBox chooseRepositoryBox;

    @UiField
    public ListBox chooseSourceBranchBox;

    @UiField
    public ListBox chooseTargetBranchBox;

    @UiField
    public Button promoteButton;

    @Inject
    private Event<NotificationEvent> notification;

    public PromoteChangesViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final PromoteChangesPresenter presenter ) {
        this.presenter = presenter;

        chooseRepositoryBox.addChangeHandler( new ChangeHandler() {

            @Override
            public void onChange( ChangeEvent event ) {
                String value = chooseRepositoryBox.getValue();
                GWT.log( value );

                presenter.loadBranches( value );
            }
        } );

        presenter.loadRepositories();
    }

    @UiHandler( "promoteButton" )
    public void promoteButton( ClickEvent e ) {
        presenter.promoteChanges( chooseRepositoryBox.getValue(), chooseSourceBranchBox.getValue(), chooseTargetBranchBox.getValue() );

    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public ListBox getChooseRepositoryBox() {
        return chooseRepositoryBox;
    }

    @Override
    public ListBox getChooseSourceBranchBox() {
        return chooseSourceBranchBox;
    }

    @Override
    public ListBox getChooseTargetBranchBox() {
        return chooseTargetBranchBox;
    }

}
