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

package org.drools.guvnor.server.util;

import junit.framework.TestCase;

/**
 * @author Toni Rikkola
 *
 */
public class AssetLockManagerTest extends TestCase {

    public void testLockAndUnlock() throws Exception {
        AssetLockManager alm = new AssetLockManager();

        final String userName1 = "user1";

        final String uuid = "uuid";

        alm.lockAsset( uuid,
                       userName1 );

        assertTrue( alm.isAssetLocked( uuid ) );

        assertEquals( alm.getAssetLockerUserName( uuid ),
                      userName1 );

        alm.unLockAsset( uuid );

        assertFalse( alm.isAssetLocked( uuid ) );

        assertNull( alm.getAssetLockerUserName( uuid ) );

    }

    public void testLockAndOverWritelock() throws Exception {
        AssetLockManager alm = new AssetLockManager();

        final String userName1 = "user1";
        final String userName2 = "user2";

        final String uuid = "uuid";

        alm.lockAsset( uuid,
                       userName1 );

        assertTrue( alm.isAssetLocked( uuid ) );

        assertEquals( alm.getAssetLockerUserName( uuid ),
                      userName1 );

        alm.lockAsset( uuid,
                       userName2 );

        assertTrue( alm.isAssetLocked( uuid ) );

        assertEquals( alm.getAssetLockerUserName( uuid ),
                      userName2 );

    }
}
