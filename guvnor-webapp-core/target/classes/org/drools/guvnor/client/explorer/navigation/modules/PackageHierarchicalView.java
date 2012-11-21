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
package org.drools.guvnor.client.explorer.navigation.modules;

import org.drools.guvnor.client.rpc.Module;

/**
 * A hierarchical representation of PackageConfigData, i.e. sub-packages are
 * held in sub-folders. A sub-package is a package name split on "."
 */
public class PackageHierarchicalView extends PackageView {

    void doAddPackage(String name,
                      Module conf) {
        Folder folder = root;
        String[] folders = name.split( "\\." );
        for ( int i = 0; i < folders.length; i++ ) {
            String f = folders[i];

            // create a new package if not existing, or cannot be considered as a container (ie contains rules)
            Folder existing = folder.getChildContainer( f );
            if ( existing == null ) {
                if ( i == folders.length - 1 ) {
                    //leaf
                    folder = folder.add( f,
                                         conf );
                } else {
                    folder = folder.add( f,
                                         null );
                }

            } else {
                folder = existing;
            }
        }
        addSubPackages( name,
                        conf.getSubModules() );
    }

}