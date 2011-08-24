/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.common;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.AssetServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Well this one should be pretty obvious what it does.
 * I feel like I have wasted valuable time writing this comment, but I hope
 * you enjoyed reading it.
 */
public class StatusChangePopup extends FormStylePopup {

    private static Images images    = (Images) GWT.create( Images.class );
    private Constants     constants = ((Constants) GWT.create( Constants.class ));

    private boolean       isPackage;
    private String        uuid;
    private String        newStatus;
    private Command       changedStatus;

    public StatusChangePopup(String uuid,
                             boolean isPackage) {

        this.uuid = uuid;
        this.isPackage = isPackage;

        super.addRow( new HTML( AbstractImagePrototype.create(images.statusSmall()).getHTML() + "<b>" + constants.ChangeStatus() + "</b>" ) );

        HorizontalPanel horiz = new HorizontalPanel();
        final ListBox box = new ListBox();

        LoadingPopup.showMessage( constants.PleaseWaitDotDotDot() );
        RepositoryServiceFactory.getService().listStates( new GenericCallback<String[]>() {
            public void onSuccess(String[] list) {
                box.addItem( constants.ChooseOne() );
                for ( int i = 0; i < list.length; i++ ) {
                    box.addItem( list[i] );
                }
                LoadingPopup.close();
            }
        } );
        box.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                newStatus = box.getItemText( box.getSelectedIndex() );
            }
        } );

        horiz.add( box );
        Button ok = new Button( constants.ChangeStatus() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                String newState = box.getItemText( box.getSelectedIndex() );
                changeState( newState );
                hide();
            }
        } );
        horiz.add( ok );

        Button close = new Button( constants.Cancel() );
        close.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        } );
        horiz.add( close );

        addRow( horiz );

    }

    /** Apply the state change */
    private void changeState(String newState) {
        LoadingPopup.showMessage( constants.UpdatingStatus() );
        AssetServiceAsync assetService = RepositoryServiceFactory.getAssetService();
        if ( isPackage ) {
            assetService.changePackageState( uuid,
                                             newStatus,
                                             createGenericCallbackForChaneState() );
        } else {
            assetService.changeState( uuid,
                                      newStatus,
                                      createGenericCallbackForChaneState() );
        }
    }

    private GenericCallback<Void> createGenericCallbackForChaneState() {
        return new GenericCallback<java.lang.Void>() {
            public void onSuccess(Void v) {
                changedStatus.execute();
                LoadingPopup.close();
            }
        };
    }

    /**
     * Get what the state was changed to.
     */
    public String getState() {
        return this.newStatus;
    }

    /**
     * set the status change event
     */
    public void setChangeStatusEvent(Command command) {
        this.changedStatus = command;
    }
}
