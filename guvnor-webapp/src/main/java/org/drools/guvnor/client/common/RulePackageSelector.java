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

import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A rule package selector widget.
 */
public class RulePackageSelector extends Composite {

    /** Used to remember what the "current package" we are working in is...
     * Should be the one the user has most recently dealt with... */
    public static String currentlySelectedPackage;

    private ListBox      packageList;
    private boolean      loadGlobalArea = false;

    public RulePackageSelector() {
        this( false );
    }

    public RulePackageSelector(boolean loadGlobalArea) {
        this.loadGlobalArea = loadGlobalArea;

        packageList = new ListBox();

        Scheduler scheduler = Scheduler.get();

        scheduler.scheduleDeferred( new ScheduledCommand() {
            public void execute() {
                loadPackageList();
            }
        } );

        initWidget( packageList );
    }

    private void loadPackageList() {
        RepositoryServiceFactory.getPackageService().listPackages( new GenericCallback<PackageConfigData[]>() {

            public void onSuccess(PackageConfigData[] list) {
                for ( int i = 0; i < list.length; i++ ) {
                    packageList.addItem( list[i].getName(),
                                         list[i].getUuid() );
                    if ( currentlySelectedPackage != null && list[i].getName().equals( currentlySelectedPackage ) ) {
                        packageList.setSelectedIndex( i );
                    }
                }

                if ( loadGlobalArea ) {
                    packageList.addItem( "globalArea",
                                         "nouuidavailable" );
                }

                packageList.addChangeHandler( new ChangeHandler() {
                    public void onChange(ChangeEvent event) {
                        currentlySelectedPackage = getSelectedPackage();
                    }
                } );
            }
        } );
    }

    /**
     * Returns the selected package.
     */
    public String getSelectedPackage() {
        int index = packageList.getSelectedIndex();
        String value = packageList.getItemText( index );
        return value;
    }
    
    public String getSelectedPackageUUID() {
        int index = packageList.getSelectedIndex();
        String UUID = packageList.getValue(index);
        return UUID;
    }

}
