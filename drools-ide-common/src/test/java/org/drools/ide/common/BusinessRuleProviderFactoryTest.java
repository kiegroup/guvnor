package org.drools.ide.common;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.drools.CheckedDroolsException;
import org.drools.compiler.BusinessRuleProvider;
import org.drools.compiler.BusinessRuleProviderFactory;
import org.junit.Test;

public class BusinessRuleProviderFactoryTest {

	@Test
	public void testGetProvider() throws CheckedDroolsException {
		BusinessRuleProvider provider = BusinessRuleProviderFactory.getInstance().getProvider();
		assertNotNull(provider);
		assertTrue(provider instanceof BusinessRuleProviderDefaultImpl);
	}
}
