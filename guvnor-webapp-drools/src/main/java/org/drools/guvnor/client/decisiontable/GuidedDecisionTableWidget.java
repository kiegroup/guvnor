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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.SaveCommand;
import org.drools.guvnor.client.asseteditor.SaveEventListener;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.kie.uberfirebootstrap.client.widgets.FormStylePopup;
import org.drools.guvnor.client.common.IBindingProvider;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.kie.uberfirebootstrap.client.widgets.SmallLabel;
import org.drools.guvnor.client.decisiontable.widget.VerticalDecisionTableWidget;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.resources.DroolsGuvnorImageResources;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.guvnor.client.resources.GuvnorImages;
import org.drools.guvnor.client.resources.decisiontable.DecisionTableResources;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.util.AddButton;
import org.drools.guvnor.client.util.DecoratedDisclosurePanel;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLConditionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLRuleModel;
import org.drools.ide.common.client.modeldriven.dt52.CompositeColumn;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryBRLActionColumn;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryBRLConditionColumn;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the new guided decision table editor for the web.
 */
public class GuidedDecisionTableWidget extends Composite
        implements
        SaveEventListener,
        EditorWidget,
        IBindingProvider,
        BRLActionColumnView.Presenter,
        BRLConditionColumnView.Presenter,
        LimitedEntryBRLConditionColumnView.Presenter,
        LimitedEntryBRLActionColumnView.Presenter {

    private Asset                       asset;
    private GuidedDecisionTable52       guidedDecisionTable;
    private VerticalPanel               layout;
    private PrettyFormLayout            configureColumnsNote;
    private VerticalPanel               attributeConfigWidget;
    private VerticalPanel               conditionsConfigWidget;
    private String                      packageName;
    private String                      packageUUID;
    private VerticalPanel               actionsConfigWidget;
    private SuggestionCompletionEngine  sce;
    private BRLRuleModel                rm;

    private VerticalDecisionTableWidget dtable;
    private SimplePanel                 dtableContainer   = new SimplePanel();

    //This EventBus is local to the screen and should be used for local operations, set data, add rows etc
    private EventBus                    eventBus          = new SimpleEventBus();

    //This EventBus is global to Guvnor and should be used for global operations, navigate pages etc 
    @SuppressWarnings("unused")
    private EventBus                    globalEventBus;

    private ClientFactory               clientFactory;

    private static String               SECTION_SEPARATOR = "..................";

    private enum NewColumnTypes {
        METADATA_ATTRIBUTE,
        CONDITION_SIMPLE,
        CONDITION_BRL_FRAGMENT,
        ACTION_UPDATE_FACT_FIELD,
        ACTION_INSERT_FACT_FIELD,
        ACTION_RETRACT_FACT,
        ACTION_WORKITEM,
        ACTION_WORKITEM_UPDATE_FACT_FIELD,
        ACTION_WORKITEM_INSERT_FACT_FIELD,
        ACTION_BRL_FRAGMENT
    }

    private final BRLActionColumnView.Presenter                BRL_ACTION_PRESENTER                  = this;

    private final BRLConditionColumnView.Presenter             BRL_CONDITION_PRESENTER               = this;

    private final LimitedEntryBRLConditionColumnView.Presenter LIMITED_ENTRY_BRL_CONDITION_PRESENTER = this;

    private final LimitedEntryBRLActionColumnView.Presenter    LIMITED_ENTRY_BRL_ACTION_PRESENTER    = this;

    private final boolean                                      isReadOnly;

    public GuidedDecisionTableWidget(final Asset asset,
                                     final RuleViewer viewer,
                                     final ClientFactory clientFactory,
                                     final EventBus globalEventBus) {

        this.asset = asset;
        this.guidedDecisionTable = (GuidedDecisionTable52) asset.getContent();
        this.guidedDecisionTable.initAnalysisColumn();
        this.packageName = asset.getMetaData().getModuleName();
        this.packageUUID = asset.getMetaData().getModuleUUID();
        this.guidedDecisionTable.setTableName( asset.getName() );
        this.globalEventBus = globalEventBus;
        this.clientFactory = clientFactory;
        this.rm = new BRLRuleModel( guidedDecisionTable );
        this.isReadOnly = asset.isReadonly();

        layout = new VerticalPanel();

        setupDecisionTable();

        configureColumnsNote = new PrettyFormLayout();
        configureColumnsNote.startSection();
        configureColumnsNote.addRow( new HTML( AbstractImagePrototype.create( DroolsGuvnorImageResources.INSTANCE.information() ).getHTML()
                                               + "&nbsp;"
                                               + Constants.INSTANCE.ConfigureColumnsNote() ) );
        configureColumnsNote.endSection();

        DecoratedDisclosurePanel disclosurePanel = new DecoratedDisclosurePanel( Constants.INSTANCE.DecisionTable() );
        disclosurePanel.setWidth( "100%" );
        disclosurePanel.setTitle( Constants.INSTANCE.DecisionTable() );

        VerticalPanel config = new VerticalPanel();
        config.setWidth( "100%" );
        disclosurePanel.add( config );

        //Can't add new columns if the asset is read-only
        if ( !isReadOnly ) {
            config.add( newColumn() );
        }

        DecoratedDisclosurePanel conditions = new DecoratedDisclosurePanel( Constants.INSTANCE.ConditionColumns() );
        conditions.setOpen( false );
        conditions.setWidth( "75%" );
        conditions.add( getConditions() );
        config.add( conditions );

        DecoratedDisclosurePanel actions = new DecoratedDisclosurePanel( Constants.INSTANCE.ActionColumns() );
        actions.setOpen( false );
        actions.setWidth( "75%" );
        actions.add( getActions() );
        config.add( actions );

        DecoratedDisclosurePanel options = new DecoratedDisclosurePanel( Constants.INSTANCE.Options() );
        options.setOpen( false );
        options.setWidth( "75%" );
        options.add( getAttributes() );
        config.add( options );

        layout.add( disclosurePanel );
        layout.add( configureColumnsNote );
        layout.add( dtableContainer );

        initWidget( layout );
    }

    private Widget getActions() {
        actionsConfigWidget = new VerticalPanel();
        refreshActionsWidget();
        return actionsConfigWidget;
    }

    private void refreshActionsWidget() {
        this.actionsConfigWidget.clear();

        //Each Action is a row in a vertical panel
        final VerticalPanel actionsPanel = new VerticalPanel();

        //Wire-up DnD for Actions. All DnD related widgets must be contained in the AbsolutePanel
        AbsolutePanel actionsBoundaryPanel = new AbsolutePanel();
        PickupDragController actionsDragController = new PickupDragController( actionsBoundaryPanel,
                                                                               false );
        actionsDragController.setBehaviorConstrainedToBoundaryPanel( false );
        VerticalPanelDropController actionsDropController = new VerticalPanelDropController( actionsPanel );
        actionsDragController.registerDropController( actionsDropController );

        //Add DnD container to main Conditions container
        actionsBoundaryPanel.add( actionsPanel );
        this.actionsConfigWidget.add( actionsBoundaryPanel );

        //Add a DragHandler to handle the actions resulting from the drag operation
        actionsDragController.addDragHandler( new ActionDragHandler( actionsPanel,
                                                                     guidedDecisionTable,
                                                                     dtable ) );

        //Add Actions to panel
        List<ActionCol52> actions = guidedDecisionTable.getActionCols();
        boolean bAreActionsDraggable = actions.size() > 1 && !isReadOnly;
        for ( ActionCol52 c : actions ) {
            HorizontalPanel hp = new HorizontalPanel();
            if ( !isReadOnly ) {
                hp.add( removeAction( c ) );
            }
            hp.add( editAction( c ) );
            Label actionLabel = makeColumnLabel( c );
            hp.add( actionLabel );
            actionsPanel.add( hp );
            if ( bAreActionsDraggable ) {
                actionsDragController.makeDraggable( hp,
                                                     actionLabel );
            }

        }
        setupColumnsNote();
    }

    private SmallLabel makeColumnLabel(ActionCol52 ac) {
        SmallLabel label = new SmallLabel( ac.getHeader() );
        if ( ac.isHideColumn() ) {
            label.setStylePrimaryName( DecisionTableResources.INSTANCE.style().columnLabelHidden() );
        }
        return label;
    }

    private Widget editAction(final ActionCol52 c) {

        if ( c instanceof ActionWorkItemSetFieldCol52 ) {
            final ActionWorkItemSetFieldCol52 awisf = (ActionWorkItemSetFieldCol52) c;
            Image edit = GuvnorImages.INSTANCE.Edit();
            edit.setAltText(Constants.INSTANCE.EditThisActionColumnConfiguration());
            return new ImageButton( edit,
                                    Constants.INSTANCE.EditThisActionColumnConfiguration(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent w) {
                                            ActionWorkItemSetFieldPopup ed = new ActionWorkItemSetFieldPopup( getSCE(),
                                                                                                              guidedDecisionTable,
                                                                                                              new GenericColumnCommand() {
                                                                                                                  public void execute(DTColumnConfig52 column) {
                                                                                                                      dtable.updateColumn( awisf,
                                                                                                                                           (ActionWorkItemSetFieldCol52) column );
                                                                                                                      refreshActionsWidget();
                                                                                                                  }
                                                                                                              },
                                                                                                              awisf,
                                                                                                              false,
                                                                                                              isReadOnly );
                                            ed.show();
                                        }
                                    } );

        } else if ( c instanceof ActionSetFieldCol52 ) {
            final ActionSetFieldCol52 asf = (ActionSetFieldCol52) c;
            Image edit = GuvnorImages.INSTANCE.Edit();
            edit.setAltText(Constants.INSTANCE.EditThisActionColumnConfiguration());
            return new ImageButton( edit,
                                    Constants.INSTANCE.EditThisActionColumnConfiguration(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent w) {
                                            ActionSetFieldPopup ed = new ActionSetFieldPopup( getSCE(),
                                                                                              guidedDecisionTable,
                                                                                              new GenericColumnCommand() {
                                                                                                  public void execute(DTColumnConfig52 column) {
                                                                                                      dtable.updateColumn( asf,
                                                                                                                           (ActionSetFieldCol52) column );
                                                                                                      refreshActionsWidget();
                                                                                                  }
                                                                                              },
                                                                                              asf,
                                                                                              false,
                                                                                              isReadOnly );
                                            ed.show();
                                        }
                                    } );

        } else if ( c instanceof ActionWorkItemInsertFactCol52 ) {
            final ActionWorkItemInsertFactCol52 awiif = (ActionWorkItemInsertFactCol52) c;
            Image edit = GuvnorImages.INSTANCE.Edit();
            edit.setAltText(Constants.INSTANCE.EditThisActionColumnConfiguration());
            return new ImageButton( edit,
                                    Constants.INSTANCE.EditThisActionColumnConfiguration(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent w) {
                                            ActionWorkItemInsertFactPopup ed = new ActionWorkItemInsertFactPopup( getSCE(),
                                                                                                                  guidedDecisionTable,
                                                                                                                  new GenericColumnCommand() {
                                                                                                                      public void execute(DTColumnConfig52 column) {
                                                                                                                          dtable.updateColumn( awiif,
                                                                                                                                               (ActionWorkItemInsertFactCol52) column );
                                                                                                                          refreshActionsWidget();
                                                                                                                      }
                                                                                                                  },
                                                                                                                  awiif,
                                                                                                                  false,
                                                                                                                  isReadOnly );
                                            ed.show();
                                        }
                                    } );

        } else if ( c instanceof ActionInsertFactCol52 ) {
            final ActionInsertFactCol52 asf = (ActionInsertFactCol52) c;
            Image edit = GuvnorImages.INSTANCE.Edit();
            edit.setAltText(Constants.INSTANCE.EditThisActionColumnConfiguration());
            return new ImageButton( edit,
                                    Constants.INSTANCE.EditThisActionColumnConfiguration(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent w) {
                                            ActionInsertFactPopup ed = new ActionInsertFactPopup( getSCE(),
                                                                                                  guidedDecisionTable,
                                                                                                  new GenericColumnCommand() {
                                                                                                      public void execute(DTColumnConfig52 column) {
                                                                                                          dtable.updateColumn( asf,
                                                                                                                               (ActionInsertFactCol52) column );
                                                                                                          refreshActionsWidget();
                                                                                                      }
                                                                                                  },
                                                                                                  asf,
                                                                                                  false,
                                                                                                  isReadOnly );
                                            ed.show();
                                        }
                                    } );

        } else if ( c instanceof ActionRetractFactCol52 ) {
            final ActionRetractFactCol52 arf = (ActionRetractFactCol52) c;
            Image edit = GuvnorImages.INSTANCE.Edit();
            edit.setAltText(Constants.INSTANCE.EditThisActionColumnConfiguration());
            return new ImageButton( edit,
                                    Constants.INSTANCE.EditThisActionColumnConfiguration(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent w) {
                                            ActionRetractFactPopup ed = new ActionRetractFactPopup( guidedDecisionTable,
                                                                                                    new GenericColumnCommand() {
                                                                                                        public void execute(DTColumnConfig52 column) {
                                                                                                            dtable.updateColumn( arf,
                                                                                                                                 (ActionRetractFactCol52) column );
                                                                                                            refreshActionsWidget();
                                                                                                        }
                                                                                                    },
                                                                                                    arf,
                                                                                                    false,
                                                                                                    isReadOnly );
                                            ed.show();
                                        }
                                    } );

        } else if ( c instanceof ActionWorkItemCol52 ) {
            final ActionWorkItemCol52 awi = (ActionWorkItemCol52) c;
            Image edit = GuvnorImages.INSTANCE.Edit();
            edit.setAltText(Constants.INSTANCE.EditThisActionColumnConfiguration());
            return new ImageButton( edit,
                                    Constants.INSTANCE.EditThisActionColumnConfiguration(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent w) {
                                            ActionWorkItemPopup popup = new ActionWorkItemPopup( clientFactory,
                                                                                                 packageUUID,
                                                                                                 guidedDecisionTable,
                                                                                                 GuidedDecisionTableWidget.this,
                                                                                                 new GenericColumnCommand() {
                                                                                                     public void execute(DTColumnConfig52 column) {
                                                                                                         dtable.updateColumn( awi,
                                                                                                                              (ActionWorkItemCol52) column );
                                                                                                         refreshActionsWidget();
                                                                                                     }
                                                                                                 },
                                                                                                 awi,
                                                                                                 false,
                                                                                                 isReadOnly );
                                            popup.show();
                                        }
                                    } );

        } else if ( c instanceof LimitedEntryBRLActionColumn ) {
            final LimitedEntryBRLActionColumn column = (LimitedEntryBRLActionColumn) c;
            Image edit = GuvnorImages.INSTANCE.Edit();
            edit.setAltText(Constants.INSTANCE.EditThisActionColumnConfiguration());
            return new ImageButton( edit,
                                    Constants.INSTANCE.EditThisActionColumnConfiguration(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent w) {
                                            LimitedEntryBRLActionColumnViewImpl popup = new LimitedEntryBRLActionColumnViewImpl( sce,
                                                                                                                                 guidedDecisionTable,
                                                                                                                                 false,
                                                                                                                                 asset,
                                                                                                                                 column,
                                                                                                                                 clientFactory,
                                                                                                                                 eventBus );
                                            popup.setPresenter( LIMITED_ENTRY_BRL_ACTION_PRESENTER );
                                            popup.show();
                                        }
                                    } );

        } else if ( c instanceof BRLActionColumn ) {
            final BRLActionColumn column = (BRLActionColumn) c;
            Image edit = GuvnorImages.INSTANCE.Edit();
            edit.setAltText(Constants.INSTANCE.EditThisActionColumnConfiguration());
            return new ImageButton( edit,
                                    Constants.INSTANCE.EditThisActionColumnConfiguration(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent w) {
                                            BRLActionColumnViewImpl popup = new BRLActionColumnViewImpl( sce,
                                                                                                         guidedDecisionTable,
                                                                                                         false,
                                                                                                         asset,
                                                                                                         column,
                                                                                                         clientFactory,
                                                                                                         eventBus );
                                            popup.setPresenter( BRL_ACTION_PRESENTER );
                                            popup.show();
                                        }
                                    } );
        }
        //Should never happen!
        throw new IllegalArgumentException( "Unrecognised Action column definition." );
    }

    private Widget removeAction(final ActionCol52 c) {
        if ( c instanceof LimitedEntryBRLActionColumn ) {
            Image image = DroolsGuvnorImages.INSTANCE.DeleteItemSmall();
            image.setAltText(Constants.INSTANCE.RemoveThisActionColumn());
            return new ImageButton( image,
                                    Constants.INSTANCE.RemoveThisActionColumn(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent w) {
                                            String cm = Constants.INSTANCE.DeleteActionColumnWarning( c.getHeader() );
                                            if ( com.google.gwt.user.client.Window.confirm( cm ) ) {
                                                dtable.deleteColumn( (LimitedEntryBRLActionColumn) c );
                                                refreshActionsWidget();
                                            }
                                        }
                                    } );

        } else if ( c instanceof BRLActionColumn ) {
            Image image = DroolsGuvnorImages.INSTANCE.DeleteItemSmall();
            image.setAltText(Constants.INSTANCE.RemoveThisActionColumn());
            return new ImageButton( image,
                                    Constants.INSTANCE.RemoveThisActionColumn(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent w) {
                                            String cm = Constants.INSTANCE.DeleteActionColumnWarning( c.getHeader() );
                                            if ( com.google.gwt.user.client.Window.confirm( cm ) ) {
                                                dtable.deleteColumn( (BRLActionColumn) c );
                                                refreshActionsWidget();
                                            }
                                        }
                                    } );

        }
        Image image = DroolsGuvnorImages.INSTANCE.DeleteItemSmall();
        image.setAltText(Constants.INSTANCE.RemoveThisActionColumn());
        return new ImageButton( image,
                                Constants.INSTANCE.RemoveThisActionColumn(),
                                new ClickHandler() {
                                    public void onClick(ClickEvent w) {
                                        String cm = Constants.INSTANCE.DeleteActionColumnWarning( c.getHeader() );
                                        if ( com.google.gwt.user.client.Window.confirm( cm ) ) {
                                            dtable.deleteColumn( c );
                                            refreshActionsWidget();
                                        }
                                    }
                                } );
    }

    private Widget getConditions() {
        conditionsConfigWidget = new VerticalPanel();
        refreshConditionsWidget();
        return conditionsConfigWidget;
    }

    private void refreshConditionsWidget() {
        this.conditionsConfigWidget.clear();

        //Each Pattern is a row in a vertical panel
        final VerticalPanel patternsPanel = new VerticalPanel();

        //Wire-up DnD for Patterns. All DnD related widgets must be contained in the AbsolutePanel
        AbsolutePanel patternsBoundaryPanel = new AbsolutePanel();
        PickupDragController patternsDragController = new PickupDragController( patternsBoundaryPanel,
                                                                                false );
        patternsDragController.setBehaviorConstrainedToBoundaryPanel( false );
        VerticalPanelDropController widgetDropController = new VerticalPanelDropController( patternsPanel );
        patternsDragController.registerDropController( widgetDropController );

        //Add DnD container to main Conditions container
        conditionsConfigWidget.add( patternsBoundaryPanel );
        patternsBoundaryPanel.add( patternsPanel );

        //Add a DragHandler to handle the actions resulting from the drag operation
        patternsDragController.addDragHandler( new PatternDragHandler( patternsPanel,
                                                                       guidedDecisionTable,
                                                                       dtable ) );

        List<CompositeColumn< ? >> columns = guidedDecisionTable.getConditions();
        boolean arePatternsDraggable = columns.size() > 1 && !isReadOnly;
        for ( CompositeColumn< ? > column : columns ) {
            if ( column instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) column;
                VerticalPanel patternPanel = new VerticalPanel();
                VerticalPanel conditionsPanel = new VerticalPanel();
                HorizontalPanel patternHeaderPanel = new HorizontalPanel();
                patternHeaderPanel.setStylePrimaryName( DecisionTableResources.INSTANCE.style().patternSectionHeader() );
                Label patternLabel = makePatternLabel( p );
                patternHeaderPanel.add( patternLabel );
                patternPanel.add( patternHeaderPanel );
                patternsPanel.add( patternPanel );

                //Wire-up DnD for Conditions. All DnD related widgets must be contained in the AbsolutePanel
                AbsolutePanel conditionsBoundaryPanel = new AbsolutePanel();
                PickupDragController conditionsDragController = new PickupDragController( conditionsBoundaryPanel,
                                                                                          false );
                conditionsDragController.setBehaviorConstrainedToBoundaryPanel( false );
                VerticalPanelDropController conditionsDropController = new VerticalPanelDropController( conditionsPanel );
                conditionsDragController.registerDropController( conditionsDropController );

                //Add DnD container to main Conditions container
                conditionsBoundaryPanel.add( conditionsPanel );
                patternPanel.add( conditionsBoundaryPanel );

                //Add a DragHandler to handle the actions resulting from the drag operation
                conditionsDragController.addDragHandler( new ConditionDragHandler( conditionsPanel,
                                                                                   p,
                                                                                   dtable ) );

                List<ConditionCol52> conditions = p.getChildColumns();
                boolean bAreConditionsDraggable = conditions.size() > 1 && !isReadOnly;
                for ( ConditionCol52 c : p.getChildColumns() ) {
                    HorizontalPanel hp = new HorizontalPanel();
                    hp.setStylePrimaryName( DecisionTableResources.INSTANCE.style().patternConditionSectionHeader() );
                    if ( !isReadOnly ) {
                        hp.add( removeCondition( c ) );
                    }
                    hp.add( editCondition( p,
                                           c ) );
                    SmallLabel conditionLabel = makeColumnLabel( c );
                    hp.add( conditionLabel );
                    conditionsPanel.add( hp );
                    if ( bAreConditionsDraggable ) {
                        conditionsDragController.makeDraggable( hp,
                                                                conditionLabel );
                    }
                }

                if ( arePatternsDraggable ) {
                    patternsDragController.makeDraggable( patternPanel,
                                                          patternLabel );
                }

            } else if ( column instanceof BRLConditionColumn ) {
                BRLConditionColumn brl = (BRLConditionColumn) column;

                HorizontalPanel patternHeaderPanel = new HorizontalPanel();
                patternHeaderPanel.setStylePrimaryName( DecisionTableResources.INSTANCE.style().patternSectionHeader() );
                HorizontalPanel patternPanel = new HorizontalPanel();
                if ( !isReadOnly ) {
                    patternPanel.add( removeCondition( brl ) );
                }
                patternPanel.add( editCondition( brl ) );
                Label patternLabel = makePatternLabel( brl );
                patternPanel.add( patternLabel );
                patternHeaderPanel.add( patternPanel );
                patternsPanel.add( patternHeaderPanel );

                if ( arePatternsDraggable ) {
                    patternsDragController.makeDraggable( patternHeaderPanel,
                                                          patternLabel );
                }

            }

        }
        setupColumnsNote();
    }

    private Widget newColumn() {
        AddButton addButton = new AddButton();
        addButton.setText( Constants.INSTANCE.NewColumn() );
        addButton.setTitle( Constants.INSTANCE.AddNewColumn() );

        addButton.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                final FormStylePopup pop = new FormStylePopup();
                pop.setModal( false );

                //List of basic column types
                final ListBox choice = new ListBox();
                choice.setVisibleItemCount( NewColumnTypes.values().length );

                choice.addItem( Constants.INSTANCE.AddNewMetadataOrAttributeColumn(),
                                NewColumnTypes.METADATA_ATTRIBUTE.name() );
                choice.addItem( SECTION_SEPARATOR );
                choice.addItem( Constants.INSTANCE.AddNewConditionSimpleColumn(),
                                NewColumnTypes.CONDITION_SIMPLE.name() );
                choice.addItem( SECTION_SEPARATOR );
                choice.addItem( Constants.INSTANCE.SetTheValueOfAField(),
                                NewColumnTypes.ACTION_UPDATE_FACT_FIELD.name() );
                choice.addItem( Constants.INSTANCE.SetTheValueOfAFieldOnANewFact(),
                                NewColumnTypes.ACTION_INSERT_FACT_FIELD.name() );
                choice.addItem( Constants.INSTANCE.RetractAnExistingFact(),
                                NewColumnTypes.ACTION_RETRACT_FACT.name() );

                //Checkbox to include Advanced Action types
                final CheckBox chkIncludeAdvancedOptions = new CheckBox( SafeHtmlUtils.fromString( Constants.INSTANCE.IncludeAdvancedOptions() ) );
                chkIncludeAdvancedOptions.setValue( false );
                chkIncludeAdvancedOptions.addClickHandler( new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        if ( chkIncludeAdvancedOptions.getValue() ) {
                            addItem( 3,
                                     Constants.INSTANCE.AddNewConditionBRLFragment(),
                                     NewColumnTypes.CONDITION_BRL_FRAGMENT.name() );
                            addItem( Constants.INSTANCE.WorkItemAction(),
                                     NewColumnTypes.ACTION_WORKITEM.name() );
                            addItem( Constants.INSTANCE.WorkItemActionSetField(),
                                     NewColumnTypes.ACTION_WORKITEM_UPDATE_FACT_FIELD.name() );
                            addItem( Constants.INSTANCE.WorkItemActionInsertFact(),
                                     NewColumnTypes.ACTION_WORKITEM_INSERT_FACT_FIELD.name() );
                            addItem( Constants.INSTANCE.AddNewActionBRLFragment(),
                                     NewColumnTypes.ACTION_BRL_FRAGMENT.name() );
                        } else {
                            removeItem( NewColumnTypes.CONDITION_BRL_FRAGMENT.name() );
                            removeItem( NewColumnTypes.ACTION_WORKITEM.name() );
                            removeItem( NewColumnTypes.ACTION_WORKITEM_UPDATE_FACT_FIELD.name() );
                            removeItem( NewColumnTypes.ACTION_WORKITEM_INSERT_FACT_FIELD.name() );
                            removeItem( NewColumnTypes.ACTION_BRL_FRAGMENT.name() );
                        }
                        pop.center();
                    }

                    private void addItem(int index,
                                         String item,
                                         String value) {
                        for ( int itemIndex = 0; itemIndex < choice.getItemCount(); itemIndex++ ) {
                            if ( choice.getValue( itemIndex ).equals( value ) ) {
                                return;
                            }
                        }
                        choice.insertItem( item,
                                           value,
                                           index );
                    }

                    private void addItem(String item,
                                         String value) {
                        for ( int itemIndex = 0; itemIndex < choice.getItemCount(); itemIndex++ ) {
                            if ( choice.getValue( itemIndex ).equals( value ) ) {
                                return;
                            }
                        }
                        choice.addItem( item,
                                        value );
                    }

                    private void removeItem(String value) {
                        for ( int itemIndex = 0; itemIndex < choice.getItemCount(); itemIndex++ ) {
                            if ( choice.getValue( itemIndex ).equals( value ) ) {
                                choice.removeItem( itemIndex );
                                break;
                            }
                        }
                    }

                } );

                //OK button to create column
                final Button ok = new Button( Constants.INSTANCE.OK() );
                ok.addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent w) {
                        String s = choice.getValue( choice.getSelectedIndex() );
                        if ( s.equals( NewColumnTypes.METADATA_ATTRIBUTE.name() ) ) {
                            showMetaDataAndAttribute();
                        } else if ( s.equals( NewColumnTypes.CONDITION_SIMPLE.name() ) ) {
                            showConditionSimple();
                        } else if ( s.equals( NewColumnTypes.CONDITION_BRL_FRAGMENT.name() ) ) {
                            showConditionBRLFragment();
                        } else if ( s.equals( NewColumnTypes.ACTION_UPDATE_FACT_FIELD.name() ) ) {
                            showActionSet();
                        } else if ( s.equals( NewColumnTypes.ACTION_INSERT_FACT_FIELD.name() ) ) {
                            showActionInsert();
                        } else if ( s.equals( NewColumnTypes.ACTION_RETRACT_FACT.name() ) ) {
                            showActionRetract();
                        } else if ( s.equals( NewColumnTypes.ACTION_WORKITEM.name() ) ) {
                            showActionWorkItemAction();
                        } else if ( s.equals( NewColumnTypes.ACTION_WORKITEM_UPDATE_FACT_FIELD.name() ) ) {
                            showActionWorkItemActionSet();
                        } else if ( s.equals( NewColumnTypes.ACTION_WORKITEM_INSERT_FACT_FIELD.name() ) ) {
                            showActionWorkItemActionInsert();
                        } else if ( s.equals( NewColumnTypes.ACTION_BRL_FRAGMENT.name() ) ) {
                            showActionBRLFragment();
                        }
                        pop.hide();
                    }

                    private void showMetaDataAndAttribute() {
                        // show choice of attributes
                        Image image = new Image(DroolsGuvnorImageResources.INSTANCE.config());
                        image.setAltText(Constants.INSTANCE.Config());
                        final FormStylePopup pop = new FormStylePopup(image,
                                                                       Constants.INSTANCE.AddAnOptionToTheRule() );
                        final ListBox list = RuleAttributeWidget.getAttributeList();

                        //This attribute is only used for Decision Tables
                        list.addItem( GuidedDecisionTable52.NEGATE_RULE_ATTR );

                        // Remove any attributes already added
                        for ( AttributeCol52 col : guidedDecisionTable.getAttributeCols() ) {
                            for ( int iItem = 0; iItem < list.getItemCount(); iItem++ ) {
                                if ( list.getItemText( iItem ).equals( col.getAttribute() ) ) {
                                    list.removeItem( iItem );
                                    break;
                                }
                            }
                        }

                        final Image addbutton = DroolsGuvnorImages.INSTANCE.NewItem();
                        final TextBox box = new TextBox();
                        box.setVisibleLength( 15 );

                        list.setSelectedIndex( 0 );

                        list.addChangeHandler( new ChangeHandler() {
                            public void onChange(ChangeEvent event) {
                                AttributeCol52 attr = new AttributeCol52();
                                attr.setAttribute( list.getItemText( list.getSelectedIndex() ) );
                                dtable.addColumn( attr );
                                refreshAttributeWidget();
                                pop.hide();
                            }
                        } );

                        addbutton.setTitle( Constants.INSTANCE.AddMetadataToTheRule() );

                        addbutton.addClickHandler( new ClickHandler() {
                            public void onClick(ClickEvent w) {

                                String metadata = box.getText();
                                if ( !isUnique( metadata ) ) {
                                    Window.alert( Constants.INSTANCE.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                                    return;
                                }
                                MetadataCol52 met = new MetadataCol52();
                                met.setHideColumn( true );
                                met.setMetadata( metadata );
                                dtable.addColumn( met );
                                refreshAttributeWidget();
                                pop.hide();
                            }

                            private boolean isUnique(String metadata) {
                                for ( MetadataCol52 mc : guidedDecisionTable.getMetadataCols() ) {
                                    if ( metadata.equals( mc.getMetadata() ) ) {
                                        return false;
                                    }
                                }
                                return true;
                            }

                        } );
                        DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();
                        horiz.add( box );
                        horiz.add( addbutton );

                        pop.addAttribute( Constants.INSTANCE.Metadata1(),
                                          horiz );
                        pop.addAttribute( Constants.INSTANCE.Attribute(),
                                          list );
                        pop.show();
                    }

                    private void showConditionSimple() {
                        final ConditionCol52 column = makeNewConditionColumn();
                        ConditionPopup dialog = new ConditionPopup( getSCE(),
                                                                    guidedDecisionTable,
                                                                    new ConditionColumnCommand() {
                                                                        public void execute(Pattern52 pattern,
                                                                                            ConditionCol52 column) {

                                                                            //Update UI
                                                                            dtable.addColumn( pattern,
                                                                                              column );
                                                                            refreshConditionsWidget();
                                                                        }
                                                                    },
                                                                    column,
                                                                    true,
                                                                    isReadOnly );
                        dialog.show();
                    }

                    private void showConditionBRLFragment() {
                        final BRLConditionColumn column = makeNewConditionBRLFragment();
                        switch ( guidedDecisionTable.getTableFormat() ) {
                            case EXTENDED_ENTRY :
                                BRLConditionColumnViewImpl popup = new BRLConditionColumnViewImpl( sce,
                                                                                                   guidedDecisionTable,
                                                                                                   true,
                                                                                                   asset,
                                                                                                   column,
                                                                                                   clientFactory,
                                                                                                   eventBus );
                                popup.setPresenter( BRL_CONDITION_PRESENTER );
                                popup.show();
                                break;
                            case LIMITED_ENTRY :
                                LimitedEntryBRLConditionColumnViewImpl limtedEntryPopup = new LimitedEntryBRLConditionColumnViewImpl( sce,
                                                                                                                                      guidedDecisionTable,
                                                                                                                                      true,
                                                                                                                                      asset,
                                                                                                                                      (LimitedEntryBRLConditionColumn) column,
                                                                                                                                      clientFactory,
                                                                                                                                      eventBus );
                                limtedEntryPopup.setPresenter( LIMITED_ENTRY_BRL_CONDITION_PRESENTER );
                                limtedEntryPopup.show();
                                break;
                        }
                    }

                    private void showActionInsert() {
                        final ActionInsertFactCol52 afc = makeNewActionInsertColumn();
                        ActionInsertFactPopup ins = new ActionInsertFactPopup( getSCE(),
                                                                               guidedDecisionTable,
                                                                               new GenericColumnCommand() {
                                                                                   public void execute(DTColumnConfig52 column) {
                                                                                       newActionAdded( (ActionCol52) column );
                                                                                   }
                                                                               },
                                                                               afc,
                                                                               true,
                                                                               isReadOnly );
                        ins.show();
                    }

                    private void showActionSet() {
                        final ActionSetFieldCol52 afc = makeNewActionSetColumn();
                        ActionSetFieldPopup set = new ActionSetFieldPopup( getSCE(),
                                                                           guidedDecisionTable,
                                                                           new GenericColumnCommand() {
                                                                               public void execute(DTColumnConfig52 column) {
                                                                                   newActionAdded( (ActionCol52) column );
                                                                               }
                                                                           },
                                                                           afc,
                                                                           true,
                                                                           isReadOnly );
                        set.show();
                    }

                    private void showActionRetract() {
                        final ActionRetractFactCol52 arf = makeNewActionRetractFact();
                        ActionRetractFactPopup popup = new ActionRetractFactPopup( guidedDecisionTable,
                                                                                   new GenericColumnCommand() {
                                                                                       public void execute(DTColumnConfig52 column) {
                                                                                           newActionAdded( (ActionCol52) column );
                                                                                       }
                                                                                   },
                                                                                   arf,
                                                                                   true,
                                                                                   isReadOnly );
                        popup.show();
                    }

                    private void showActionWorkItemAction() {
                        final ActionWorkItemCol52 awi = makeNewActionWorkItem();
                        ActionWorkItemPopup popup = new ActionWorkItemPopup( clientFactory,
                                                                             packageUUID,
                                                                             guidedDecisionTable,
                                                                             GuidedDecisionTableWidget.this,
                                                                             new GenericColumnCommand() {
                                                                                 public void execute(DTColumnConfig52 column) {
                                                                                     newActionAdded( (ActionCol52) column );
                                                                                 }
                                                                             },
                                                                             awi,
                                                                             true,
                                                                             isReadOnly );
                        popup.show();
                    }

                    private void showActionWorkItemActionSet() {
                        final ActionWorkItemSetFieldCol52 awisf = makeNewActionWorkItemSetField();
                        ActionWorkItemSetFieldPopup popup = new ActionWorkItemSetFieldPopup( getSCE(),
                                                                                             guidedDecisionTable,
                                                                                             new GenericColumnCommand() {
                                                                                                 public void execute(DTColumnConfig52 column) {
                                                                                                     newActionAdded( (ActionCol52) column );
                                                                                                 }
                                                                                             },
                                                                                             awisf,
                                                                                             true,
                                                                                             isReadOnly );
                        popup.show();
                    }

                    private void showActionWorkItemActionInsert() {
                        final ActionWorkItemInsertFactCol52 awiif = makeNewActionWorkItemInsertFact();
                        ActionWorkItemInsertFactPopup popup = new ActionWorkItemInsertFactPopup( getSCE(),
                                                                                                     guidedDecisionTable,
                                                                                                     new GenericColumnCommand() {
                                                                                                         public void execute(DTColumnConfig52 column) {
                                                                                                             newActionAdded( (ActionCol52) column );
                                                                                                         }
                                                                                                     },
                                                                                                     awiif,
                                                                                                     true,
                                                                                                     isReadOnly );
                        popup.show();
                    }

                    private void showActionBRLFragment() {
                        final BRLActionColumn column = makeNewActionBRLFragment();
                        switch ( guidedDecisionTable.getTableFormat() ) {
                            case EXTENDED_ENTRY :
                                BRLActionColumnViewImpl popup = new BRLActionColumnViewImpl( sce,
                                                                                             guidedDecisionTable,
                                                                                             true,
                                                                                             asset,
                                                                                             column,
                                                                                             clientFactory,
                                                                                             eventBus );
                                popup.setPresenter( BRL_ACTION_PRESENTER );
                                popup.show();
                                break;
                            case LIMITED_ENTRY :
                                LimitedEntryBRLActionColumnViewImpl limtedEntryPopup = new LimitedEntryBRLActionColumnViewImpl( sce,
                                                                                                                                guidedDecisionTable,
                                                                                                                                true,
                                                                                                                                asset,
                                                                                                                                (LimitedEntryBRLActionColumn) column,
                                                                                                                                clientFactory,
                                                                                                                                eventBus );
                                limtedEntryPopup.setPresenter( LIMITED_ENTRY_BRL_ACTION_PRESENTER );
                                limtedEntryPopup.show();
                                break;
                        }

                    }

                    private void newActionAdded(ActionCol52 column) {
                        dtable.addColumn( column );
                        refreshActionsWidget();
                    }
                } );

                //If a separator is clicked disable OK button
                choice.addClickHandler( new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        int itemIndex = choice.getSelectedIndex();
                        if ( itemIndex < 0 ) {
                            return;
                        }
                        ok.setEnabled( !choice.getValue( itemIndex ).equals( SECTION_SEPARATOR ) );
                    }

                } );

                pop.setTitle( Constants.INSTANCE.AddNewColumn() );
                pop.addAttribute( Constants.INSTANCE.TypeOfColumn(),
                                  choice );
                pop.addAttribute( "",
                                  chkIncludeAdvancedOptions );
                pop.addAttribute( "",
                                  ok );
                pop.show();
            }

            private ConditionCol52 makeNewConditionColumn() {
                switch ( guidedDecisionTable.getTableFormat() ) {
                    case LIMITED_ENTRY :
                        return new LimitedEntryConditionCol52();
                    default :
                        return new ConditionCol52();
                }
            }

            private ActionInsertFactCol52 makeNewActionInsertColumn() {
                switch ( guidedDecisionTable.getTableFormat() ) {
                    case LIMITED_ENTRY :
                        return new LimitedEntryActionInsertFactCol52();
                    default :
                        return new ActionInsertFactCol52();
                }
            }

            private ActionSetFieldCol52 makeNewActionSetColumn() {
                switch ( guidedDecisionTable.getTableFormat() ) {
                    case LIMITED_ENTRY :
                        return new LimitedEntryActionSetFieldCol52();
                    default :
                        return new ActionSetFieldCol52();
                }
            }

            private ActionRetractFactCol52 makeNewActionRetractFact() {
                switch ( guidedDecisionTable.getTableFormat() ) {
                    case LIMITED_ENTRY :
                        LimitedEntryActionRetractFactCol52 ler = new LimitedEntryActionRetractFactCol52();
                        ler.setValue( new DTCellValue52( "" ) );
                        return ler;
                    default :
                        return new ActionRetractFactCol52();
                }
            }

            private ActionWorkItemCol52 makeNewActionWorkItem() {
                //WorkItems are defined within the column and always boolean (i.e. Limited Entry) in the table
                return new ActionWorkItemCol52();
            }

            private ActionWorkItemSetFieldCol52 makeNewActionWorkItemSetField() {
                //Actions setting Field Values from Work Item Result Parameters are always boolean (i.e. Limited Entry) in the table
                return new ActionWorkItemSetFieldCol52();
            }

            private ActionWorkItemInsertFactCol52 makeNewActionWorkItemInsertFact() {
                //Actions setting Field Values from Work Item Result Parameters are always boolean (i.e. Limited Entry) in the table
                return new ActionWorkItemInsertFactCol52();
            }

            private BRLActionColumn makeNewActionBRLFragment() {
                switch ( guidedDecisionTable.getTableFormat() ) {
                    case LIMITED_ENTRY :
                        return new LimitedEntryBRLActionColumn();
                    default :
                        return new BRLActionColumn();
                }
            }

            private BRLConditionColumn makeNewConditionBRLFragment() {
                switch ( guidedDecisionTable.getTableFormat() ) {
                    case LIMITED_ENTRY :
                        return new LimitedEntryBRLConditionColumn();
                    default :
                        return new BRLConditionColumn();
                }
            }

        } );

        return addButton;
    }

    private SmallLabel makeColumnLabel(ConditionCol52 cc) {
        StringBuilder sb = new StringBuilder();
        if ( cc.isBound() ) {
            sb.append( cc.getBinding() );
            sb.append( " : " );
        }
        sb.append( cc.getHeader() );
        SmallLabel label = new SmallLabel( sb.toString() );
        if ( cc.isHideColumn() ) {
            label.setStylePrimaryName( DecisionTableResources.INSTANCE.style().columnLabelHidden() );
        }
        return label;
    }

    private Label makePatternLabel(Pattern52 p) {
        StringBuilder patternLabel = new StringBuilder();
        String factType = p.getFactType();
        String boundName = p.getBoundName();
        if ( factType != null && factType.length() > 0 ) {
            if ( p.isNegated() ) {
                patternLabel.append( Constants.INSTANCE.negatedPattern() ).append( " " ).append( factType );
            } else {
                patternLabel.append( factType ).append( " [" ).append( boundName ).append( "]" );
            }
        }
        return new Label( patternLabel.toString() );
    }

    private Label makePatternLabel(BRLConditionColumn brl) {
        StringBuilder sb = new StringBuilder();
        sb.append( brl.getHeader() );
        return new Label( sb.toString() );
    }

    private Widget editCondition(final Pattern52 origPattern,
                                 final ConditionCol52 origCol) {
        Image edit = GuvnorImages.INSTANCE.Edit();
        edit.setAltText(Constants.INSTANCE.EditThisColumnsConfiguration());
        return new ImageButton( edit,
                                Constants.INSTANCE.EditThisColumnsConfiguration(),
                                new ClickHandler() {
                                    public void onClick(ClickEvent w) {
                                        ConditionPopup dialog = new ConditionPopup( getSCE(),
                                                                                    guidedDecisionTable,
                                                                                    new ConditionColumnCommand() {
                                                                                        public void execute(Pattern52 pattern,
                                                                                                            ConditionCol52 column) {

                                                                                            //Update UI
                                                                                            dtable.updateColumn( origPattern,
                                                                                                                 origCol,
                                                                                                                 pattern,
                                                                                                                 column );
                                                                                            refreshConditionsWidget();
                                                                                        }
                                                                                    },
                                                                                    origCol,
                                                                                    false,
                                                                                    isReadOnly );
                                        dialog.show();
                                    }
                                } );
    }

    private Widget editCondition(final BRLConditionColumn origCol) {
        Image edit = GuvnorImages.INSTANCE.Edit();
        edit.setAltText(Constants.INSTANCE.EditThisColumnsConfiguration());
        if ( origCol instanceof LimitedEntryBRLConditionColumn ) {
            return new ImageButton( edit,
                                    Constants.INSTANCE.EditThisColumnsConfiguration(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent w) {
                                            LimitedEntryBRLConditionColumnViewImpl popup = new LimitedEntryBRLConditionColumnViewImpl( sce,
                                                                                                                                       guidedDecisionTable,
                                                                                                                                       false,
                                                                                                                                       asset,
                                                                                                                                       (LimitedEntryBRLConditionColumn) origCol,
                                                                                                                                       clientFactory,
                                                                                                                                       eventBus );
                                            popup.setPresenter( LIMITED_ENTRY_BRL_CONDITION_PRESENTER );
                                            popup.show();
                                        }
                                    } );
        }
        return new ImageButton( edit,
                                Constants.INSTANCE.EditThisColumnsConfiguration(),
                                new ClickHandler() {
                                    public void onClick(ClickEvent w) {
                                        BRLConditionColumnViewImpl popup = new BRLConditionColumnViewImpl( sce,
                                                                                                           guidedDecisionTable,
                                                                                                           false,
                                                                                                           asset,
                                                                                                           origCol,
                                                                                                           clientFactory,
                                                                                                           eventBus );
                                        popup.setPresenter( BRL_CONDITION_PRESENTER );
                                        popup.show();
                                    }
                                } );
    }

    private SuggestionCompletionEngine getSCE() {
        if ( sce == null ) {
            this.sce = SuggestionCompletionCache.getInstance().getEngineFromCache( this.packageName );
        }
        return sce;
    }

    private Widget removeCondition(final ConditionCol52 c) {
        Image image = DroolsGuvnorImages.INSTANCE.DeleteItemSmall();
        image.setAltText(Constants.INSTANCE.RemoveThisConditionColumn());
        if ( c instanceof LimitedEntryBRLConditionColumn ) {
            return new ImageButton( image,
                                    Constants.INSTANCE.RemoveThisConditionColumn(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent w) {
                                            if ( !canConditionBeDeleted( (LimitedEntryBRLConditionColumn) c ) ) {
                                                Window.alert( Constants.INSTANCE.UnableToDeleteConditionColumn( c.getHeader() ) );
                                                return;
                                            }
                                            String cm = Constants.INSTANCE.DeleteConditionColumnWarning( c.getHeader() );
                                            if ( com.google.gwt.user.client.Window.confirm( cm ) ) {
                                                dtable.deleteColumn( (LimitedEntryBRLConditionColumn) c );
                                                refreshConditionsWidget();
                                            }
                                        }
                                    } );

        } else if ( c instanceof BRLConditionColumn ) {
            return new ImageButton( image,
                                    Constants.INSTANCE.RemoveThisConditionColumn(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent w) {
                                            if ( !canConditionBeDeleted( (BRLConditionColumn) c ) ) {
                                                Window.alert( Constants.INSTANCE.UnableToDeleteConditionColumn( c.getHeader() ) );
                                                return;
                                            }
                                            String cm = Constants.INSTANCE.DeleteConditionColumnWarning( c.getHeader() );
                                            if ( com.google.gwt.user.client.Window.confirm( cm ) ) {
                                                dtable.deleteColumn( (BRLConditionColumn) c );
                                                refreshConditionsWidget();
                                            }
                                        }
                                    } );

        }
        return new ImageButton( image,
                                Constants.INSTANCE.RemoveThisConditionColumn(),
                                new ClickHandler() {
                                    public void onClick(ClickEvent w) {
                                        if ( !canConditionBeDeleted( c ) ) {
                                            Window.alert( Constants.INSTANCE.UnableToDeleteConditionColumn( c.getHeader() ) );
                                            return;
                                        }
                                        String cm = Constants.INSTANCE.DeleteConditionColumnWarning( c.getHeader() );
                                        if ( com.google.gwt.user.client.Window.confirm( cm ) ) {
                                            dtable.deleteColumn( c );
                                            refreshConditionsWidget();
                                        }
                                    }
                                } );
    }

    private Widget getAttributes() {
        attributeConfigWidget = new VerticalPanel();
        refreshAttributeWidget();
        return attributeConfigWidget;
    }

    private void refreshAttributeWidget() {
        this.attributeConfigWidget.clear();
        if ( guidedDecisionTable.getMetadataCols().size() > 0 ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;&nbsp;" ) );
            hp.add( new SmallLabel( Constants.INSTANCE.Metadata1() ) );
            attributeConfigWidget.add( hp );
        }
        for ( MetadataCol52 atc : guidedDecisionTable.getMetadataCols() ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
            hp.add( new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );
            if ( !isReadOnly ) {
                hp.add( removeMeta( atc ) );
            }
            final SmallLabel label = makeColumnLabel( atc );
            hp.add( label );

            final MetadataCol52 at = atc;
            final CheckBox hide = new CheckBox( Constants.INSTANCE.HideThisColumn() );
            hide.setStyleName( "form-field" );
            hide.setValue( atc.isHideColumn() );
            hide.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent sender) {
                    at.setHideColumn( hide.getValue() );
                    dtable.setColumnVisibility( at,
                                                !at.isHideColumn() );
                    setColumnLabelStyleWhenHidden( label,
                                                   hide.getValue() );
                }
            } );
            hp.add( new HTML( "&nbsp;&nbsp;" ) );
            hp.add( hide );

            attributeConfigWidget.add( hp );
        }
        if ( guidedDecisionTable.getAttributeCols().size() > 0 ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;&nbsp;" ) );
            hp.add( new SmallLabel( Constants.INSTANCE.Attributes() ) );
            attributeConfigWidget.add( hp );
        }

        for ( AttributeCol52 atc : guidedDecisionTable.getAttributeCols() ) {
            final AttributeCol52 at = atc;
            HorizontalPanel hp = new HorizontalPanel();
            hp.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );

            hp.add( new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );
            if ( !isReadOnly ) {
                hp.add( removeAttr( at ) );
            }
            final SmallLabel label = makeColumnLabel( atc );
            hp.add( label );

            final TextBox defaultValue = new TextBox();
            final DTCellValue52 dcv = at.getDefaultValue();
            defaultValue.setStyleName( "form-field" );
            defaultValue.setText( (dcv == null ? "" : dcv.getStringValue()) );
            defaultValue.setEnabled( !isReadOnly );
            defaultValue.addChangeHandler( new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    at.setDefaultValue( new DTCellValue52( defaultValue.getText() ) );
                }
            } );

            if ( at.getAttribute().equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                hp.add( new HTML( "&nbsp;&nbsp;" ) );
                final CheckBox useRowNumber = new CheckBox( Constants.INSTANCE.UseRowNumber() );
                useRowNumber.setStyleName( "form-field" );
                useRowNumber.setValue( at.isUseRowNumber() );
                useRowNumber.setEnabled( !isReadOnly );
                hp.add( useRowNumber );

                hp.add( new SmallLabel( "(" ) );
                final CheckBox reverseOrder = new CheckBox( Constants.INSTANCE.ReverseOrder() );
                reverseOrder.setStyleName( "form-field" );
                reverseOrder.setValue( at.isReverseOrder() );
                reverseOrder.setEnabled( at.isUseRowNumber() && !isReadOnly );

                useRowNumber.addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent sender) {
                        at.setUseRowNumber( useRowNumber.getValue() );
                        reverseOrder.setEnabled( useRowNumber.getValue() );
                        dtable.updateSystemControlledColumnValues();
                    }
                } );

                reverseOrder.addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent sender) {
                        at.setReverseOrder( reverseOrder.getValue() );
                        dtable.updateSystemControlledColumnValues();
                    }
                } );
                hp.add( reverseOrder );
                hp.add( new SmallLabel( ")" ) );
            }
            hp.add( new HTML( "&nbsp;&nbsp;" ) );
            hp.add( new SmallLabel( Constants.INSTANCE.DefaultValue() ) );
            hp.add( defaultValue );

            final CheckBox hide = new CheckBox( Constants.INSTANCE.HideThisColumn() );
            hide.setStyleName( "form-field" );
            hide.setValue( at.isHideColumn() );
            hide.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent sender) {
                    at.setHideColumn( hide.getValue() );
                    dtable.setColumnVisibility( at,
                                                !at.isHideColumn() );
                    setColumnLabelStyleWhenHidden( label,
                                                   hide.getValue() );
                }
            } );
            hp.add( new HTML( "&nbsp;&nbsp;" ) );
            hp.add( hide );

            attributeConfigWidget.add( hp );
            setupColumnsNote();
        }

    }

    private SmallLabel makeColumnLabel(MetadataCol52 mdc) {
        SmallLabel label = new SmallLabel( mdc.getMetadata() );
        setColumnLabelStyleWhenHidden( label,
                                       mdc.isHideColumn() );
        return label;
    }

    private SmallLabel makeColumnLabel(AttributeCol52 ac) {
        SmallLabel label = new SmallLabel( ac.getAttribute() );
        setColumnLabelStyleWhenHidden( label,
                                       ac.isHideColumn() );
        return label;
    }

    private void setColumnLabelStyleWhenHidden(SmallLabel label,
                                               boolean isHidden) {
        if ( isHidden ) {
            label.addStyleName( DecisionTableResources.INSTANCE.style().columnLabelHidden() );
        } else {
            label.removeStyleName( DecisionTableResources.INSTANCE.style().columnLabelHidden() );
        }
    }

    private Widget removeAttr(final AttributeCol52 at) {
        Image image = DroolsGuvnorImages.INSTANCE.DeleteItemSmall();
        image.setAltText(Constants.INSTANCE.RemoveThisAttribute());

        return new ImageButton( image,
                                     Constants.INSTANCE.RemoveThisAttribute(),
                                     new ClickHandler() {
                                         public void onClick(ClickEvent w) {
                                             String ms = Constants.INSTANCE.DeleteActionColumnWarning( at.getAttribute() );
                                             if ( com.google.gwt.user.client.Window.confirm( ms ) ) {
                                                 dtable.deleteColumn( at );
                                                 refreshAttributeWidget();
                                             }
                                         }
                                     } );

    }

    private Widget removeMeta(final MetadataCol52 md) {
        Image image = DroolsGuvnorImages.INSTANCE.DeleteItemSmall();
        image.setAltText(Constants.INSTANCE.RemoveThisMetadata());

        return new ImageButton( image,
                                     Constants.INSTANCE.RemoveThisMetadata(),
                                     new ClickHandler() {
                                         public void onClick(ClickEvent w) {
                                             String ms = Constants.INSTANCE.DeleteActionColumnWarning( md.getMetadata() );
                                             if ( com.google.gwt.user.client.Window.confirm( ms ) ) {
                                                 dtable.deleteColumn( md );
                                                 refreshAttributeWidget();
                                             }
                                         }
                                     } );
    }

    private void setupColumnsNote() {
        configureColumnsNote.setVisible( guidedDecisionTable.getAttributeCols().size() == 0
                                         && guidedDecisionTable.getConditionsCount() == 0
                                         && guidedDecisionTable.getActionCols().size() == 0 );
    }

    private void setupDecisionTable() {

        //Get the current user's security context for audit logging. This requires a 
        //call to the server so instantiation of the Decision Table grid is deferred.
        dtable = new VerticalDecisionTableWidget(guidedDecisionTable,
                getSCE(),
                clientFactory.getIdentity(),
                isReadOnly,
                eventBus);
        dtable.setPixelSize(1000,
                400);
        dtableContainer.setWidget(dtable);

    }

    //Check if any of the bound Fact Patterns in the BRL Fragment are used elsewhere
    private boolean canConditionBeDeleted(BRLConditionColumn col) {
        for ( IPattern p : col.getDefinition() ) {
            if ( p instanceof FactPattern ) {
                FactPattern fp = (FactPattern) p;
                if ( fp.isBound() ) {
                    if ( isBindingUsed( fp.getBoundName() ) ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //Check if the Pattern to which the Condition relates is used elsewhere
    private boolean canConditionBeDeleted(ConditionCol52 col) {
        Pattern52 pattern = guidedDecisionTable.getPattern( col );
        if ( pattern.getChildColumns().size() > 1 ) {
            return true;
        }
        if ( isBindingUsed( pattern.getBoundName() ) ) {
            return false;
        }
        return true;
    }

    private boolean isBindingUsed(String binding) {
        return rm.isBoundFactUsed( binding );
    }

    public Set<String> getBindings(String className) {
        //For some reason, Fact Pattern data-types use the leaf name of the fully qualified Class Name 
        //whereas Fields use the fully qualified Class Name. We don't use the generic fieldType (see 
        //SuggestionCompletionEngine.TYPE) as we can't distinguish between different numeric types
        String simpleClassName = className;
        if ( simpleClassName != null && simpleClassName.lastIndexOf( "." ) > 0 ) {
            simpleClassName = simpleClassName.substring( simpleClassName.lastIndexOf( "." ) + 1 );
        }
        Set<String> bindings = new HashSet<String>();
        for ( Pattern52 p : this.guidedDecisionTable.getPatterns() ) {
            if ( className == null || p.getFactType().equals( simpleClassName ) ) {
                String binding = p.getBoundName();
                if ( !(binding == null || "".equals( binding )) ) {
                    bindings.add( binding );
                }
            }
            for ( ConditionCol52 c : p.getChildColumns() ) {
                if ( c.isBound() ) {
                    String fieldDataType = sce.getFieldClassName( p.getFactType(),
                                                                  c.getFactField() );
                    if ( fieldDataType.equals( className ) ) {
                        bindings.add( c.getBinding() );
                    }
                }
            }
        }
        return bindings;
    }

    public void insertColumn(BRLConditionColumn column) {
        dtable.addColumn( column );
        refreshConditionsWidget();
    }

    public void updateColumn(BRLConditionColumn originalColumn,
                             BRLConditionColumn editedColumn) {
        dtable.updateColumn( originalColumn,
                             editedColumn );
        refreshConditionsWidget();
    }

    public void insertColumn(LimitedEntryBRLConditionColumn column) {
        dtable.addColumn( column );
        refreshConditionsWidget();
    }

    public void updateColumn(LimitedEntryBRLConditionColumn originalColumn,
                             LimitedEntryBRLConditionColumn editedColumn) {
        dtable.updateColumn( originalColumn,
                             editedColumn );
        refreshConditionsWidget();
    }

    public void insertColumn(BRLActionColumn column) {
        dtable.addColumn( column );
        refreshActionsWidget();
    }

    public void updateColumn(BRLActionColumn originalColumn,
                             BRLActionColumn editedColumn) {
        dtable.updateColumn( originalColumn,
                             editedColumn );
        refreshActionsWidget();
    }

    public void insertColumn(LimitedEntryBRLActionColumn column) {
        dtable.addColumn( column );
        refreshActionsWidget();
    }

    public void updateColumn(LimitedEntryBRLActionColumn originalColumn,
                             LimitedEntryBRLActionColumn editedColumn) {
        dtable.updateColumn( originalColumn,
                             editedColumn );
        refreshActionsWidget();
    }

    public void onSave(SaveCommand saveCommand) {
        saveCommand.save();
    }

    public void onAfterSave() {
        // not needed.
    }

}
