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

package org.drools.guvnor.client.widgets.assetviewer;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.explorer.AcceptItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ModuleEditorPlace;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.widgets.tables.AssetPagedTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An Activity to view a Package's assets
 */
public class AssetViewerActivity extends Activity
        implements
        AssetViewerActivityView.Presenter {

    private final ClientFactory     clientFactory;
    private PackageConfigData       packageConfigData;
    private AssetViewerActivityView view;
    private String                  uuid;

    public AssetViewerActivity(String uuid,
                                ClientFactory clientFactory) {
        this.uuid = uuid;
        this.clientFactory = clientFactory;
        this.view = clientFactory.getAssetViewerActivityView();
        view.setPresenter( this );
    }

    @Override
    public void start(final AcceptItem acceptTabItem,
                       EventBus eventBus) {

        view.showLoadingPackageInformationMessage();
        // title is not used.
        acceptTabItem.add(null, view);
        clientFactory.getPackageService().loadPackageConfig( uuid,
                                                             new GenericCallback<PackageConfigData>() {
                                                                 public void onSuccess(PackageConfigData conf) {
                                                                     packageConfigData = conf;
                                                                     RulePackageSelector.currentlySelectedPackage = packageConfigData.getName();


                                                                     fillModuleItemStructure();

                                                                     view.setPackageConfigData( packageConfigData );
                                                                     view.closeLoadingPackageInformationMessage();
                                                                 }
                                                             } );
    }

    public void viewPackageDetail(PackageConfigData packageConfigData) {
        clientFactory.getPlaceController().goTo( new ModuleEditorPlace( packageConfigData.getUuid() ) );
    }

    private void fillModuleItemStructure() {
        //If two or more asset editors (that are associated with different formats) have same titles,
        //we group them together and display them as one node on the package tree.
        String[] registeredFormats = clientFactory.getAssetEditorFactory().getRegisteredAssetEditorFormats();

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

    private String getTitle(String format) {
        return clientFactory.getAssetEditorFactory().getAssetEditorTitle( format );
    }

    private void addTitleItems(List<FormatList> formatListGroupedByTitles) {
        for ( FormatList formatList : formatListGroupedByTitles ) {

            //Don't add a section for anything we don't have assets for
            if ( formatList.size() > 0 ) {
                view.addAssetFormat( clientFactory.getAssetEditorFactory().getAssetEditorIcon( formatList.getFirst() ),
                                     getTitle( formatList.getFirst() ),
                                     packageConfigData.getName(),
                                     makeTable( formatList ) );
            }
        }
    }

    private AssetPagedTable makeTable(FormatList formatList) {

        List<String> formatsInList = null;
        Boolean formatIsRegistered = null;
        if ( formatList.getFormats() != null && formatList.getFormats().length > 0 ) {
            formatsInList = Arrays.asList( formatList.getFormats() );
        } else {
            formatIsRegistered = false;
        }
        return new AssetPagedTable(
                                    packageConfigData.getUuid(),
                                    formatsInList,
                                    formatIsRegistered,
                                    view.getFeedUrl( packageConfigData.getName() ),
                                    clientFactory );
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
            return getTitle( addedFormat ).equals( getTitle( format ) );
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
