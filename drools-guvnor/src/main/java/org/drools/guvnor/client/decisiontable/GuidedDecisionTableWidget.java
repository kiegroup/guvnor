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

package org.drools.guvnor.client.decisiontable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.ui.DatePickerTextBox;
import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.ruleeditor.SaveEventListener;
import org.drools.guvnor.client.util.AddButton;
import org.drools.guvnor.client.util.Format;
import org.drools.guvnor.client.util.NumbericFilterKeyPressHandler;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.drools.ide.common.client.modeldriven.ui.ConstraintValueEditorHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.GroupingStore;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SortState;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolbarMenuButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GroupingView;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.ColumnModelListenerAdapter;
import com.gwtext.client.widgets.grid.event.GridCellListenerAdapter;
import com.gwtext.client.widgets.grid.event.GridColumnListener;
import com.gwtext.client.widgets.grid.event.GridColumnListenerAdapter;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;

/**
 * This is the new guided decision table editor for the web.
 * @author Michael Neale
 */
public class GuidedDecisionTableWidget extends Composite
    implements
    SaveEventListener {

    private Constants                   constants      = ((Constants) GWT.create( Constants.class ));

    private GuidedDecisionTable         guidedDecisionTable;
    private VerticalPanel               layout;
    private PrettyFormLayout            configureColumnsNote;
    private GridPanel                   grid;
    private FieldDef[]                  fds;
    private VerticalPanel               attributeConfigWidget;
    private VerticalPanel               conditionsConfigWidget;
    private String                      packageName;
    private VerticalPanel               actionsConfigWidget;
    private Map<String, DTColumnConfig> colMap;
    private SuggestionCompletionEngine  sce;
    private GroupingStore               store;
    private RecordDef                   recordDef;
    private GroupingsPanel              groupingsPanel = null;

    public GuidedDecisionTableWidget(RuleAsset asset,
                                     RuleViewer viewer) {
        this( asset );
    }

    public GuidedDecisionTableWidget(RuleAsset asset) {

        this.guidedDecisionTable = (GuidedDecisionTable) asset.content;
        this.packageName = asset.metaData.packageName;
        this.guidedDecisionTable.setTableName( asset.metaData.name );

        layout = new VerticalPanel();

        DisclosurePanel disclosurePanel = new DisclosurePanel( constants.DecisionTable() );
        disclosurePanel.setAnimationEnabled( true );
        disclosurePanel.setWidth( "100%" );
        disclosurePanel.setTitle( constants.DecisionTable() );

        VerticalPanel config = new VerticalPanel();
        disclosurePanel.add( config );

        FieldSet conditions = new FieldSet( constants.ConditionColumns() );
        conditions.setCollapsible( true );
        conditions.add( getConditions() );
        config.add( conditions );

        FieldSet actions = new FieldSet( constants.ActionColumns() );
        actions.setCollapsible( true );
        actions.add( getActions() );
        config.add( actions );

        FieldSet grouping = new FieldSet( constants.options() );
        grouping.setCollapsible( true );
        grouping.setCollapsed( true );
        grouping.add( getGrouping() );
        grouping.add( getAttributes() );
        config.add( grouping );
        layout.add( disclosurePanel );

        VerticalPanel buttonPanel = new VerticalPanel();
        buttonPanel.add( getToolbarMenuButton() );
        layout.add( buttonPanel );

        configureColumnsNote = new PrettyFormLayout();
        configureColumnsNote.startSection();
        configureColumnsNote.addRow( new HTML( "<img src='images/information.gif'/>&nbsp;" + constants.ConfigureColumnsNote() ) );
        configureColumnsNote.endSection();
        configureColumnsNote.setVisible( false );
        layout.add( configureColumnsNote );

        refreshGrid();

        initWidget( layout );
    }

    private Widget getGrouping() {

        this.groupingsPanel = new GroupingsPanel( guidedDecisionTable,
                                                  new Command() {

                                                      public void execute() {
                                                          scrapeData( -1 );
                                                          refreshGrid();
                                                      }
                                                  } );
        return groupingsPanel;
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
    }

    private Widget editAction(final ActionCol c) {
        return new ImageButton( "images/edit.gif",
                                constants.EditThisActionColumnConfiguration(),
                                new ClickHandler() { //NON-NLS
                                    public void onClick(ClickEvent w) {
                                        if ( c instanceof ActionSetFieldCol ) {
                                            ActionSetFieldCol asf = (ActionSetFieldCol) c;
                                            ActionSetColumn ed = new ActionSetColumn( getSCE(),
                                                                                      guidedDecisionTable,
                                                                                      new Command() {
                                                                                          public void execute() {
                                                                                              scrapeData( -1 );
                                                                                              refreshGrid();
                                                                                              refreshActionsWidget();
                                                                                              refreshGroupingsPanel();
                                                                                          }
                                                                                      },
                                                                                      asf,
                                                                                      false );
                                            ed.show();
                                        } else if ( c instanceof ActionInsertFactCol ) {
                                            ActionInsertFactCol asf = (ActionInsertFactCol) c;
                                            ActionInsertColumn ed = new ActionInsertColumn( getSCE(),
                                                                                            guidedDecisionTable,
                                                                                            new Command() {
                                                                                                public void execute() {
                                                                                                    scrapeData( -1 );
                                                                                                    refreshGrid();
                                                                                                    refreshActionsWidget();
                                                                                                    refreshGroupingsPanel();
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

        addButton.addClickHandler( new ClickHandler() { //NON-NLS
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
                        ActionInsertColumn ins = new ActionInsertColumn( getSCE(),
                                                                         guidedDecisionTable,
                                                                         new Command() {
                                                                             public void execute() {
                                                                                 newActionAdded();
                                                                             }
                                                                         },
                                                                         new ActionInsertFactCol(),
                                                                         true );
                        ins.show();
                    }

                    private void showSet() {
                        ActionSetColumn set = new ActionSetColumn( getSCE(),
                                                                   guidedDecisionTable,
                                                                   new Command() {
                                                                       public void execute() {
                                                                           newActionAdded();
                                                                       }
                                                                   },
                                                                   new ActionSetFieldCol(),
                                                                   true );
                        set.show();
                    }

                    private void newActionAdded() {
                        //want to add in a blank row into the data
                        scrapeData( guidedDecisionTable.getMetadataCols().size() + guidedDecisionTable.getAttributeCols().size() + guidedDecisionTable.getConditionCols().size() + guidedDecisionTable.getActionCols().size() + 1 );
                        refreshGrid();
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
        Image del = new ImageButton( "images/delete_item_small.gif",
                                     constants.RemoveThisActionColumn(),
                                     new ClickHandler() { //NON-NLS
                                         public void onClick(ClickEvent w) {
                                             String cm = Format.format( constants.DeleteActionColumnWarning(),
                                                                        c.getHeader() );
                                             if ( com.google.gwt.user.client.Window.confirm( cm ) ) {
                                                 guidedDecisionTable.getActionCols().remove( c );
                                                 removeField( c.getHeader() );
                                                 scrapeData( -1 );
                                                 refreshGrid();
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

    }

    private Widget newCondition() {
        final ConditionCol newCol = new ConditionCol();
        newCol.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        AddButton addButton = new AddButton();
        addButton.setText( constants.NewColumn() );
        addButton.setTitle( constants.AddANewConditionColumn() );
        addButton.addClickHandler( new ClickHandler() { //NON-NLS
            public void onClick(ClickEvent w) {
                GuidedDTColumnConfig dialog = new GuidedDTColumnConfig( getSCE(),
                                                                        guidedDecisionTable,
                                                                        new Command() {
                                                                            public void execute() {
                                                                                //want to add in a blank row into the data
                                                                                scrapeData( guidedDecisionTable.getMetadataCols().size() + guidedDecisionTable.getAttributeCols().size() + guidedDecisionTable.getConditionCols().size() + 1 );
                                                                                refreshGrid();
                                                                                refreshConditionsWidget();
                                                                                refreshGroupingsPanel();
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
        return new ImageButton( "images/edit.gif",
                                constants.EditThisColumnsConfiguration(),
                                new ClickHandler() { //NON-NLS
                                    public void onClick(ClickEvent w) {
                                        GuidedDTColumnConfig dialog = new GuidedDTColumnConfig( getSCE(),
                                                                                                guidedDecisionTable,
                                                                                                new Command() {
                                                                                                    public void execute() {
                                                                                                        scrapeData( -1 );
                                                                                                        refreshGrid();
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
            this.sce = SuggestionCompletionCache.getInstance().getEngineFromCache( this.packageName );
        }
        return sce;
    }

    private Widget removeCondition(final ConditionCol c) {
        Image del = new ImageButton( "images/delete_item_small.gif",
                                     constants.RemoveThisConditionColumn(),
                                     new ClickHandler() { //NON-NLS
                                         public void onClick(ClickEvent w) {
                                             String cm = Format.format( constants.DeleteConditionColumnWarning(),
                                                                        c.getHeader() );
                                             if ( com.google.gwt.user.client.Window.confirm( cm ) ) {
                                                 guidedDecisionTable.getConditionCols().remove( c );
                                                 removeField( c.getHeader() );
                                                 scrapeData( -1 );
                                                 refreshGrid();
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
            hp.add( new HTML( "&nbsp;&nbsp;" ) ); //NON-NLS
            hp.add( new SmallLabel( constants.Metadata() ) );
            attributeConfigWidget.add( hp );
        }
        for ( MetadataCol at : guidedDecisionTable.getMetadataCols() ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) ); //NON-NLS
            hp.add( removeMeta( at ) );
            hp.add( new SmallLabel( at.attr ) );
            attributeConfigWidget.add( hp );
        }
        if ( guidedDecisionTable.getAttributeCols().size() > 0 ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;&nbsp;" ) ); //NON-NLS
            hp.add( new SmallLabel( constants.Attributes() ) );
            attributeConfigWidget.add( hp );
        }

        for ( AttributeCol atc : guidedDecisionTable.getAttributeCols() ) {
            final AttributeCol at = atc;
            HorizontalPanel hp = new HorizontalPanel();

            hp.add( new SmallLabel( at.attr ) );

            hp.add( removeAttr( at ) );
            final TextBox defaultValue = new TextBox();
            defaultValue.setText( at.getDefaultValue() );
            defaultValue.addChangeHandler( new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    at.setDefaultValue( defaultValue.getText() );
                }
            } );

            if ( at.attr.equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                hp.add( new HTML( "&nbsp;&nbsp;" ) );
                final CheckBox useRowNumber = new CheckBox();
                useRowNumber.setValue( at.isUseRowNumber() );
                useRowNumber.addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent sender) {
                        at.setUseRowNumber( useRowNumber.isEnabled() );
                    }
                } );
                hp.add( useRowNumber );
                hp.add( new SmallLabel( constants.UseRowNumber() ) );
                hp.add( new SmallLabel( "(" ) );
                final CheckBox reverseOrder = new CheckBox();
                reverseOrder.setValue( at.isReverseOrder() );
                reverseOrder.addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent sender) {
                        at.setReverseOrder( reverseOrder.isEnabled() );
                    }
                } );
                hp.add( reverseOrder );
                hp.add( new SmallLabel( constants.ReverseOrder() ) );
                hp.add( new SmallLabel( ")" ) );
            }
            hp.add( new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) ); //NON-NLS
            hp.add( new SmallLabel( constants.DefaultValue() ) );
            hp.add( defaultValue );

            final CheckBox hide = new CheckBox();
            hide.setValue( at.isHideColumn() );
            hide.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent sender) {
                    at.setHideColumn( hide.isEnabled() );
                }
            } );
            hp.add( hide );
            hp.add( new SmallLabel( constants.HideThisColumn() ) );

            attributeConfigWidget.add( hp );
        }

    }

    private Widget newAttr() {
        ImageButton but = new ImageButton( "images/new_item.gif",
                                           constants.AddANewAttributeMetadata(),
                                           new ClickHandler() { //NON-NLS
                                               public void onClick(ClickEvent w) {
                                                   //show choice of attributes
                                                   final FormStylePopup pop = new FormStylePopup( "images/config.png",
                                                                                                  constants.AddAnOptionToTheRule() ); //NON-NLS
                                                   final ListBox list = RuleAttributeWidget.getAttributeList();
                                                   final Image addbutton = new ImageButton( "images/new_item.gif" ); //NON-NLS
                                                   final TextBox box = new TextBox();
                                                   box.setVisibleLength( 15 );

                                                   list.setSelectedIndex( 0 );

                                                   list.addChangeHandler( new ChangeHandler() {
                                                       public void onChange(ChangeEvent event) {
                                                           AttributeCol attr = new AttributeCol();
                                                           attr.attr = list.getItemText( list.getSelectedIndex() );

                                                           guidedDecisionTable.getAttributeCols().add( attr );
                                                           scrapeData( guidedDecisionTable.getMetadataCols().size() + guidedDecisionTable.getAttributeCols().size() + 1 );
                                                           refreshGrid();
                                                           refreshAttributeWidget();
                                                           pop.hide();
                                                       }
                                                   } );

                                                   addbutton.setTitle( constants.AddMetadataToTheRule() );

                                                   addbutton.addClickHandler( new ClickHandler() {
                                                       public void onClick(ClickEvent w) {
                                                           MetadataCol met = new MetadataCol();
                                                           met.attr = box.getText();
                                                           guidedDecisionTable.getMetadataCols().add( met );
                                                           scrapeData( guidedDecisionTable.getMetadataCols().size() + 1 );
                                                           refreshGrid();
                                                           refreshAttributeWidget();
                                                           pop.hide();
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
        Image del = new ImageButton( "images/delete_item_small.gif",
                                     constants.RemoveThisAttribute(),
                                     new ClickHandler() { //NON-NLS
                                         public void onClick(ClickEvent w) {
                                             String ms = Format.format( constants.DeleteActionColumnWarning(),
                                                                        at.attr );
                                             if ( com.google.gwt.user.client.Window.confirm( ms ) ) {
                                                 guidedDecisionTable.getAttributeCols().remove( at );
                                                 removeField( at.attr );
                                                 scrapeData( -1 );
                                                 refreshGrid();
                                                 refreshAttributeWidget();
                                             }
                                         }
                                     } );

        return del;
    }

    private Widget removeMeta(final MetadataCol md) {
        Image del = new ImageButton( "images/delete_item_small.gif",
                                     constants.RemoveThisMetadata(),
                                     new ClickHandler() { //NON-NLS
                                         public void onClick(ClickEvent w) {
                                             String ms = Format.format( constants.DeleteActionColumnWarning(),
                                                                        md.attr );
                                             if ( com.google.gwt.user.client.Window.confirm( ms ) ) {
                                                 guidedDecisionTable.getMetadataCols().remove( md );
                                                 removeField( md.attr );
                                                 scrapeData( -1 );
                                                 refreshGrid();
                                                 refreshAttributeWidget();
                                             }
                                         }
                                     } );

        return del;
    }

    /**
     * Here we read the record data from the grid into the data in the model.
     * if we have an insertCol - then a new empty column of data will be added in that
     * row position.
     */
    private void scrapeData(int insertCol) {
        Record[] recs = grid.getStore().getRecords();
        guidedDecisionTable.setData( new String[recs.length][] );
        for ( int i = 0; i < recs.length; i++ ) {
            Record r = recs[i];
            if ( insertCol == -1 ) {
                String[] row = new String[fds.length];
                guidedDecisionTable.getData()[i] = row;
                for ( int j = 0; j < fds.length; j++ ) {
                    row[j] = r.getAsString( fds[j].getName() );
                }
            } else {
                String[] row = new String[fds.length + 1];
                guidedDecisionTable.getData()[i] = row;
                for ( int j = 0; j < fds.length; j++ ) {
                    if ( j < insertCol ) {
                        row[j] = r.getAsString( fds[j].getName() );
                    } else if ( j >= insertCol ) {
                        row[j + 1] = r.getAsString( fds[j].getName() );
                    }
                }
            }
        }
    }

    /**
     * removes the field from the field def.
     * @param headerName
     */
    private void removeField(String headerName) {
        FieldDef[] fds_ = new FieldDef[fds.length - 1];
        int new_i = 0;
        for ( int i = 0; i < fds.length; i++ ) {
            FieldDef fd = fds[i];
            if ( !fd.getName().equals( headerName ) ) {
                fds_[new_i] = fd;
                new_i++;
            }
        }
        this.fds = fds_;

        refreshGroupingsPanel();
    }

    private void refreshGroupingsPanel() {
        if ( groupingsPanel != null ) {
            groupingsPanel.refresh();
        }
    }

    private void refreshGrid() {
        configureColumnsNote.setVisible( guidedDecisionTable.getActionCols().size() == 0 && guidedDecisionTable.getConditionCols().size() == 0 && guidedDecisionTable.getActionCols().size() == 0 );

        if ( layout.getWidgetIndex( grid ) >= 0 ) {
            layout.remove( grid );
        }
        grid = doGrid();
        layout.add( grid );
    }

    private GridPanel doGrid() {

        fds = new FieldDef[guidedDecisionTable.getMetadataCols().size() + guidedDecisionTable.getAttributeCols().size() + guidedDecisionTable.getActionCols().size() + guidedDecisionTable.getConditionCols().size() + 2]; //its +2 as we have counter and description data

        colMap = new HashMap<String, DTColumnConfig>();

        fds[0] = new IntegerFieldDef( "num" ); //NON-NLS
        fds[1] = new StringFieldDef( "desc" ); //NON-NLS

        int colCount = 0;

        BaseColumnConfig[] cols = new BaseColumnConfig[fds.length]; //its +1 as we have the separator -> thing.
        cols[0] = new ColumnConfig() {
            {
                setDataIndex( "num" ); //NON-NLS
                setWidth( 60 );
                setSortable( false );
                setHeader( "Row Number" );
                setRenderer( new Renderer() {
                    public String render(Object value,
                                         CellMetadata cellMetadata,
                                         Record record,
                                         int rowIndex,
                                         int colNum,
                                         Store store) {
                    	return "<span class='x-grid3-cell-inner x-grid3-td-numberer'>" + (rowIndex + 1) + "</span>"; //NON-NLS
                    }
                } );
            }
        };
        colCount++;
        cols[1] = new ColumnConfig() {
            {
                setDataIndex( "desc" ); //NON-NLS
                setSortable( true );
                setHeader( constants.Description() );
                if ( guidedDecisionTable.getDescriptionWidth() != -1 ) {
                    setWidth( guidedDecisionTable.getDescriptionWidth() );
                }
            }
        };
        colCount++;

        //now to metadata
        for ( int i = 0; i < guidedDecisionTable.getMetadataCols().size(); i++ ) {
            final MetadataCol attr = guidedDecisionTable.getMetadataCols().get( i );
            fds[colCount] = new StringFieldDef( attr.attr );
            cols[colCount] = new ColumnConfig() {
                {
                    setHeader( attr.attr );
                    setDataIndex( attr.attr );
                    setSortable( true );
                    if ( attr.getWidth() != -1 ) {
                        setWidth( attr.getWidth() );
                    }
                    if ( attr.isHideColumn() ) {
                        setHidden( true );
                    }

                }
            };
            colMap.put( attr.attr,
                        attr );
            colCount++;
        }

        //now to attributes
        for ( int i = 0; i < guidedDecisionTable.getAttributeCols().size(); i++ ) {
            final AttributeCol attr = guidedDecisionTable.getAttributeCols().get( i );
            fds[colCount] = new StringFieldDef( attr.attr );
            cols[colCount] = new ColumnConfig() {
                {
                    setHeader( attr.attr );
                    setDataIndex( attr.attr );
                    setSortable( true );
                    if ( attr.getWidth() != -1 ) {
                        setWidth( attr.getWidth() );
                    }

                    if ( attr.isHideColumn() ) {
                        setHidden( true );
                    }

                }
            };
            colMap.put( attr.attr,
                        attr );
            colCount++;
        }

        //do all the condition cols
        for ( int i = 0; i < guidedDecisionTable.getConditionCols().size(); i++ ) {
            //here we could also deal with numeric type?
            final ConditionCol c = guidedDecisionTable.getConditionCols().get( i );
            fds[colCount] = new StringFieldDef( c.getHeader() );
            cols[colCount] = new ColumnConfig() {
                {
                    setHeader( c.getHeader() );
                    setDataIndex( c.getHeader() );
                    setSortable( true );
                    if ( c.getWidth() != -1 ) {
                        setWidth( c.getWidth() );
                    }

                    if ( c.isHideColumn() ) {
                        setHidden( true );
                    }
                }
            };
            colMap.put( c.getHeader(),
                        c );
            colCount++;
        }

        for ( int i = 0; i < guidedDecisionTable.getActionCols().size(); i++ ) {
            //here we could also deal with numeric type?
            final ActionCol c = guidedDecisionTable.getActionCols().get( i );
            fds[colCount] = new StringFieldDef( c.getHeader() );

            cols[colCount] = new ColumnConfig() {
                {
                    setHeader( c.getHeader() );
                    setDataIndex( c.getHeader() );
                    //and here we do the appropriate editor
                    setSortable( true );
                    if ( c.getWidth() != -1 ) {
                        setWidth( c.getWidth() );
                    }

                    if ( c.isHideColumn() ) {
                        setHidden( true );
                    }
                }
            };
            colMap.put( c.getHeader(),
                        c );
            colCount++;
        }

        recordDef = new RecordDef( fds );
        ArrayReader reader = new ArrayReader( recordDef );
        MemoryProxy proxy = new MemoryProxy( guidedDecisionTable.getData() );

        ColumnModel cm = new ColumnModel( cols );
        store = new GroupingStore();
        store.setReader( reader );
        store.setDataProxy( proxy );
        store.setSortInfo( new SortState( "num",
                                          SortDir.ASC ) ); //NON-NLS
        if ( this.guidedDecisionTable.getGroupField() != null ) {
            store.setGroupField( guidedDecisionTable.getGroupField() );
        }
        cm.addListener( new ColumnModelListenerAdapter() {
            public void onHiddenChange(ColumnModel cm,
                                       int colIndex,
                                       boolean hidden) {
                final String dta = cm.getDataIndex( colIndex );
                if ( colMap.containsKey( dta ) ) {
                    DTColumnConfig col = colMap.get( dta );
                    col.setHideColumn( hidden );
                }
            }
        } );

        store.load();

        final GridPanel grid = new GridPanel( store,
                                              cm );
        grid.setStripeRows( true );

        grid.addGridColumnListener( new GridColumnListener() {
            public void onColumnMove(GridPanel grid,
                                     int oldIndex,
                                     int newIndex) {

                if ( DecisionTableHandler.validateMove( guidedDecisionTable,
                                                        oldIndex,
                                                        newIndex ) ) {

                    // Save any changes to the dt.data.
                    scrapeData( -1 );

                    DecisionTableHandler.moveColumn( guidedDecisionTable,
                                                     oldIndex,
                                                     newIndex );

                    grid = doGrid();

                } else {
                    // Refresh undoes the move.
                    refreshGrid();
                    ErrorPopup.showMessage( constants.CanNotMoveColumnsFromOneTypeGroupToAnother() );
                }
            }

            public void onColumnResize(GridPanel grid,
                                       int colIndex,
                                       int newSize) {
                // Nothing
            }
        } );

        GroupingView gv = new GroupingView();

        //to stretch it out
        gv.setForceFit( true );
        gv.setGroupTextTpl( "{text} ({[values.rs.length]} {[values.rs.length > 1 ? \"" //NON-NLS
                            + constants.Items() + "\" : \"" + constants.Item() + "\"]})" );

        grid.setView( gv );

        grid.setStore( store );

        int width = 900;
        if ( cm.getColumnCount() > 10 ) {
            width = 900 + (90 * (cm.getColumnCount() - 10));
        }
        grid.setWidth( width );
        grid.setHeight( 500 );

        //Add the cell listener for when the user wants to edit.
        grid.addGridCellListener( new GridCellListenerAdapter() {
            public void onCellDblClick(GridPanel grid,
                                       int rowIndex,
                                       int colIndex,
                                       EventObject e) {

                final String dataIdx = grid.getColumnModel().getDataIndex( colIndex );
                final Record r = store.getAt( rowIndex );
                String val = r.getAsString( dataIdx );
                DTColumnConfig colConf = colMap.get( dataIdx );
                String[] vals = guidedDecisionTable.getValueList( colConf,
                                                                  getSCE() );
                if ( vals.length == 0 ) {
                    showTextEditor( e,
                                    dataIdx,
                                    r,
                                    val,
                                    colConf );
                } else {
                    showDropDownEditor( e,
                                        dataIdx,
                                        r,
                                        val,
                                        vals );
                }
            }

        } );

        //remember any size changes
        grid.addGridColumnListener( new GridColumnListenerAdapter() {
            public void onColumnResize(GridPanel grid,
                                       int colIndex,
                                       int newSize) {
                final String dta = grid.getColumnModel().getDataIndex( colIndex );
                if ( dta.equals( "desc" ) ) { //NON-NLS
                    guidedDecisionTable.setDescriptionWidth( newSize );
                } else {
                    if ( colMap.containsKey( dta ) ) {
                        DTColumnConfig col = colMap.get( dta );
                        col.setWidth( newSize );
                    }
                }
            }
        } );

        return grid;
    }

    private ToolbarMenuButton getToolbarMenuButton() {
        Menu menu = new Menu();
        menu.addItem( new Item( constants.AddRow(),
                                new BaseItemListenerAdapter() {
                                    public void onClick(BaseItem item,
                                                        EventObject e) {
                                        Record r = recordDef.createRecord( new Object[recordDef.getFields().length] );
                                        r.set( "num",
                                               store.getRecords().length + 1 ); //NON-NLS
                                        store.add( r );
                                        renumberSalience( store.getRecords() );
                                    }
                                } ) );

        menu.addItem( new Item( constants.AddRowBeforeSelectedRow(),
                                new BaseItemListenerAdapter() {
                                    public void onClick(BaseItem item,
                                                        EventObject e) {

                                        Record[] selectedRows = grid.getSelectionModel().getSelections();
                                        if ( selectedRows.length == 1 ) {
                                            int selected = selectedRows[0].getAsInteger( "num" );

                                            Record newRecord = recordDef.createRecord( new Object[recordDef.getFields().length] );

                                            Record[] records = store.getRecords();

                                            for ( int i = 0; i < records.length; i++ ) {
                                                Record temp = records[i];
                                                int num = temp.getAsInteger( "num" );
                                                if ( num == selected ) {
                                                    newRecord.set( "num",
                                                                   num ); //NON-NLS
                                                    temp.set( "num",
                                                              num + 1 ); //NON-NLS
                                                    store.addSorted( newRecord );

                                                } else if ( num > selected ) {
                                                    temp.set( "num",
                                                              num + 1 ); //NON-NLS
                                                }
                                            }
                                            renumberSalience( store.getRecords() );
                                        } else {
                                            ErrorPopup.showMessage( constants.PleaseSelectARow() );
                                        }
                                    }
                                } ) );

        menu.addItem( new Item( constants.RemoveSelectedRowS(),
                                new BaseItemListenerAdapter() {
                                    public void onClick(BaseItem item,
                                                        EventObject e) {
                                        Record[] selected = grid.getSelectionModel().getSelections();
                                        if ( com.google.gwt.user.client.Window.confirm( constants.AreYouSureYouWantToDeleteTheSelectedRowS() ) ) {
                                            for ( int i = 0; i < selected.length; i++ ) {
                                                store.remove( selected[i] );
                                            }
                                            renumber( store.getRecords() );
                                            renumberSalience( store.getRecords() );
                                        }
                                    }
                                } ) );
        menu.addItem( new Item( constants.CopySelectedRowS(),
                                new BaseItemListenerAdapter() {
                                    public void onClick(BaseItem item,
                                                        EventObject e) {
                                        Record[] selected = grid.getSelectionModel().getSelections();
                                        for ( int i = 0; i < selected.length; i++ ) {
                                            Record r = recordDef.createRecord( new Object[recordDef.getFields().length] );
                                            Record orig = selected[i];
                                            for ( int j = 0; j < fds.length; j++ ) {
                                                r.set( fds[j].getName(),
                                                       orig.getAsString( fds[j].getName() ) );
                                            }
                                            store.add( r );
                                        }
                                        renumber( store.getRecords() );
                                        renumberSalience( store.getRecords() );
                                    }
                                } ) );

        ToolbarMenuButton tbb = new ToolbarMenuButton( constants.Modify(),
                                                       menu );

        return tbb;
    }

    /**
     * Show a drop down editor, obviously.
     */

    private void showDropDownEditor(EventObject e,
                                    final String dataIdx,
                                    final Record r,
                                    String val,
                                    String[] vals) {
        final Window w = new Window();
        w.setWidth( 200 );
        w.setPlain( true );
        w.setBodyBorder( false );
        w.setAutoDestroy( true );
        w.setTitle( dataIdx );
        final ListBox drop = new ListBox();
        for ( int i = 0; i < vals.length; i++ ) {
            String v = vals[i].trim();
            if ( v.indexOf( '=' ) > 0 ) {
                String[] splut = ConstraintValueEditorHelper.splitValue( v );
                drop.addItem( splut[1],
                              splut[0] );
                if ( splut[0].equals( val ) ) {
                    drop.setSelectedIndex( i );
                }
            } else {
                drop.addItem( v,
                              v );
                if ( v.equals( val ) ) {
                    drop.setSelectedIndex( i );
                }
            }

        }
        drop.addKeyUpHandler( new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    r.set( dataIdx,
                           drop.getValue( drop.getSelectedIndex() ) );
                    w.destroy();
                }
            }
        } );

        Panel p = new Panel();
        p.add( drop );
        w.add( p );
        w.setBorder( false );

        Button ok = new Button( constants.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent wg) {
                r.set( dataIdx,
                       drop.getValue( drop.getSelectedIndex() ) );
                w.destroy();
            }
        } );
        p.add( ok );

        w.setPosition( e.getPageX(),
                       e.getPageY() );
        w.show();

    }

    private void renumber(Record[] rs) {
        for ( int i = 0; i < rs.length; i++ ) {
            rs[i].set( "num",
                       "" + (i + 1) ); //NON-NLS
        }
    }

    private void renumberSalience(Record[] rs) {
        List<AttributeCol> attcols = guidedDecisionTable.getAttributeCols();
        for ( AttributeCol ac : attcols ) {
            if ( ac.isUseRowNumber() ) {
                for ( int i = 0; i < rs.length; i++ ) {
                    Record nextrecord = rs[i];
                    List<String> allFields = Arrays.asList( nextrecord.getFields() );
                    if ( allFields.contains( "salience" ) ) {
                        if ( ac.isReverseOrder() ) {
                            rs[i].set( "salience",
                                       "" + (rs.length - i) ); //NON-NLS
                        } else {
                            rs[i].set( "salience",
                                       "" + (i + 1) ); //NON-NLS
                        }
                    }
                }
            }
            break;
        }
    }

    /**
     * Show a plain old text editor for a cell.
     */
    private void showTextEditor(EventObject e,
                                final String dta,
                                final Record r,
                                String val,
                                DTColumnConfig colConf) {
        final Window w = new Window();
        w.setWidth( 200 );
        w.setAutoDestroy( true );
        w.setPlain( true );
        w.setBodyBorder( false );
        w.setTitle( dta );

        String typeDescription = guidedDecisionTable.getType( colConf,
                                                              getSCE() );
        Panel p = new Panel();

        if ( typeDescription != null && typeDescription.equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
            final DatePickerTextBox datePicker = new DatePickerTextBox( val );
            String m = Format.format( ((Constants) GWT.create( Constants.class )).ValueFor0(),
                                      dta );
            datePicker.setTitle( m );
            datePicker.addValueChanged( new ValueChanged() {
                public void valueChanged(String newValue) {
                    r.set( dta,
                           newValue );
                }
            } );

            p.add( datePicker );
            p.add( new InfoPopup( constants.CategoryParentRules(),
                                  Format.format( constants.FillInColumnWithValue(),
                                                 typeDescription ) ) );

            w.add( p );
            w.setBorder( false );

            Button ok = new Button( constants.OK() );
            ok.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent arg0) {
                    r.set( dta,
                           datePicker.getDateString() );
                    w.destroy();
                }
            } );

            p.add( ok );

        } else {
            final TextBox box = new TextBox();
            box.setText( val );
            box.addKeyUpHandler( new KeyUpHandler() {

                public void onKeyUp(KeyUpEvent event) {
                    if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                        r.set( dta,
                               box.getText() );
                        w.destroy();
                    }
                }
            } );

            if ( guidedDecisionTable.isNumeric( colConf,
                                                getSCE() ) ) {
                box.addKeyPressHandler( new NumbericFilterKeyPressHandler( box ) );
            }

            p.add( box );
            if ( typeDescription != null ) {
                p.add( new InfoPopup( constants.CategoryParentRules(),
                                      Format.format( constants.FillInColumnWithValue(),
                                                     typeDescription ) ) );
            }
            w.add( p );
            w.setBorder( false );

            Button ok = new Button( constants.OK() );
            ok.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent wg) {
                    r.set( dta,
                           box.getText() );
                    w.destroy();
                }
            } );
            p.add( ok );
        }

        w.setPosition( e.getPageX(),
                       e.getPageY() );
        w.show();
    }

    /**
     * Need to copy the data from the record store.
     */
    public void onSave() {
        String[] fields = store.getFields();
        for ( int i = 0; i < fields.length; i++ ) {
            System.out.print( fields[i] + " | " );
        }
        this.scrapeData( -1 );
    }

    public void onAfterSave() {
        //not needed.

    }

}
