package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.YesNoDialog;
import org.drools.brms.client.rpc.brxml.RuleAttribute;
import org.drools.brms.client.rpc.brxml.RuleModel;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Displays a list of rule options (attributes).
 * 
 * @author Michael Neale
 */
public class RuleAttributeWidget extends Composite {

    private FormStyleLayout layout;
    private RuleModel model;
    private RuleModeller parent;

    public RuleAttributeWidget(RuleModeller parent, RuleModel model) {
        this.parent = parent;
        this.model = model;
        layout = new FormStyleLayout();
        RuleAttribute[] attrs = model.attributes;
        for ( int i = 0; i < attrs.length; i++ ) {
            RuleAttribute at = attrs[i];
            layout.addAttribute( at.attributeName, getEditorWidget(at, i));
        }
        
        initWidget( layout );
    }

    private Widget getEditorWidget(final RuleAttribute at, final int idx) {
        if (at.attributeName.equals( "no-loop" )) {
            return getRemoveIcon( idx );
        }
        
        final TextBox box = new TextBox();
        box.setVisibleLength( (at.value.length() < 3) ? 3 : at.value.length() );
        box.setText( at.value );
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                at.value = box.getText();
            }
        });
        HorizontalPanel horiz = new HorizontalPanel();
        horiz.add( box );
        horiz.add( getRemoveIcon( idx ) );
        
        return horiz;
    }

    private Image getRemoveIcon(final int idx) {
        Image remove = new Image( "images/delete_item_small.gif" );
        remove.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                YesNoDialog diag = new YesNoDialog("Remove this rule option?", new Command() {
                    public void execute() {
                        model.removeAttribute( idx);
                        parent.refreshWidget();                            
                    }
                });
                diag.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
                diag.show();
            }
        } );
        return remove;
    }
    
}
