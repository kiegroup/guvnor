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
import org.drools.guvnor.client.asseteditor.SaveEventListener;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.IBindingProvider;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.decisiontable.widget.DecisionTableControlsWidget;
import org.drools.guvnor.client.decisiontable.widget.VerticalDecisionTableWidget;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.util.AddButton;
import org.drools.guvnor.client.util.DecoratedDisclosurePanel;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
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
        IBindingProvider {

    private Constants                   constants = GWT.create( Constants.class );
    private static Images               images    = GWT.create( Images.class );

    private GuidedDecisionTable52       guidedDecisionTable;
    private VerticalPanel               layout;
    private PrettyFormLayout            configureColumnsNote;
    private VerticalPanel               attributeConfigWidget;
    private VerticalPanel               conditionsConfigWidget;
    private String                      packageName;
    private String                      packageUUID;
    private VerticalPanel               actionsConfigWidget;
    private SuggestionCompletionEngine  sce;

    private VerticalDecisionTableWidget dtable;
    private EventBus                    eventBus;
    private ClientFactory               clientFactory;

    private enum ActionTypes {
        UPDATE_FACT_FIELD,
        INSERT_FACT_FIELD,
        RETRACT_FACT,
        WORKITEM,
        WORKITEM_UPDATE_FACT_FIELD,
        WORKITEM_INSERT_FACT_FIELD
    }

    public GuidedDecisionTableWidget(RuleAsset asset,
                                     RuleViewer viewer,
                                     ClientFactory clientFactory,
                                     EventBus eventBus) {

        this.guidedDecisionTable = (GuidedDecisionTable52) asset.getContent();
        this.guidedDecisionTable.initAnalysisColumn();
        this.packageName = asset.getMetaData().getPackageName();
        this.packageUUID = asset.getMetaData().getPackageUUID();
        this.guidedDecisionTable.setTableName( asset.getName() );
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;

        layout = new VerticalPanel();

        setupDecisionTable();

        configureColumnsNote = new PrettyFormLayout();
        configureColumnsNote.startSection();
        configureColumnsNote.addRow( new HTML( AbstractImagePrototype.create( images.information() ).getHTML()
                                               + "&nbsp;"
                                               + constants.ConfigureColumnsNote() ) );
        configureColumnsNote.endSection();

        DecoratedDisclosurePanel disclosurePanel = new DecoratedDisclosurePanel( constants.DecisionTable() );
        disclosurePanel.setWidth( "100%" );
        disclosurePanel.setTitle( constants.DecisionTable() );

        VerticalPanel config = new VerticalPanel();
        config.setWidth( "100%" );
        disclosurePanel.add( config );

        DecoratedDisclosurePanel conditions = new DecoratedDisclosurePanel( constants.ConditionColumns() );
        conditions.setOpen( false );
        conditions.setWidth( "75%" );
        conditions.add( getConditions() );
        config.add( conditions );

        DecoratedDisclosurePanel actions = new DecoratedDisclosurePanel( constants.ActionColumns() );
        actions.setOpen( false );
        actions.setWidth( "75%" );
        actions.add( getActions() );
        config.add( actions );

        DecoratedDisclosurePanel options = new DecoratedDisclosurePanel( constants.Options() );
        options.setOpen( false );
        options.setWidth( "75%" );
        options.add( getAttributes() );
        config.add( options );

        layout.add( disclosurePanel );
        layout.add( configureColumnsNote );
        layout.add( dtable );

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
        boolean bAreActionsDraggable = actions.size() > 1;
        for ( ActionCol52 c : actions ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( removeAction( c ) );
            hp.add( editAction( c ) );
            Label actionLabel = makeColumnLabel( c );
            hp.add( actionLabel );
            actionsPanel.add( hp );
            if ( bAreActionsDraggable ) {
                actionsDragController.makeDraggable( hp,
                                                     actionLabel );
            }

        }
        actionsConfigWidget.add( newAction() );
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
        return new ImageButton( images.edit(),
                                constants.EditThisActionColumnConfiguration(),
                                new ClickHandler() {
                                    public void onClick(ClickEvent w) {
                                        if ( c instanceof ActionWorkItemSetFieldCol52 ) {
                                            final ActionWorkItemSetFieldCol52 awisf = (ActionWorkItemSetFieldCol52) c;
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
                                                                                                                       false );
                                            ed.show();

                                        } else if ( c instanceof ActionSetFieldCol52 ) {
                                            final ActionSetFieldCol52 asf = (ActionSetFieldCol52) c;
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
                                                                                                false );
                                            ed.show();

                                        } else if ( c instanceof ActionWorkItemInsertFactCol52 ) {
                                            final ActionWorkItemInsertFactCol52 awiif = (ActionWorkItemInsertFactCol52) c;
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
                                                                                                                  false );
                                            ed.show();

                                        } else if ( c instanceof ActionInsertFactCol52 ) {
                                            final ActionInsertFactCol52 asf = (ActionInsertFactCol52) c;
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
                                                                                                  false );
                                            ed.show();

                                        } else if ( c instanceof ActionRetractFactCol52 ) {
                                            final ActionRetractFactCol52 arf = (ActionRetractFactCol52) c;
                                            ActionRetractFactPopup ed = new ActionRetractFactPopup( guidedDecisionTable,
                                                                                                    new GenericColumnCommand() {
                                                                                                        public void execute(DTColumnConfig52 column) {
                                                                                                            dtable.updateColumn( arf,
                                                                                                                                 (ActionRetractFactCol52) column );
                                                                                                            refreshActionsWidget();
                                                                                                        }
                                                                                                    },
                                                                                                    arf,
                                                                                                    false );
                                            ed.show();

                                        } else if ( c instanceof ActionWorkItemCol52 ) {
                                            final ActionWorkItemCol52 awi = (ActionWorkItemCol52) c;
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
                                                                                                 false );
                                            popup.show();

                                        }

                                    }
                                } );

    }

    private Widget newAction() {
        AddButton addButton = new AddButton();
        addButton.setText( constants.NewColumn() );
        addButton.setTitle( constants.CreateANewActionColumn() );

        addButton.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                final FormStylePopup pop = new FormStylePopup();
                pop.setModal( false );

                //List of basic Action types
                final ListBox choice = new ListBox();
                choice.addItem( constants.SetTheValueOfAField(),
                                ActionTypes.UPDATE_FACT_FIELD.name() );
                choice.addItem( constants.SetTheValueOfAFieldOnANewFact(),
                                ActionTypes.INSERT_FACT_FIELD.name() );
                choice.addItem( constants.RetractAnExistingFact(),
                                ActionTypes.RETRACT_FACT.name() );

                //Checkbox to include Advanced Action types
                final CheckBox chkIncludeAdvancedOptions = new CheckBox( SafeHtmlUtils.fromString( constants.IncludeAdvancedOptions() ) );
                chkIncludeAdvancedOptions.setValue( false );
                chkIncludeAdvancedOptions.addClickHandler( new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        if ( chkIncludeAdvancedOptions.getValue() ) {
                            addItem( constants.WorkItemAction(),
                                     ActionTypes.WORKITEM.name() );
                            addItem( constants.WorkItemActionSetField(),
                                     ActionTypes.WORKITEM_UPDATE_FACT_FIELD.name() );
                            addItem( constants.WorkItemActionInsertFact(),
                                     ActionTypes.WORKITEM_INSERT_FACT_FIELD.name() );
                        } else {
                            removeItem( ActionTypes.WORKITEM.name() );
                            removeItem( ActionTypes.WORKITEM_UPDATE_FACT_FIELD.name() );
                            removeItem( ActionTypes.WORKITEM_INSERT_FACT_FIELD.name() );
                        }
                        pop.center();
                    }

                    private void addItem(String item,
                                         String value) {
                        for ( int index = 0; index < choice.getItemCount(); index++ ) {
                            if ( choice.getValue( index ).equals( value ) ) {
                                return;
                            }
                        }
                        choice.addItem( item,
                                        value );
                    }

                    private void removeItem(String value) {
                        for ( int index = 0; index < choice.getItemCount(); index++ ) {
                            if ( choice.getValue( index ).equals( value ) ) {
                                choice.removeItem( index );
                                break;
                            }
                        }
                    }

                } );

                //OK button to create column
                Button ok = new Button( "OK" );
                ok.addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent w) {
                        String s = choice.getValue( choice.getSelectedIndex() );
                        if ( s.equals( ActionTypes.UPDATE_FACT_FIELD.name() ) ) {
                            showSet();
                        } else if ( s.equals( ActionTypes.INSERT_FACT_FIELD.name() ) ) {
                            showInsert();
                        } else if ( s.equals( ActionTypes.RETRACT_FACT.name() ) ) {
                            showRetract();
                        } else if ( s.equals( ActionTypes.WORKITEM.name() ) ) {
                            showWorkItemAction();
                        } else if ( s.equals( ActionTypes.WORKITEM_UPDATE_FACT_FIELD.name() ) ) {
                            showWorkItemActionSet();
                        } else if ( s.equals( ActionTypes.WORKITEM_INSERT_FACT_FIELD.name() ) ) {
                            showWorkItemActionInsert();
                        }
                        pop.hide();
                    }

                    private void showInsert() {
                        final ActionInsertFactCol52 afc = makeNewActionInsertColumn();
                        ActionInsertFactPopup ins = new ActionInsertFactPopup( getSCE(),
                                                                                   guidedDecisionTable,
                                                                                   new GenericColumnCommand() {
                                                                                       public void execute(DTColumnConfig52 column) {
                                                                                           newActionAdded( (ActionCol52) column );
                                                                                       }
                                                                                   },
                                                                                   afc,
                                                                                   true );
                        ins.show();
                    }

                    private void showSet() {
                        final ActionSetFieldCol52 afc = makeNewActionSetColumn();
                        ActionSetFieldPopup set = new ActionSetFieldPopup( getSCE(),
                                                                             guidedDecisionTable,
                                                                             new GenericColumnCommand() {
                                                                                 public void execute(DTColumnConfig52 column) {
                                                                                     newActionAdded( (ActionCol52) column );
                                                                                 }
                                                                             },
                                                                             afc,
                                                                             true );
                        set.show();
                    }

                    private void showRetract() {
                        final ActionRetractFactCol52 arf = makeNewActionRetractFact();
                        ActionRetractFactPopup popup = new ActionRetractFactPopup( guidedDecisionTable,
                                                                                   new GenericColumnCommand() {
                                                                                       public void execute(DTColumnConfig52 column) {
                                                                                           newActionAdded( (ActionCol52) column );
                                                                                       }
                                                                                   },
                                                                                   arf,
                                                                                   true );
                        popup.show();
                    }

                    private void showWorkItemAction() {
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
                                                                             true );
                        popup.show();
                    }

                    private void showWorkItemActionSet() {
                        final ActionWorkItemSetFieldCol52 awisf = makeNewActionWorkItemSetField();
                        ActionWorkItemSetFieldPopup popup = new ActionWorkItemSetFieldPopup( getSCE(),
                                                                                               guidedDecisionTable,
                                                                                               new GenericColumnCommand() {
                                                                                                   public void execute(DTColumnConfig52 column) {
                                                                                                       newActionAdded( (ActionCol52) column );
                                                                                                   }
                                                                                               },
                                                                                               awisf,
                                                                                               true );
                        popup.show();
                    }

                    private void showWorkItemActionInsert() {
                        final ActionWorkItemInsertFactCol52 awiif = makeNewActionWorkItemInsertFact();
                        ActionWorkItemInsertFactPopup popup = new ActionWorkItemInsertFactPopup( getSCE(),
                                                                                                     guidedDecisionTable,
                                                                                                     new GenericColumnCommand() {
                                                                                                         public void execute(DTColumnConfig52 column) {
                                                                                                             newActionAdded( (ActionCol52) column );
                                                                                                         }
                                                                                                     },
                                                                                                     awiif,
                                                                                                     true );
                        popup.show();
                    }

                    private void newActionAdded(ActionCol52 column) {
                        dtable.addColumn( column );
                        refreshActionsWidget();
                    }
                } );
                pop.addAttribute( constants.TypeOfActionColumn(),
                                  choice );
                pop.addAttribute( "",
                                  chkIncludeAdvancedOptions );
                pop.addAttribute( "",
                                  ok );
                pop.show();
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
                ///Actions setting Field Values from Work Item Result Parameters are always boolean (i.e. Limited Entry) in the table
                return new ActionWorkItemSetFieldCol52();
            }

            private ActionWorkItemInsertFactCol52 makeNewActionWorkItemInsertFact() {
                ///Actions setting Field Values from Work Item Result Parameters are always boolean (i.e. Limited Entry) in the table
                return new ActionWorkItemInsertFactCol52();
            }

        } );

        return addButton;
    }

    private Widget removeAction(final ActionCol52 c) {
        Image del = new ImageButton( images.deleteItemSmall(),
                                     constants.RemoveThisActionColumn(),
                                     new ClickHandler() {
                                         public void onClick(ClickEvent w) {
                                             String cm = constants.DeleteActionColumnWarning( c.getHeader() );
                                             if ( com.google.gwt.user.client.Window.confirm( cm ) ) {
                                                 dtable.deleteColumn( c );
                                                 refreshActionsWidget();
                                             }
                                         }
                                     } );

        return del;
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

        List<Pattern52> patterns = guidedDecisionTable.getConditionPatterns();
        boolean arePatternsDraggable = patterns.size() > 1;
        for ( Pattern52 p : patterns ) {

            VerticalPanel patternPanel = new VerticalPanel();
            VerticalPanel conditionsPanel = new VerticalPanel();
            HorizontalPanel patternHeaderPanel = new HorizontalPanel();
            Label patternLabel = makePatternLabel( p );
            patternHeaderPanel.add( patternLabel );
            patternHeaderPanel.setStylePrimaryName( DecisionTableResources.INSTANCE.style().patternSectionHeader() );
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

            List<ConditionCol52> conditions = p.getConditions();
            boolean bAreConditionsDraggable = conditions.size() > 1;
            for ( ConditionCol52 c : p.getConditions() ) {
                HorizontalPanel hp = new HorizontalPanel();
                hp.add( removeCondition( c ) );
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
            //End of new Widget code

        }
        conditionsConfigWidget.add( newCondition() );
        setupColumnsNote();
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
        StringBuilder sb = new StringBuilder();
        if ( p.getBoundName() != null && !p.getBoundName().equals( "" ) ) {
            sb.append( p.getBoundName() );
            sb.append( " : " );
        }
        sb.append( p.getFactType() );
        return new Label( sb.toString() );
    }

    private Widget newCondition() {
        final ConditionCol52 newCol = makeNewConditionColumn();
        newCol.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        AddButton addButton = new AddButton();
        addButton.setText( constants.NewColumn() );
        addButton.setTitle( constants.AddANewConditionColumn() );
        addButton.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
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
                                                            newCol,
                                                            true );
                dialog.show();
            }
        } );
        return addButton;
    }

    private ConditionCol52 makeNewConditionColumn() {
        switch ( guidedDecisionTable.getTableFormat() ) {
            case LIMITED_ENTRY :
                return new LimitedEntryConditionCol52();
            default :
                return new ConditionCol52();
        }
    }

    private Widget editCondition(final Pattern52 origPattern,
                                 final ConditionCol52 origCol) {
        return new ImageButton( images.edit(),
                                constants.EditThisColumnsConfiguration(),
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
                                                                                    false );
                                        dialog.show();
                                    }
                                } );
    }

    private SuggestionCompletionEngine getSCE() {
        if ( sce == null ) {
            this.sce = SuggestionCompletionCache.getInstance()
                    .getEngineFromCache( this.packageName );
        }
        return sce;
    }

    private Widget removeCondition(final ConditionCol52 origCol) {
        Image del = new ImageButton( images.deleteItemSmall(),
                                     constants.RemoveThisConditionColumn(),
                                     new ClickHandler() {
                                         public void onClick(ClickEvent w) {
                                             if ( !canConditionBeDeleted( origCol ) ) {
                                                 Window.alert( constants.UnableToDeleteConditionColumn( origCol.getHeader() ) );
                                                 return;
                                             }

                                             String cm = constants.DeleteConditionColumnWarning( origCol.getHeader() );
                                             if ( com.google.gwt.user.client.Window.confirm( cm ) ) {

                                                 //Update UI
                                                 dtable.deleteColumn( origCol );
                                                 refreshConditionsWidget();
                                             }
                                         }
                                     } );

        return del;
    }

    private Widget getAttributes() {
        attributeConfigWidget = new VerticalPanel();
        refreshAttributeWidget();
        return attributeConfigWidget;
    }

    private void refreshAttributeWidget() {
        this.attributeConfigWidget.clear();
        attributeConfigWidget.add( newAttr() );
        if ( guidedDecisionTable.getMetadataCols().size() > 0 ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;&nbsp;" ) );
            hp.add( new SmallLabel( constants.Metadata1() ) );
            attributeConfigWidget.add( hp );
        }
        for ( MetadataCol52 atc : guidedDecisionTable.getMetadataCols() ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
            hp.add( new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );
            hp.add( removeMeta( atc ) );
            final SmallLabel label = makeColumnLabel( atc );
            hp.add( label );

            final MetadataCol52 at = atc;
            final CheckBox hide = new CheckBox( constants.HideThisColumn() );
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
            hp.add( new SmallLabel( constants.Attributes() ) );
            attributeConfigWidget.add( hp );
        }

        for ( AttributeCol52 atc : guidedDecisionTable.getAttributeCols() ) {
            final AttributeCol52 at = atc;
            HorizontalPanel hp = new HorizontalPanel();
            hp.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );

            hp.add( new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );
            hp.add( removeAttr( at ) );
            final SmallLabel label = makeColumnLabel( atc );
            hp.add( label );

            final TextBox defaultValue = new TextBox();
            defaultValue.setStyleName( "form-field" );
            defaultValue.setText( at.getDefaultValue() );
            defaultValue.addChangeHandler( new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    at.setDefaultValue( defaultValue.getText() );
                }
            } );

            if ( at.getAttribute().equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                hp.add( new HTML( "&nbsp;&nbsp;" ) );
                final CheckBox useRowNumber = new CheckBox( constants.UseRowNumber() );
                useRowNumber.setStyleName( "form-field" );
                useRowNumber.setValue( at.isUseRowNumber() );
                hp.add( useRowNumber );

                hp.add( new SmallLabel( "(" ) );
                final CheckBox reverseOrder = new CheckBox( constants.ReverseOrder() );
                reverseOrder.setStyleName( "form-field" );
                reverseOrder.setValue( at.isReverseOrder() );
                reverseOrder.setEnabled( at.isUseRowNumber() );

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
            hp.add( new SmallLabel( constants.DefaultValue() ) );
            hp.add( defaultValue );

            final CheckBox hide = new CheckBox( constants.HideThisColumn() );
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

    private Widget newAttr() {
        ImageButton but = new ImageButton( images.newItem(),
                                           constants.AddANewAttributeMetadata(),
                                           new ClickHandler() {
                                               public void onClick(ClickEvent w) {

                                                   // show choice of attributes
                                                   final FormStylePopup pop = new FormStylePopup( images.config(),
                                                                                                  constants.AddAnOptionToTheRule() );
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

                                                   final Image addbutton = new ImageButton( images.newItem() );
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

                                                   addbutton.setTitle( constants.AddMetadataToTheRule() );

                                                   addbutton.addClickHandler( new ClickHandler() {
                                                       public void onClick(ClickEvent w) {

                                                           String metadata = box.getText();
                                                           if ( !isUnique( metadata ) ) {
                                                               Window.alert( constants.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
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

                                                   pop.addAttribute( constants.Metadata1(),
                                                                     horiz );
                                                   pop.addAttribute( constants.Attribute(),
                                                                     list );
                                                   pop.show();
                                               }

                                           } );
        HorizontalPanel h = new HorizontalPanel();
        h.add( new SmallLabel( constants.AddAttributeMetadata() ) );
        h.add( but );
        return h;
    }

    private Widget removeAttr(final AttributeCol52 at) {
        Image del = new ImageButton( images.deleteItemSmall(),
                                     constants.RemoveThisAttribute(),
                                     new ClickHandler() {
                                         public void onClick(ClickEvent w) {
                                             String ms = constants.DeleteActionColumnWarning( at.getAttribute() );
                                             if ( com.google.gwt.user.client.Window.confirm( ms ) ) {
                                                 dtable.deleteColumn( at );
                                                 refreshAttributeWidget();
                                             }
                                         }
                                     } );

        return del;
    }

    private Widget removeMeta(final MetadataCol52 md) {
        Image del = new ImageButton( images.deleteItemSmall(),
                                     constants.RemoveThisMetadata(),
                                     new ClickHandler() {
                                         public void onClick(ClickEvent w) {
                                             String ms = constants.DeleteActionColumnWarning( md.getMetadata() );
                                             if ( com.google.gwt.user.client.Window.confirm( ms ) ) {
                                                 dtable.deleteColumn( md );
                                                 refreshAttributeWidget();
                                             }
                                         }
                                     } );

        return del;
    }

    private void setupColumnsNote() {
        configureColumnsNote.setVisible( guidedDecisionTable.getAttributeCols().size() == 0
                                         && guidedDecisionTable.getConditionsCount() == 0
                                         && guidedDecisionTable.getActionCols().size() == 0 );
    }

    private void setupDecisionTable() {
        dtable = new VerticalDecisionTableWidget( new DecisionTableControlsWidget(),
                                                  getSCE(),
                                                  eventBus );
        dtable.setPixelSize( 1000,
                             400 );
        dtable.setModel( guidedDecisionTable );
    }

    //A Condition column cannot be deleted if it is the only column for a bound pattern 
    //used by an Action. i.e. the dependent Actions should be deleted first.
    private boolean canConditionBeDeleted(ConditionCol52 col) {
        Pattern52 pattern = guidedDecisionTable.getPattern( col );
        if ( pattern.getConditions().size() > 1 ) {
            return true;
        }
        for ( ActionCol52 ac : guidedDecisionTable.getActionCols() ) {
            if ( ac instanceof ActionSetFieldCol52 ) {
                ActionSetFieldCol52 asfc = (ActionSetFieldCol52) ac;
                if ( asfc.getBoundName().equals( pattern.getBoundName() ) ) {
                    return false;
                }
            } else if ( ac instanceof ActionRetractFactCol52 ) {

                if ( ac instanceof LimitedEntryActionRetractFactCol52 ) {

                    //Check whether Limited Entry retraction is bound to Pattern
                    LimitedEntryActionRetractFactCol52 ler = (LimitedEntryActionRetractFactCol52) ac;
                    if ( ler.getValue().getStringValue().equals( pattern.getBoundName() ) ) {
                        return false;
                    }
                } else {

                    //Check whether data for column contains Pattern binding
                    int iCol = guidedDecisionTable.getAllColumns().indexOf( ac );
                    for ( List<DTCellValue52> row : guidedDecisionTable.getData() ) {
                        DTCellValue52 dcv = row.get( iCol );
                        if ( dcv != null && pattern.getBoundName().equals( dcv.getStringValue() ) ) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
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
        for ( Pattern52 p : this.guidedDecisionTable.getConditionPatterns() ) {
            if ( className == null || p.getFactType().equals( simpleClassName ) ) {
                String binding = p.getBoundName();
                if ( !(binding == null || "".equals( binding )) ) {
                    bindings.add( binding );
                }
            }
            for ( ConditionCol52 c : p.getConditions() ) {
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

    public void onSave() {
        // not needed.
    }

    public void onAfterSave() {
        // not needed.
    }

}
