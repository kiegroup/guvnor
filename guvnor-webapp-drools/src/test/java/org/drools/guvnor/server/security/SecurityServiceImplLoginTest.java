/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.server.security;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.drools.guvnor.server.GuvnorTestBase;
import org.junit.Test;

// This test just tests the SecurityServiceImpl login method. It is not part of the general SecurityServiceImplTest 
// suite as the login test calls Seam 3's Identity.logout() twice which causes a 'java.lang.IllegalStateException: 
// invalidate: Session already invalidated' exception. This behaviour is different to Seam 2.
//
// See http://seamframework.org/Community/OrgjbossseamsecurityIdentityImpllogoutRegression
//
public class SecurityServiceImplLoginTest extends GuvnorTestBase {

    private static final String USER_NAME = "securityServiceImplUser";

    @Inject
    private SecurityServiceImpl securityService;

    public SecurityServiceImplLoginTest() {
        autoLoginAsAdmin = false;
    }

    @Test
    public void testLogin() throws Exception {
        assertTrue( securityService.login( USER_NAME,
                                           USER_NAME ) );
        securityService.logout();
    }

}
