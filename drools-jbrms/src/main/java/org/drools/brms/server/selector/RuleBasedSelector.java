package org.drools.brms.server.selector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.drools.CheckedDroolsException;
import org.drools.RuleBase;
import org.drools.RuntimeDroolsException;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.compiler.RuleBaseLoader;
import org.drools.repository.AssetItem;

/**
 * This uses rules to decide if an asset is to be included in a build.
 *
 * @author Michael Neale
 *
 */
public class RuleBasedSelector implements AssetSelector {

    private static final Logger log = Logger.getLogger( RuleBasedSelector.class );


	String ruleFile;
    private RuleBase ruleBase;

    public RuleBasedSelector(String val) {
        this.ruleFile = val;

        InputStream ins = this.getClass().getResourceAsStream( ruleFile );
        InputStreamReader reader = new InputStreamReader(ins);


        try {
            this.ruleBase = RuleBaseLoader.getInstance().loadFromReader( reader );
        } catch ( CheckedDroolsException e ) {
            log.error( e );
            throw new RuntimeDroolsException(e);
        } catch ( IOException e ) {
            log.error( e );
            throw new RuntimeDroolsException(e);
        }

    }

    public boolean isAssetAllowed(AssetItem asset) {
        return evalRules( asset );
	}

    boolean evalRules(Object asset) {
        StatelessSession session = ruleBase.newStatelessSession();
        StatelessSessionResult result = session.executeWithResults( asset );

        java.util.Iterator objects = result.iterateObjects();
		while(objects.hasNext()) {
		    if (objects.next() instanceof Allow) {
		        return true;
            }
        }
		return false;
    }

}
