/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.server.util;

import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

/**
 * Helper class for getting Drools Header
 */
public class DroolsHeader {
    public static String getDroolsHeader(PackageItem pkg) {
        if ( pkg.containsAsset( "drools" ) ) {
            return pkg.loadAsset( "drools" ).getContent();
        }
        return "";
    }

    public static void updateDroolsHeader(String string,
                                          PackageItem pkg) {
        pkg.checkout();
        AssetItem conf;
        if ( pkg.containsAsset( "drools" ) ) {
            conf = pkg.loadAsset( "drools" );
            conf.updateContent( string );

            conf.checkin( "" );
        } else {
            conf = pkg.addAsset( "drools",
                                 "" );
            conf.updateFormat( "package" );
            conf.updateContent( string );

            conf.checkin( "" );
        }

    }
}
