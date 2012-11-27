/*
 * Copyright 2012 JBoss Inc
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

package org.kie.guvnor.editors.guided.client.editor.templates;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.kie.guvnor.datamodel.api.DataModel;
import org.kie.guvnor.editors.guided.client.editor.RuleModelEditor;
import org.kie.guvnor.editors.guided.client.editor.RuleModeller;
import org.kie.guvnor.editors.guided.client.resources.i18n.Constants;
import org.kie.guvnor.editors.guided.model.templates.TemplateModel;
import org.kie.guvnor.editors.guided.service.GuidedEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.FormStylePopup;

/**
 * Guided Editor for rules using templates
 */
public class RuleTemplateEditor
        extends Composite
        implements RuleModelEditor {

    private TemplateModel model;
    private RuleModeller ruleModeller;

    private TemplateDataTableWidget table;

    //This EventBus is local to the screen and should be used for local operations, set data, add rows etc
    private EventBus eventBus = new SimpleEventBus();

    private boolean readOnly;

    public RuleTemplateEditor( final Path path ) {

        MessageBuilder.createCall( new RemoteCallback<TemplateModel>() {
            public void callback( final TemplateModel response ) {

                RuleTemplateEditor.this.model = response;

                RuleTemplateEditor.this.ruleModeller = new RuleModeller( path,
                                                                         model,
                                                                         eventBus,
                                                                         new TemplateModellerWidgetFactory() );

                final VerticalPanel tPanel = new VerticalPanel();
                tPanel.setWidth( "100%" );

                tPanel.add( new Button( Constants.INSTANCE.LoadTemplateData(),
                                        new ClickHandler() {

                                            public void onClick( ClickEvent event ) {
                                                int height = (int) ( Window.getClientHeight() * 0.7 );
                                                int width = (int) ( Window.getClientWidth() * 0.7 );

                                                final FormStylePopup popUp = new FormStylePopup( (Image) null,
                                                                                                 Constants.INSTANCE.TemplateData(),
                                                                                                 width );

                                                //Initialise table to edit data
                                                table = new TemplateDataTableWidget( model,
                                                                                     getDataModel(),
                                                                                     isReadOnly(),
                                                                                     eventBus );
                                                table.setPixelSize( width,
                                                                    height );
                                                popUp.addAttribute( "",
                                                                    table );

                                                Button btnOK = new Button( Constants.INSTANCE.OK(),
                                                                           new ClickHandler() {
                                                                               public void onClick( ClickEvent event ) {
                                                                                   popUp.hide();
                                                                               }
                                                                           } );

                                                Button btnAddRow = new Button( Constants.INSTANCE.AddRow(),
                                                                               new ClickHandler() {

                                                                                   public void onClick( ClickEvent event ) {
                                                                                       table.appendRow();
                                                                                   }

                                                                               } );

                                                HorizontalPanel pnlClose = new HorizontalPanel();
                                                pnlClose.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_RIGHT );
                                                pnlClose.add( btnOK );
                                                pnlClose.add( btnAddRow );
                                                popUp.addAttribute( "",
                                                                    pnlClose );

                                                popUp.show();
                                            }
                                        } ) );
                tPanel.add( ruleModeller );
                initWidget( tPanel );

            }
        }, GuidedEditorService.class ).loadTemplateModel( path );
    }

    public RuleModeller getRuleModeller() {
        return ruleModeller;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public DataModel getDataModel() {
        return null;
    }
}
