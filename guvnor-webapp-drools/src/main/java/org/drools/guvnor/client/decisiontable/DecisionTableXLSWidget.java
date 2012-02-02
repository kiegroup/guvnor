/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.decisiontable;

import org.drools.guvnor.client.asseteditor.AssetAttachmentFileWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.ConversionResult;
import org.drools.guvnor.client.rpc.ConversionResult.ConversionMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget deals with XLS files in "classic" decision tables.
 */
public class DecisionTableXLSWidget extends AssetAttachmentFileWidget {

    public DecisionTableXLSWidget(Asset asset,
                                  RuleViewer viewer,
                                  ClientFactory clientFactory,
                                  EventBus eventBus) {
        super( asset,
               viewer,
               clientFactory,
               eventBus );

        //Set-up supplementary widgets
        if ( !asset.isReadonly() ) {
            //TODO {manstis} super.addSupplementaryWidget( makeConvertToGuidedDecisionTableWidget( asset ) );
        }
        super.addSupplementaryWidget( makeDescriptionWidget() );
    }

    private Widget makeConvertToGuidedDecisionTableWidget(Asset asset) {
        Button convertButton = new Button( constants.ConvertTo0( constants.DecisionTableWebGuidedEditor() ) );
        convertButton.setEnabled( asset.versionNumber > 0 );
        convertButton.addClickHandler( getConvertButtonClickHandler() );
        return convertButton;
    }

    private ClickHandler getConvertButtonClickHandler() {
        return new ClickHandler() {

            public void onClick(ClickEvent event) {
                clientFactory.getAssetService().convertAsset( asset.getUuid(),
                                                              AssetFormats.DECISION_TABLE_GUIDED,
                                                              new GenericCallback<ConversionResult>() {

                                                                  public void onSuccess(ConversionResult result) {
                                                                      if ( result.isConverted() ) {
                                                                          Window.alert( result.getNewAssetUUID() );
                                                                          openEditor( result.getNewAssetUUID() );
                                                                      } else {
                                                                          StringBuilder sb = new StringBuilder();
                                                                          for ( ConversionMessage message : result.getMessages() ) {
                                                                              sb.append( message.getMessage() ).append( "\n" );
                                                                          }
                                                                          Window.alert( sb.toString() );
                                                                      }
                                                                  }

                                                              } );
            }

        };
    }

    private void openEditor(String uuid) {
        clientFactory.getPlaceController().goTo( new AssetEditorPlace( uuid ) );
    }

    private Widget makeDescriptionWidget() {
        return new HTML( constants.DecisionTableWidgetDescription() );
    }

    public ImageResource getIcon() {
        return images.decisionTable();
    }

    public String getOverallStyleName() {
        return "decision-Table-upload"; //NON-NLS
    }

}
