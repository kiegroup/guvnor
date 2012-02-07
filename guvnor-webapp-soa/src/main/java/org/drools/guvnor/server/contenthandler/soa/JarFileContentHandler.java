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

package org.drools.guvnor.server.contenthandler.soa;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.server.builder.ClassLoaderBuilder;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ICanHasAttachment;
import org.drools.repository.AssetItem;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * This is used for handling jar files: List the classes that are part of the jar.
 */
public class JarFileContentHandler extends ContentHandler
    implements
    ICanHasAttachment {

    public void retrieveAssetContent(Asset asset,
                                     AssetItem item) throws SerializationException {
        if (item.getBinaryContentAttachment() != null) {

            try {
                String classesInJar = getClassesFromJar(item);
                RuleContentText text = new RuleContentText();
                text.content = classesInJar;
                asset.setContent(text);
            } catch (Exception e) {
                throw new SerializationException(e.getMessage());
            }
        }
    }

    public void storeAssetContent(Asset asset,
                                  AssetItem repoAsset) throws SerializationException {
        // do nothing, as its read-only
    }
    
    public void onAttachmentAdded(AssetItem asset) throws IOException {
    
    }
    
    public void onAttachmentRemoved(AssetItem item) throws IOException {
    }
    
    private String getClassesFromJar(AssetItem assetItem) throws IOException {
        Map<String, String> nonCollidingImports = new HashMap<String, String>();
        String assetPackageName = assetItem.getModuleName();

        //Setup class-loader to check for class visibility
        JarInputStream cljis = new JarInputStream( assetItem.getBinaryContentAttachment() );
        List<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();
        jarInputStreams.add( cljis );
        ClassLoaderBuilder clb = new ClassLoaderBuilder( jarInputStreams );
        ClassLoader cl = clb.buildClassLoader();

        //Reset stream to read classes
        JarInputStream jis = new JarInputStream( assetItem.getBinaryContentAttachment() );
        JarEntry entry = null;

        //Get Class names from JAR, only the first occurrence of a given Class leaf name will be inserted. Thus 
        //"org.apache.commons.lang.NumberUtils" will be imported but "org.apache.commons.lang.math.NumberUtils"
        //will not, assuming it follows later in the JAR structure.
        while ( (entry = jis.getNextJarEntry()) != null ) {
            if ( !entry.isDirectory() ) {
                if ( entry.getName().endsWith( ".class" ) && entry.getName().indexOf( '$' ) == -1 && !entry.getName().endsWith( "package-info.class" ) ) {
                    String fullyQualifiedName = convertPathToName( entry.getName() );
                    if ( isClassVisible( cl,
                                         fullyQualifiedName,
                                         assetPackageName ) ) {
                        String leafName = getLeafName( fullyQualifiedName );
                        if(!nonCollidingImports.containsKey( leafName )) {
                        nonCollidingImports.put( leafName,
                                                 fullyQualifiedName );
                        }
                    }
                }
            }
        }

        //Build list of classes
        StringBuffer classes = new StringBuffer();
        for ( String value : nonCollidingImports.values() ) {
             classes.append( value + "\n");
        }

        return classes.toString();
    }

    private String getLeafName(String fullyQualifiedName) {
        int index = fullyQualifiedName.lastIndexOf( "." );
        if ( index == -1 ) {
            return fullyQualifiedName;
        }
        return fullyQualifiedName.substring( index + 1 );
    }

    //Only import public classes; or those in the same package as the Asset
    private boolean isClassVisible(ClassLoader cl,
                                   String className,
                                   String assetPackageName) {
        try {
            Class< ? > cls = cl.loadClass( className );
            int modifiers = cls.getModifiers();
            if ( Modifier.isPublic( modifiers ) ) {
                return true;
            }
            String packageName = className.substring( 0,
                                                      className.lastIndexOf( "." ) );
            if ( !packageName.equals( assetPackageName ) ) {
                return false;
            }
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    public static String convertPathToName(String name) {
        return name.replace( ".class",
                             "" ).replace( "/",
                                           "." );
    }

}
