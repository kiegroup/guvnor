package org.drools.brms.client;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

/**
 * Introduction page.
 */
public class Info extends JBRMSFeature {

    public static ComponentInfo init() {
        return new ComponentInfo( "Info",
                                  "JBoss Rules Managment System." ) {
            public JBRMSFeature createInstance() {
                return new Info();
            }

        };
    }

    public Info() {
        initWidget( getLayout() );
    }

    private Widget getLayout() {
        
        Frame f = new Frame("welcome.html");
        
        f.setStyleName("welcome-Page");
        return f;
    }

    public void onShow() {
    }
}
