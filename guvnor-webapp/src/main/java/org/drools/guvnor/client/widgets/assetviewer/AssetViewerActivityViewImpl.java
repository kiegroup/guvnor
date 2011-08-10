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

import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PushClient;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.ServerPushNotification;
import org.drools.guvnor.client.util.LazyStackPanel;
import org.drools.guvnor.client.util.LoadContentCommand;
import org.drools.guvnor.client.util.Util;
import org.drools.guvnor.client.widgets.tables.AssetPagedTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A View displaying a package's assets
 */
public class AssetViewerActivityViewImpl extends Composite
        implements
        AssetViewerActivityView {

    private static final Images    images    = GWT.create( Images.class );
    private static final Constants constants = GWT.create( Constants.class );

    interface AssetViewerActivityViewImplBinder
            extends
            UiBinder<Widget, AssetViewerActivityViewImpl> {
    }

    private static AssetViewerActivityViewImplBinder uiBinder   = GWT.create( AssetViewerActivityViewImplBinder.class );

    private PackageConfigData                        packageConfigData;

    private Presenter                                presenter;

    private Label                                    caption;

    @UiField(provided = true)
    PrettyFormLayout                                 prettyForm = new PrettyFormLayout();

    @UiField
    VerticalPanel                                    assetGroupsContainer;

    public AssetViewerActivityViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );

        //PrettyFormLayout cannot be setup with UiBinder
        caption = new Label();
        caption.getElement().getStyle().setFontWeight( FontWeight.BOLD );
        prettyForm.addHeader( images.packageLarge(),
                              caption );

        Button button = new Button( constants.ViewPackageConfiguration() );
        button.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                presenter.viewPackageDetail( packageConfigData );
            }

        } );

        prettyForm.startSection();
        prettyForm.addRow( button );
        prettyForm.endSection();
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void showLoadingPackageInformationMessage() {
        LoadingPopup.showMessage( constants.LoadingPackageInformation() );
    }

    public void closeLoadingPackageInformationMessage() {
        LoadingPopup.close();
    }

    public void setPackageConfigData(PackageConfigData packageConfigData) {
        this.packageConfigData = packageConfigData;
        caption.setText( constants.PackageAssets( packageConfigData.getName() ) );
    }

    public String getFeedUrl(String packageName) {
        return GWT.getModuleBaseURL()
                + "feed/package?name="
                + packageName
                + "&viewUrl="
                + Util.getSelfURL()
                + "&status=*";
    }

    public void addAssetFormat(final ImageResource icon,
                                final String title,
                                final String packageName,
                                final AssetPagedTable table) {

        LazyStackPanel lsp = new LazyStackPanel();
        lsp.add( title,
                 icon,
                 new LoadContentCommand() {

                     public Widget load() {
                         return setUpTable( packageName,
                                            table );
                     }

                 } );
        assetGroupsContainer.add( lsp );
    }

    private Widget setUpTable(final String packageName,
                               final AssetPagedTable table) {

        final ServerPushNotification sub = new ServerPushNotification() {
            public void messageReceived(PushResponse response) {
                if ( response.messageType.equals( "packageChange" )
                        && response.message.equals( packageName ) ) {
                    table.refresh();
                }
            }
        };
        PushClient.instance().subscribe( sub );
        table.addUnloadListener( new Command() {
            public void execute() {
                PushClient.instance().unsubscribe( sub );
            }
        } );
        return table;
    }

}
