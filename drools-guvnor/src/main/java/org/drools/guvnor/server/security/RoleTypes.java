package org.drools.guvnor.server.security;

import java.lang.reflect.Field;

public class RoleTypes {


	/** Admin can do everything */
	public final static String ADMIN = "admin";

	/**
	 * Analyst only see the "rules" view, and we specify what category paths they
	 * can see. They can't create anything, only edit rules, and run tests etc,
	 * but only things that are exposed to them via categories
	 */
	public final static String ANALYST = "analyst";

	/**
	 * Read only for categories (analyst view)
	 */
	public final static String ANALYST_READ = "analyst.readonly";


    /** package.admin can do everything within this package */
	public final static String PACKAGE_ADMIN = "package.admin";

	/**
	 * package.developer can do anything in that package but not snapshots. This
	 * includes creating a new package (in which case they inherit permissions
	 * for it).
	 */
	public final static String PACKAGE_DEVELOPER = "package.developer";

	/**
	 * Read only for package.
	 */
	public final static String PACKAGE_READONLY = "package.readonly";




	/**
	 * @return A list of all available types.
	 */
	public static String[] listAvailableTypes() {
		try {
			Field[] flds = RoleTypes.class.getFields();
			String[] r = new String[flds.length];
			for (int i = 0; i < flds.length; i++) {
					r[i] = flds[i].get(RoleTypes.class).toString();
			}
			return r;
		} catch (Exception e) {
			throw new IllegalStateException("Can't get list of permission types.");
		}
	}

}
