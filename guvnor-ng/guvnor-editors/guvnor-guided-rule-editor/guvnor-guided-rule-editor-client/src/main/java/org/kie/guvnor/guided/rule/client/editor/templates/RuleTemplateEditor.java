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

package org.kie.guvnor.guided.rule.client.editor.templates;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.guided.rule.client.editor.ModellerWidgetFactory;
import org.kie.guvnor.guided.rule.client.editor.RuleModelEditor;
import org.kie.guvnor.guided.rule.client.editor.RuleModeller;
import org.kie.guvnor.guided.rule.client.resources.GuidedRuleEditorResources;
import org.kie.guvnor.guided.rule.client.resources.i18n.Constants;
import org.kie.guvnor.guided.rule.model.templates.TemplateModel;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.FormStylePopup;

/**
 * Guided Editor for rules using templates
 */
public class RuleTemplateEditor
        extends Composite
        implements RuleModelEditor {

    private Path path;
    private TemplateModel model;
    private DataModelOracle dataModel;

    private RuleModeller ruleModeller;
    private TemplateDataTableWidget table;
    private ModellerWidgetFactory widgetFactory;

    private EventBus eventBus;

    private boolean readOnly = false;

    public RuleTemplateEditor( final Path path,
                               final TemplateModel model,
                               final DataModelOracle dataModel,
                               final ModellerWidgetFactory widgetFactory,
                               final EventBus eventBus ) {
        this.path = path;
        this.model = model;
        this.dataModel = dataModel;
        this.widgetFactory = widgetFactory;
        this.eventBus = eventBus;

        doLayout();
    }

    private void doLayout() {
        this.ruleModeller = new RuleModeller( path,
                                              model,
                                              dataModel,
                                              widgetFactory,
                                              eventBus );

        final VerticalPanel tPanel = new VerticalPanel();
        tPanel.setWidth( "100%" );

        tPanel.add( new Button( Constants.INSTANCE.LoadTemplateData(),
                                new ClickHandler() {

                                    public void onClick( ClickEvent event ) {
                                        int height = (int) ( Window.getClientHeight() * 0.7 );
                                        int width = (int) ( Window.getClientWidth() * 0.7 );

                                        final FormStylePopup popUp = new FormStylePopup( GuidedRuleEditorResources.INSTANCE.images().guidedRuleTemplateIcon(),
                                                                                         Constants.INSTANCE.TemplateData(),
                                                                                         width );

                                        //Initialise table to edit data
                                        table = new TemplateDataTableWidget( model,
                                                                             dataModel,
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

    public RuleModeller getRuleModeller() {
        return ruleModeller;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

}
