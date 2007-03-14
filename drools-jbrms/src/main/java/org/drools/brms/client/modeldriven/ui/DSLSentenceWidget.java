package org.drools.brms.client.modeldriven.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.brms.client.modeldriven.brxml.DSLSentence;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This displays a widget to edit a DSL sentence.
 * @author Michael Neale
 */
public class DSLSentenceWidget extends Composite {

    private final HorizontalPanel horiz;
    private final List  widgets;
    private final DSLSentence sentence;
    public DSLSentenceWidget(DSLSentence sentence) {
        horiz = new HorizontalPanel();
        widgets = new ArrayList();
        this.sentence = sentence;
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
        for ( int i = 0; i < chars.length; i++ ) {
            char c = chars[i];
            if (c == '{') {
                currentLabel = null;
                currentBox = new FieldEditor(); 
                addWidget( currentBox );
                
            } else if (c == '}') {
                currentBox.setVisibleLength( currentBox.getText().length() + 1);
                currentBox = null;
            } else {
                if (currentBox == null && currentLabel == null) {
                    currentLabel = new Label();
                    addWidget( currentLabel );
                }
                if (currentLabel != null) {
                    currentLabel.setText( currentLabel.getText() + c );
                } else if (currentBox != null) {
                    currentBox.setText( currentBox.getText() + c );
                }
                
            }
        }
        
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
            }
        }
        this.sentence.sentence = newSentence.trim();
        
    }    
    
    class FieldEditor extends Composite {

        private TextBox box;
        private HorizontalPanel panel = new HorizontalPanel();
        
        public FieldEditor() {
            box = new TextBox();
            box.setStyleName( "dsl-field-TextBox" );
            
            panel.add( new HTML("&nbsp;") );
            panel.add( box );
            panel.add( new HTML("&nbsp;") );
            
            box.addChangeListener( new ChangeListener() {
                public void onChange(Widget w) {
                    updateSentence();
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
    
    
}
