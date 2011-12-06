/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.decisiontable;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModelEditor;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModellerConfiguration;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.TemplateModellerWidgetFactory;
import org.drools.guvnor.client.common.Popup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * An editor for BRL Column definitions
 */
public abstract class AbstractBRLColumnViewImpl extends Popup
    implements
    RuleModelEditor,
    AbstractBRLColumnView {

    protected static final Constants constants  = GWT.create( Constants.class );

    protected int                    MIN_WIDTH  = 500;
    protected int                    MIN_HEIGHT = 200;

    protected Presenter              presenter;

    @UiField(provided = true)
    RuleModeller                     ruleModeller;

    @UiField
    TextBox                          txtColumnHeader;

    @UiField(provided = true)
    CheckBox                         chkHideColumn;

    @UiField
    ScrollPanel                      brlEditorContainer;

    Widget                           popupContent;

    interface AbstractBRLColumnEditorBinder
        extends
        UiBinder<Widget, AbstractBRLColumnViewImpl> {
    }

    private static AbstractBRLColumnEditorBinder uiBinder = GWT.create( AbstractBRLColumnEditorBinder.class );

    protected final RuleModel                    ruleModel;
    protected final ClientFactory                clientFactory;
    protected final EventBus                     eventBus;

    public AbstractBRLColumnViewImpl(RuleAsset asset,
                                     RuleModel ruleModel,
                                     ClientFactory clientFactory,
                                     EventBus eventBus) {
        this.ruleModel = ruleModel;
        this.eventBus = eventBus;
        this.clientFactory = clientFactory;
        this.ruleModeller = new RuleModeller( asset,
                                              this.ruleModel,
                                              getRuleModellerConfiguration(),
                                              new TemplateModellerWidgetFactory(),
                                              clientFactory,
                                              eventBus );

        //TODO {manstis} This needs to come from the column!
        this.chkHideColumn = DTCellValueWidgetFactory.getHideColumnIndicator( new DTColumnConfig52() );

        this.popupContent = uiBinder.createAndBindUi( this );

        setHeight( getPopupHeight() + "px" );
        setWidth( getPopupWidth() + "px" );
        this.brlEditorContainer.setHeight( (getPopupHeight() - 120) + "px" );
        this.brlEditorContainer.setWidth( getPopupWidth() + "px" );
    }

    public abstract RuleModellerConfiguration getRuleModellerConfiguration();

    public RuleModeller getRuleModeller() {
        return this.ruleModeller;
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget getContent() {
        return popupContent;
    }

    /**
     * Width of pop-up, 1/4 of the client width or MIN_WIDTH
     * 
     * @return
     */
    private int getPopupWidth() {
        int w = Window.getClientWidth() / 4;
        if ( w < MIN_WIDTH ) {
            w = MIN_WIDTH;
        }
        return w;
    }

    /**
     * Height of pop-up, 1/2 of the client height or MIN_HEIGHT
     * 
     * @return
     */
    protected int getPopupHeight() {
        int h = Window.getClientHeight() / 2;
        if ( h < MIN_HEIGHT ) {
            h = MIN_HEIGHT;
        }
        return h;
    }

}
