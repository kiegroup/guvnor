package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.rpc.RuleContentText;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * This is a textual rule editor, which provides DSL content assistance.
 * This is similar (but simpler) to the IDE based one.
 * @author michael neale
 */
public class DSLRuleEditor extends Composite {
    
    private TextArea text;
    final private RuleContentText data;
    private String[] conditions;
    private String[] actions;
    

    
    public DSLRuleEditor(RuleContentText tex, final String[] dslConditions, final String[] dslActions) {
        
        this.data = tex;
        text = new TextArea();
        text.setWidth("100%");
        text.setHeight("100%");
        text.setVisibleLines(10);
        text.setText(tex.content);

        this.conditions = dslConditions;
        this.actions = dslActions;       
        
        text.setStyleName( "dsl-text-Editor" );
        
        FlexTable layout = new FlexTable();
        layout.setWidget( 0, 0, text );
        
        text.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                data.content = text.getText();
            }
        });


        text.addKeyboardListener( new KeyboardListenerAdapter() {



            public void onKeyDown(Widget arg0,
                                   char arg1,
                                   int arg2) {
                if (arg1 == ' ' && arg2 == MODIFIER_CTRL) {
                    showInTextOptions( ); 
                } 
                
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
    
    protected void showInTextOptions() {
        String prev = text.getText().substring( 0, this.text.getCursorPos() );
        if (prev.indexOf( "then" ) > -1) {
            PickList pick = new PickList("Choose an action", actions , this);
            pick.setPopupPosition( text.getAbsoluteLeft(), text.getAbsoluteTop() );
            pick.show();
            
        } else {
            PickList pick = new PickList("Choose a condition", conditions , this);
            pick.setPopupPosition( text.getAbsoluteLeft(), text.getAbsoluteTop() );
            pick.show();
            
        }
        
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
        this.data.content = text.getText();
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
            
            
            
            FlexTable buttons = new FlexTable();
            
            
            Button ok = new Button("OK");
            ok.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    self.insertText( list.getItemText( list.getSelectedIndex() ) );
                    hide();
                }
            }  );
            buttons.setWidget( 0, 0, ok );
            buttons.getFlexCellFormatter().setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE );
            
            
            
            Button close = new Button("close");
            close.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    hide();
                }
            } );
            
            buttons.setWidget( 0, 1, close);
            buttons.getFlexCellFormatter().setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE );
            
            buttons.setWidth( "100%" );
            vert.add( buttons );
            
            setWidget( vert );
        }
    }

}
