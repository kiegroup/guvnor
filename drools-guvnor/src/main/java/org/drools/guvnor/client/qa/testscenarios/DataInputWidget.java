package org.drools.guvnor.client.qa.testscenarios;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.util.Format;

/**
 * Created by IntelliJ IDEA.
 * User: nheron
 * Date: 7 nov. 2009
 * Time: 19:34:49
 * To change this template use File | Settings | File Templates.
 */
public class DataInputWidget extends DirtyableComposite {

    private Grid                       outer;
    private Scenario                   scenario;
    private SuggestionCompletionEngine suggestionCompletionEngine;
    private String                     type;
    private ScenarioWidget             parent;
    private Constants                  constants = ((Constants) GWT.create( Constants.class ));
    private ExecutionTrace             executionTrace;

    public DataInputWidget(String factType,
                           List<Fixture> defList,
                           boolean isGlobal,
                           Scenario sc,
                           ScenarioWidget parent,
                           ExecutionTrace executionTrace) {

        outer = new Grid( 2,
                          1 );
        scenario = sc;
        this.suggestionCompletionEngine = parent.suggestionCompletionEngine;
        this.type = factType;

        this.parent = parent;
        this.executionTrace = executionTrace;
        outer.getCellFormatter().setStyleName( 0,
                                               0,
                                               "modeller-fact-TypeHeader" ); //NON-NLS
        outer.getCellFormatter().setAlignment( 0,
                                               0,
                                               HasHorizontalAlignment.ALIGN_CENTER,
                                               HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName( "modeller-fact-pattern-Widget" ); //NON-NLS

        if ( isGlobal ) {
            outer.setWidget( 0,
                             0,
                             getLabel( Format.format( constants.globalForScenario(),
                                                      factType ),
                                       defList,
                                       sc ) );
        } else {
            FactData first = (FactData) defList.get( 0 );
            if ( first.isModify ) {
                outer.setWidget( 0,
                                 0,
                                 getLabel( Format.format( constants.modifyForScenario(),
                                                          factType ),
                                           defList,
                                           sc ) );
            } else {
                outer.setWidget( 0,
                                 0,
                                 getLabel( Format.format( constants.insertForScenario(),
                                                          factType ),
                                           defList,
                                           sc ) );
            }
        }

        FlexTable t = render( defList,
                              sc );

        outer.setWidget( 1,
                         0,
                         t );
        initWidget( outer );
    }

    private Widget getLabel(String text,
                            final List<Fixture> defList,
                            Scenario sc) {
        //now we put in button to add new fields
        ClickableLabel clbl = new ClickableLabel( text,
                                                  addFieldCL( defList,
                                                              sc ) );
        return clbl;
    }

    private ClickHandler addFieldCL(final List<Fixture> defList,
                                    final Scenario sc) {
        return new ClickHandler() {

            public void onClick(ClickEvent event) {
                //build up a list of what we have got, don't want to add it twice
                HashSet<String> existingFields = new HashSet<String>();
                if ( defList.size() > 0 ) {
                    FactData factData = (FactData) defList.get( 0 );
                    for ( FieldData fieldData : factData.fieldData ) {
                        existingFields.add( fieldData.name );
                    }

                }
                String[] fields = (String[]) suggestionCompletionEngine.getModelFields( type );
                final FormStylePopup pop = new FormStylePopup(); //NON-NLS
                pop.setTitle( constants.ChooseDotDotDot() );
                final ListBox fieldsListBox = new ListBox();
                for ( int i = 0; i < fields.length; i++ ) {
                    String field = fields[i];
                    if ( !existingFields.contains( field ) ) fieldsListBox.addItem( field );
                }

                Button ok = new Button( constants.OK() );
                ok.addClickHandler( new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        String field = fieldsListBox.getItemText( fieldsListBox.getSelectedIndex() );
                        for ( Fixture fixture : defList ) {
                            if ( fixture instanceof FactData ) {
                                FactData factData = (FactData) fixture;
                                factData.fieldData.add( new FieldData( field,
                                                                       "" ) );
                            }
                        }
                        outer.setWidget( 1,
                                         0,
                                         render( defList,
                                                 sc ) );
                        pop.hide();
                    }
                } );
                HorizontalPanel h = new HorizontalPanel();
                h.add( fieldsListBox );
                h.add( ok );
                pop.addAttribute( constants.ChooseAFieldToAdd(),
                                  h );

                Button remove = new Button( constants.RemoveThisBlockOfData() );
                remove.addClickHandler( new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        if ( Window.confirm( constants.AreYouSureYouWantToRemoveThisBlockOfData() ) ) {
                            scenario.globals.removeAll( defList );
                            parent.renderEditor();
                            pop.hide();
                        }
                    }
                } );
                pop.addAttribute( "",
                                  remove );

                pop.show();
            }
        };
    }

    private FlexTable render(final List<Fixture> defList,
                             final Scenario sc) {
        DirtyableFlexTable flexTableLayout = new DirtyableFlexTable();
        if ( defList.size() == 0 ) {
            parent.renderEditor();
        }

        //This will work out what row is for what field, addin labels and remove icons

        Map<String, Integer> fields = new HashMap<String, Integer>();
        int col = 0;
        int totalCols = defList.size();
        for ( Fixture fixture : defList ) {
            if ( fixture instanceof FactData ) {
                final FactData d = (FactData) fixture;

                for ( int i = 0; i < d.fieldData.size(); i++ ) {
                    final FieldData fd = d.fieldData.get( i );
                    if ( !fields.containsKey( fd.name ) ) {
                        int idx = fields.size() + 1;
                        fields.put( fd.name,
                                    new Integer( idx ) );
                        flexTableLayout.setWidget( idx,
                                                   0,
                                                   new SmallLabel( fd.name + ":" ) );
                        Image del = new ImageButton( "images/delete_item_small.gif",
                                                     constants.RemoveThisRow() );
                        del.addClickHandler( new ClickHandler() {
                            public void onClick(ClickEvent event) {
                                if ( Window.confirm( Format.format( constants.AreYouSureYouWantToRemoveRow0(),
                                                                    d.name ) ) ) {
                                    ScenarioHelper.removeFields( defList,
                                                                 fd.name );
                                    outer.setWidget( 1,
                                                     0,
                                                     render( defList,
                                                             sc ) );

                                }
                            }
                        } );
                        flexTableLayout.setWidget( idx,
                                                   totalCols + 1,
                                                   del );
                        flexTableLayout.getCellFormatter().setHorizontalAlignment( idx,
                                                                                   0,
                                                                                   HasHorizontalAlignment.ALIGN_RIGHT );
                    }
                }
            }
        }

        int totalRows = fields.size();

        flexTableLayout.getFlexCellFormatter().setHorizontalAlignment( totalRows + 1,
                                                                       0,
                                                                       HasHorizontalAlignment.ALIGN_RIGHT );

        //now we go through the facts and the fields, adding them to the grid
        //if a fact is missing a FieldData, we will add it in (so people can enter data later on)
        col = 0;
        for ( Fixture fixture : defList ) {
            final FactData factData = (FactData) fixture;
            flexTableLayout.setWidget( 0,
                                       ++col,
                                       new SmallLabel( "[" + factData.name + "]" ) );
            Image del = new ImageButton( "images/delete_item_small.gif",
                                         Format.format( constants.RemoveTheColumnForScenario(),
                                                        factData.name ) );
            del.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if ( scenario.isFactNameUsed( factData ) ) {
                        Window.alert( Format.format( constants.CanTRemoveThisColumnAsTheName0IsBeingUsed(),
                                                     factData.name ) );
                    } else if ( Window.confirm( Format.format( constants.AreYouSureYouWantToRemoveColumn0(),
                                                               factData.name ) ) ) {
                        scenario.removeFixture( factData );
                        defList.remove( factData );
                        outer.setWidget( 1,
                                         0,
                                         render( defList,
                                                 sc ) );
                    }
                }
            } );
            flexTableLayout.setWidget( totalRows + 1,
                                       col,
                                       del );
            Map<String, Integer> presentFields = new HashMap<String, Integer>( fields );
            for ( int i = 0; i < factData.fieldData.size(); i++ ) {
                FieldData fd = factData.fieldData.get( i );
                int fldRow = ((Integer) fields.get( fd.name )).intValue();
                flexTableLayout.setWidget( fldRow,
                                           col,
                                           editableCell( fd,
                                                         factData,
                                                         factData.type,
                                                         this.executionTrace ) );
                presentFields.remove( fd.name );
            }

            for ( Map.Entry<String, Integer> e : presentFields.entrySet() ) {
                int fldRow = ((Integer) e.getValue()).intValue();
                FieldData fd = new FieldData( (String) e.getKey(),
                                              "" );
                factData.fieldData.add( fd );
                flexTableLayout.setWidget( fldRow,
                                           col,
                                           editableCell( fd,
                                                         factData,
                                                         factData.type,
                                                         this.executionTrace ) );
            }
        }

        if ( fields.size() == 0 ) {
            Button b = new Button( constants.AddAField() );
            b.addClickHandler( addFieldCL( defList,
                                           sc ) );

            flexTableLayout.setWidget( 1,
                                       1,
                                       b );
        }
        return flexTableLayout;
    }

    /**
     * This will provide a cell editor. It will filter non numerics, show choices etc as appropriate.
     * @param fd
     * @param factType
     * @return
     */
    private Widget editableCell(final FieldData fd,
                                FactData factData,
                                String factType,
                                ExecutionTrace executionTrace) {
        return new FieldDataConstraintEditor( factType,
                                              new ValueChanged() {
                                                  public void valueChanged(String newValue) {
                                                      fd.value = newValue;
                                                      makeDirty();
                                                  }
                                              },
                                              fd,
                                              factData,
                                              suggestionCompletionEngine,
                                              scenario,
                                              executionTrace );
    }
}
