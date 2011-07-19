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

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.rpc.PackageConfigData;

/**
 * A logical folder containing PackageConfigData
 */
public class Folder {

    private String            name;
    private PackageConfigData conf;
    private List<Folder>      children = new ArrayList<Folder>();

    Folder add(String f,
                      PackageConfigData conf) {
        Folder n = new Folder();
        n.name = f;
        n.conf = conf;
        children.add( n );
        return n;
    }

    Folder getChildContainer(String name) {
        for ( Folder fld : children ) {
            if ( fld.name.equals( name ) ) {
                return fld;
            }
        }
        return null;
    }

    public List<Folder> getChildren() {
        return this.children;
    }
    
    public PackageConfigData getPackageConfigData() {
        return this.conf;
    }
    
    public String getFolderName() {
        return this.name;
    }

}
