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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.decisiontable.widget.DecisionTableControlsWidget;
import org.drools.guvnor.client.decisiontable.widget.VerticalDecisionTableWidget;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.EditorWidget;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.ruleeditor.SaveEventListener;
import org.drools.guvnor.client.util.AddButton;
import org.drools.guvnor.client.util.DecoratedDisclosurePanel;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.*;

/**
 * This is the new guided decision table editor for the web.
 */
public class GuidedDecisionTableWidget extends Composite
        implements
        SaveEventListener,
        EditorWidget {

    private Constants                   constants = GWT.create( Constants.class );
    private static Images               images    = GWT.create( Images.class );

    private GuidedDecisionTable52       guidedDecisionTable;
    private VerticalPanel               layout;
    private PrettyFormLayout            configureColumnsNote;
    private VerticalPanel               attributeConfigWidget;
    private VerticalPanel               conditionsConfigWidget;
    private String                      packageName;
    private VerticalPanel               actionsConfigWidget;
    private SuggestionCompletionEngine  sce;

    private VerticalDecisionTableWidget dtable;

    public GuidedDecisionTableWidget(RuleAsset asset,
                                      RuleViewer viewer,
                                      ClientFactory clientFactory,
                                      EventBus eventBus) {
        this( asset );
    }

    public GuidedDecisionTableWidget(RuleAsset asset) {

        this.guidedDecisionTable = (GuidedDecisionTable52) asset.getContent();
        this.packageName = asset.getMetaData().getPackageName();
        this.guidedDecisionTable.setTableName( asset.getName() );

        layout = new VerticalPanel();

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

        setupDecisionTable();

        initWidget( layout );
    }

    private Widget getActions() {
        actionsConfigWidget = new VerticalPanel();
        refreshActionsWidget();
        return actionsConfigWidget;
    }

    private void refreshActionsWidget() {
        this.actionsConfigWidget.clear();
        for ( ActionCol52 c : guidedDecisionTable.getActionCols() ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( removeAction( c ) );
            hp.add( editAction( c ) );
            hp.add( new SmallLabel( c.getHeader() ) );
            actionsConfigWidget.add( hp );
        }
        actionsConfigWidget.add( newAction() );
        setupColumnsNote();
    }

    private Widget editAction(final ActionCol52 c) {
        return new ImageButton( images.edit(),
                                constants.EditThisActionColumnConfiguration(),
                                new ClickHandler() {
                                    public void onClick(ClickEvent w) {
                                        if ( c instanceof ActionSetFieldCol52 ) {
                                            final ActionSetFieldCol52 asf = (ActionSetFieldCol52) c;
                                            ActionSetColumn ed = new ActionSetColumn( getSCE(),
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
                                        } else if ( c instanceof ActionInsertFactCol52 ) {
                                            final ActionInsertFactCol52 asf = (ActionInsertFactCol52) c;
                                            ActionInsertColumn ed = new ActionInsertColumn(
                                                                                            getSCE(),
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

                final ListBox choice = new ListBox();
                choice.addItem( constants.SetTheValueOfAField(),
                                "set" );
                choice.addItem( constants.SetTheValueOfAFieldOnANewFact(),
                                "insert" );
                Button ok = new Button( "OK" );
                ok.addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent w) {
                        String s = choice.getValue( choice.getSelectedIndex() );
                        if ( s.equals( "set" ) ) {
                            showSet();
                        } else if ( s.equals( "insert" ) ) {
                            showInsert();
                        }
                        pop.hide();
                    }

                    private void showInsert() {
                        final ActionInsertFactCol52 afc = makeNewActionInsertColumn();
                        ActionInsertColumn ins = new ActionInsertColumn(
                                                                         getSCE(),
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
                        ActionSetColumn set = new ActionSetColumn( getSCE(),
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

                    private void newActionAdded(ActionCol52 column) {
                        dtable.addColumn( column );
                        dtable.scrapeColumns();
                        refreshActionsWidget();
                    }
                } );
                pop.addAttribute( constants.TypeOfActionColumn(),
                                  choice );
                pop.addAttribute( "",
                                  ok );
                pop.show();
            }

        } );

        return addButton;
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

    private Widget removeAction(final ActionCol52 c) {
        Image del = new ImageButton( images.deleteItemSmall(),
                                     constants.RemoveThisActionColumn(),
                                     new ClickHandler() {
                                         public void onClick(ClickEvent w) {
                                             String cm = constants.DeleteActionColumnWarning( c.getHeader() );
                                             if ( com.google.gwt.user.client.Window.confirm( cm ) ) {
                                                 dtable.deleteColumn( c );
                                                 dtable.scrapeColumns();
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
        for ( Pattern52 p : guidedDecisionTable.getConditionPatterns() ) {
            for ( ConditionCol52 c : p.getConditions() ) {
                HorizontalPanel hp = new HorizontalPanel();
                hp.add( removeCondition( c ) );
                hp.add( editCondition( c ) );
                hp.add( new SmallLabel( c.getHeader() ) );
                conditionsConfigWidget.add( hp );
            }
        }
        conditionsConfigWidget.add( newCondition() );
        setupColumnsNote();
    }

    private Widget newCondition() {
        final ConditionCol52 newCol = makeNewConditionColumn();
        newCol.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        AddButton addButton = new AddButton();
        addButton.setText( constants.NewColumn() );
        addButton.setTitle( constants.AddANewConditionColumn() );
        addButton.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                GuidedDTColumnConfig dialog = new GuidedDTColumnConfig(
                                                                        getSCE(),
                                                                        guidedDecisionTable,
                                                                        new ConditionColumnCommand() {
                                                                            public void execute(Pattern52 pattern,
                                                                                                ConditionCol52 column) {

                                                                                //Add pattern to model, if applicable
                                                                                if ( !guidedDecisionTable.getConditionPatterns().contains( pattern ) ) {
                                                                                    guidedDecisionTable.getConditionPatterns().add( pattern );
                                                                                }

                                                                                //Add new column to pattern
                                                                                pattern.getConditions().add( column );

                                                                                //Update UI
                                                                                dtable.addColumn( column );
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

    private Widget editCondition(final ConditionCol52 origCol) {
        return new ImageButton( images.edit(),
                                constants.EditThisColumnsConfiguration(),
                                new ClickHandler() {
                                    public void onClick(ClickEvent w) {
                                        GuidedDTColumnConfig dialog = new GuidedDTColumnConfig(
                                                                                                getSCE(),
                                                                                                guidedDecisionTable,
                                                                                                new ConditionColumnCommand() {
                                                                                                    public void execute(Pattern52 pattern,
                                                                                                                        ConditionCol52 column) {

                                                                                                        //Add pattern to model, if applicable
                                                                                                        if ( !guidedDecisionTable.getConditionPatterns().contains( pattern ) ) {
                                                                                                            guidedDecisionTable.getConditionPatterns().add( pattern );
                                                                                                        }

                                                                                                        //Move column from original pattern to new pattern, if applicable
                                                                                                        Pattern52 origPattern = guidedDecisionTable.getPattern( origCol );
                                                                                                        if ( !origPattern.getBoundName().equals( pattern.getBoundName() ) ) {
                                                                                                            origPattern.getConditions().remove( origCol );
                                                                                                            if ( origPattern.getConditions().size() == 0 ) {
                                                                                                                guidedDecisionTable.getConditionPatterns().remove( origPattern );
                                                                                                            }
                                                                                                            pattern.getConditions().add( column );
                                                                                                        }

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

                                                 //Remove condition from pattern
                                                 Pattern52 origPattern = guidedDecisionTable.getPattern( origCol );
                                                 origPattern.getConditions().remove( origCol );

                                                 //Remove pattern if it contains zero conditions
                                                 if ( origPattern.getConditions().size() == 0 ) {
                                                     guidedDecisionTable.getConditionPatterns().remove( origPattern );
                                                 }

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
            hp.add( new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );
            hp.add( removeMeta( atc ) );
            hp.add( new SmallLabel( atc.getMetadata() ) );

            final MetadataCol52 at = atc;
            final CheckBox hide = new CheckBox();
            hide.setValue( atc.isHideColumn() );
            hide.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent sender) {
                    at.setHideColumn( hide.getValue() );
                    dtable.setColumnVisibility( at,
                                                !at.isHideColumn() );
                }
            } );
            hp.add( new HTML( "&nbsp;&nbsp;" ) );
            hp.add( hide );
            hp.add( new SmallLabel( constants.HideThisColumn() ) );

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

            hp.add( new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );
            hp.add( removeAttr( at ) );
            hp.add( new SmallLabel( at.getAttribute() ) );

            final TextBox defaultValue = new TextBox();
            defaultValue.setText( at.getDefaultValue() );
            defaultValue.addChangeHandler( new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    at.setDefaultValue( defaultValue.getText() );
                }
            } );

            if ( at.getAttribute().equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                hp.add( new HTML( "&nbsp;&nbsp;" ) );
                final CheckBox useRowNumber = new CheckBox();
                useRowNumber.setValue( at.isUseRowNumber() );

                hp.add( useRowNumber );
                hp.add( new SmallLabel( constants.UseRowNumber() ) );
                hp.add( new SmallLabel( "(" ) );
                final CheckBox reverseOrder = new CheckBox();
                reverseOrder.setValue( at.isReverseOrder() );
                reverseOrder.setEnabled( at.isUseRowNumber() );

                useRowNumber.addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent sender) {
                        at.setUseRowNumber( useRowNumber.getValue() );
                        reverseOrder.setEnabled( useRowNumber.getValue() );
                        dtable.updateSystemControlledColumnValues();
                        dtable.redrawSystemControlledColumns();
                    }
                } );

                reverseOrder.addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent sender) {
                        at.setReverseOrder( reverseOrder.getValue() );
                        dtable.updateSystemControlledColumnValues();
                        dtable.redrawSystemControlledColumns();
                    }
                } );
                hp.add( reverseOrder );
                hp.add( new SmallLabel( constants.ReverseOrder() ) );
                hp.add( new SmallLabel( ")" ) );
            }
            hp.add( new HTML( "&nbsp;&nbsp;" ) );
            hp.add( new SmallLabel( constants.DefaultValue() ) );
            hp.add( defaultValue );

            final CheckBox hide = new CheckBox();
            hide.setValue( at.isHideColumn() );
            hide.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent sender) {
                    at.setHideColumn( hide.getValue() );
                    dtable.setColumnVisibility( at,
                                                !at.isHideColumn() );
                }
            } );
            hp.add( new HTML( "&nbsp;&nbsp;" ) );
            hp.add( hide );
            hp.add( new SmallLabel( constants.HideThisColumn() ) );

            attributeConfigWidget.add( hp );
            setupColumnsNote();
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

                                                   // Remove any attributes
                                                   // already added
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
                                                           dtable.scrapeColumns();
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
                                                           dtable.scrapeColumns();
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
                                                 dtable.scrapeColumns();
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
                                                 dtable.scrapeColumns();
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
        if ( dtable == null ) {
            dtable = new VerticalDecisionTableWidget( new DecisionTableControlsWidget(),
                                                      getSCE() );
            dtable.setPixelSize( 1000,
                                 400 );
            dtable.setModel( guidedDecisionTable );
        }
        layout.add( dtable );
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
            }
        }
        return true;
    }

    /**
     * Need to copy the data from the Decision Table
     */
    public void onSave() {
        dtable.scrapeData();
    }

    public void onAfterSave() {
        // not needed.
    }

}
