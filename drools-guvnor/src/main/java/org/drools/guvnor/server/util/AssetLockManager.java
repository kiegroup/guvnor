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

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

/**
 * Manages the locks for assets.
 * 
 * @author Toni Rikkola
 *
 */
@Scope(ScopeType.APPLICATION)
@Startup
@Name("assetLockManager")
public class AssetLockManager {

    private static final long LOCK_EXPIRATION_TIME = 1200000;

    // UUID, Lock
    private Map<String, Lock> map                  = new HashMap<String, Lock>();

    class Lock {
        String userName;
        long   timeStamp;

        Lock(String userName) {
            this.userName = userName;
            timeStamp = System.currentTimeMillis();
        }
    }
    
    public static AssetLockManager instance()
    {
        return (AssetLockManager) Component.getInstance("assetLockManager");
    }
    
    public boolean isAssetLocked(String uuid) {
        if ( map.keySet().contains( uuid ) ) {
            long timeStamp = map.get( uuid ).timeStamp;
            long currentTime = System.currentTimeMillis();

            // Check if time expiration time has passed
            if ( (currentTime - timeStamp) > LOCK_EXPIRATION_TIME ) {
                // Remove the lock
                map.remove( uuid );

                return false;

            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * Return the lockers user name.
     * 
     * @param uuid Id of the asset.
     * @return Lockers user name or null  if there is no lock with this uuid.
     */
    public String getAssetLockerUserName(String uuid) {
        if ( isAssetLocked( uuid ) ) {
            return map.get( uuid ).userName;
        } else {
            return null;
        }
    }

    /**
     * Locks the asset, if a lock already exists this over writes it.
     * 
     * @param uuid Id of the asset.
     * @param userName User name of the user that is locking the asset.
     */
    public void lockAsset(String uuid,
                          String userName) {
        map.put( uuid,
                 new Lock( userName ) );
    }

    public void unLockAsset(String uuid) {
        map.remove( uuid );
    }
}
