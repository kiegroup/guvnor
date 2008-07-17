package org.drools.guvnor.server.security;

public class RoleTypes {
	//Admin can do everything
	public final static String ADMIN = "admin";
	
	/*
	 * Analyst only see the "rules" view, and we specify what category paths they
	 * can see. They can't create anything, only edit rules, and run tests etc,
	 * but only things that are exposed to them via categories
	 */
	public final static String ANALYST = "analyst";
	
    //package.admin can do everything within this package
	public final static String PACKAGE_ADMIN = "package.admin";
	
	/*
	 * package.developer can do anything in that package but not snapshots. This
	 * includes creating a new package (in which case they inherit permissions
	 * for it).
	 */
	public final static String PACKAGE_DEVELOPER = "package.developer";
	
	//Read only
	public final static String PACKAGE_READONLY = "package.readonly";
}
