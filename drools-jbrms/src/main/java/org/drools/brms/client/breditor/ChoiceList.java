package org.drools.brms.client.breditor;

import java.util.List;

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
    private ClickListener okClickListener;
    private TextBox filter;
    
    public void setOKClickListener(ClickListener listener) {
        this.okClickListener = listener;
    }
    
    /**
     * Pass in a list of suggestions for the popup lists.
     * Set a click listener to get notified when the OK button is clicked.
     */
    public ChoiceList(final List data) {
        super( true );
        
        
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
                populateList( ListUtil.filter(data, filter.getText()) );
            }
            
        });
        filter.setFocus( true );
        
        
        VerticalPanel panel = new VerticalPanel();
        panel.add( filter );
        
        list = new ListBox();
        list.setVisibleItemCount( 5 );
        
        populateList( data );        
        
        
        panel.add( list );
        
        Button ok = new Button("ok");
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget btn) {                
                onOkClicked();
            }
            
        });
        panel.add( ok );        
        add( panel );      
        setStyleName( "ks-popups-Popup" );
        
    }

    private void populateList(List data) {
        list.clear();
        for (int i = 0; i < data.size(); i++) {
            list.addItem((String) data.get( i ));
        }
    }
    
    private void onOkClicked() {        
        this.okClickListener.onClick( this );
        this.hide();
    }
    
    public String getSelectedItem() {
        return list.getItemText( list.getSelectedIndex() );
    }
    
    
}
