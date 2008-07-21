package org.drools.guvnor.server.security;


/**
 * This class is used to indicate this is a type that contains categoryPath
 *  
 */
public class CategoryPathType {
	private String categoryPath;
	
	public CategoryPathType(String categoryPath) {
		this.categoryPath = categoryPath;
	}
	
	String getCategoryPath() {
		return categoryPath;
	}

}
