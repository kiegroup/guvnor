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



import java.util.Iterator;

import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A rule package selector widget.
 * @author michael neale
 */
public class RulePackageSelector extends Composite {

    private ListBox packageList;
    private String currentlySelectedPackage;

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
		System.err.println("-->Loading packages");
		RepositoryServiceFactory.getService().listPackages( new GenericCallback() {

            public void onSuccess(Object o) {
                PackageConfigData[] list = (PackageConfigData[]) o;

                for ( int i = 0; i < list.length; i++ ) {
                    packageList.addItem( list[i].name );
                    if (currentlySelectedPackage != null &&
                            list[i].name.equals( currentlySelectedPackage )) {
                        packageList.setSelectedIndex( i );
                    }
                }

            }

        });
	}

    /**
     * Returns the selected package.
     */
    public String getSelectedPackage() {
        return packageList.getItemText( packageList.getSelectedIndex() );
    }

    public void selectPackage(String currentlySelectedPackage) {
        this.currentlySelectedPackage = currentlySelectedPackage;
    }

}