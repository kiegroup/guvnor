/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.server.builder;

import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.RulesRepositoryException;
import org.drools.rule.MapBackedClassLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ClassLoaderBuilder {

    private final List<JarInputStream> jarInputStreams;

    public ClassLoaderBuilder(AssetItemIterator assetItemIterator) {
        this.jarInputStreams = getJars(assetItemIterator);
    }

    public ClassLoaderBuilder(List<JarInputStream> jarInputStreams) {
        this.jarInputStreams = jarInputStreams;
    }

    /**
     * Load up all the Jars for the given package.
     *
     * @param assetItemIterator
     */
    private List<JarInputStream> getJars(AssetItemIterator assetItemIterator) {
        List<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();
        while (assetItemIterator.hasNext()) {
            AssetItem item = assetItemIterator.next();
            if (item.getBinaryContentAttachment() != null) {
                try {
                    jarInputStreams.add(new JarInputStream(item.getBinaryContentAttachment(), false));
                } catch (IOException e) {
                    //TODO: Not a place for RulesRepositoryException -Rikkola-
                    throw new RulesRepositoryException(e);
                }
            }
        }
        return jarInputStreams;
    }

    public List<JarInputStream> getJarInputStreams() {
        return jarInputStreams;
    }

    /**
     * For a given list of Jars, create a class loader.
     */
    public MapBackedClassLoader buildClassLoader() {
        MapBackedClassLoader mapBackedClassLoader = getMapBackedClassLoader();

        try {
            for (JarInputStream jis : jarInputStreams) {
                JarEntry entry = null;
                byte[] buf = new byte[1024];
                int len = 0;
                while ((entry = jis.getNextJarEntry()) != null) {
                    if (!entry.isDirectory() && !entry.getName().endsWith(".java")) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        while ((len = jis.read(buf)) >= 0) {
                            out.write(buf, 0, len);
                        }

                        mapBackedClassLoader.addResource(entry.getName(), out.toByteArray());
                    }
                }

            }
        } catch (IOException e) {
            //TODO: Not a place for RulesRepositoryException -Rikkola-
            throw new RulesRepositoryException(e);
        }

        return mapBackedClassLoader;
    }

    private MapBackedClassLoader getMapBackedClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<MapBackedClassLoader>() {
            public MapBackedClassLoader run() {
                return new MapBackedClassLoader(getParentClassLoader());
            }
        });
    }

    private ClassLoader getParentClassLoader() {
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        if (parentClassLoader == null) {
            parentClassLoader = BRMSPackageBuilder.class.getClassLoader();
        }
        return parentClassLoader;
    }

    public boolean hasJars() {
        return jarInputStreams != null && !jarInputStreams.isEmpty();
    }
}
