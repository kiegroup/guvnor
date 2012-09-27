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

package org.drools.guvnor.client.moduleeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.explorer.AcceptItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ModuleEditorPlace;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.util.Activity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Image;

/**
 * An Activity to view a Module's assets
 */
public class AssetViewerActivity extends Activity
        implements
        AssetViewerActivityView.Presenter {

    private final ClientFactory     clientFactory;
    private Module       packageConfigData;
    private AssetViewerActivityView view;
    private String                  uuid;

    public AssetViewerActivity(String uuid,
                                ClientFactory clientFactory) {
        this.uuid = uuid;
        this.clientFactory = clientFactory;
        this.view = clientFactory.getNavigationViewFactory().getAssetViewerActivityView();
    }

    @Override
    public void start(final AcceptItem acceptTabItem,
                      EventBus eventBus) {

        view.showLoadingPackageInformationMessage();

        // title is not used.
        acceptTabItem.add( null,
                           view );
        clientFactory.getModuleService().loadModule( uuid,
                                                             new GenericCallback<Module>() {
                                                                 public void onSuccess(Module conf) {
                                                                     packageConfigData = conf;
                                                                     RulePackageSelector.currentlySelectedPackage = packageConfigData.getName();

                                                                     fillModuleItemStructure();

                                                                     view.closeLoadingPackageInformationMessage();
                                                                 }
                                                             } );
    }

    public void viewPackageDetail(Module packageConfigData) {
        clientFactory.getPlaceManager().goTo( new ModuleEditorPlace( packageConfigData.getUuid() ) );
    }

    private void fillModuleItemStructure() {
        //If two or more asset editors (that are associated with different formats) have same titles,
        //we group them together and display them as one node on the package tree.
        String[] registeredFormats = clientFactory.getPerspectiveFactory().getRegisteredAssetEditorFormats( packageConfigData.getFormat() );

        //Use list to preserve the order of asset editors defined in configuration.
        List<FormatList> formatListGroupedByTitles = new ArrayList<FormatList>();
        for ( String format : registeredFormats ) {
            boolean found = false;
            for ( FormatList formatListWithSameTitle : formatListGroupedByTitles ) {
                found = formatListWithSameTitle.titleAlreadyExists( format );
                if ( found ) {
                    formatListWithSameTitle.add( format );
                    break;
                }
            }
            if ( !found ) {
                FormatList formatListWithSameTile = new FormatList();
                formatListWithSameTile.add( format );
                formatListGroupedByTitles.add( formatListWithSameTile );
            }
        }

        addTitleItems( formatListGroupedByTitles );
    }

    private void addTitleItems(List<FormatList> formatListGroupedByTitles) {

        //Record the number of groups expected to have assets, if all are found to be empty we show a warning
        final AssetGroupSemaphore s = new AssetGroupSemaphore( formatListGroupedByTitles.size() );
        final AssetViewerSection[] sections = new AssetViewerSection[formatListGroupedByTitles.size()];

        for ( int i = 0; i < formatListGroupedByTitles.size(); i++ ) {

            final FormatList formatList = formatListGroupedByTitles.get( i );
            final int sectionIndex = i;

            //Only add a section to the view if the Format Group contains Format Types
            if ( formatList.size() == 0 ) {
                continue;
            }

            final List<String> formatsInList = getFormatsInList( formatList );
            final Boolean formatIsRegistered = getFormatIsRegistered( formatList );

            //Check if there are any assets for the group
            AssetPageRequest request = new AssetPageRequest( packageConfigData.getUuid(),
                                                             formatsInList,
                                                             formatIsRegistered );
            clientFactory.getAssetService().getAssetCount( request,
                                                           new GenericCallback<Long>() {
                                                               public void onSuccess(Long count) {

                                                                   s.recordProcessedGroup();

                                                                   if ( count > 0 ) {

                                                                       //If the group contains assets add a section
                                                                       String title = getGroupTitle( formatList.getFirst() );
                                                                       Image icon = getGroupIcon( formatList.getFirst() );
                                                                       AssetViewerSection section = new AssetViewerSection( title,
                                                                                                                            icon,
                                                                                                                            formatsInList,
                                                                                                                            formatIsRegistered );
                                                                       sections[sectionIndex] = section;
                                                                   } else {

                                                                       //Otherwise record empty group and show warning, if applicable
                                                                       s.recordEmptyGroup();
                                                                       if ( s.areAllGroupsEmpty() ) {
                                                                           view.showHasNoAssetsWarning( true );
                                                                       }

                                                                   }

                                                                   //If all groups have been processed add sections to UI
                                                                   if ( s.areAllGroupsProcessed() ) {
                                                                       for (AssetViewerSection section : sections) {
                                                                           if (section != null) {
                                                                               view.addAssetFormat(section.formatsInList,
                                                                                       section.formatIsRegistered,
                                                                                       section.title,
                                                                                       section.icon,
                                                                                       packageConfigData,
                                                                                       clientFactory);
                                                                           }
                                                                       }
                                                                   }
                                                               }
                                                           } );
        }
    }

    //A container for the number of Asset Groups to be shown for this Activity. The number of
    //Assets in each Group is determined with asynchronous GWT-RPC calls. Since we cannot
    //control when the responses are received we keep a running total of the number of
    //Groups containing Assets and those processed.
    private static class AssetGroupSemaphore {

        int numberOfGroups          = 0;
        int numberOfGroupsProcessed = 0;

        AssetGroupSemaphore(int numberOfGroups) {
            this.numberOfGroups = numberOfGroups;
            this.numberOfGroupsProcessed = numberOfGroups;
        }

        synchronized void recordEmptyGroup() {
            numberOfGroups--;
        }

        synchronized void recordProcessedGroup() {
            numberOfGroupsProcessed--;
        }

        synchronized boolean areAllGroupsEmpty() {
            return this.numberOfGroups == 0;
        }

        synchronized boolean areAllGroupsProcessed() {
            return this.numberOfGroupsProcessed == 0;
        }

    }

    //A container for a table that needs to be added to the Asset Viewer
    private static class AssetViewerSection {

        AssetViewerSection(String title,
                           Image icon,
                           List<String> formatsInList,
                           Boolean formatIsRegistered) {
            this.title = title;
            this.icon = icon;
            this.formatsInList = formatsInList;
            this.formatIsRegistered = formatIsRegistered;
        }

        String        title;

        Image icon;

        List<String>  formatsInList;

        Boolean       formatIsRegistered;

    }

    private List<String> getFormatsInList(FormatList formatList) {
        List<String> formatsInList = null;
        if ( formatList.getFormats() != null && formatList.getFormats().length > 0 ) {
            formatsInList = Arrays.asList( formatList.getFormats() );
        }
        return formatsInList;
    }

    private Boolean getFormatIsRegistered(FormatList formatList) {
        Boolean formatIsRegistered = null;
        if ( formatList.getFormats() == null || formatList.getFormats().length == 0 ) {
            formatIsRegistered = false;
        }
        return formatIsRegistered;
    }

    private String getGroupTitle(String format) {
        return clientFactory.getAssetEditorFactory().getAssetEditorTitle( format );
    }

    private Image getGroupIcon(String format) {
        return clientFactory.getAssetEditorFactory().getAssetEditorIcon( format );
    }

    class FormatList {

        private List<String> formatList = new ArrayList<String>();

        private boolean titleAlreadyExists(String format) {
            for ( String addedFormat : formatList ) {
                //If two formats has same tile, group them together
                if ( hasSameTitle( format,
                                   addedFormat ) ) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasSameTitle(String format,
                                     String addedFormat) {
            return getGroupTitle( addedFormat ).equals( getGroupTitle( format ) );
        }

        String[] getFormats() {
            if ( formatList.size() == 1 && "".equals( formatList.get( 0 ) ) ) {
                return new String[0];
            } else {
                return formatList.toArray( new String[formatList.size()] );
            }
        }

        public void add(String format) {
            formatList.add( format );
        }

        public int size() {
            return formatList.size();
        }

        public String[] toArray(String[] strings) {
            return formatList.toArray( strings );
        }

        public String getFirst() {
            return formatList.get( 0 );
        }
    }

}
