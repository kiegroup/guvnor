package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.DSLSentence;
import org.drools.brms.client.packages.SuggestionCompletionCache;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
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
    private DSLSentence[] conditions;
    private DSLSentence[] actions;
    

    
    public DSLRuleEditor(RuleAsset asset) {
        
        RuleContentText cont = (RuleContentText) asset.content;
        
        this.data = cont;
        text = new TextArea();
        text.setWidth("100%");
        text.setHeight("100%");
        text.setVisibleLines(10);
        text.setText(data.content);
        SuggestionCompletionEngine eng = SuggestionCompletionCache.getInstance().getEngineFromCache( asset.metaData.packageName );
        this.actions = eng.actionDSLSentences;
        this.conditions = eng.conditionDSLSentences;
      
        
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
                showSuggestions( conditions );
            }
        });        
        
        Image rhsOptions = new Image("images/new_dsl_action.gif");   
        final String msg2 =  "Add an action";
        rhsOptions.setTitle( msg2 );
        rhsOptions.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showSuggestions( actions );
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
            showSuggestions(this.actions);
        } else {
            showSuggestions(this.conditions);
        }
        
    }

    private void showSuggestions(DSLSentence[] items) {
        ChoiceList choice = new ChoiceList(items, this);
        choice.setPopupPosition( text.getAbsoluteLeft() + 20, text.getAbsoluteTop() + 20 );
        choice.show();        
    }
    
  
    
    void insertText(String ins) {
        int i = text.getCursorPos();
        String left = text.getText().substring( 0, i );
        String right = text.getText().substring( i, text.getText().length() );
        text.setText( left + ins + right );
        this.data.content = text.getText();
    }    



}
