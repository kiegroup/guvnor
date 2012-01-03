/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.asseteditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.drools.guvnor.client.asseteditor.ruleflow.RuleFlowViewer;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleFlowContentModel;
import org.drools.guvnor.client.util.DecoratedDisclosurePanel;

public class RuleFlowWrapper extends Composite
        implements
        SaveEventListener,
        EditorWidget {

    private Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );

    private RuleViewer viewer;
    private Asset asset;

    private RuleFlowViewer ruleFlowViewer;
    private DecoratedDisclosurePanel parameterPanel;
    private final ClientFactory clientFactory;
    private final EventBus eventBus;

    public RuleFlowWrapper( final Asset asset,
                            final RuleViewer viewer,
                            ClientFactory clientFactory,
                            EventBus eventBus) {
        this.viewer = viewer;
        this.asset = asset;
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        initWidgets();
    }

    protected void initWidgets() {

        RuleFlowUploadWidget uploadWidget = new RuleFlowUploadWidget(
                asset,
                viewer,
                clientFactory,
                eventBus );

        VerticalPanel panel = new VerticalPanel();
        panel.add( uploadWidget );

        if ( ApplicationPreferences.showVisualRuleFlow() ) {
            initRuleflowViewer();

            if ( ruleFlowViewer != null && parameterPanel != null ) {
                Button viewSource = new Button();
                viewSource.setText( constants.OpenEditorInNewWindow() );
                viewSource.addClickHandler( new ClickHandler() {
                    public void onClick( ClickEvent arg0 ) {
                        doViewDiagram();

                        ruleFlowViewer.update();
                    }
                } );

                panel.add( viewSource );
            }
        }

        initWidget( panel );

        this.setStyleName( getOverallStyleName() );
    }

    private void doViewDiagram() {
        LoadingPopup.showMessage( constants.CalculatingSource() );

        try {
            FormStylePopup pop = new FormStylePopup( images.viewSource(),
                    constants.ViewingDiagram(),
                    new Integer( 800 ) );

            pop.addRow( new ScrollPanel( ruleFlowViewer ) );
            pop.addRow( parameterPanel );

            pop.show();
        } catch (Exception e) {
            ErrorPopup.showMessage( constants.CouldNotCreateTheRuleflowDiagramItIsPossibleThatTheRuleflowFileIsInvalid() );
        }

        LoadingPopup.close();
    }

    private void initRuleflowViewer() {
        RuleFlowContentModel rfcm = (RuleFlowContentModel) asset.getContent();

        if ( rfcm != null && rfcm.getXml() != null && rfcm.getNodes() != null ) {
            try {
                parameterPanel = new DecoratedDisclosurePanel( constants.Parameters() );
                parameterPanel.ensureDebugId( "cwDisclosurePanel" );
                parameterPanel.setWidth( "100%" );
                parameterPanel.setOpen( false );

                FormStyleLayout parametersForm = new FormStyleLayout();
                parametersForm.setHeight( "120px" );
                parameterPanel.setContent( parametersForm );

                ruleFlowViewer = new RuleFlowViewer( rfcm,
                        parametersForm );
            } catch (Exception e) {
                Window.alert( e.toString() );
            }
        } else if ( rfcm != null && rfcm.getXml() == null ) {

            // If the XML is not set there was some problem when the diagram was
            // created.
            Window.alert( constants.CouldNotCreateTheRuleflowDiagramItIsPossibleThatTheRuleflowFileIsInvalid() );

        }
    }

    public ImageResource getIcon() {
        return images.ruleflowLarge();
    }

    public String getOverallStyleName() {
        return "decision-Table-upload"; // NON-NLS
    }

    public void onAfterSave() {

    }

    public void onSave() {

        RuleFlowContentModel rfcm = (RuleFlowContentModel) asset.getContent();

        rfcm.setNodes( ruleFlowViewer.getTransferNodes() );

    }

    public RuleFlowViewer getRuleFlowViewer() {
        return ruleFlowViewer;
    }
}
