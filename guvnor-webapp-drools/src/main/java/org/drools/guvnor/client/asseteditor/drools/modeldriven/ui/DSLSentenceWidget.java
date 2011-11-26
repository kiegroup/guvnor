/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.common.DatePickerLabel;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DropDownValueChanged;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This displays a widget to edit a DSL sentence.
 */
public class DSLSentenceWidget extends RuleModellerWidget {

    private final List<Widget>      widgets;
    private final List<DSLDropDown> dropDownWidgets;
    private final DSLSentence       sentence;
    private final VerticalPanel     layout;
    private HorizontalPanel         currentRow;
    private boolean                 readOnly;

    public DSLSentenceWidget(RuleModeller modeller,
                             DSLSentence sentence) {
        this( modeller,
              sentence,
              null );
    }

    public DSLSentenceWidget(RuleModeller modeller,
                             DSLSentence sentence,
                             Boolean readOnly) {
        super( modeller );
        widgets = new ArrayList<Widget>();
        dropDownWidgets = new ArrayList<DSLDropDown>();
        this.sentence = sentence;

        if ( readOnly == null ) {
            this.readOnly = false;
        } else {
            this.readOnly = readOnly;
        }

        this.layout = new VerticalPanel();
        this.currentRow = new HorizontalPanel();
        this.layout.add( currentRow );
        this.layout.setCellWidth( currentRow,
                                  "100%" );
        this.layout.setWidth( "100%" );

        if ( this.readOnly ) {
            this.layout.addStyleName( "editor-disabled-widget" );
        }

        init();
    }

    private void init() {
        makeWidgets( this.sentence );
        initWidget( this.layout );
    }

    /**
     * This will take a DSL line item, and split it into widget thingamies for
     * displaying. One day, if this is too complex, this will have to be done on
     * the server side.
     */
    public void makeWidgets(DSLSentence sentence) {

        String dslDefinition = sentence.getDefinition();
        List<String> dslValues = sentence.getValues();
        int index = 0;

        int startVariable = dslDefinition.indexOf( "{" );
        List<Widget> lineWidgets = new ArrayList<Widget>();

        boolean firstOneIsBracket = (dslDefinition.indexOf( "{" ) == 0);

        String startLabel = "";
        if ( startVariable > 0 ) {
            startLabel = dslDefinition.substring( 0,
                                                  startVariable );
        } else if ( !firstOneIsBracket ) {
            // There are no curly brackets in the text. Just print it
            startLabel = dslDefinition;
        }

        Widget label = getLabel( startLabel );
        lineWidgets.add( label );

        while ( startVariable > 0 || firstOneIsBracket ) {
            firstOneIsBracket = false;
            int endVariable = getIndexForEndOfVariable( dslDefinition,
                                                        startVariable );
            String currVariable = dslDefinition.substring( startVariable + 1,
                                                           endVariable );

            String value = dslValues.get( index );
            Widget varWidget = processVariable( currVariable,
                                                value );
            lineWidgets.add( varWidget );
            index++;

            // Parse out the next label between variables
            startVariable = dslDefinition.indexOf( "{",
                                                   endVariable );
            String lbl;
            if ( startVariable > 0 ) {
                lbl = dslDefinition.substring( endVariable + 1,
                                               startVariable );
            } else {
                lbl = dslDefinition.substring( endVariable + 1,
                                               dslDefinition.length() );
            }

            if ( lbl.indexOf( "\\n" ) > -1 ) {
                String[] lines = lbl.split( "\\\\n" );
                for ( int i = 0; i < lines.length; i++ ) {
                    lineWidgets.add( new NewLine() );
                    lineWidgets.add( getLabel( lines[i] ) );
                }
            } else {
                Widget currLabel = getLabel( lbl );
                lineWidgets.add( currLabel );
            }

        }

        for ( Widget widg : lineWidgets ) {
            addWidget( widg );
        }

        updateEnumDropDowns();
    }

    private int getIndexForEndOfVariable(String dsl,
                                         int start) {
        int end = -1;
        int bracketCount = 0;
        if ( start > dsl.length() ) {
            return end;
        }
        for ( int i = start; i < dsl.length(); i++ ) {
            char c = dsl.charAt( i );
            if ( c == '{' ) {
                bracketCount++;
            }
            if ( c == '}' ) {
                bracketCount--;
                if ( bracketCount == 0 ) {
                    end = i;
                    return end;
                }
            }
        }
        return -1;
    }

    class NewLine extends Widget {
    }

    public Widget processVariable(String currVariable,
                                  String value) {

        Widget result = null;
        // Formats are: 
        // <varName>:ENUM:<Field.type>
        // <varName>:DATE:<dateFormat>
        // <varName>:BOOLEAN:[checked | unchecked] <-initial value
        // Note: <varName> is no longer overwritten with the value; values are stored in DSLSentence.values()

        if ( currVariable.contains( ":" ) ) {
            if ( currVariable.contains( ":" + DSLSentence.ENUM_TAG + ":" ) ) {
                result = getEnumDropdown( currVariable,
                                          value );
            } else if ( currVariable.contains( ":" + DSLSentence.DATE_TAG + ":" ) ) {
                result = getDateSelector( currVariable,
                                          value );
            } else if ( currVariable.contains( ":" + DSLSentence.BOOLEAN_TAG + ":" ) ) {
                result = getCheckbox( currVariable,
                                      value );
            } else {
                String regex = currVariable.substring( currVariable.indexOf( ":" ) + 1,
                                                       currVariable.length() );
                result = getBox( value,
                                 regex );
            }
        } else {
            result = getBox( value,
                             "" );
        }

        return result;
    }

    public Widget getEnumDropdown(String variableDef,
                                  String value) {

        DSLDropDown resultWidget = new DSLDropDown( variableDef,
                                                    value );
        dropDownWidgets.add( resultWidget );
        return resultWidget;
    }

    public Widget getBox(String variableDef,
                         String regex) {

        FieldEditor currentBox = new FieldEditor();
        currentBox.setVisibleLength( variableDef.length() + 1 );
        currentBox.setText( variableDef );
        currentBox.setRestriction( regex );

        return currentBox;
    }

    public Widget getCheckbox(String variableDef,
                              String value) {
        return new DSLCheckBox( variableDef,
                                value );
    }

    public Widget getDateSelector(String variableDef,
                                  String value) {
        String[] parts = variableDef.split( ":" + DSLSentence.DATE_TAG + ":" );
        return new DSLDateSelector( value,
                                    parts[1] );
    }

    public Widget getLabel(String labelDef) {
        Label label = new SmallLabel();
        label.setText( labelDef.trim() );

        return label;
    }

    private void addWidget(Widget currentBox) {
        if ( currentBox instanceof NewLine ) {
            currentRow = new HorizontalPanel();
            layout.add( currentRow );
            layout.setCellWidth( currentRow,
                                 "100%" );
        } else {
            currentRow.add( currentBox );
        }
        widgets.add( currentBox );
    }

    /**
     * This will go through the widgets and extract the values
     */
    protected void updateSentence() {
        int iVariable = 0;
        for ( Iterator<Widget> iter = widgets.iterator(); iter.hasNext(); ) {
            Widget wid = iter.next();
            if ( wid instanceof FieldEditor ) {
                FieldEditor editor = (FieldEditor) wid;
                sentence.getValues().set( iVariable++,
                                          editor.getText().trim() );
            } else if ( wid instanceof DSLDropDown ) {
                DSLDropDown drop = (DSLDropDown) wid;
                sentence.getValues().set( iVariable++,
                                          drop.getSelectedValue() );

            } else if ( wid instanceof DSLCheckBox ) {
                DSLCheckBox check = (DSLCheckBox) wid;
                sentence.getValues().set( iVariable++,
                                          check.getCheckedValue() );

            } else if ( wid instanceof DSLDateSelector ) {
                DSLDateSelector dateSel = (DSLDateSelector) wid;
                String dateString = dateSel.getDateString();
                sentence.getValues().set( iVariable++,
                                          dateString );

            }
        }
        this.setModified( true );
    }

    class FieldEditor extends DirtyableComposite {

        private TextBox   box;
        private String    oldValue  = "";
        private String    regex     = "";
        private Constants constants = ((Constants) GWT.create( Constants.class ));

        public FieldEditor() {
            box = new TextBox();
            box.addChangeHandler( new ChangeHandler() {

                public void onChange(ChangeEvent event) {
                    TextBox otherBox = (TextBox) event.getSource();

                    if ( !regex.equals( "" ) && !otherBox.getText().matches( regex ) ) {
                        Window.alert( constants.TheValue0IsNotValidForThisField( otherBox.getText() ) );
                        box.setText( oldValue );
                    } else {
                        oldValue = otherBox.getText();
                        updateSentence();
                        makeDirty();
                    }
                }
            } );

            //Wrap widget within a HorizontalPanel to add a space before and after the Widget
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;" ) );
            hp.add( box );
            hp.add( new HTML( "&nbsp;" ) );

            initWidget( hp );
        }

        public void setRestriction(String regex) {
            this.regex = regex;
        }

        public void setText(String t) {
            box.setText( t );
        }

        public void setVisibleLength(int l) {
            box.setVisibleLength( l );
        }

        public String getText() {
            return box.getText();
        }

    }

    class DSLDropDown extends DirtyableComposite {

        final SuggestionCompletionEngine completions  = getModeller().getSuggestionCompletions();
        EnumDropDown                     resultWidget = null;
        String                           factType;
        String                           factField;
        String                           selectedValue;

        public DSLDropDown(final String variableDef,
                           final String value) {

            //Parse Fact Type and Field for retrieving DropDown data from Suggestion Completion Engine
            //Format for the drop-down definition within a DSLSentence is <varName>:<type>:<Fact.field>
            int lastIndex = variableDef.lastIndexOf( ":" );
            String factAndField = variableDef.substring( lastIndex + 1,
                                                         variableDef.length() );
            int dotIndex = factAndField.indexOf( "." );
            factType = factAndField.substring( 0,
                                               dotIndex );
            factField = factAndField.substring( dotIndex + 1,
                                                factAndField.length() );
            selectedValue = value;

            //ChangeHandler for drop-down; not called when initialising the drop-down
            DropDownValueChanged handler = new DropDownValueChanged() {

                public void valueChanged(String newText,
                                         String newValue) {

                    makeDirty();
                    selectedValue = newValue;

                    //When the value changes we need to reset the content of *ALL* DSLSentenceWidget drop-downs.
                    //An improvement would be to determine the chain of dependent drop-downs and only update
                    //children of the one whose value changes. However in reality DSLSentences only contain
                    //a couple of drop-downs so it's quicker to simply update them all.
                    updateEnumDropDowns();
                }
            };

            DropDownData dropDownData = getDropDownData();
            resultWidget = new EnumDropDown( value,
                                             handler,
                                             dropDownData );

            //Wrap widget within a HorizontalPanel to add a space before and after the Widget
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;" ) );
            hp.add( resultWidget );
            hp.add( new HTML( "&nbsp;" ) );

            initWidget( hp );
        }

        public String getSelectedValue() {
            int selectedIndex = resultWidget.getSelectedIndex();
            if ( selectedIndex != -1 ) {
                return resultWidget.getValue( selectedIndex );
            } else {
                return "";
            }
        }

        public void refreshDropDownData() {
            resultWidget.setDropDownData( selectedValue,
                                          getDropDownData() );
        }

        private DropDownData getDropDownData() {
            DropDownData dropDownData = completions.getEnums( factType,
                                                              factField,
                                                              sentence.getEnumFieldValueMap() );
            return dropDownData;
        }

    }

    class DSLCheckBox extends Composite {

        ListBox resultWidget = null;

        public DSLCheckBox(String variableDef,
                           String value) {

            resultWidget = new ListBox();
            resultWidget.addItem( "true" );
            resultWidget.addItem( "false" );

            if ( value.equalsIgnoreCase( "true" ) ) {
                resultWidget.setSelectedIndex( 0 );
            } else {
                resultWidget.setSelectedIndex( 1 );
            }

            resultWidget.addChangeHandler( new ChangeHandler() {

                public void onChange(ChangeEvent event) {
                    updateSentence();
                    makeDirty();
                }
            } );

            resultWidget.setVisible( true );

            //Wrap widget within a HorizontalPanel to add a space before and after the Widget
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;" ) );
            hp.add( resultWidget );
            hp.add( new HTML( "&nbsp;" ) );
            initWidget( hp );
        }

        public String getCheckedValue() {
            return this.resultWidget.getSelectedIndex() == 0 ? "true" : "false";

        }
    }

    class DSLDateSelector extends Composite {

        DatePickerLabel resultWidget = null;

        public DSLDateSelector(String selectedDate,
                               String dateFormat) {

            resultWidget = new DatePickerLabel( selectedDate,
                                                dateFormat );

            resultWidget.addValueChanged( new ValueChanged() {
                public void valueChanged(String newValue) {
                    updateSentence();
                    makeDirty();
                }
            } );

            //Wrap widget within a HorizontalPanel to add a space before and after the Widget
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;" ) );
            hp.add( resultWidget );
            hp.add( new HTML( "&nbsp;" ) );
            initWidget( hp );
        }

        public String getDateString() {
            return resultWidget.getDateString();
        }
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    //When a value in a drop-down changes we need to reset the content of *ALL* DSLSentenceWidget drop-downs.
    //An improvement would be to determine the chain of dependent drop-downs and only update children of the
    //one whose value changes. However in reality DSLSentences only contain a couple of drop-downs so it's 
    //quicker to simply update them all.
    private void updateEnumDropDowns() {

        //Copy selections in UI to data-model, used to drive dependent drop-downs
        updateSentence();

        for ( DSLDropDown dd : dropDownWidgets ) {
            dd.refreshDropDownData();

            //Copy selections in UI to data-model again, as updating the drop-downs
            //can lead to some selected values being cleared when dependent drop-downs
            //are used.
            updateSentence();
        }
    }

}
