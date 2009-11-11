package org.drools.guvnor.client.common;
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



import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * A rule package selector widget.
 */
public class GlobalAreaAssetSelector extends Composite {

    /** Used to remember what the "current asset" we are working in is...
     * Should be the one the user has most recently dealt with... */
    public static String currentlySelectedAsset;

    private ListBox assetList;


    public GlobalAreaAssetSelector() {
        assetList = new ListBox();

        DeferredCommand.addCommand(new Command() {
			public void execute() {
		        loadAssetList();
			}
        });


        initWidget( assetList );
    }

	private void loadAssetList() {
		RepositoryServiceFactory.getService().listRulesInPackage("globalArea", new GenericCallback<String[]>() {

            public void onSuccess(String[] list) {
                for ( int i = 0; i < list.length; i++ ) {
                    assetList.addItem( list[i] );
                    if (currentlySelectedAsset != null &&
                            list[i].equals( currentlySelectedAsset )) {
                        assetList.setSelectedIndex( i );
                    }
                }
                assetList.addChangeListener(new ChangeListener() {
                    public void onChange(Widget sender) {
                         currentlySelectedAsset = getSelectedAsset();                       
                    }
                });

            }

        });
	}

    /**
     * Returns the selected package.
     */
    public String getSelectedAsset() {
        return assetList.getItemText( assetList.getSelectedIndex() );
    }


}