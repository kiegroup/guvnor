package org.drools.brms.client.breditor;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ChoiceList extends PopupPanel {

    private ListBox list;    
    private ClickListener okClickListener;
    
    public ChoiceList(ClickListener okClickListener) {
        super( true );
        
        this.okClickListener = okClickListener;
        
        list = new ListBox();
        list.setVisibleItemCount( 5 );
        list.addItem( "There is a person {bob} who is blah" );
        list.addItem( "There is a cheese {bob} who is {type}" );
        list.addItem( "- age is less then {number}" );
        list.addItem( "- likes doing '{number}'" );
        
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
