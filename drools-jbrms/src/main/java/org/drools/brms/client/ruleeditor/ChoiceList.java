package org.drools.brms.client.ruleeditor;

import java.util.List;

import org.drools.brms.client.modeldriven.brxml.DSLSentence;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a popup list for "content assistance" - although on the web, 
 * its not assistance - its mandatory ;)
 */
public class ChoiceList extends PopupPanel {

    private ListBox list;
    private final DSLSentence[] sentences;    
    
    private TextBox filter;
    

        /**
     * Pass in a list of suggestions for the popup lists.
     * Set a click listener to get notified when the OK button is clicked.
     */
    public ChoiceList(final DSLSentence[] sen, final DSLRuleEditor self) {
        super( true );
        
        this.sentences = sen;
        filter = new TextBox();
        filter.addKeyboardListener( new KeyboardListener() {

            public void onKeyDown(Widget arg0,
                                  char arg1,
                                  int arg2) {
                
            }

            public void onKeyPress(Widget arg0,
                                   char arg1,
                                   int arg2) {
            }

            public void onKeyUp(Widget arg0,
                                char arg1,
                                int arg2) {
                if (arg1 == KEY_ENTER) {
                    applyChoice( self );
                } else {
                    populateList( ListUtil.filter(sentences, filter.getText()) );
                }
            }
            
        });
        filter.setFocus( true );
        
        
        VerticalPanel panel = new VerticalPanel();
        panel.add( filter );
        
        list = new ListBox();
        list.setVisibleItemCount( 5 );
        
        populateList( ListUtil.filter( this.sentences, "" ));        
        
        
        panel.add( list );
        
        Button ok = new Button("ok");
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget btn) {                
                applyChoice( self );
            }


            
        });
        panel.add( ok );        
        add( panel );      
        setStyleName( "ks-popups-Popup" );
        
    }

    private void applyChoice(final DSLRuleEditor self) {
        self.insertText( getSelectedItem() );
        hide();
    }
    
    private void populateList(List filtered) {
        list.clear();
        for (int i = 0; i < filtered.size(); i++) {
            list.addItem(((DSLSentence)filtered.get( i )).sentence);
        }        
    }
    

    
    public String getSelectedItem() {
        return list.getItemText( list.getSelectedIndex() );
    }
    
    
}
