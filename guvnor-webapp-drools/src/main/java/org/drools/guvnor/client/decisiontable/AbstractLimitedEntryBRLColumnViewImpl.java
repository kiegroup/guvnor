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

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.ModellerWidgetFactory;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModelEditor;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModellerConfiguration;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModellerWidgetFactory;
import org.drools.guvnor.client.common.Popup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.dt52.BRLColumn;
import org.drools.ide.common.client.modeldriven.dt52.BaseColumn;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * An editor for Limited Entry BRL Column definitions
 */
public abstract class AbstractLimitedEntryBRLColumnViewImpl<T, C extends BaseColumn> extends Popup
    implements
    RuleModelEditor {

    protected static final Constants constants  = GWT.create( Constants.class );

    protected int                    MIN_WIDTH  = 500;
    protected int                    MIN_HEIGHT = 200;

    @UiField(provided = true)
    RuleModeller                     ruleModeller;

    @UiField
    TextBox                          txtColumnHeader;

    @UiField
    CheckBox                         chkHideColumn;

    @UiField
    ScrollPanel                      brlEditorContainer;

    @UiField
    Button                           cmdApplyChanges;

    Widget                           popupContent;

    @SuppressWarnings("rawtypes")
    interface AbstractLimitedEntryBRLColumnEditorBinder
        extends
        UiBinder<Widget, AbstractLimitedEntryBRLColumnViewImpl> {
    }

    private static AbstractLimitedEntryBRLColumnEditorBinder uiBinder = GWT.create( AbstractLimitedEntryBRLColumnEditorBinder.class );

    protected final GuidedDecisionTable52                    model;
    protected final ClientFactory                            clientFactory;
    protected final EventBus                                 eventBus;
    protected final boolean                                  isNew;

    protected final BRLColumn<T, C>                          editingCol;
    protected final BRLColumn<T, C>                          originalCol;

    protected final RuleModel                                ruleModel;

    public AbstractLimitedEntryBRLColumnViewImpl(final SuggestionCompletionEngine sce,
                                                 final GuidedDecisionTable52 model,
                                                 final boolean isNew,
                                                 final Asset asset,
                                                 final BRLColumn<T, C> column,
                                                 final ClientFactory clientFactory,
                                                 final EventBus eventBus) {
        this.model = model;
        this.isNew = isNew;
        this.eventBus = eventBus;
        this.clientFactory = clientFactory;

        this.originalCol = column;
        this.editingCol = cloneBRLColumn( column );

        setModal( false );

        this.ruleModel = getRuleModel( editingCol );

        //Limited Entry decision tables do not permit field values to be defined with Template Keys
        ModellerWidgetFactory widgetFactory = new RuleModellerWidgetFactory();

        this.ruleModeller = new RuleModeller( asset,
                                              this.ruleModel,
                                              getRuleModellerConfiguration(),
                                              widgetFactory,
                                              clientFactory,
                                              eventBus );

        this.popupContent = uiBinder.createAndBindUi( this );

        setHeight( getPopupHeight() + "px" );
        setWidth( getPopupWidth() + "px" );
        this.brlEditorContainer.setHeight( (getPopupHeight() - 120) + "px" );
        this.brlEditorContainer.setWidth( getPopupWidth() + "px" );
        this.txtColumnHeader.setText( editingCol.getHeader() );
        this.chkHideColumn.setValue( editingCol.isHideColumn() );
    }

    protected abstract boolean isHeaderUnique(String header);

    protected abstract RuleModel getRuleModel(BRLColumn<T, C> column);

    protected abstract RuleModellerConfiguration getRuleModellerConfiguration();

    protected abstract void doInsertColumn();

    protected abstract void doUpdateColumn();

    protected abstract BRLColumn<T, C> cloneBRLColumn(BRLColumn<T, C> col);

    protected abstract boolean isDefined();

    public RuleModeller getRuleModeller() {
        return this.ruleModeller;
    }

    @Override
    public Widget getContent() {
        return popupContent;
    }

    /**
     * Width of pop-up, 75% of the client width or MIN_WIDTH
     * 
     * @return
     */
    private int getPopupWidth() {
        int w = (int) (Window.getClientWidth() * 0.75);
        if ( w < MIN_WIDTH ) {
            w = MIN_WIDTH;
        }
        return w;
    }

    /**
     * Height of pop-up, 75% of the client height or MIN_HEIGHT
     * 
     * @return
     */
    protected int getPopupHeight() {
        int h = (int) (Window.getClientHeight() * 0.75);
        if ( h < MIN_HEIGHT ) {
            h = MIN_HEIGHT;
        }
        return h;
    }

    @UiHandler("txtColumnHeader")
    void columnHanderChangeHandler(ChangeEvent event) {
        editingCol.setHeader( txtColumnHeader.getText() );
    }

    @UiHandler("chkHideColumn")
    void hideColumnClickHandler(ClickEvent event) {
        editingCol.setHideColumn( chkHideColumn.getValue() );
    }

    @UiHandler("cmdApplyChanges")
    void applyChangesClickHandler(ClickEvent event) {

        //Validation
        if ( null == editingCol.getHeader() || "".equals( editingCol.getHeader() ) ) {
            Window.alert( constants.YouMustEnterAColumnHeaderValueDescription() );
            return;
        }
        if ( isNew ) {
            if ( !isHeaderUnique( editingCol.getHeader() ) ) {
                Window.alert( constants.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                return;
            }
            if ( isDefined() ) {
                doInsertColumn();
            } else {
                Window.alert( constants.DecisionTableBRLFragmentNothingDefined() );
                return;
            }

        } else {
            if ( !originalCol.getHeader().equals( editingCol.getHeader() ) ) {
                if ( !isHeaderUnique( editingCol.getHeader() ) ) {
                    Window.alert( constants.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                    return;
                }
            }
            if ( isDefined() ) {
                doUpdateColumn();
            } else {
                Window.alert( constants.DecisionTableBRLFragmentNothingDefined() );
                return;
            }

        }

        hide();
    }

}
