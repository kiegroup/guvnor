package org.drools.guvnor.client.rpc;

import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is passed back to the client to give the UI some context information on what to display and
 * not display.
 *
 * @author Michael Neale
 *
 */
public class UserSecurityContext implements IsSerializable {

	public String userName;


	/**
	 * @gwt.typeArgs <java.lang.String>
	 */
	public Set disabledFeatures;

	public UserSecurityContext() {}
	public UserSecurityContext(String userName, Set disableFeatures) {
		this.userName = userName;
		this.disabledFeatures = disableFeatures;
	}

}
