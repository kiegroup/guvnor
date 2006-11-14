package org.drools.brms.client.ruleeditor;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * 
 * A popup and confirmation dialog for committing an asset.
 * 
 * @author Michael Neale
 *
 */
public class CheckinPopup extends PopupPanel {

    final private FlexTable layout = new FlexTable();
    final private TextArea comment = new TextArea();
    final private ClickListener okClick;    
    
    public CheckinPopup(ClickListener click) {
        super(true);        
        this.okClick = click;
        
        this.setStyleName( "ks-popups-Popup" );
        
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        
        layout.setWidget( 0, 0, new Image() );
        layout.setWidget( 1, 0, new Label("Comment:") );
        formatter.setHorizontalAlignment( 1, 0, HasHorizontalAlignment.ALIGN_RIGHT );
        
        layout.setWidget( 1, 1, comment );
        formatter.setHorizontalAlignment( 1, 1, HasHorizontalAlignment.ALIGN_LEFT );
        comment.setWidth( "100%" );
        comment.setHeight( "100%" );
        comment.setStyleName( "rule-viewer-Documentation" );        
        
        Button ok = new Button();
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                okClick.onClick( w );
            }            
        });
        
        layout.setWidget( 2, 0,  ok);
        formatter.setHorizontalAlignment( 2, 0, HasHorizontalAlignment.ALIGN_LEFT );
        
        add( layout );
    }
    
}
