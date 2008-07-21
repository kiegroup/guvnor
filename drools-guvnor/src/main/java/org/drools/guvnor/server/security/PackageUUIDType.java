package org.drools.guvnor.server.security;


/**
 * This class is used to indicate this is a type that contains pacakge UUID
 *  
 */
public class PackageUUIDType {

	private String uuid;
	
	public PackageUUIDType(String uuid) {
		this.uuid = uuid;
	}
	
	String getUUID() {
		return uuid;
	}

}
