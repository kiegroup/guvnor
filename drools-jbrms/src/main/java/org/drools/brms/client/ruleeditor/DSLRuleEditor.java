package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.rpc.RuleContentText;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;


/**
 * This is a textual rule editor, which provides DSL content assistance.
 * This is similar (but simpler) to the IDE based one.
 * @author michael neale
 */
public class DSLRuleEditor extends Composite {
    
    private TextArea text;
    final private RuleContentText data;
    

    
    public DSLRuleEditor(RuleContentText tex, final String[] dslConditions, final String[] dslActions) {
        
        this.data = tex;
        text = new TextArea();
        text.setWidth("100%");
        text.setHeight("100%");
        text.setVisibleLines(10);
        text.setText(tex.content);
        
        text.setStyleName( "dsl-text-Editor" );
        
        FlexTable layout = new FlexTable();
        layout.setWidget( 0, 0, text );
        
        text.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                data.content = text.getText();
            }
        });


        
        VerticalPanel vert = new VerticalPanel();
        
        Image lhsOptions = new Image("images/new_dsl_pattern.gif");
        final String msg = "Add a new condition";
        lhsOptions.setTitle( msg );
        lhsOptions.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showOptions( dslConditions, w, msg );
            }
        });        
        
        Image rhsOptions = new Image("images/new_dsl_action.gif");   
        final String msg2 =  "Add an action";
        rhsOptions.setTitle( msg2 );
        rhsOptions.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showOptions( dslActions, w, msg2 );
            }
        });   
        
        vert.add( lhsOptions );    
        vert.add( rhsOptions );
        layout.setWidget( 0, 1, vert );
        
        layout.getCellFormatter().setWidth( 0, 0, "95%" );
        layout.getCellFormatter().setWidth( 0, 1, "5%" );
        
        layout.setWidth( "100%" );
        layout.setHeight( "100%" );
        
        initWidget( layout );
    }
    
    private void showOptions(final String[] items,
                             Widget w, String message) {
        PickList pick = new PickList(message, items, this);
        pick.setPopupPosition( w.getAbsoluteLeft() - 250, w.getAbsoluteTop() );
        pick.show();
    }    
    
    private void insertText(String ins) {
        int i = text.getCursorPos();
        String left = text.getText().substring( 0, i );
        String right = text.getText().substring( i, text.getText().length() );
        text.setText( left + ins + right );
    }    

    /**
     * The popup content assistance. Can be dragged around.
     */
    static class PickList extends DialogBox {

        public PickList(String message, String[] items, final DSLRuleEditor self) {            
            setStyleName( "ks-popups-Popup" );

            final ListBox list = new ListBox();
            for ( int i = 0; i < items.length; i++ ) {
                list.addItem( items[i] );
            }
            
            
            setText( message );
            
            VerticalPanel vert = new VerticalPanel();
            list.setVisibleItemCount( 6 );
            vert.add( list );
            
            DockPanel buttons = new DockPanel();
            
            
            Button ok = new Button("OK");
            ok.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    self.insertText( list.getItemText( list.getSelectedIndex() ) );
                    hide();
                }
            }  );
            buttons.add( ok, DockPanel.WEST );
            
            
            
            Button close = new Button("close");
            close.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    hide();
                }
            } );
            buttons.add( close, DockPanel.EAST );
            buttons.setWidth( "100%" );
            vert.add( buttons );
            
            setWidget( vert );
        }
    }

}
