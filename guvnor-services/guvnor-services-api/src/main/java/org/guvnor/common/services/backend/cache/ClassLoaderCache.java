/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.backend.cache;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.uberfire.java.nio.file.Path;

/***
 * Contract to manage Resources URI in terms of pom dependencies (included transient)
 * and .class and resources (.drl and similar) present in the target folders and List of deps raw,
 * instead of rebuild to read the same resources from a previous build
 */
public interface ClassLoaderCache {

    boolean containsPomDependencies(Path projectRootPath);

    void clearClassloaderResourcesMap();

    void removeProjectDeps(Path projectRootPath);



    /** Event types*/

    void addEventTypes(Path projectRootPath, Set<String> types);

    Optional<Set<String>> getEventTypes(Path projectRootPath);

    void removeEventTypes(Path projectPath);



    /** Declared types*/

    void addDeclaredTypes(Path projectRootPath, Map<String, byte[]> store);

    Optional<Map<String, byte[]>> getDeclaredTypes(Path projectPath);

    void removeDeclaredTypes(Path projectPath);



    /** Target classloader*/

    void addTargetMapClassLoader(Path projectRootPath, ClassLoader classLoader);

    Optional<ClassLoader> getTargetMapClassLoader(Path projectRootPath);

    void removeTargetMapClassloader(Path projectRootPath);



    /** Dependencies classloader*/

    void addDependenciesClassLoader(Path projectRootPath, ClassLoader classLoader);

    Optional<ClassLoader> getDependenciesClassLoader(Path projectRootPath);

    void removeDependenciesClassloader(Path projectRootPath);



    /** Target Project dependencies*/

    void addTargetProjectDependencies(Path projectRootPath, List<String> uri);

    void removeTargetProjectDependencies(Path projectRootPath);

    List<String> getTargetsProjectDependencies(Path projectRootPath);

    List<String> getTargetsProjectDependenciesFiltered(Path projectRootPath, String packageName);

}
