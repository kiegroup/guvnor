package org.drools.guvnor.server.selector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.drools.repository.AssetItem;


public class SelectorManager {

	private static final Logger log = Logger.getLogger( SelectorManager.class );
	public static String SELECTOR_CONFIG_PROPERTIES = "/selectors.properties";
	private static final SelectorManager INSTANCE = new SelectorManager(SELECTOR_CONFIG_PROPERTIES);

    /**
     * This is a map of the selectors to use.
     */
	public final Map<String, AssetSelector> selectors = new HashMap<String, AssetSelector>();



	SelectorManager(String configPath) {
		log.debug("Loading selectors");
		Properties props = new Properties();
		try {
			props.load(this.getClass().getResourceAsStream(configPath));
			for (Iterator iter = props.keySet().iterator(); iter.hasNext();) {
				String selectorName = (String) iter.next();
				String val = props.getProperty(selectorName);
                try {
                    if (val.endsWith("drl")) {
                        selectors.put(selectorName ,loadRuleSelector( val) );
                    } else {
                        selectors.put(selectorName, loadSelectorImplementation( val ));
                    }
                } catch (RuntimeException e) {
                    log.error("Unable to load a selector [" + val + "]", e);
                }
			}
		} catch (IOException e) {
			log.error("Unable to load selectors.", e);
		}
	}

    /**
     * Return a selector. If the name is null or empty it will return a nil/default selector
     * (one that lets everything through). If the selector iis not found, it will return null;
     */
    public AssetSelector getSelector(String name) {
        if (name == null || "".equals(name.trim())) {
            return nilSelector();
        } else {
            if (this.selectors.containsKey( name )) {

                return this.selectors.get( name );
            } else {
                log.debug( "No selector found by the name of " + name );
                return null;
            }
        }
    }



	private AssetSelector nilSelector() {
        return new AssetSelector() {
            public boolean isAssetAllowed(AssetItem asset) {
                return true;
            }
        };
    }

    private AssetSelector loadSelectorImplementation(String val) throws IOException {

		try {
            return (AssetSelector) Thread.currentThread().getContextClassLoader().loadClass( val ).newInstance();

        } catch ( InstantiationException e ) {
            log.error( e );
            return null;
        } catch ( IllegalAccessException e ) {
            log.error( e );
            return null;
        } catch ( ClassNotFoundException e ) {
            log.error( e );
            return null;
        }

	}




	private AssetSelector loadRuleSelector(String val) {

		return new RuleBasedSelector(val);
	}




	public static SelectorManager getInstance() {
		return INSTANCE;
	}

}
