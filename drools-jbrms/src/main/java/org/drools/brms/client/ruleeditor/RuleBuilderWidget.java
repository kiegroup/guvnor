package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.rpc.RuleAsset;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget wraps a rule asset widget, and provides actions to validate and view source.
 * @author Michael Neale
 */
public class RuleBuilderWidget extends Composite {

    
    private RuleAsset asset;

    public RuleBuilderWidget(Widget editor, RuleAsset asset) {
        this.asset = asset;
        
        
        FlexTable layout = new FlexTable();
        layout.setStyleName( "asset-editor-Layout" );
        layout.setWidget( 0, 0, editor );
        layout.setWidget( 1, 0, validatorActions() );
        layout.getCellFormatter().setAlignment( 1, 0, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE );
        
        
        
        initWidget( editor );
    }

    private Widget validatorActions() {
        HorizontalPanel horiz = new HorizontalPanel();
        Button viewSource = new Button("View source");
        horiz.add( viewSource );
        
        Button validate = new Button("Validate");
        horiz.add( validate );
        
        viewSource.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                //doViewsource();
            }
        });
        
        horiz.setStyleName( "asset-validator-Buttons" );
        return horiz;
    }
    
}
