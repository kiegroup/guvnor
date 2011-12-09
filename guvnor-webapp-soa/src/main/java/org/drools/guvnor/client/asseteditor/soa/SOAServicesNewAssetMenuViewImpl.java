/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.asseteditor.soa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import org.drools.guvnor.client.asseteditor.soa.NewAssetWizard;
import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.soa.NewSOAServiceWizard;
import org.drools.guvnor.client.perspective.PerspectiveFactory;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.util.Util;

public class SOAServicesNewAssetMenuViewImpl implements SOAServicesNewAssetMenuView {

    private static Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );

    private MenuBar createNewMenu = new MenuBar( true );
    private Presenter presenter;

    private MenuBar getMenu() {
        addNewServiceMenuItem();

        //We assume for every asset type, we always have a corresponding "New Asset" menu item for this type
        PerspectiveFactory perspectiveFactory = GWT.create(PerspectiveFactory.class);
        String[] formats = perspectiveFactory.getRegisteredAssetEditorFormats("soaservice");
        for(final String format:formats) {
            addNewAssetMenuItem(format);         
        }

        MenuBar rootMenuBar = new MenuBar( true );
        rootMenuBar.setAutoOpen( true );
        rootMenuBar.setAnimationEnabled( true );

        rootMenuBar.addItem( new MenuItem( constants.CreateNew(), createNewMenu ) );

        return rootMenuBar;
    }

    private void addNewServiceMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.newPackage(), "New Service" ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewService();
                    }
                } );
    }

    private void addNewAssetMenuItem(final String format) {
        AssetEditorFactory assetEditorFactory = GWT.create( AssetEditorFactory.class );
        String title = "New " + assetEditorFactory.getAssetEditorTitle(format);
        createNewMenu.addItem( Util.getHeader( assetEditorFactory.getAssetEditorIcon(format), title ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewAsset(format);
                    }
                } );   
    }
    
    public Widget asWidget() {
        return getMenu();
    }

    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }
    public void openNewServiceWizard( ClientFactory clientFactory, EventBus eventBus ) {
        NewSOAServiceWizard wiz = new NewSOAServiceWizard( clientFactory, eventBus );
        wiz.show();
    }

    public void openNewAssetWizardWithoutCategories( String format, ClientFactory clientFactory, EventBus eventBus  ) {
        openWizard( format, false, clientFactory, eventBus);
    }

    public void openNewAssetWizardWithCategories( String format, ClientFactory clientFactory, EventBus eventBus  ) {
        openWizard( format, true, clientFactory, eventBus);
    }

    private void openWizard( String format, boolean showCategories, ClientFactory clientFactory, EventBus eventBus  ) {
        NewAssetWizard pop = new NewAssetWizard( showCategories, format, clientFactory, eventBus);

        pop.show();
    }

    public void showLoadingPopUpRebuildingPackageBinaries() {
        LoadingPopup.showMessage( constants.RebuildingPackageBinaries() );
    }

    public void closeLoadingPopUp() {
        LoadingPopup.close();
    }

}
