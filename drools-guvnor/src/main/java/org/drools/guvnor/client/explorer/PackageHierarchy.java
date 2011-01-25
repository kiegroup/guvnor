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

package org.drools.guvnor.client.explorer;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.rpc.PackageConfigData;

public class PackageHierarchy {

    private Folder root = new Folder();

    public void addPackage(PackageConfigData config) {
        Folder folder = getRoot();
        String[] folders = config.name.split( "\\." );
        for ( int i = 0; i < folders.length; i++ ) {
            String folderName = folders[i];
            Folder existing = folder.contains( folderName );
            if ( existing == null || existing.getChildren().size() == 0 ) {
                if ( i == folders.length - 1 ) {
                    //leaf
                    folder = folder.add( folderName,
                                         config );
                } else {
                    folder = folder.add( folderName,
                                         null );
                }

            } else {
                folder = existing;
            }
        }
    }

    public void setRoot(Folder root) {
        this.root = root;
    }

    public Folder getRoot() {
        return root;
    }

    public static class Folder {
        private String            name;
        private PackageConfigData config;
        private List<Folder>      children = new ArrayList<Folder>();

        public Folder add(String folderName,
                          PackageConfigData config) {
            Folder folder = new Folder();
            folder.setName( folderName );
            folder.setConfig( config );
            getChildren().add( folder );
            return folder;
        }

        public String toString() {
            return getName();
        }

        public Folder contains(String folderName) {
            for ( Folder folder : getChildren() ) {
                if ( folder.getName().equals( folderName ) ) {
                    return folder;
                }
            }
            return null;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setConfig(PackageConfigData config) {
            this.config = config;
        }

        public PackageConfigData getConfig() {
            return config;
        }

        public void setChildren(List<Folder> children) {
            this.children = children;
        }

        public List<Folder> getChildren() {
            return children;
        }
    }

}
