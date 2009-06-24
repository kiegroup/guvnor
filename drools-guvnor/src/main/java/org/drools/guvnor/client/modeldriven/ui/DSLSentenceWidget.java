package org.drools.guvnor.client.modeldriven.ui;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.DSLSentence;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.util.Format;

/**
 * This displays a widget to edit a DSL sentence.
 * 
 * @author Michael Neale
 */
public class DSLSentenceWidget extends Composite {

    private static final String        ENUM_TAG    = "ENUM";
    private static final String        DATE_TAG    = "DATE";
    private static final String        BOOLEAN_TAG = "BOOLEAN";
    private final List                 widgets;
    private final DSLSentence          sentence;
    private SuggestionCompletionEngine completions;
    private final VerticalPanel        layout;
    private HorizontalPanel            currentRow;

    public DSLSentenceWidget(DSLSentence sentence,
                             SuggestionCompletionEngine completions) {
        widgets = new ArrayList();
        this.sentence = sentence;
        this.completions = completions;
        this.layout = new VerticalPanel();
        this.currentRow = new HorizontalPanel();
        this.layout.add( currentRow );
        this.layout.setCellWidth( currentRow,
                                  "100%" );
        this.layout.setWidth( "100%" );
        init();
    }

    private void init() {
        makeWidgets( this.sentence.sentence );
        initWidget( this.layout );
    }

    /**
     * This will take a DSL line item, and split it into widget thingamies for
     * displaying. One day, if this is too complex, this will have to be done on
     * the server side.
     */
    public void makeWidgets(String dslLine) {

        int startVariable = dslLine.indexOf( "{" );
        List<Widget> lineWidgets = new ArrayList<Widget>();

        boolean firstOneIsBracket = (dslLine.indexOf( "{" ) == 0);

        String startLabel = "";
        if ( startVariable > 0 ) {
            startLabel = dslLine.substring( 0,
                                            startVariable );
        } else if ( !firstOneIsBracket ) {
            // There are no curly brackets in the text.
            // Just print it
            startLabel = dslLine;
        }

        Widget label = getLabel( startLabel );
        lineWidgets.add( label );

        while ( startVariable > 0 || firstOneIsBracket ) {
            firstOneIsBracket = false;

            int endVariable = dslLine.indexOf( "}",
                                               startVariable );
            String currVariable = dslLine.substring( startVariable + 1,
                                                     endVariable );

            Widget varWidget = processVariable( currVariable );
            lineWidgets.add( varWidget );

            // Parse out the next label between variables
            startVariable = dslLine.indexOf( "{",
                                             endVariable );
            String lbl;
            if ( startVariable > 0 ) {
                lbl = dslLine.substring( endVariable + 1,
                                         startVariable );
            } else {
                lbl = dslLine.substring( endVariable + 1,
                                         dslLine.length() );
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
        updateSentence();
    }

    class NewLine extends Widget {
    }

    public Widget processVariable(String currVariable) {

        Widget result = null;
        // Formats are: <varName>:ENUM:<Field.type>
        // <varName>:DATE:<dateFormat>
        // <varName>:BOOLEAN:[checked | unchecked] <-initial value

        if ( currVariable.contains( ":" ) ) {
            if ( currVariable.contains( ":" + ENUM_TAG + ":" ) ) {
                result = getEnumDropdown( currVariable );
            } else if ( currVariable.contains( ":" + DATE_TAG + ":" ) ) {
                result = getDateSelector( currVariable );
            } else if ( currVariable.contains( ":" + BOOLEAN_TAG + ":" ) ) {
                result = getCheckbox( currVariable );
            } else {
                String regex = currVariable.substring( currVariable.indexOf( ":" ) + 1,
                                                       currVariable.length() );
                result = getBox( currVariable,
                                 regex );
            }
        } else {
            result = getBox( currVariable,
                             "" );
        }

        return result;
    }

    public Widget getEnumDropdown(String variableDef) {

        Widget resultWidget = new DSLDropDown( variableDef );
        return resultWidget;
    }

    public Widget getBox(String variableDef,
                         String regex) {

        int colonIndex = variableDef.indexOf( ":" );
        if ( colonIndex > 0 ) {
            variableDef = variableDef.substring( 0,
                                                 colonIndex );
        }
        FieldEditor currentBox = new FieldEditor();
        currentBox.setVisibleLength( variableDef.length() + 1 );
        currentBox.setText( variableDef );
        currentBox.setRestriction( regex );

        return currentBox;
    }

    public Widget getCheckbox(String variableDef) {
        return new DSLCheckBox( variableDef );
    }

    public Widget getDateSelector(String variableDef) {
        String[] parts = variableDef.split( ":" + DATE_TAG + ":" );

        return new DSLDateSelector( parts[0],
                                    parts[1] );
    }

    public Widget getLabel(String labelDef) {
        Label label = new SmallLabel();
        label.setText( labelDef );

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
     * This will go through the widgets and build up a sentence.
     */
    protected void updateSentence() {
        String newSentence = "";
        for ( Iterator iter = widgets.iterator(); iter.hasNext(); ) {
            Widget wid = (Widget) iter.next();
            if ( wid instanceof Label ) {
                newSentence = newSentence + ((Label) wid).getText();
            } else if ( wid instanceof FieldEditor ) {
                FieldEditor editor = (FieldEditor) wid;

                String varString = editor.getText();
                String restriction = editor.getRestriction();
                if ( !restriction.equals( "" ) ) {
                    varString = varString + ":" + restriction;
                }

                newSentence = newSentence + "{" + varString + "}";
            } else if ( wid instanceof DSLDropDown ) {

                // Add the meta-data back to the field so that is shows up as a
                // dropdown when refreshed from repo
                DSLDropDown drop = (DSLDropDown) wid;
                ListBox box = drop.getListBox();
                String type = drop.getType();
                String factAndField = drop.getFactAndField();

                newSentence = newSentence + "{" + box.getValue( box.getSelectedIndex() ) + ":" + type + ":" + factAndField + "} ";
            } else if ( wid instanceof DSLCheckBox ) {

                DSLCheckBox check = (DSLCheckBox) wid;
                String checkValue = check.getCheckedValue();
                newSentence = newSentence + "{" + checkValue + ":" + check.getType() + ":" + checkValue + "} ";
            } else if ( wid instanceof DSLDateSelector ) {
                DSLDateSelector dateSel = (DSLDateSelector) wid;
                String dateString = dateSel.getDateString();
                String format = dateSel.getVisualFormat();
                newSentence = newSentence + "{" + dateString + ":" + dateSel.getType() + ":" + format + "} ";
            } else if ( wid instanceof NewLine ) {
                newSentence = newSentence + "\\n";
            }
        }
        this.sentence.sentence = newSentence.trim();
    }

    class FieldEditor extends DirtyableComposite {

        private TextBox         box;
        private HorizontalPanel panel     = new HorizontalPanel();
        private String          oldValue  = "";
        private String          regex     = "";
        private Constants       constants = ((Constants) GWT.create( Constants.class ));

        public FieldEditor() {
            box = new TextBox();
            // box.setStyleName( "dsl-field-TextBox" );

            panel.add( new HTML( "&nbsp;" ) );
            panel.add( box );
            panel.add( new HTML( "&nbsp;" ) );

            box.addChangeListener( new ChangeListener() {
                public void onChange(Widget w) {
                    TextBox otherBox = (TextBox) w;

                    if ( !regex.equals( "" ) && !otherBox.getText().matches( regex ) ) {
                        Window.alert( Format.format( constants.TheValue0IsNotValidForThisField(),
                                                     otherBox.getText() ) );
                        box.setText( oldValue );
                    } else {
                        oldValue = otherBox.getText();
                        updateSentence();
                        makeDirty();
                    }
                }
            } );

            initWidget( panel );
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

        public void setRestriction(String regex) {
            this.regex = regex;
        }

        public String getRestriction() {
            return this.regex;
        }

        public boolean isValid() {
            boolean result = true;
            if ( !regex.equals( "" ) ) result = this.box.getText().matches( this.regex );

            return result;
        }
    }

    class DSLDropDown extends DirtyableComposite {

        ListBox        resultWidget = null;
        // Format for the dropdown def is <varName>:<type>:<Fact.field>
        private String varName      = "";
        private String type         = "";
        private String factAndField = "";

        public DSLDropDown(String variableDef) {
            int firstIndex = variableDef.indexOf( ":" );
            int lastIndex = variableDef.lastIndexOf( ":" );
            varName = variableDef.substring( 0,
                                             firstIndex );
            type = variableDef.substring( firstIndex + 1,
                                          lastIndex );
            factAndField = variableDef.substring( lastIndex + 1,
                                                  variableDef.length() );

            int dotIndex = factAndField.indexOf( "." );
            String type = factAndField.substring( 0,
                                                  dotIndex );
            String field = factAndField.substring( dotIndex + 1,
                                                   factAndField.length() );

            String[] data = completions.getEnumValues( type,
                                                       field );
            ListBox list = new ListBox();

            if ( data != null ) {
                int selected = -1;
                for ( int i = 0; i < data.length; i++ ) {
                    String realValue = data[i];
                    String display = data[i];
                    if ( data[i].indexOf( '=' ) > -1 ) {
                        String[] vs = ConstraintValueEditorHelper.splitValue( data[i] );
                        realValue = vs[0];
                        display = vs[1];
                    }
                    if ( varName.equals( realValue ) ) {
                        selected = i;
                    }
                    list.addItem( display,
                                  realValue );
                }
                if ( selected >= 0 ) list.setSelectedIndex( selected );
            }
            list.addChangeListener( new ChangeListener() {
                public void onChange(Widget w) {
                    updateSentence();
                    makeDirty();
                }
            } );

            initWidget( list );
            resultWidget = list;
        }

        public ListBox getListBox() {
            return resultWidget;
        }

        public void setListBox(ListBox resultWidget) {
            this.resultWidget = resultWidget;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFactAndField() {
            return factAndField;
        }

        public void setFactAndField(String factAndField) {
            this.factAndField = factAndField;
        }
    }

    class DSLCheckBox extends Composite {
        ListBox        resultWidget = null;
        // Format for the dropdown def is <varName>:<type>:<Fact.field>
        private String varName      = "";

        public DSLCheckBox(String variableDef) {

            int firstIndex = variableDef.indexOf( ":" );
            int lastIndex = variableDef.lastIndexOf( ":" );
            varName = variableDef.substring( 0,
                                             firstIndex );
            String checkedUnchecked = variableDef.substring( lastIndex + 1,
                                                             variableDef.length() );

            resultWidget = new ListBox();
            resultWidget.addItem( "true" );
            resultWidget.addItem( "false" );

            if ( checkedUnchecked.equalsIgnoreCase( "true" ) ) {
                resultWidget.setSelectedIndex( 0 );
            } else {
                resultWidget.setSelectedIndex( 1 );
            }

            resultWidget.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    updateSentence();
                }
            } );

            resultWidget.setVisible( true );
            initWidget( resultWidget );
        }

        public ListBox getListBox() {
            return resultWidget;
        }

        public void setListBox(ListBox resultWidget) {
            this.resultWidget = resultWidget;
        }

        public String getType() {
            return BOOLEAN_TAG;
        }

        public String getVarName() {
            return varName;
        }

        public void setVarName(String varName) {
            this.varName = varName;
        }

        public String getCheckedValue() {
            return this.resultWidget.getSelectedIndex() == 0 ? "true" : "false";

        }
    }

    class DSLDateSelector extends DatePickerLabel {

        public DSLDateSelector(String selectedDate,
                               String dateFormat) {
            super( selectedDate,
                   dateFormat );

            addValueChanged( new ValueChanged() {
                public void valueChanged(String newValue) {
                    updateSentence();
                }
            } );
        }

        public String getType() {
            return DATE_TAG;
        }
    }
}