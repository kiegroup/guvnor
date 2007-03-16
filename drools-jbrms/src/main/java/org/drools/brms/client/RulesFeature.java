package org.drools.brms.client;

import org.drools.brms.client.rulelist.AssetBrowser;

/**
 * This controls the "Rules manager" top level feature.
 * @author Michael Neale
 */
public class RulesFeature extends JBRMSFeature {

	public static ComponentInfo init() {
		return new ComponentInfo("Rules", "Find and edit rules.") {
			public JBRMSFeature createInstance() {
				return new RulesFeature();
			}

		};
	}

	
	public RulesFeature() {
	    initWidget( new AssetBrowser() );
	}

}
