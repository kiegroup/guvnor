package org.drools.guvnor.client.security;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;

/**
 * The capabilities manager
 * @author esteban.aliverti@gmail.com
 *
 */
public class CapabilitiesManager {
	
	private static CapabilitiesManager INSTANCE;
	
	/**
     * These are used to decide what to display or not.
     */
	private Capabilities capabilities;
	
	private CapabilitiesManager(){
		
	}
	
	public static synchronized CapabilitiesManager getInstance(){
		if (INSTANCE == null) INSTANCE = new CapabilitiesManager();
		return INSTANCE;
	}
	
	public void refreshAllowedCapabilities(final Command command){
		RepositoryServiceFactory.getSecurityService().getUserCapabilities(new GenericCallback<Capabilities>() {
			public void onSuccess(Capabilities cp) {
				capabilities = cp;
				if (command != null){
					command.execute();
				}
			}
		});
	}

	public Capabilities getCapabilities() {
		return this.capabilities;
	}
	
	public boolean shouldShow(Integer... capability) {
        for ( Integer cap : capability ) {
            if ( capabilities.list.contains( cap ) ) {
                return true;
            }
        }
        return false;
    }
	
}
