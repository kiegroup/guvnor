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



import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * A rule package selector widget.
 * @author michael neale
 */
public class RulePackageSelector extends Composite {

    /** Used to remember what the "current package" we are working in is...
     * Should be the one the user has most recently dealt with... */
    public static String currentlySelectedPackage;

    private ListBox packageList;


    public RulePackageSelector() {
        packageList = new ListBox();

        DeferredCommand.addCommand(new Command() {
			public void execute() {
		        loadPackageList();
			}
        });

        initWidget( packageList );
    }

	private void loadPackageList() {
		RepositoryServiceFactory.getService().listPackages( new GenericCallback<PackageConfigData[]>() {

            public void onSuccess(PackageConfigData[] list) {
                for ( int i = 0; i < list.length; i++ ) {
                    packageList.addItem( list[i].name, list[i].uuid );
                    if (currentlySelectedPackage != null &&
                            list[i].name.equals( currentlySelectedPackage )) {
                        packageList.setSelectedIndex( i );
                    }
                }
                packageList.addChangeListener(new ChangeListener() {
                    public void onChange(Widget sender) {
                         currentlySelectedPackage = getSelectedPackage();                       
                    }
                });
            }
        });
	}

    /**
     * Returns the selected package.
     */
    public String getSelectedPackage() {
        return packageList.getItemText( packageList.getSelectedIndex() );
    }

    /**
     * Returns the selected package.
     */
    public String getSelectedPackageUUID() {
        return packageList.getValue( packageList.getSelectedIndex() );
    }
}