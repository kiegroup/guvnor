/*
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

package org.drools.guvnor.server;

import org.drools.guvnor.server.security.PackageUUIDType;
import org.drools.repository.AssetItem;
import org.jboss.seam.security.Identity;

public class AssetItemFilter extends AbstractFilter<AssetItem> {

    public AssetItemFilter(Identity identity) {
        super( AssetItem.class, identity );
    }

    @Override
    protected boolean checkPermission(AssetItem assetItem,
                                      String action) {
        return identity.hasPermission( new PackageUUIDType( assetItem.getPackage().getUUID() ),
                                                  action );
    }

}
