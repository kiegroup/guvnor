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
package org.kie.guvnor.guided.scorecard.client.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.kie.guvnor.commons.ui.client.widget.CustomEditTextCell;
import org.kie.guvnor.commons.ui.client.widget.DynamicSelectionCell;
import org.kie.guvnor.commons.ui.client.widget.EnumDropDown;
import org.kie.guvnor.commons.ui.client.widget.TextBoxFactory;
import org.kie.guvnor.datamodel.model.DropDownData;
import org.kie.guvnor.datamodel.model.ModelField;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.oracle.DataType;
import org.kie.guvnor.guided.scorecard.client.resources.i18n.Constants;
import org.kie.guvnor.guided.scorecard.model.Attribute;
import org.kie.guvnor.guided.scorecard.model.Characteristic;
import org.kie.guvnor.guided.scorecard.model.ScoreCardModel;
import org.uberfire.client.common.DecoratedDisclosurePanel;
import org.uberfire.client.common.DirtyableFlexTable;
import org.uberfire.client.common.DropDownValueChanged;

public class GuidedScoreCardEditor extends Composite {

    private static final String[] reasonCodeAlgorithms = new String[]{ "none", "pointsAbove", "pointsBelow" };
    private static final String[] typesForAttributes = new String[]{ "String", "int", "double", "boolean" };
    private static final String[] typesForScore = new String[]{ "double" };
    private static final String[] typesForRC = new String[]{ "List" };

    private static final String[] stringOperators = new String[]{ "=", "in" };
    private static final String[] booleanOperators = new String[]{ "true", "false" };
    private static final String[] numericOperators = new String[]{ "=", ">", "<", ">=", "<=", ">..<", ">=..<", ">=..<=", ">..<=" };

    private Map<String, ModelField[]> oracleModelFields;

    private VerticalPanel container = new VerticalPanel();

    private Button btnAddCharacteristic;
    private VerticalPanel characteristicsPanel;
    private List<DirtyableFlexTable> characteristicsTables = new ArrayList<DirtyableFlexTable>();
    private Map<DirtyableFlexTable, ListDataProvider<Attribute>> characteristicsAttrMap = new HashMap<DirtyableFlexTable, ListDataProvider<Attribute>>();

    private EnumDropDown ddUseReasonCode;
    private EnumDropDown ddReasonCodeAlgorithm;
    private EnumDropDown ddReasonCodeField;
    private TextBox tbBaselineScore;
    private TextBox tbInitialScore;
    private Grid scorecardPropertiesGrid;

    private ScoreCardModel model;
    private DataModelOracle oracle;

    public GuidedScoreCardEditor() {
        initWidget( container );
    }

    public void setContent( final ScoreCardModel model,
                            final DataModelOracle oracle ) {
        this.model = model;
        this.oracle = oracle;
        this.oracleModelFields = oracle.getModelFields();

        final DecoratedDisclosurePanel disclosurePanel = new DecoratedDisclosurePanel( "Scorecard " + " ( " + model.getName() + " )" );
        disclosurePanel.setWidth( "100%" );
        disclosurePanel.setTitle( Constants.INSTANCE.scorecard() );
        disclosurePanel.setOpen( true );

        final DecoratedDisclosurePanel configPanel = new DecoratedDisclosurePanel( "Setup Parameters" );
        configPanel.setWidth( "95%" );
        configPanel.setOpen( true );
        configPanel.add( getScorecardProperties() );

        final DecoratedDisclosurePanel characteristicsPanel = new DecoratedDisclosurePanel( "Characteristics" );
        characteristicsPanel.setOpen( model.getCharacteristics().size() > 0 );
        characteristicsPanel.setWidth( "95%" );
        characteristicsPanel.add( getCharacteristics() );

        final VerticalPanel config = new VerticalPanel();
        config.setWidth( "100%" );
        config.add( configPanel );
        config.add( characteristicsPanel );

        disclosurePanel.add( config );
        container.add( disclosurePanel );

        for ( final Characteristic characteristic : model.getCharacteristics() ) {
            final DirtyableFlexTable flexTable = addCharacteristic( characteristic );
            for ( Attribute attribute : characteristic.getAttributes() ) {
                addAttribute( flexTable,
                              attribute );
            }
        }
    }

    public ScoreCardModel getModel() {
        model.setBaselineScore( Double.parseDouble( tbBaselineScore.getValue() ) );
        model.setInitialScore( Double.parseDouble( tbInitialScore.getValue() ) );
        model.setReasonCodesAlgorithm( ddReasonCodeAlgorithm.getValue( ddReasonCodeAlgorithm.getSelectedIndex() ) );
        model.setUseReasonCodes( ddUseReasonCode.getSelectedIndex() == 1 );

        EnumDropDown enumDropDown = (EnumDropDown) scorecardPropertiesGrid.getWidget( 1,
                                                                                      0 );
        if ( enumDropDown.getSelectedIndex() > -1 ) {
            final String factName = enumDropDown.getValue( enumDropDown.getSelectedIndex() );
            model.setFactName( factName );
            if ( oracleModelFields.get( factName ) != null ) {
                for ( final ModelField mf : oracleModelFields.get( factName ) ) {
                    if ( mf.getType().equals( factName ) ) {
                        model.setFactName( mf.getClassName() );
                        break;
                    }
                }
            }
        }

        enumDropDown = (EnumDropDown) scorecardPropertiesGrid.getWidget( 1,
                                                                         1 );
        if ( enumDropDown.getSelectedIndex() > -1 ) {
            String fieldName = enumDropDown.getValue( enumDropDown.getSelectedIndex() );
            fieldName = fieldName.substring( 0, fieldName.indexOf( ":" ) ).trim();
            model.setFieldName( fieldName );
        } else {
            model.setFieldName( "" );
        }

        if ( ddReasonCodeField.getSelectedIndex() > -1 ) {
            String rcField = ddReasonCodeField.getValue( ddReasonCodeField.getSelectedIndex() );
            rcField = rcField.substring( 0, rcField.indexOf( ":" ) ).trim();
            model.setReasonCodeField( rcField );
        }

        model.getCharacteristics().clear();
        for ( final DirtyableFlexTable flexTable : characteristicsTables ) {
            final Characteristic characteristic = new Characteristic();
            characteristic.setName( ( (TextBox) flexTable.getWidget( 0,
                                                                     1 ) ).getValue() );

            //Characteristic Fact Type
            enumDropDown = (EnumDropDown) flexTable.getWidget( 2,
                                                               0 );
            if ( enumDropDown.getSelectedIndex() > -1 ) {
                final String simpleFactName = enumDropDown.getValue( enumDropDown.getSelectedIndex() );
                characteristic.setFact( simpleFactName );
                if ( oracleModelFields.get( simpleFactName ) != null ) {
                    for ( ModelField mf : oracleModelFields.get( simpleFactName ) ) {
                        if ( mf.getType().equals( simpleFactName ) ) {
                            characteristic.setFact( mf.getClassName() );
                            break;
                        }
                    }
                }

                //Characteristic Field (cannot be set if no Fact Type has been set)
                enumDropDown = (EnumDropDown) flexTable.getWidget( 2,
                                                                   1 );
                if ( enumDropDown.getSelectedIndex() > -1 ) {
                    String fieldName = enumDropDown.getValue( enumDropDown.getSelectedIndex() );
                    fieldName = fieldName.substring( 0, fieldName.indexOf( ":" ) ).trim();
                    characteristic.setField( fieldName );
                } else {
                    characteristic.setField( "" );
                }
                characteristic.setDataType( getDataTypeForField( simpleFactName,
                                                                 characteristic.getField() ) );
            }

            //Characteristic Reason Code
            characteristic.setReasonCode( ( (TextBox) flexTable.getWidget( 2,
                                                                           3 ) ).getValue() );

            //Characteristic Base Line Score
            final String baselineScore = ( (TextBox) flexTable.getWidget( 2,
                                                                          2 ) ).getValue();
            try {
                characteristic.setBaselineScore( Double.parseDouble( baselineScore ) );
            } catch ( Exception e ) {
                characteristic.setBaselineScore( 0.0d );
            }

            //Characteristic Attributes
            characteristic.getAttributes().clear();
            characteristic.getAttributes().addAll( characteristicsAttrMap.get( flexTable ).getList() );

            model.getCharacteristics().add( characteristic );
        }

        return model;
    }

    private Widget getScorecardProperties() {

        scorecardPropertiesGrid = new Grid( 4, 4 );
        scorecardPropertiesGrid.setCellSpacing( 5 );
        scorecardPropertiesGrid.setCellPadding( 5 );

        tbInitialScore = TextBoxFactory.getTextBox( DataType.TYPE_NUMERIC_DOUBLE );
        tbInitialScore.setText( Double.toString( model.getInitialScore() ) );

        String factName = model.getFactName();
        if ( factName.lastIndexOf( "." ) > -1 ) {
            // if fact is a fully qualified className, strip off the packageName
            factName = factName.substring( factName.lastIndexOf( "." ) + 1 );
        }
        final EnumDropDown dropDownFields = new EnumDropDown( "",
                                                              new DropDownValueChanged() {
                                                                  public void valueChanged( final String newText,
                                                                                            final String newValue ) {
                                                                      //do nothing
                                                                  }
                                                              }, DropDownData.create( new String[]{ } ) );

        EnumDropDown dropDownFacts = new EnumDropDown( factName,
                                                       new DropDownValueChanged() {
                                                           public void valueChanged( final String newText,
                                                                                     final String newValue ) {
                                                               String selectedField = model.getFieldName();
                                                               selectedField = selectedField + " : double";
                                                               dropDownFields.setDropDownData( selectedField,
                                                                                               DropDownData.create( getEligibleFields( newValue,
                                                                                                                                       typesForScore ) ) );
                                                           }
                                                       }, DropDownData.create( oracle.getFactTypes() ) );

        ddReasonCodeField = new EnumDropDown( "",
                                              new DropDownValueChanged() {
                                                  public void valueChanged( final String newText,
                                                                            final String newValue ) {
                                                      //do nothing
                                                  }
                                              }, DropDownData.create( new String[]{ } ) );

        final String rcField = model.getReasonCodeField() + " : List";
        ddReasonCodeField.setDropDownData( rcField,
                                           DropDownData.create( getEligibleFields( factName,
                                                                                   typesForRC ) ) );

        final boolean useReasonCodes = model.isUseReasonCodes();
        String reasonCodesAlgo = model.getReasonCodesAlgorithm();
        if ( reasonCodesAlgo == null || reasonCodesAlgo.trim().length() == 0 ) {
            reasonCodesAlgo = "none";
        }

        ddUseReasonCode = booleanEditor( Boolean.toString( useReasonCodes ) );
        ddReasonCodeAlgorithm = dropDownEditor( DropDownData.create( reasonCodeAlgorithms ),
                                                reasonCodesAlgo );
        tbBaselineScore = TextBoxFactory.getTextBox( DataType.TYPE_NUMERIC_DOUBLE );

        scorecardPropertiesGrid.setText( 0,
                                         0,
                                         "Facts" );
        scorecardPropertiesGrid.setText( 0,
                                         1,
                                         "Resultant Score Field" );
        scorecardPropertiesGrid.setText( 0,
                                         2,
                                         "Initial Score" );

        scorecardPropertiesGrid.setWidget( 1,
                                           0,
                                           dropDownFacts );
        scorecardPropertiesGrid.setWidget( 1,
                                           1,
                                           dropDownFields );
        scorecardPropertiesGrid.setWidget( 1,
                                           2,
                                           tbInitialScore );

        scorecardPropertiesGrid.setText( 2,
                                         0,
                                         "Use Reason Codes" );
        scorecardPropertiesGrid.setText( 2,
                                         1,
                                         "Resultant Reason Codes Field" );
        scorecardPropertiesGrid.setText( 2,
                                         2,
                                         "Reason Codes Algorithm" );
        scorecardPropertiesGrid.setText( 2,
                                         3,
                                         "Baseline Score" );

        scorecardPropertiesGrid.setWidget( 3,
                                           0,
                                           ddUseReasonCode );
        scorecardPropertiesGrid.setWidget( 3,
                                           1,
                                           ddReasonCodeField );
        scorecardPropertiesGrid.setWidget( 3,
                                           2,
                                           ddReasonCodeAlgorithm );
        scorecardPropertiesGrid.setWidget( 3,
                                           3,
                                           tbBaselineScore );

        /* TODO : Remove this explicitly Disabled Reasoncode support field*/
        ddUseReasonCode.setEnabled( false );

        tbBaselineScore.setText( Double.toString( model.getBaselineScore() ) );

        scorecardPropertiesGrid.getCellFormatter().setWidth( 0,
                                                             0,
                                                             "200px" );
        scorecardPropertiesGrid.getCellFormatter().setWidth( 0,
                                                             1,
                                                             "250px" );
        scorecardPropertiesGrid.getCellFormatter().setWidth( 0,
                                                             2,
                                                             "200px" );
        scorecardPropertiesGrid.getCellFormatter().setWidth( 0,
                                                             3,
                                                             "200px" );

        int index = Arrays.asList( oracle.getFactTypes() ).indexOf( factName );
        dropDownFacts.setSelectedIndex( index );
        dropDownFields.setDropDownData( model.getFieldName() + " : double", DropDownData.create( getEligibleFields( factName,
                                                                                                                    typesForScore ) ) );

        return scorecardPropertiesGrid;
    }

    private Widget getCharacteristics() {
        characteristicsPanel = new VerticalPanel();
        final HorizontalPanel toolbar = new HorizontalPanel();
        btnAddCharacteristic = new Button( "New Characteristic",
                                           new ClickHandler() {
                                               public void onClick( ClickEvent event ) {
                                                   addCharacteristic( null );
                                               }
                                           } );
        toolbar.add( btnAddCharacteristic );

        toolbar.setHeight( "24" );
        characteristicsPanel.add( toolbar );
        final SimplePanel gapPanel = new SimplePanel();
        gapPanel.add( new HTML( "<br/>" ) );
        characteristicsPanel.add( gapPanel );
        return characteristicsPanel;
    }

    private void removeCharacteristic( final DirtyableFlexTable selectedTable ) {
        if ( selectedTable != null ) {
            final TextBox tbName = (TextBox) selectedTable.getWidget( 0, 1 );
            String name = tbName.getValue();
            if ( name == null || name.trim().length() == 0 ) {
                name = "Untitled";
            }
            final String msg = "Are you sure you want to delete '" + ( name ) + "' Characteristic?";
            if ( Window.confirm( msg ) ) {
                characteristicsTables.remove( selectedTable );
                characteristicsAttrMap.remove( selectedTable );
                final Widget parent = selectedTable.getParent().getParent();
                final int i = characteristicsPanel.getWidgetIndex( parent );
                characteristicsPanel.remove( parent );
                characteristicsPanel.remove( i );
            }
        }
    }

    private void addAttribute( final DirtyableFlexTable selectedTable,
                               final Attribute attribute ) {
        Attribute newAttribute = null;
        if ( attribute != null ) {
            characteristicsAttrMap.get( selectedTable ).getList().add( attribute );
        } else {
            newAttribute = new Attribute();
            characteristicsAttrMap.get( selectedTable ).getList().add( newAttribute );
        }
        characteristicsAttrMap.get( selectedTable ).refresh();

        //disable the fact & field dropdowns
        ( (EnumDropDown) selectedTable.getWidget( 2,
                                                  0 ) ).setEnabled( false );
        ( (EnumDropDown) selectedTable.getWidget( 2,
                                                  1 ) ).setEnabled( false );
        final EnumDropDown edd = ( (EnumDropDown) selectedTable.getWidget( 2,
                                                                           1 ) );
        if ( edd.getSelectedIndex() > -1 ) {
            String field = edd.getValue( edd.getSelectedIndex() );
            field = field.substring( field.indexOf( ":" ) + 1 ).trim();
            final CellTable<Attribute> cellTable = (CellTable<Attribute>) characteristicsAttrMap.get( selectedTable ).getDataDisplays().iterator().next();
            final DynamicSelectionCell dynamicSelectionCell = (DynamicSelectionCell) cellTable.getColumn( 0 ).getCell();
            List<String> newOptions = null;
            if ( "double".equalsIgnoreCase( field ) || "int".equalsIgnoreCase( field ) ) {
                newOptions = Arrays.asList( numericOperators );
            } else if ( "boolean".equalsIgnoreCase( field ) ) {
                newOptions = Arrays.asList( booleanOperators );
                CustomEditTextCell etc = (CustomEditTextCell) cellTable.getColumn( 1 ).getCell();
                etc.setEnabled( false );
                ( (Button) selectedTable.getWidget( 0, 3 ) ).setEnabled( characteristicsAttrMap.get( selectedTable ).getList().size() != 2 );
                if ( newAttribute != null ) {
                    newAttribute.setValue( "N/A" );
                }
            } else if ( "String".equalsIgnoreCase( field ) ) {
                newOptions = Arrays.asList( stringOperators );
            }
            dynamicSelectionCell.setOptions( newOptions );
            if ( newAttribute != null ) {
                if ( newOptions != null ) {
                    newAttribute.setOperator( newOptions.get( 0 ) );
                }
            }
        }
    }

    private DirtyableFlexTable addCharacteristic( final Characteristic characteristic ) {
        final DirtyableFlexTable cGrid = new DirtyableFlexTable();
        cGrid.setBorderWidth( 0 );
        cGrid.setCellPadding( 1 );
        cGrid.setCellSpacing( 1 );

        cGrid.setStyleName( "rule-ListHeader" );

        Button btnAddAttribute = new Button( "Add Attribute",
                                             new ClickHandler() {
                                                 public void onClick( final ClickEvent event ) {
                                                     addAttribute( cGrid,
                                                                   null );
                                                 }
                                             } );

        Button btnRemoveCharacteristic = new Button( "Remove Characteristic",
                                                     new ClickHandler() {
                                                         public void onClick( ClickEvent event ) {
                                                             removeCharacteristic( cGrid );
                                                         }
                                                     } );

        String selectedFact = "";
        if ( characteristic != null ) {
            selectedFact = characteristic.getFact();
            if ( selectedFact.lastIndexOf( "." ) > -1 ) {
                selectedFact = selectedFact.substring( selectedFact.lastIndexOf( "." ) + 1 );
            }
        }
        final EnumDropDown dropDownFields = new EnumDropDown( "",
                                                              new DropDownValueChanged() {
                                                                  public void valueChanged( final String newText,
                                                                                            final String newValue ) {
                                                                      //do nothing
                                                                  }
                                                              }, DropDownData.create( new String[]{ } ) );

        EnumDropDown dropDownFacts = new EnumDropDown( selectedFact,
                                                       new DropDownValueChanged() {
                                                           public void valueChanged( final String newText,
                                                                                     final String newValue ) {
                                                               String selectedField = "";
                                                               if ( characteristic != null ) {
                                                                   selectedField = characteristic.getField();
                                                                   selectedField = selectedField + " : " + characteristic.getDataType();
                                                               }
                                                               dropDownFields.setDropDownData( selectedField, DropDownData.create( getEligibleFields( newValue,
                                                                                                                                                      typesForAttributes ) ) );
                                                               //dropDownFields.setSelectedIndex(0);
                                                           }
                                                       }, DropDownData.create( oracle.getFactTypes() ) );

        final DropDownData dropDownData = DropDownData.create( getEligibleFields( selectedFact,
                                                                                  typesForAttributes ) );
        dropDownFields.setDropDownData( "",
                                        dropDownData );

        cGrid.setWidget( 0,
                         0,
                         new Label( "Name" ) );
        final TextBox tbName = TextBoxFactory.getTextBox( DataType.TYPE_STRING );
        cGrid.setWidget( 0,
                         1,
                         tbName );
        cGrid.setWidget( 0,
                         2,
                         btnRemoveCharacteristic );
        cGrid.setWidget( 0,
                         3,
                         btnAddAttribute );

        cGrid.setWidget( 1,
                         0,
                         new Label( "Fact" ) );
        cGrid.setWidget( 1,
                         1,
                         new Label( "Characteristic" ) );
        cGrid.setWidget( 1,
                         2,
                         new Label( "Baseline Score" ) );
        cGrid.setWidget( 1,
                         3,
                         new Label( "Reason Code" ) );

        cGrid.setWidget( 2,
                         0,
                         dropDownFacts );
        cGrid.setWidget( 2,
                         1,
                         dropDownFields );

        final TextBox tbBaseline = TextBoxFactory.getTextBox( DataType.TYPE_NUMERIC_DOUBLE );
        final boolean useReasonCodesValue = "true".equalsIgnoreCase( ddUseReasonCode.getValue( ddUseReasonCode.getSelectedIndex() ) );
        tbBaseline.setEnabled( useReasonCodesValue );
        cGrid.setWidget( 2,
                         2,
                         tbBaseline );

        final TextBox tbReasonCode = TextBoxFactory.getTextBox( DataType.TYPE_STRING );
        tbReasonCode.setEnabled( useReasonCodesValue );
        cGrid.setWidget( 2,
                         3,
                         tbReasonCode );

        final SimplePanel gapPanel = new SimplePanel();
        gapPanel.add( new HTML( "<br/>" ) );

        final VerticalPanel panel = new VerticalPanel();
        panel.add( cGrid );
        panel.add( addAttributeCellTable( cGrid,
                                          characteristic ) );
        panel.setWidth( "100%" );
        DecoratorPanel decoratorPanel = new DecoratorPanel();
        decoratorPanel.add( panel );

        characteristicsPanel.add( decoratorPanel );
        characteristicsPanel.add( gapPanel );
        characteristicsTables.add( cGrid );

        cGrid.getColumnFormatter().setWidth( 0,
                                             "150px" );
        cGrid.getColumnFormatter().setWidth( 1,
                                             "250px" );
        cGrid.getColumnFormatter().setWidth( 2,
                                             "150px" );
        cGrid.getColumnFormatter().setWidth( 3,
                                             "150px" );

        if ( characteristic != null ) {
            tbReasonCode.setValue( characteristic.getReasonCode() );
            tbBaseline.setValue( "" + characteristic.getBaselineScore() );

            final int index = Arrays.asList( oracle.getFactTypes() ).indexOf( selectedFact );
            dropDownFacts.setSelectedIndex( index );

            final String modifiedFieldName = characteristic.getField() + " : " + characteristic.getDataType();
            dropDownFields.setSelectedIndex( Arrays.asList( getEligibleFields( selectedFact, typesForAttributes ) ).indexOf( modifiedFieldName ) );
            tbName.setValue( characteristic.getName() );
        }

        return cGrid;
    }

    private Widget addAttributeCellTable( final DirtyableFlexTable cGrid,
                                          final Characteristic characteristic ) {
        final CellTable<Attribute> attributeCellTable = new CellTable<Attribute>();
        final List<String> operators = new ArrayList<String>();
        String dataType;
        if ( characteristic == null ) {
            dataType = "String";
        } else {
            dataType = characteristic.getDataType();
        }

        if ( "String".equalsIgnoreCase( dataType ) ) {
            operators.addAll( Arrays.asList( stringOperators ) );
        } else if ( "boolean".equalsIgnoreCase( dataType ) ) {
            operators.addAll( Arrays.asList( booleanOperators ) );
        } else {
            operators.addAll( Arrays.asList( numericOperators ) );
        }

        //Operators column
        final DynamicSelectionCell categoryCell = new DynamicSelectionCell( operators );
        final Column<Attribute, String> operatorColumn = new Column<Attribute, String>( categoryCell ) {
            public String getValue( final Attribute object ) {
                return object.getOperator();
            }
        };
        operatorColumn.setFieldUpdater( new FieldUpdater<Attribute, String>() {
            public void update( int index,
                                Attribute object,
                                String value ) {
                object.setOperator( value );
                attributeCellTable.redraw();
            }
        } );

        //Value column
        final Column<Attribute, String> valueColumn = new Column<Attribute, String>( new CustomEditTextCell() ) {
            public String getValue( final Attribute attribute ) {
                return attribute.getValue();
            }
        };
        valueColumn.setFieldUpdater( new FieldUpdater<Attribute, String>() {
            public void update( int index,
                                Attribute object,
                                String value ) {
                object.setValue( value );
                attributeCellTable.redraw();
            }
        } );

        //Partial Score column
        final EditTextCell partialScoreCell = new EditTextCell();
        final Column<Attribute, String> partialScoreColumn = new Column<Attribute, String>( partialScoreCell ) {
            public String getValue( final Attribute attribute ) {
                return "" + attribute.getPartialScore();
            }
        };
        partialScoreColumn.setFieldUpdater( new FieldUpdater<Attribute, String>() {
            public void update( int index,
                                Attribute object,
                                String value ) {
                try {
                    double d = Double.parseDouble( value );
                    object.setPartialScore( d );
                } catch ( Exception e1 ) {
                    partialScoreCell.clearViewData( object );
                }
                attributeCellTable.redraw();
            }
        } );

        //Reason Code column
        final Column<Attribute, String> reasonCodeColumn = new Column<Attribute, String>( new EditTextCell() ) {
            public String getValue( final Attribute attribute ) {
                return attribute.getReasonCode();
            }
        };
        reasonCodeColumn.setFieldUpdater( new FieldUpdater<Attribute, String>() {
            public void update( int index,
                                Attribute object,
                                String value ) {
                object.setReasonCode( value );
                attributeCellTable.redraw();
            }
        } );

        final ActionCell.Delegate<Attribute> delegate = new ActionCell.Delegate<Attribute>() {
            public void execute( final Attribute attribute ) {
                if ( Window.confirm( "Remove this attribute?" ) ) {
                    final List<Attribute> list = characteristicsAttrMap.get( cGrid ).getList();
                    list.remove( attribute );
                    ( (EnumDropDown) cGrid.getWidget( 2,
                                                      0 ) ).setEnabled( list.size() == 0 );
                    ( (EnumDropDown) cGrid.getWidget( 2,
                                                      1 ) ).setEnabled( list.size() == 0 );
                    ( (Button) cGrid.getWidget( 0,
                                                3 ) ).setEnabled( list.size() != 2 );
                    attributeCellTable.redraw();
                }
            }
        };

        final Cell<Attribute> actionCell = new ActionCell<Attribute>( "Remove",
                                                                      delegate );
        final Column<Attribute, String> actionColumn = new IdentityColumn( actionCell );

        // Add the columns.
        attributeCellTable.addColumn( operatorColumn,
                                      "Operator" );
        attributeCellTable.addColumn( valueColumn,
                                      "Value" );
        attributeCellTable.addColumn( partialScoreColumn,
                                      "Partial Score" );
        attributeCellTable.addColumn( reasonCodeColumn,
                                      "Reason Code" );
        attributeCellTable.addColumn( actionColumn,
                                      "Actions" );
        attributeCellTable.setWidth( "100%",
                                     true );

        attributeCellTable.setColumnWidth( operatorColumn,
                                           5.0,
                                           Style.Unit.PCT );
        attributeCellTable.setColumnWidth( valueColumn,
                                           10.0,
                                           Style.Unit.PCT );
        attributeCellTable.setColumnWidth( partialScoreColumn,
                                           10.0,
                                           Style.Unit.PCT );
        attributeCellTable.setColumnWidth( reasonCodeColumn,
                                           10.0,
                                           Style.Unit.PCT );
        attributeCellTable.setColumnWidth( actionColumn,
                                           5.0,
                                           Style.Unit.PCT );

        ListDataProvider<Attribute> dataProvider = new ListDataProvider<Attribute>();
        dataProvider.addDataDisplay( attributeCellTable );
        characteristicsAttrMap.put( cGrid,
                                    dataProvider );
        return ( attributeCellTable );
    }

    private String[] getEligibleFields( final String factName,
                                        final String[] types ) {
        final List<String> fields = new ArrayList<String>();
        for ( final String clazz : oracleModelFields.keySet() ) {
            if ( clazz.equalsIgnoreCase( factName ) ) {
                for ( final ModelField field : oracleModelFields.get( clazz ) ) {
                    String type = field.getClassName();
                    if ( type.lastIndexOf( "." ) > -1 ) {
                        type = type.substring( type.lastIndexOf( "." ) + 1 );
                    }
                    for ( final String t : types ) {
                        if ( type.equalsIgnoreCase( t ) ) {
                            fields.add( field.getName() + " : " + type );
                            break;
                        }
                    }
                }
            }
        }
        return fields.toArray( new String[]{ } );
    }

    private String getDataTypeForField( final String factName,
                                        final String fieldName ) {
        for ( final String clazz : oracleModelFields.keySet() ) {
            if ( clazz.equalsIgnoreCase( factName ) ) {
                for ( final ModelField field : oracleModelFields.get( clazz ) ) {
                    if ( fieldName.equalsIgnoreCase( field.getName() ) ) {
                        String type = field.getClassName();
                        if ( type.endsWith( "String" ) ) {
                            type = "String";
                        } else if ( type.endsWith( "Double" ) ) {
                            type = "Double";
                        } else if ( type.endsWith( "Integer" ) ) {
                            type = "int";
                        }
                        return type;
                    }
                }
            }
        }
        return null;
    }

    private EnumDropDown booleanEditor( final String currentValue ) {
        return new EnumDropDown( currentValue,
                                 new DropDownValueChanged() {
                                     public void valueChanged( final String newText,
                                                               final String newValue ) {
                                         boolean enabled = "true".equalsIgnoreCase( newValue );
                                         ddReasonCodeAlgorithm.setEnabled( enabled );
                                         tbBaselineScore.setEnabled( enabled );
                                         ddReasonCodeField.setEnabled( enabled );
                                         for ( final DirtyableFlexTable cGrid : characteristicsTables ) {
                                             //baseline score for each characteristic
                                             ( (TextBox) cGrid.getWidget( 2,
                                                                          2 ) ).setEnabled( enabled );
                                             //reason code for each characteristic
                                             ( (TextBox) cGrid.getWidget( 2,
                                                                          3 ) ).setEnabled( enabled );
                                         }
                                     }
                                 },
                                 DropDownData.create( new String[]{ "false", "true" } ) );
    }

    private EnumDropDown dropDownEditor( final DropDownData dropDownData,
                                         final String currentValue ) {
        return new EnumDropDown( currentValue,
                                 new DropDownValueChanged() {
                                     public void valueChanged( final String newText,
                                                               final String newValue ) {
                                         //valueHasChanged(newValue);
                                     }
                                 },
                                 dropDownData );
    }
}
