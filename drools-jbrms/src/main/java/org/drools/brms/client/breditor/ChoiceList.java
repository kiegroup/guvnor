package org.drools.brms.client.breditor;

import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a popup list for "content assistance" - although on the web, 
 * its not assistance - its mandatory ;)
 */
public class ChoiceList extends PopupPanel {

    private ListBox list;    
    private ClickListener okClickListener;
    
    public void setOKClickListener(ClickListener listener) {
        this.okClickListener = listener;
    }
    
    /**
     * Pass in a list of suggestions for the popup lists.
     * Set a click listener to get notified when the OK button is clicked.
     */
    public ChoiceList(List data) {
        super( true );
        
        list = new ListBox();
        list.setVisibleItemCount( 5 );
        
        for (int i = 0; i < data.size(); i++) {
            list.addItem((String) data.get( i ));
        }        
        
        VerticalPanel panel = new VerticalPanel();
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
    
    private void onOkClicked() {        
        this.okClickListener.onClick( this );
        this.hide();
    }
    
    public String getSelectedItem() {
        return list.getItemText( list.getSelectedIndex() );
    }
    
    
}
