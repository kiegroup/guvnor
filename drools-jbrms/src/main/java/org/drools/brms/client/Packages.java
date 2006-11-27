package org.drools.brms.client;

import java.util.HashMap;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.ui.PatternWidget;
import org.drools.brms.client.modeldriven.ui.RuleScorecardWidget;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is the package management feature. 
 * For managing packages (namespaces, imports etc) for rule assets.
 * 
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

        //TODO: this will really be loaded from the server, as will the current rule stuff
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();
        
        com.addFact( "Person", new String[] {"age", "name"}  );
        com.addFact("Vehicle", new String[] {"type", "make"} );
        com.addOperators( "Person", "name", new String[] {"==", "!="});
        com.addOperators( "Vehicle", "age", new String[] {"==", "!=", "<", ">"});

        com.addConnectiveOperators( "Vehicle", "make", new String[] {"|="});  

        
        
        panel.add( new RuleScorecardWidget(com ) );
        
        panel.setSpacing( 8 );
        initWidget( panel );
    }


    public void onShow() {
    }
}
