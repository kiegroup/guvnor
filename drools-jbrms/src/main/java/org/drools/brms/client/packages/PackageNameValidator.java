package org.drools.brms.client.packages;

public class PackageNameValidator {
    public static boolean validatePackageName(String name) {
    	if (name == null) return false;
    	return name.matches("^[a-zA-Z_\\$][\\w\\$]*(?:\\.[a-zA-Z_\\$][\\w\\$]*)*$");
    }
}
