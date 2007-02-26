package org.drools.brms.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * Introduction page.
 */
public class Info extends JBRMSFeature {

    public static ComponentInfo init() {
        return new ComponentInfo( "Info",
                                  "JBoss Rules Managment Console." ) {
            public JBRMSFeature createInstance() {
                return new Info();
            }

        };
    }

    public Info() {
        initWidget( getLayout() );
    }

    private Widget getLayout() {
        
        FlexTable layout = new FlexTable();
        Image logo = new Image( "images/logo.png" );
        layout.setWidget( 0, 0, logo);
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setColSpan( 0, 0, 1 );
        
        formatter.setHorizontalAlignment( 0, 0, HasHorizontalAlignment.ALIGN_CENTER );
        
        layout.setWidth( "100%" );
        layout.setHeight( "100%" );
        
        HTML html = new HTML("<i>Product web site</i>");
        
        html.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
                Window.open( "http://www.jboss.com/products/rules", "JBoss Rules", "" );                
            }
            
        });
        
        HTML html2 = new HTML("<i>Community web site</i>");
        html2.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                Window.open( "http://labs.jboss.com/portal/jbossrules", "JBoss Rules Community", "" );                

            }
        } );
        layout.setWidget( 1, 0, html);
        layout.setWidget( 1, 1, html2 );
        
        formatter.setHorizontalAlignment( 1, 0, HasHorizontalAlignment.ALIGN_LEFT );
        formatter.setHorizontalAlignment( 1, 1, HasHorizontalAlignment.ALIGN_RIGHT );
        
        
        layout.setStyleName( "editable-Surface" );
        
        return layout;
    }

    public void onShow() {
    }
}
