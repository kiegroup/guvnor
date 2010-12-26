/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
