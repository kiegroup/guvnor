package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the widget for choosing/viewing the fact type.
 * 
 * @author Michael Neale
 *
 */
public class FactTypeWidget extends Composite {

    private final HorizontalPanel widget = new HorizontalPanel();
    private SuggestionCompletionEngine completion;
    private Label type;
    private Command onChange;
    
    public FactTypeWidget(String factType, SuggestionCompletionEngine com, Command onChange) {        
        this.completion = com;
        this.onChange = onChange;
        
        type = new Label(factType);
        Image edit = new Image("images/edit_tiny.gif");
        edit.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
                FactTypeList list = new FactTypeList();
                list.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
                list.show();
                
            }
            
        });
        
        widget.add( edit );
        widget.add( type );
        
        initWidget( widget );
    }
    
    /**
     * This returns the current fact type.
     */
    public String getFactType() {
        return type.getText();
    }
    
    /**
     * The popup for choosing a fact type.
     * 
     * @author Michael Neale
     */
    class FactTypeList extends PopupPanel {
        
        public FactTypeList() {
            super(true);
            final ListBox box = new ListBox();
            String[] types = completion.getFactTypes();
            box.addItem( "-- please choose --" );
            for ( int i = 0; i < types.length; i++ ) {
                box.addItem( types[i] );
            }
            
            
            box.addChangeListener( new ChangeListener() {

                public void onChange(Widget w) {
                    type.setText(box.getValue( box.getSelectedIndex()));
                    onChange.execute();
                    hide();
                }
                
            });
            
            add( box );
        }
        
    }
    
}
