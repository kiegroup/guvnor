package org.drools.guvnor.client.decisiontable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.dt.ActionCol;
import org.drools.guvnor.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.guvnor.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.guvnor.client.modeldriven.dt.AttributeCol;
import org.drools.guvnor.client.modeldriven.dt.ConditionCol;
import org.drools.guvnor.client.modeldriven.dt.DTColumnConfig;
import org.drools.guvnor.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.guvnor.client.modeldriven.dt.MetadataCol;
import org.drools.guvnor.client.modeldriven.ui.ActionValueEditor;
import org.drools.guvnor.client.modeldriven.ui.ConstraintValueEditorHelper;
import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.ruleeditor.SaveEventListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
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
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarMenuButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GroupingView;
import com.gwtext.client.widgets.grid.Renderer;
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

    private GuidedDecisionTable        dt;
    private VerticalPanel              layout;
    private GridPanel                  grid;
    private FieldDef[]                 fds;
    private VerticalPanel              attributeConfigWidget;
    private VerticalPanel              conditionsConfigWidget;
    private String                     packageName;
    private VerticalPanel              actionsConfigWidget;
    private Map                        colMap;
    private SuggestionCompletionEngine sce;
    private GroupingStore              store;
    private Constants                  constants = ((Constants) GWT.create( Constants.class ));

    public GuidedDecisionTableWidget(RuleAsset asset,
                                     RuleViewer viewer) {
        this( asset );
    }

    public GuidedDecisionTableWidget(RuleAsset asset) {

        this.dt = (GuidedDecisionTable) asset.content;
        this.packageName = asset.metaData.packageName;
        this.dt.tableName = asset.metaData.name;

        layout = new VerticalPanel();

        FormPanel config = new FormPanel();
        config.setTitle( constants.DecisionTable() );

        config.setBodyBorder( false );
        config.setCollapsed( true );
        config.setCollapsible( true );

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

        layout.add( config );

        refreshGrid();

        initWidget( layout );
    }

    private Widget getGrouping() {
        final ListBox list = new ListBox();

        list.addItem( constants.Description(),
                      "desc" ); //NON-NLS
        if ( dt.getMetadataCols() == null ) {
            dt.setMetadataCols( new ArrayList<MetadataCol>() );
        }
        for ( Iterator iterator = dt.getMetadataCols().iterator(); iterator.hasNext(); ) {
            MetadataCol c = (MetadataCol) iterator.next();
            list.addItem( c.attr,
                          c.attr );
            if ( c.attr.equals( dt.groupField ) ) {
                list.setSelectedIndex( list.getItemCount() - 1 );
            }
        }
        for ( Iterator iterator = dt.attributeCols.iterator(); iterator.hasNext(); ) {
            AttributeCol c = (AttributeCol) iterator.next();
            list.addItem( c.attr,
                          c.attr );
            if ( c.attr.equals( dt.groupField ) ) {
                list.setSelectedIndex( list.getItemCount() - 1 );
            }
        }
        for ( Iterator iterator = dt.conditionCols.iterator(); iterator.hasNext(); ) {
            ConditionCol c = (ConditionCol) iterator.next();
            list.addItem( c.header,
                          c.header );
            if ( c.header.equals( dt.groupField ) ) {
                list.setSelectedIndex( list.getItemCount() - 1 );
            }
        }
        for ( Iterator iterator = dt.actionCols.iterator(); iterator.hasNext(); ) {
            ActionCol c = (ActionCol) iterator.next();
            list.addItem( c.header,
                          c.header );
            if ( c.header.equals( dt.groupField ) ) {
                list.setSelectedIndex( list.getItemCount() - 1 );
            }
        }

        list.addItem( constants.none(),
                      "" );
        if ( dt.groupField == null ) {
            list.setSelectedIndex( list.getItemCount() - 1 );
        }

        HorizontalPanel h = new HorizontalPanel();
        h.add( new SmallLabel( constants.GroupByColumn() ) );
        h.add( list );

        Button ok = new Button( constants.Apply() );
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                dt.groupField = list.getValue( list.getSelectedIndex() );
                scrapeData( -1 );
                refreshGrid();
            }
        } );

        h.add( ok );

        return h;
    }

    private Widget getActions() {
        actionsConfigWidget = new VerticalPanel();
        refreshActionsWidget();
        return actionsConfigWidget;
    }

    private void refreshActionsWidget() {
        this.actionsConfigWidget.clear();
        for ( int i = 0; i < dt.actionCols.size(); i++ ) {
            ActionCol c = (ActionCol) dt.actionCols.get( i );
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( removeAction( c ) );
            hp.add( editAction( c ) );
            hp.add( new SmallLabel( c.header ) );
            actionsConfigWidget.add( hp );
        }
        actionsConfigWidget.add( newAction() );

    }

    private Widget editAction(final ActionCol c) {
        return new ImageButton( "images/edit.gif",
                                constants.EditThisActionColumnConfiguration(),
                                new ClickListener() { //NON-NLS
                                    public void onClick(Widget w) {
                                        if ( c instanceof ActionSetFieldCol ) {
                                            ActionSetFieldCol asf = (ActionSetFieldCol) c;
                                            ActionSetColumn ed = new ActionSetColumn( getSCE(),
                                                                                      dt,
                                                                                      new Command() {
                                                                                          public void execute() {
                                                                                              scrapeData( -1 );
                                                                                              refreshGrid();
                                                                                              refreshActionsWidget();
                                                                                          }
                                                                                      },
                                                                                      asf,
                                                                                      false );
                                            ed.show();
                                        } else if ( c instanceof ActionInsertFactCol ) {
                                            ActionInsertFactCol asf = (ActionInsertFactCol) c;
                                            ActionInsertColumn ed = new ActionInsertColumn( getSCE(),
                                                                                            dt,
                                                                                            new Command() {
                                                                                                public void execute() {
                                                                                                    scrapeData( -1 );
                                                                                                    refreshGrid();
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
        return new ImageButton( "images/new_item.gif",
                                constants.CreateANewActionColumn(),
                                new ClickListener() { //NON-NLS
                                    public void onClick(Widget w) {
                                        final FormStylePopup pop = new FormStylePopup();
                                        pop.setModal( false );

                                        final ListBox choice = new ListBox();
                                        choice.addItem( constants.SetTheValueOfAField(),
                                                        "set" );
                                        choice.addItem( constants.SetTheValueOfAFieldOnANewFact(),
                                                        "insert" );
                                        Button ok = new Button( "OK" );
                                        ok.addClickListener( new ClickListener() {
                                            public void onClick(Widget w) {
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
                                                                                                 dt,
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
                                                                                           dt,
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
                                                scrapeData( dt.getMetadataCols().size() + dt.attributeCols.size() + dt.conditionCols.size() + dt.actionCols.size() + 1 );
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

    }

    private Widget removeAction(final ActionCol c) {
        Image del = new ImageButton( "images/delete_item_small.gif",
                                     constants.RemoveThisActionColumn(),
                                     new ClickListener() { //NON-NLS
                                         public void onClick(Widget w) {
                                             String cm = Format.format( constants.DeleteActionColumnWarning(),
                                                                        c.header );
                                             if ( com.google.gwt.user.client.Window.confirm( cm ) ) {
                                                 dt.actionCols.remove( c );
                                                 removeField( c.header );
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
        for ( int i = 0; i < dt.conditionCols.size(); i++ ) {
            ConditionCol c = dt.conditionCols.get( i );
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( removeCondition( c ) );
            hp.add( editCondition( c ) );
            hp.add( new SmallLabel( c.header ) );
            conditionsConfigWidget.add( hp );
        }
        conditionsConfigWidget.add( newCondition() );

    }

    private Widget newCondition() {
        final ConditionCol newCol = new ConditionCol();
        newCol.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        return new ImageButton( "images/new_item.gif",
                                constants.AddANewConditionColumn(),
                                new ClickListener() { //NON-NLS
                                    public void onClick(Widget w) {
                                        GuidedDTColumnConfig dialog = new GuidedDTColumnConfig( getSCE(),
                                                                                                dt,
                                                                                                new Command() {
                                                                                                    public void execute() {
                                                                                                        //want to add in a blank row into the data
                                                                                                        scrapeData( dt.getMetadataCols().size() + dt.attributeCols.size() + dt.conditionCols.size() + 1 );
                                                                                                        refreshGrid();
                                                                                                        refreshConditionsWidget();
                                                                                                    }
                                                                                                },
                                                                                                newCol,
                                                                                                true );
                                        dialog.show();
                                    }
                                } );
    }

    private Widget editCondition(final ConditionCol c) {
        return new ImageButton( "images/edit.gif",
                                constants.EditThisColumnsConfiguration(),
                                new ClickListener() { //NON-NLS
                                    public void onClick(Widget w) {
                                        GuidedDTColumnConfig dialog = new GuidedDTColumnConfig( getSCE(),
                                                                                                dt,
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
                                     new ClickListener() { //NON-NLS
                                         public void onClick(Widget w) {
                                             String cm = Format.format( constants.DeleteConditionColumnWarning(),
                                                                        c.header );
                                             if ( com.google.gwt.user.client.Window.confirm( cm ) ) {
                                                 dt.conditionCols.remove( c );
                                                 removeField( c.header );
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
        if ( dt.getMetadataCols().size() > 0 ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;&nbsp;" ) ); //NON-NLS
            hp.add( new SmallLabel( constants.Metadata() ) );
            attributeConfigWidget.add( hp );
        }
        for ( int i = 0; i < dt.getMetadataCols().size(); i++ ) {
            MetadataCol at = (MetadataCol) dt.getMetadataCols().get( i );
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) ); //NON-NLS
            hp.add( removeMeta( at ) );
            hp.add( new SmallLabel( at.attr ) );
            attributeConfigWidget.add( hp );
        }
        if ( dt.attributeCols.size() > 0 ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;&nbsp;" ) ); //NON-NLS
            hp.add( new SmallLabel( constants.Attributes() ) );
            attributeConfigWidget.add( hp );
        }

        for ( int i = 0; i < dt.attributeCols.size(); i++ ) {

            final AttributeCol at = dt.attributeCols.get( i );
            HorizontalPanel hp = new HorizontalPanel();

            hp.add( new SmallLabel( at.attr ) );

            hp.add( removeAttr( at ) );
            final TextBox defaultValue = new TextBox();
            defaultValue.setText( at.defaultValue );
            defaultValue.addChangeListener( new ChangeListener() {
                public void onChange(Widget sender) {
                    at.defaultValue = defaultValue.getText();
                }
            } );
            hp.add( new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) ); //NON-NLS
            hp.add( new SmallLabel( constants.DefaultValue() ) );
            hp.add( defaultValue );

            final CheckBox hide = new CheckBox();
            hide.setChecked( at.hideColumn );
            hide.addClickListener( new ClickListener() {
                public void onClick(Widget sender) {
                    at.hideColumn = hide.isChecked();
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
                                           new ClickListener() { //NON-NLS
                                               public void onClick(Widget w) {
                                                   //show choice of attributes
                                                   final FormStylePopup pop = new FormStylePopup( "images/config.png",
                                                                                                  constants.AddAnOptionToTheRule() ); //NON-NLS
                                                   final ListBox list = RuleAttributeWidget.getAttributeList();
                                                   final Image addbutton = new ImageButton( "images/new_item.gif" ); //NON-NLS
                                                   final TextBox box = new TextBox();
                                                   box.setVisibleLength( 15 );

                                                   list.setSelectedIndex( 0 );

                                                   list.addChangeListener( new ChangeListener() {
                                                       public void onChange(Widget w) {
                                                           AttributeCol attr = new AttributeCol();
                                                           attr.attr = list.getItemText( list.getSelectedIndex() );

                                                           dt.attributeCols.add( attr );
                                                           scrapeData( dt.getMetadataCols().size() + dt.attributeCols.size() + 1 );
                                                           refreshGrid();
                                                           refreshAttributeWidget();
                                                           pop.hide();
                                                       }
                                                   } );

                                                   addbutton.setTitle( constants.AddMetadataToTheRule() );

                                                   addbutton.addClickListener( new ClickListener() {
                                                       public void onClick(Widget w) {
                                                           MetadataCol met = new MetadataCol();
                                                           met.attr = box.getText();
                                                           dt.getMetadataCols().add( met );
                                                           scrapeData( dt.getMetadataCols().size() + 1 );
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
                                                   //		        pop.addAttribute("", ok);
                                                   pop.show();
                                               }

                                               private void addItem(String at,
                                                                    final ListBox list) {
                                                   if ( !hasAttribute( at,
                                                                       dt.attributeCols ) ) list.addItem( at );
                                               }

                                               private boolean hasAttribute(String at,
                                                                            List attributeCols) {
                                                   for ( Iterator iterator = attributeCols.iterator(); iterator.hasNext(); ) {
                                                       AttributeCol c = (AttributeCol) iterator.next();
                                                       if ( c.attr.equals( at ) ) {
                                                           return true;
                                                       }
                                                   }
                                                   return false;
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
                                     new ClickListener() { //NON-NLS
                                         public void onClick(Widget w) {
                                             String ms = Format.format( constants.DeleteActionColumnWarning(),
                                                                        at.attr );
                                             if ( com.google.gwt.user.client.Window.confirm( ms ) ) {
                                                 dt.attributeCols.remove( at );
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
                                     new ClickListener() { //NON-NLS
                                         public void onClick(Widget w) {
                                             String ms = Format.format( constants.DeleteActionColumnWarning(),
                                                                        md.attr );
                                             if ( com.google.gwt.user.client.Window.confirm( ms ) ) {
                                                 dt.getMetadataCols().remove( md );
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
        dt.data = new String[recs.length][];
        for ( int i = 0; i < recs.length; i++ ) {
            Record r = recs[i];
            if ( insertCol == -1 ) {
                String[] row = new String[fds.length];
                dt.data[i] = row;
                for ( int j = 0; j < fds.length; j++ ) {
                    row[j] = r.getAsString( fds[j].getName() );
                }
            } else {
                String[] row = new String[fds.length + 1];
                dt.data[i] = row;
                for ( int j = 0; j < fds.length; j++ ) {
                    if ( j < insertCol ) {
                        row[j] = r.getAsString( fds[j].getName() );
                    } else if ( j >= insertCol ) {
                        row[j + 1] = r.getAsString( fds[j].getName() );
                    }
                }
            }
        }
        //		String groupF = store.getGroupField();
        //		if (groupF == null || groupF.equals("")) {
        //			dt.groupField = groupF;
        //		}
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

    }

    private void refreshGrid() {
        if ( layout.getWidgetCount() > 1 ) {
            layout.remove( 1 );
        }
        if ( dt.actionCols.size() == 0 && dt.conditionCols.size() == 0 && dt.actionCols.size() == 0 ) {
            VerticalPanel vp = new VerticalPanel();
            vp.setWidth( "100%" );
            PrettyFormLayout pfl = new PrettyFormLayout();
            pfl.startSection();
            pfl.addRow( new HTML( "<img src='images/information.gif'/>&nbsp;" + constants.ConfigureColumnsNote() ) );

            pfl.endSection();
            vp.add( pfl );
            grid = doGrid();
            vp.add( grid );
            layout.add( vp );

        } else {
            grid = doGrid();
            layout.add( grid );
        }
    }

    private GridPanel doGrid() {

        fds = new FieldDef[dt.getMetadataCols().size() + dt.attributeCols.size() + dt.actionCols.size() + dt.conditionCols.size() + 2]; //its +2 as we have counter and description data

        colMap = new HashMap();

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
                if ( dt.descriptionWidth != -1 ) {
                    setWidth( dt.descriptionWidth );
                }
            }
        };
        colCount++;

        //now to metadata
        for ( int i = 0; i < dt.getMetadataCols().size(); i++ ) {
            final MetadataCol attr = dt.getMetadataCols().get( i );
            fds[colCount] = new StringFieldDef( attr.attr );
            cols[colCount] = new ColumnConfig() {
                {
                    setHeader( attr.attr );
                    setDataIndex( attr.attr );
                    setSortable( true );
                    if ( attr.width != -1 ) {
                        setWidth( attr.width );
                    }
                    if ( attr.hideColumn ) {
                        setHidden( true );
                    }

                }
            };
            colMap.put( attr.attr,
                        attr );
            colCount++;
        }

        //now to attributes
        for ( int i = 0; i < dt.attributeCols.size(); i++ ) {
            final AttributeCol attr = dt.attributeCols.get( i );
            fds[colCount] = new StringFieldDef( attr.attr );
            cols[colCount] = new ColumnConfig() {
                {
                    setHeader( attr.attr );
                    setDataIndex( attr.attr );
                    setSortable( true );
                    if ( attr.width != -1 ) {
                        setWidth( attr.width );
                    }

                    if ( attr.hideColumn ) {
                        setHidden( true );
                    }

                }
            };
            colMap.put( attr.attr,
                        attr );
            colCount++;
        }

        //do all the condition cols
        for ( int i = 0; i < dt.conditionCols.size(); i++ ) {
            //here we could also deal with numeric type?
            final ConditionCol c = dt.conditionCols.get( i );
            fds[colCount] = new StringFieldDef( c.header );
            cols[colCount] = new ColumnConfig() {
                {
                    setHeader( c.header );
                    setDataIndex( c.header );
                    setSortable( true );
                    if ( c.width != -1 ) {
                        setWidth( c.width );
                    }

                    if ( c.hideColumn ) {
                        setHidden( true );
                    }
                }
            };
            colMap.put( c.header,
                        c );
            colCount++;
        }

        //the split thing
        //The separator column causes confusion, see GUVNOR-498. Remove this column for now until  
        //we find a better way to represent a column for the purpose of separator. 
/*        cols[colCount] = new ColumnConfig() {
            {
                setDataIndex( "x" );
                setHeader( "x" );
                //setFixed(true);
                setSortable( false );
                setResizable( false );
                //setWidth(60);
                setRenderer( new Renderer() {
                    public String render(Object value,
                                         CellMetadata cellMetadata,
                                         Record record,
                                         int rowIndex,
                                         int colNum,
                                         Store store) {
                        return "<image src='images/production.gif'/>"; //NON-NLS
                    }
                } );
                setWidth( 20 );
            }
        };
        colCount++;*/

        for ( int i = 0; i < dt.actionCols.size(); i++ ) {
            //here we could also deal with numeric type?
            final ActionCol c = dt.actionCols.get( i );
            fds[colCount] = new StringFieldDef( c.header );

            cols[colCount] = new ColumnConfig() {
                {
                    setHeader( c.header );
                    setDataIndex( c.header );
                    //and here we do the appropriate editor
                    setSortable( true );
                    if ( c.width != -1 ) {
                        setWidth( c.width );
                    }

                    if ( c.hideColumn ) {
                        setHidden( true );
                    }
                }
            };
            colMap.put( c.header,
                        c );
            colCount++;
        }

        final RecordDef recordDef = new RecordDef( fds );
        ArrayReader reader = new ArrayReader( recordDef );
        MemoryProxy proxy = new MemoryProxy( dt.data );

        ColumnModel cm = new ColumnModel( cols );
        store = new GroupingStore();
        store.setReader( reader );
        store.setDataProxy( proxy );
        store.setSortInfo( new SortState( "num",
                                          SortDir.ASC ) ); //NON-NLS
        if ( this.dt.groupField != null ) {
            store.setGroupField( dt.groupField );
        }

        store.load();

        final GridPanel grid = new GridPanel( store,
                                              cm );
        grid.setStripeRows( true );

        grid.addGridColumnListener( new GridColumnListener() {
            public void onColumnMove(GridPanel grid,
                                     int oldIndex,
                                     int newIndex) {

                if ( DecisionTableHandler.validateMove( dt,
                                                        oldIndex,
                                                        newIndex ) ) {

                    // Save any changes to the dt.data.
                    scrapeData( -1 );

                    DecisionTableHandler.moveColumn( dt,
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
                DTColumnConfig colConf = (DTColumnConfig) colMap.get( dataIdx );
                String[] vals = dt.getValueList( colConf,
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
                    dt.descriptionWidth = newSize;
                } else {
                    if ( colMap.containsKey( dta ) ) {
                        DTColumnConfig col = (DTColumnConfig) colMap.get( dta );
                        col.width = newSize;
                    }
                }
            }
        } );

        Toolbar tb = new Toolbar();
        Menu menu = new Menu();
        menu.addItem( new Item( constants.AddRow(),
                                new BaseItemListenerAdapter() {
                                    public void onClick(BaseItem item,
                                                        EventObject e) {
                                        Record r = recordDef.createRecord( new Object[recordDef.getFields().length] );
                                        r.set( "num",
                                               store.getRecords().length + 1 ); //NON-NLS

                                        store.add( r );
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
                                    }
                                } ) );

        //        Menu subMenu = new Menu();
        //        subMenu.addItem( new Item( "Move up",
        //                                   new BaseItemListenerAdapter() {
        //                                       public void onClick(BaseItem item,
        //                                                           EventObject e) {
        //                                           Record[] selected = grid.getSelectionModel().getSelections();
        //                                           if ( selected.length == 1 ) {
        //                                               Record from = selected[0];
        //
        //                                               grid.getSelectionModel().selectPrevious();
        //
        //                                               selected = grid.getSelectionModel().getSelections();
        //                                               Record to = selected[0];
        //
        //                                               changeRowPositions( from,
        //                                                                   to );
        //
        //                                           } else {
        //                                               // TODO: Popup: Please select one.
        //                                           }
        //                                       }
        //                                   } ) );
        //        subMenu.addItem( new Item( "Move down",
        //                                   new BaseItemListenerAdapter() {
        //                                       public void onClick(BaseItem item,
        //                                                           EventObject e) {
        //                                           Record[] selected = grid.getSelectionModel().getSelections();
        //                                           if ( selected.length == 1 ) {
        //                                               Record from = selected[0];
        //
        //                                               grid.getSelectionModel().selectNext();
        //
        //                                               selected = grid.getSelectionModel().getSelections();
        //                                               Record to = selected[0];
        //
        //                                               changeRowPositions( from,
        //                                                                   to );
        //
        //                                           } else {
        //                                               // TODO: Popup: Please select one.
        //                                           }
        //                                       }
        //                                   } ) );
        //        subMenu.addItem( new Item( "Switch selected",
        //                                   new BaseItemListenerAdapter() {
        //                                       public void onClick(BaseItem item,
        //                                                           EventObject e) {
        //                                           Record[] selected = grid.getSelectionModel().getSelections();
        //                                           if ( selected.length == 2 ) {
        //
        //                                               changeRowPositions( selected[0],
        //                                                                   selected[1] );
        //                                           } else {
        //                                               // TODO: Popup: Please select two.
        //                                           }
        //                                       }
        //                                   } ) );
        //        menu.addItem( new com.gwtext.client.widgets.menu.MenuItem( "Move",
        //                                                                   subMenu ) );

        ToolbarMenuButton tbb = new ToolbarMenuButton( constants.Modify(),
                                                       menu );

        tb.addButton( tbb );
        grid.add( tb );

        return grid;

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
        drop.addKeyboardListener( new KeyboardListenerAdapter() {
            public void onKeyUp(Widget sender,
                                char keyCode,
                                int modifiers) {
                if ( keyCode == KeyboardListener.KEY_ENTER ) {
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
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget wg) {
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
        final TextBox box = new TextBox();
        box.setText( val );
        box.addKeyboardListener( new KeyboardListenerAdapter() {
            public void onKeyUp(Widget sender,
                                char keyCode,
                                int modifiers) {
                if ( keyCode == KeyboardListener.KEY_ENTER ) {
                    r.set( dta,
                           box.getText() );

                    w.destroy();
                }
            }
        } );

        if ( dt.isNumeric( colConf,
                           getSCE() ) ) {
            box.addKeyboardListener( ActionValueEditor.getNumericFilter( box ) );
        }

        Panel p = new Panel();
        p.add( box );
        w.add( p );
        w.setBorder( false );

        Button ok = new Button( constants.OK() );
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget wg) {
                r.set( dta,
                       box.getText() );
                w.destroy();
            }
        } );
        p.add( ok );

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

    private void changeRowPositions(Record from,
                                    Record to) {
        int fromNum = from.getAsInteger( "num" );
        int toNum = to.getAsInteger( "num" );
        from.set( "num",
                  toNum );
        to.set( "num",
                fromNum );

        scrapeData( -1 );

        refreshGrid();
    }

}
