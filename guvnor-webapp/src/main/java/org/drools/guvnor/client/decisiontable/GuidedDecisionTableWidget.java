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

import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.decisiontable.widget.DecisionTableControlsWidget;
import org.drools.guvnor.client.decisiontable.widget.VerticalDecisionTableWidget;
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
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.drools.ide.common.client.modeldriven.dt.TypeSafeGuidedDecisionTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
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
    EditorWidget {

    private Constants                   constants = GWT.create( Constants.class );
    private static Images               images    = GWT.create( Images.class );

    private TypeSafeGuidedDecisionTable guidedDecisionTable;
    private VerticalPanel               layout;
    private PrettyFormLayout            configureColumnsNote;
    private VerticalPanel               attributeConfigWidget;
    private VerticalPanel               conditionsConfigWidget;
    private String                      packageName;
    private VerticalPanel               actionsConfigWidget;
    private SuggestionCompletionEngine  sce;

    private VerticalDecisionTableWidget dtable;

    public GuidedDecisionTableWidget(RuleAsset asset,
                                     RuleViewer viewer) {
        this( asset );
    }

    public GuidedDecisionTableWidget(RuleAsset asset) {

        this.guidedDecisionTable = (TypeSafeGuidedDecisionTable) asset.getContent();
        this.packageName = asset.getMetaData().getPackageName();
        this.guidedDecisionTable.setTableName( asset.getName() );

        layout = new VerticalPanel();

        configureColumnsNote = new PrettyFormLayout();
        configureColumnsNote.startSection();
        configureColumnsNote.addRow( new HTML( "<img src='"
                                               + new Image( images.information() ).getUrl()
                                               + "'/>&nbsp;"
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
        for ( ActionCol c : guidedDecisionTable.getActionCols() ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( removeAction( c ) );
            hp.add( editAction( c ) );
            hp.add( new SmallLabel( c.getHeader() ) );
            actionsConfigWidget.add( hp );
        }
        actionsConfigWidget.add( newAction() );
        setupColumnsNote();
    }

    private Widget editAction(final ActionCol c) {
        return new ImageButton( images.edit(),
                                constants.EditThisActionColumnConfiguration(),
                                new ClickHandler() {
                                    public void onClick(ClickEvent w) {
                                        if ( c instanceof ActionSetFieldCol ) {
                                            final ActionSetFieldCol asf = (ActionSetFieldCol) c;
                                            ActionSetColumn ed = new ActionSetColumn( getSCE(),
                                                                                      dtable,
                                                                                      new ColumnCentricCommand() {
                                                                                          public void execute(DTColumnConfig column) {
                                                                                              dtable.updateColumn( asf,
                                                                                                                   (ActionSetFieldCol) column );
                                                                                              refreshActionsWidget();
                                                                                          }
                                                                                      },
                                                                                      asf,
                                                                                      false );
                                            ed.show();
                                        } else if ( c instanceof ActionInsertFactCol ) {
                                            final ActionInsertFactCol asf = (ActionInsertFactCol) c;
                                            ActionInsertColumn ed = new ActionInsertColumn(
                                                                                            getSCE(),
                                                                                            dtable,
                                                                                            new ColumnCentricCommand() {
                                                                                                public void execute(DTColumnConfig column) {
                                                                                                    dtable.updateColumn( asf,
                                                                                                                         (ActionInsertFactCol) column );
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
                        final ActionInsertFactCol afc = new ActionInsertFactCol();
                        ActionInsertColumn ins = new ActionInsertColumn(
                                                                         getSCE(),
                                                                         dtable,
                                                                         new ColumnCentricCommand() {
                                                                             public void execute(DTColumnConfig column) {
                                                                                 newActionAdded( (ActionCol) column );
                                                                             }
                                                                         },
                                                                         afc,
                                                                         true );
                        ins.show();
                    }

                    private void showSet() {
                        final ActionSetFieldCol afc = new ActionSetFieldCol();
                        ActionSetColumn set = new ActionSetColumn( getSCE(),
                                                                   dtable,
                                                                   new ColumnCentricCommand() {
                                                                       public void execute(DTColumnConfig column) {
                                                                           newActionAdded( (ActionCol) column );
                                                                       }
                                                                   },
                                                                   afc,
                                                                   true );
                        set.show();
                    }

                    private void newActionAdded(ActionCol column) {
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

    private Widget removeAction(final ActionCol c) {
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
        for ( int i = 0; i < guidedDecisionTable.getConditionCols().size(); i++ ) {
            ConditionCol c = guidedDecisionTable.getConditionCols().get( i );
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( removeCondition( c ) );
            hp.add( editCondition( c ) );
            hp.add( new SmallLabel( c.getHeader() ) );
            conditionsConfigWidget.add( hp );
        }
        conditionsConfigWidget.add( newCondition() );
        setupColumnsNote();
    }

    private Widget newCondition() {
        final ConditionCol newCol = new ConditionCol();
        newCol.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        AddButton addButton = new AddButton();
        addButton.setText( constants.NewColumn() );
        addButton.setTitle( constants.AddANewConditionColumn() );
        addButton.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                GuidedDTColumnConfig dialog = new GuidedDTColumnConfig(
                                                                        getSCE(),
                                                                        dtable,
                                                                        new ColumnCentricCommand() {
                                                                            public void execute(DTColumnConfig column) {
                                                                                dtable.addColumn( column );
                                                                                dtable.scrapeColumns();
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

    private Widget editCondition(final ConditionCol c) {
        return new ImageButton( images.edit(),
                                constants.EditThisColumnsConfiguration(),
                                new ClickHandler() {
                                    public void onClick(ClickEvent w) {
                                        GuidedDTColumnConfig dialog = new GuidedDTColumnConfig(
                                                                                                getSCE(),
                                                                                                dtable,
                                                                                                new ColumnCentricCommand() {
                                                                                                    public void execute(DTColumnConfig column) {
                                                                                                        dtable.updateColumn( c,
                                                                                                                             (ConditionCol) column );
                                                                                                        dtable.scrapeColumns();
                                                                                                        refreshConditionsWidget();
                                                                                                    }
                                                                                                },
                                                                                                c,
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

    private Widget removeCondition(final ConditionCol c) {
        Image del = new ImageButton( images.deleteItemSmall(),
                                     constants.RemoveThisConditionColumn(),
                                     new ClickHandler() {
                                         public void onClick(ClickEvent w) {
                                             String cm = constants.DeleteConditionColumnWarning( c.getHeader() );
                                             if ( com.google.gwt.user.client.Window.confirm( cm ) ) {
                                                 dtable.deleteColumn( c );
                                                 dtable.scrapeColumns();
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
            hp.add( new SmallLabel( constants.Metadata() ) );
            attributeConfigWidget.add( hp );
        }
        for ( MetadataCol atc : guidedDecisionTable.getMetadataCols() ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );
            hp.add( removeMeta( atc ) );
            hp.add( new SmallLabel( atc.getMetadata() ) );

            final MetadataCol at = atc;
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

        for ( AttributeCol atc : guidedDecisionTable.getAttributeCols() ) {
            final AttributeCol at = atc;
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
                                                   list.addItem(TypeSafeGuidedDecisionTable.NEGATE_RULE_ATTR);

                                                   // Remove any attributes
                                                   // already added
                                                   for ( AttributeCol col : guidedDecisionTable.getAttributeCols() ) {
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
                                                           AttributeCol attr = new AttributeCol();
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
                                                           MetadataCol met = new MetadataCol();
                                                           met.setHideColumn( true );
                                                           met.setMetadata( metadata );
                                                           dtable.addColumn( met );
                                                           dtable.scrapeColumns();
                                                           refreshAttributeWidget();
                                                           pop.hide();
                                                       }

                                                       private boolean isUnique(String metadata) {
                                                           for ( MetadataCol mc : guidedDecisionTable.getMetadataCols() ) {
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

    private Widget removeAttr(final AttributeCol at) {
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

    private Widget removeMeta(final MetadataCol md) {
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
                                         && guidedDecisionTable.getConditionCols().size() == 0
                                         && guidedDecisionTable.getActionCols().size() == 0 );
    }

    private void setupDecisionTable() {
        if ( dtable == null ) {
            dtable = new VerticalDecisionTableWidget( new DecisionTableControlsWidget(), getSCE() );
            dtable.setPixelSize( 1000,
                                 400 );
            dtable.setModel( guidedDecisionTable );
        }
        layout.add( dtable );
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
