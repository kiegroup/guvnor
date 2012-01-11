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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.templates;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModelEditor;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel;

/**
 * Guided Editor for rules using templates
 */
public class RuleTemplateEditor extends DirtyableComposite
        implements
        RuleModelEditor {

    private TemplateModel              model;
    private RuleModeller               ruleModeller;
    private Constants                  constants = GWT.create( Constants.class );
    private SuggestionCompletionEngine sce;

    private TemplateDataTableWidget    table;

    //This EventBus is local to the screen and should be used for local operations, set data, add rows etc
    private EventBus                   eventBus  = new SimpleEventBus();

    //This EventBus is global to Guvnor and should be used for global operations, navigate pages etc 
    @SuppressWarnings("unused")
    private EventBus                   globalEventBus;

    public RuleTemplateEditor(Asset asset,
                              RuleViewer viewer,
                              ClientFactory clientFactory,
                              EventBus globalEventBus) {

        this.globalEventBus = globalEventBus;
        model = (TemplateModel) asset.getContent();
        ruleModeller = new RuleModeller( asset,
                                         null,
                                         clientFactory,
                                         eventBus,
                                         new TemplateModellerWidgetFactory() );

        String packageName = asset.getMetaData().getModuleName();
        sce = SuggestionCompletionCache.getInstance().getEngineFromCache( packageName );

        final VerticalPanel tPanel = new VerticalPanel();
        tPanel.setWidth( "100%" );

        tPanel.add( new Button( constants.LoadTemplateData(),
                                new ClickHandler() {

                                    public void onClick(ClickEvent event) {
                                        int height = (int) (Window.getClientHeight() * 0.7);
                                        int width = (int) (Window.getClientWidth() * 0.7);

                                        final FormStylePopup popUp = new FormStylePopup( null,
                                                                                         constants.TemplateData(),
                                                                                         width );

                                        //Initialise table to edit data
                                        table = new TemplateDataTableWidget( sce,
                                                                             eventBus );
                                        table.setPixelSize( width,
                                                            height );
                                        table.setModel( model );
                                        popUp.addAttribute( "",
                                                            table );

                                        Button btnSaveAndClose = new Button( constants.SaveAndClose(),
                                                                             new ClickHandler() {
                                                                                 public void onClick(ClickEvent event) {
                                                                                     popUp.hide();
                                                                                 }
                                                                             } );

                                        Button btnAddRow = new Button( constants.AddRow(),
                                                                       new ClickHandler() {

                                                                           public void onClick(ClickEvent event) {
                                                                               table.appendRow();
                                                                           }

                                                                       } );

                                        HorizontalPanel pnlClose = new HorizontalPanel();
                                        pnlClose.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_RIGHT );
                                        pnlClose.add( btnSaveAndClose );
                                        pnlClose.add( btnAddRow );
                                        popUp.addAttribute( "",
                                                            pnlClose );

                                        popUp.show();
                                    }
                                } ) );
        tPanel.add( ruleModeller );
        initWidget( tPanel );
    }

    public RuleModeller getRuleModeller() {
        return ruleModeller;
    }
}
