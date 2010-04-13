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
import java.util.HashSet;
import java.util.Set;
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
public class ModelContentHandler extends ContentHandler
    implements
    ICanHasAttachment {

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
    public void onAttachmentAdded(AssetItem asset) throws IOException {

        PackageItem pkg = asset.getPackage();
        StringBuilder header = createNewHeader( ServiceImplementation.getDroolsHeader( pkg ) );

        Set<String> imports = getImportsFromJar( asset.getBinaryContentAttachment() );

        for ( String importLine : imports ) {
            if ( header.indexOf( importLine ) == -1 ) {
                header.append( importLine ).append( "\n" );
            }
        }

        ServiceImplementation.updateDroolsHeader( header.toString(),
                                                  pkg );

        pkg.checkin( "Imports setup automatically on model import." );

    }

    public void onAttachmentRemoved(AssetItem item) throws IOException {

        PackageItem pkg = item.getPackage();
        StringBuilder header = createNewHeader( ServiceImplementation.getDroolsHeader( pkg ) );

        Set<String> imports = getImportsFromJar( item.getBinaryContentAttachment() );

        for ( String importLine : imports ) {
            String importLineWithLineEnd = importLine + "\n";

            header = removeImportIfItExists( header,
                                             importLineWithLineEnd );
        }

        ServiceImplementation.updateDroolsHeader( header.toString(),
                                                  pkg );

        pkg.checkin( "Imports removed automatically on model archiving." );

    }

    private StringBuilder removeImportIfItExists(StringBuilder header,
                                                 String importLine) {
        if ( header.indexOf( importLine ) >= 0 ) {
            int indexOfImportLine = header.indexOf( importLine );
            header = header.replace( indexOfImportLine,
                                     indexOfImportLine + importLine.length(),
                                     "" );
        }
        return header;
    }

    private StringBuilder createNewHeader(String header) {
        StringBuilder buf = new StringBuilder();

        if ( header != null ) {
            buf.append( header );
            buf.append( '\n' );
        }
        return buf;
    }

    private Set<String> getImportsFromJar(InputStream in) throws IOException {
        Set<String> imports = new HashSet<String>();

        JarInputStream jis = new JarInputStream( in );
        JarEntry entry = null;
        while ( (entry = jis.getNextJarEntry()) != null ) {
            if ( !entry.isDirectory() ) {
                if ( entry.getName().endsWith( ".class" ) && entry.getName().indexOf( '$' ) == -1 && !entry.getName().endsWith( "package-info.class" ) ) {

                    String line = "import " + convertPathToName( entry.getName() );
                    imports.add( line );
                }
            }
        }

        return imports;
    }

    public static String convertPathToName(String name) {
        return name.replace( ".class",
                             "" ).replace( "/",
                                           "." );
    }

}