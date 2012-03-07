/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.explorer.helloworld;

import org.drools.guvnor.client.explorer.AssetEditorActivity;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.FindActivity;
import org.drools.guvnor.client.explorer.FindPlace;
import org.drools.guvnor.client.explorer.GuvnorActivityMapper;
import org.drools.guvnor.client.explorer.ModuleEditorActivity;
import org.drools.guvnor.client.explorer.ModuleEditorPlace;
import org.drools.guvnor.client.explorer.MultiAssetActivity;
import org.drools.guvnor.client.explorer.MultiAssetPlace;
import org.drools.guvnor.client.explorer.navigation.admin.ManagerActivity;
import org.drools.guvnor.client.explorer.navigation.admin.ManagerPlace;
import org.drools.guvnor.client.explorer.navigation.browse.CategoryActivity;
import org.drools.guvnor.client.explorer.navigation.browse.CategoryPlace;
import org.drools.guvnor.client.explorer.navigation.browse.InboxActivity;
import org.drools.guvnor.client.explorer.navigation.browse.InboxPlace;
import org.drools.guvnor.client.explorer.navigation.browse.StateActivity;
import org.drools.guvnor.client.explorer.navigation.browse.StatePlace;
import org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewActivity;
import org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewPlace;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesActivity;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesPlace;
import org.drools.guvnor.client.explorer.navigation.settings.PreferencesActivity;
import org.drools.guvnor.client.explorer.navigation.settings.PreferencesPlace;
import org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksActivity;
import org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksPlace;
import org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksActivity;
import org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksPlace;
import org.drools.guvnor.client.moduleeditor.AssetViewerActivity;
import org.drools.guvnor.client.moduleeditor.AssetViewerPlace;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.widgets.wizards.WizardActivity;
import org.drools.guvnor.client.widgets.wizards.WizardPlace;

import com.google.gwt.place.shared.Place;

public class GuvnorHelloWorldActivityMapper extends GuvnorActivityMapper {

    public GuvnorHelloWorldActivityMapper(ClientFactory clientFactory) {
        super(clientFactory);
    }

    public Activity getActivity(Place place) {
        if ( place instanceof FindPlace ) {
            return new FindActivity( clientFactory );
        } else if ( place instanceof AssetEditorPlace ) {
            return new AssetEditorActivity( (AssetEditorPlace) place, clientFactory );
        } else if ( place instanceof ModuleEditorPlace ) {
            return new ModuleEditorActivity( ((ModuleEditorPlace) place).getUuid(),
                    clientFactory );
        } else if ( place instanceof AssetViewerPlace ) {
            return new AssetViewerActivity( ((AssetViewerPlace) place).getUuid(),
                    clientFactory );
        } else if ( place instanceof org.drools.guvnor.client.explorer.navigation.ModuleFormatsGridPlace ) {
            return new org.drools.guvnor.client.explorer.ModuleFormatsGridPlace(
                    (org.drools.guvnor.client.explorer.navigation.ModuleFormatsGridPlace) place,
                    clientFactory );
        } else if ( place instanceof ManagerPlace ) {
            return new ManagerActivity(
                    ((ManagerPlace) place).getId(),
                    clientFactory );
        } else if ( place instanceof CategoryPlace ) {
            return new CategoryActivity(
                    ((CategoryPlace) place).getCategoryPath(),
                    clientFactory );
        } else if ( place instanceof StatePlace ) {
            return new StateActivity(
                    ((StatePlace) place).getStateName(),
                    clientFactory );
        } else if ( place instanceof InboxPlace ) {
            return new InboxActivity(
                    (InboxPlace) place,
                    clientFactory );
        } else if ( place instanceof MultiAssetPlace ) {
            return new MultiAssetActivity(
                    (MultiAssetPlace) place,
                    clientFactory );
        } else if ( place instanceof WizardPlace ) {
            return new WizardActivity(
                    (WizardPlace<?>) place,
                    clientFactory );
        } else if (place instanceof PersonalTasksPlace) {
            return new PersonalTasksActivity();
        } else if (place instanceof GroupTasksPlace) {
            return new GroupTasksActivity();
        } else if (place instanceof ReportTemplatesPlace) {
            return new ReportTemplatesActivity();
        } else if (place instanceof PreferencesPlace) {
            return new PreferencesActivity();
        } else if (place instanceof ProcessOverviewPlace) {
            return new ProcessOverviewActivity();
        } else {
            return null;
        }
    }
}
