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

package org.drools.guvnor.client.modeldriven.ui;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.TemplateModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RuleTemplateEditor extends DirtyableComposite
    implements
    RuleModelEditor {

    private TemplateModel              model;
    private RuleModeller               ruleModeller;
    private Constants                  constants = GWT.create( Constants.class );

    private SuggestionCompletionEngine sce;
    private TemplateDataTableWidget    table;

    public RuleTemplateEditor(RuleAsset asset) {
        model = (TemplateModel) asset.content;

        final VerticalPanel tPanel = new VerticalPanel();
        tPanel.setWidth( "100%" );
        ruleModeller = new RuleModeller( asset,
                                         new TemplateModellerWidgetFactory() );

        tPanel.add( new Button( constants.LoadTemplateData(),
                                new ClickHandler() {

                                    public void onClick(ClickEvent event) {
                                        int height = (int) (Window.getClientHeight() * 0.7);
                                        int width = (int) (Window.getClientWidth() * 0.7);

                                        final FormStylePopup popUp = new FormStylePopup( null,
                                                                                         constants.TemplateData(),
                                                                                         width );
                                        popUp.setHeight( height + "px" );

                                        sce = ruleModeller.getSuggestionCompletions();
                                        table = new TemplateDataTableWidget( sce );
                                        table.setPixelSize( width,
                                                            height );
                                        table.setModel( model );
                                        popUp.addAttribute( "",
                                                            table );

                                        Button close = new Button( constants.Close(),
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
                                        pnlClose.add( close );
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
