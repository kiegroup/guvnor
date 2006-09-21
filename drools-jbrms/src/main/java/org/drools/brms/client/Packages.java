package org.drools.brms.client;

import org.drools.brms.client.breditor.BREditor;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Demonstrates {@link com.google.gwt.user.client.ui.PopupPanel} and
 * {@link com.google.gwt.user.client.ui.DialogBox}.
 */
public class Packages extends JBRMSFeature {


    public static ComponentInfo init() {
        return new ComponentInfo( "Packages",
                                  "This is where you configure packages of rules." + "You select rules to belong to packages, and what version they are. A rule can "
                                          + "appear in more then one package, and possibly even different versions of the rule." ) {
            public JBRMSFeature createInstance() {
                return new Packages();
            }

            public Image getImage() {
                return new Image( "images/package.gif" );
            }
        };
    }


    public Packages() {
        VerticalPanel panel = new VerticalPanel();
        panel.add( new BREditor() );
        
        panel.setSpacing( 8 );
        initWidget( panel );
    }


    public void onShow() {
    }
}
