package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.model.CompositeFactPattern;
import org.drools.brms.client.modeldriven.model.FactPattern;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This represents a top level CE, like an OR, NOT, EXIST etc...
 * Contains a list of FactPatterns. 
 * 
 * @author Michael Neale
 *
 */
public class CompositeFactPatternWidget extends Composite {

    private final SuggestionCompletionEngine completions;
    private CompositeFactPattern             pattern;
    private Grid                             layout;
    private RuleModeller                     modeller;

    public CompositeFactPatternWidget(RuleModeller modeller,
                                      CompositeFactPattern pattern,
                                      SuggestionCompletionEngine completions) {
        this.completions = completions;
        this.pattern = pattern;
        this.modeller = modeller;

        this.layout = new Grid( 1, 2 );
        this.layout.setStyleName( "model-builderInner-Background" );

        doLayout();
        initWidget( layout );
    }

    private void doLayout() {
        this.layout.setWidget( 0,
                               0,
                               getCompositeLabel() );

        if ( pattern.patterns != null ) {
            VerticalPanel vert = new VerticalPanel();
            FactPattern[] facts = pattern.patterns;
            for ( int i = 0; i < facts.length; i++ ) {
                vert.add( new FactPatternWidget( modeller,
                                                 facts[i],
                                                 this.completions,
                                                 false ) );
            }
            this.layout.setWidget( 0,
                                   1,
                                   vert );
        }
    }

    private Widget getCompositeLabel() {

        HorizontalPanel horiz = new HorizontalPanel();
        Image edit = new Image( "images/edit.gif" );
        edit.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showFactTypeSelector( w );
            }
        } );

        horiz.add( edit );
        horiz.add( new Label( pattern.type ) );
        return horiz;
    }

    /**
     * Pops up the fact selector.
     */
    protected void showFactTypeSelector(final Widget w) {
        final ListBox box = new ListBox();
        String[] facts = completions.getFactTypes();

        for ( int i = 0; i < facts.length; i++ ) {
            box.addItem( facts[i] );
        }

        final FormStylePopup popup = new FormStylePopup( "images/new_fact.gif",
                                                         "New fact pattern..." );
        popup.addAttribute( "choose type",
                            box );
        Button ok = new Button( "OK" );
        popup.addAttribute( "",
                            ok );

        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                pattern.addFactPattern( new FactPattern( box.getItemText( box.getSelectedIndex() ) ) );
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        popup.setStyleName( "ks-popups-Popup" );

        popup.setPopupPosition( w.getAbsoluteLeft() - 400,
                                w.getAbsoluteTop() );
        popup.show();

    }

}
