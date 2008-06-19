package org.drools.guvnor.client.packages;

import org.drools.guvnor.client.packages.PackageNameValidator;

import junit.framework.TestCase;

public class NewPackageWizardTest extends TestCase {

	public void testPackageNameValidation() {
		assertTrue(PackageNameValidator.validatePackageName("foo.bar"));
		assertTrue(PackageNameValidator.validatePackageName("whee.waa2"));
		assertFalse(PackageNameValidator.validatePackageName(" hey DJ "));
		assertFalse(PackageNameValidator.validatePackageName(""));
		assertFalse(PackageNameValidator.validatePackageName(" "));
		assertFalse(PackageNameValidator.validatePackageName(null));

	}

}
