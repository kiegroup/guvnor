package org.drools.brms.client;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
        VerticalPanel horiz = new VerticalPanel();
        horiz.add( new Image( "images/logo.png" ) );
        horiz.add( new Label("Welcome to the JBoss Rules Management system.") );
        return horiz;
    }

    public void onShow() {
    }
}
