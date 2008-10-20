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
import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.modeldriven.DropDownData;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.DSLSentence;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This displays a widget to edit a DSL sentence.
 * @author Michael Neale
 */
public class DSLSentenceWidget extends DirtyableComposite {

    private final DirtyableHorizontalPane horiz;
    private final List  widgets;
    private final DSLSentence sentence;
    private SuggestionCompletionEngine completions;
    public DSLSentenceWidget(DSLSentence sentence, SuggestionCompletionEngine completions) {
        horiz = new DirtyableHorizontalPane();
        widgets = new ArrayList();
        this.sentence = sentence;
        this.completions = completions;
        init(  );
    }

    private void init( ) {
        makeWidgets(this.sentence.sentence);
        initWidget( this.horiz );
    }


    /**
     * This will take a DSL line item, and split it into widget thingamies for displaying.
     * One day, if this is too complex, this will have to be done on the server side.
     */
    public void makeWidgets(String dslLine) {

        char[] chars = dslLine.toCharArray();
        FieldEditor currentBox = null;
        Label currentLabel = null;
        
        int startVariable = dslLine.indexOf("{");
        List<Widget> lineWidgets = new ArrayList<Widget>();
        
        String startLabel = "";
        if(startVariable>0){
        	startLabel = dslLine.substring(0,startVariable);
        	
        }else{
        	startLabel = dslLine;
        }
        
        Widget label = getLabel(startLabel);
    	lineWidgets.add(label);
        	
        while(startVariable>0){
        	int endVariable = dslLine.indexOf("}",startVariable);
        	String currVariable = dslLine.substring(startVariable+1, endVariable);
        	
        	//For now assume If it has a colon then it is a dropdown. Else a textbox
        	if(currVariable.indexOf(":")>0){
        		Widget dropDown = getEnumDropdown(currVariable);
        		lineWidgets.add(dropDown);
        	}
        	else{
        		Widget box = getBox(currVariable);
        		lineWidgets.add(box);
        	}        
        	
        	//Parse out the next label between variables
        	startVariable = dslLine.indexOf("{",endVariable);
        	if(startVariable>0){
	        	String nextLabel = dslLine.substring(endVariable+1,startVariable);
	        	Widget currLabel = getLabel(nextLabel);
	        	lineWidgets.add(currLabel);
        	}else{
        		String lastLabel = dslLine.substring(endVariable+1, dslLine.length());
        		Widget currLabel = getLabel(lastLabel);
        		lineWidgets.add(currLabel);
        	}
        }
        
        for(Widget widg : lineWidgets){
        	addWidget(widg);
        }
        updateSentence();
    }

    public Widget getEnumDropdown(String variableDef){
    	Widget resultWidget = null;
    	
    	int firstIndex = variableDef.indexOf(":");
    	int lastIndex  = variableDef.lastIndexOf(":");
    	
    	//If the variable doesn't have two colons then just put in a text box
    	if(firstIndex<0 || lastIndex<0){
    		resultWidget =  getBox(variableDef);
    	}else{
    		
    		String varName = variableDef.substring(0,firstIndex);
    		String enumVal = variableDef.substring(firstIndex+1,lastIndex);
    		String typeAndField = variableDef.substring(lastIndex+1, variableDef.length());
    		
    		int dotIndex = typeAndField.indexOf(".");
    		String type = typeAndField.substring(0,dotIndex);
    		String field= typeAndField.substring(dotIndex+1,typeAndField.length());
    		
			String[] data = this.completions.getEnumValues(type, field);
	    	ListBox list = new ListBox();
	    	
	    	for(String item : data){
	    		list.addItem(item);
	    	}
	    	
	    	list.addChangeListener( new ChangeListener() {
                public void onChange(Widget w) {
                    updateSentence();
                    makeDirty();
                }
            });
	    	
	    	resultWidget = list;
	    	
    	}
    	
    	return resultWidget;
    }
    
    public Widget getBox(String variableDef){
    	FieldEditor currentBox = new FieldEditor();
    	currentBox.setVisibleLength(variableDef.length()+1);
    	currentBox.setText(variableDef);
    	
    	return currentBox;
    }
    
    public Widget getLabel(String labelDef){
    	Label label = new SmallLabel();
    	label.setText(labelDef+" ");
    	
    	return label;
    }
    
    private void addWidget(Widget currentBox) {
        this.horiz.add( currentBox );
        widgets.add( currentBox );
    }


    /**
     * This will go through the widgets and build up a sentence.
     */
    protected void updateSentence() {
        String newSentence = "";
        for ( Iterator iter = widgets.iterator(); iter.hasNext(); ) {
            Widget wid = (Widget) iter.next();
            if (wid instanceof Label) {
                newSentence = newSentence + ((Label) wid).getText();
            } else if (wid instanceof FieldEditor) {
                newSentence = newSentence + " {" + ((FieldEditor) wid).getText() + "} ";
            }else if (wid instanceof ListBox){
            	ListBox box = (ListBox)wid;
            	newSentence = newSentence + "{"+box.getItemText(box.getSelectedIndex())+ "}";
            }
        }
        this.sentence.sentence = newSentence.trim();

    }

    class FieldEditor extends DirtyableComposite {

        private TextBox box;
        private HorizontalPanel panel = new HorizontalPanel();

        public FieldEditor() {
            box = new TextBox();
            //box.setStyleName( "dsl-field-TextBox" );

            panel.add( new HTML("&nbsp;") );
            panel.add( box );
            panel.add( new HTML("&nbsp;") );

            box.addChangeListener( new ChangeListener() {
                public void onChange(Widget w) {
                    updateSentence();
                    makeDirty();
                }
            });

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
    }

    public boolean isDirty() {
        return horiz.hasDirty();
    }


}