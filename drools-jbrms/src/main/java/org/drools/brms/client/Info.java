package org.drools.brms.client;

import org.drools.brms.client.common.LoadingPopup;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbsolutePanel;
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

    private Frame infoPanel;

    public Info() {
        initWidget( getLayout() );
    }

    private Widget getLayout() {
        
        infoPanel = new Frame("welcome.html");
        infoPanel.setStyleName("welcome-Page");
       
        infoPanel.setVisible( true );
        
        return infoPanel;
    }
    
    

    public void onShow() {
    }
}
