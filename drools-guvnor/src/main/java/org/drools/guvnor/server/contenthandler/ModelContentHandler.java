package org.drools.guvnor.server.contenthandler;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * This is used for handling jar models for the rules.
 * @author Michael Neale
 */
public class ModelContentHandler extends ContentHandler {

    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {
        // do nothing, as we have an attachment
    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        // do nothing, as we have an attachment
    }

    /**
     * This is called when a model jar is attached, it will peer into it, and then automatically add imports
     * if there aren't any already in the package header configuration.
     */
    public void modelAttached(AssetItem asset) throws IOException {
        InputStream in = asset.getBinaryContentAttachment();

        PackageItem pkg = asset.getPackage();
        String header = ServiceImplementation.getDroolsHeader( pkg );
        StringBuilder buf = new StringBuilder();

        if ( header != null ) {
            buf.append( header );
            buf.append( '\n' );
        }

        JarInputStream jis = new JarInputStream( in );
        JarEntry entry = null;
        while ( (entry = jis.getNextJarEntry()) != null ) {
            if ( !entry.isDirectory() ) {
                if ( entry.getName().endsWith( ".class" ) && entry.getName().indexOf( '$' ) == -1 ) {

                    String line = "import " + convertPathToName( entry.getName() );
                    // Add imports only once
                    if ( !header.contains( line ) ) {
                        buf.append( line );
                        buf.append( "\n" );
                    }
                }
            }
        }

        ServiceImplementation.updateDroolsHeader( buf.toString(),
                                                  pkg );

        //pkg.updateHeader(buf.toString());

        pkg.checkin( "Imports setup automatically on model import." );

    }

    public static String convertPathToName(String name) {
        return name.replace( ".class",
                             "" ).replace( "/",
                                           "." );
    }

}