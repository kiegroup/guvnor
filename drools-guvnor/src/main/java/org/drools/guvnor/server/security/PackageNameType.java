package org.drools.guvnor.server.security;


/**
 * This class is used to indicate this is a type that contains packageName
 *  
 */
public class PackageNameType {

	private String packageName;
	
	public PackageNameType(String packageName) {
		this.packageName = packageName;
	}
	
	public String getPackageName() {
		return packageName;
	}

}
