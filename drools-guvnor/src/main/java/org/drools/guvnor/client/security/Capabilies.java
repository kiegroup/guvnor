package org.drools.guvnor.client.security;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is used to turn off GUI functionality. The server decides what should be visible
 * based on roles and permissions granted.
 *
 * @author Michael Neale
 *
 */
public class Capabilies implements IsSerializable {

	public static enum Allow {
		SHOW_CATEGORY_VIEW,//hmmm.. don't need this - will always show
		SHOW_PACKAGE_VIEW,  //(show status list view as well) if they have any package perms
		SHOW_CREATE_NEW_ASSET, //if they have any package perms not read only
		SHOW_CREATE_NEW_PACKAGE, //if they are package admin
		SHOW_ADMIN, //if they are admin, package admin??
		SHOW_QA, //if they have any package perms
		SHOW_DEPLOYMENT //if they are package admin??
	}

	public List<Allow> list = new ArrayList<Allow>();

	/**
	 * Grants all capabilities.
	 * Only used for when there is basically no login.
	 */
	public static Capabilies all() {
		Capabilies cp = new Capabilies();
		Allow[] all =  Allow.values();
		for (int i = 0; i < all.length; i++) {
			cp.list.add(all[i]);
		}
		return cp;
	}

}
