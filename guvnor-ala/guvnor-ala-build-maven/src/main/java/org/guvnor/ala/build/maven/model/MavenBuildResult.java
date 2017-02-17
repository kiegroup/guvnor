/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.build.maven.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class models the results of a maven build execution.
 */
public class MavenBuildResult {

    private List<String> buildExceptions = new ArrayList<>( );

    private List<MavenBuildMessage> buildMessages = new ArrayList<>( );

    /**
     * @return the list of exceptions produced during the build. If the number of exceptions is 0 the build was successful,
     * and un-successful in any other case.
     */
    public List< String > getBuildExceptions( ) {
        return buildExceptions;
    }

    public void addBuildException( String exception ) {
        buildExceptions.add( exception );
    }

    /**
     * @return true if exceptions were produced during the build, false in any other case.
     */
    public boolean hasExceptions() {
        return buildExceptions != null && !buildExceptions.isEmpty();
    }

    /**
     * @return the list of messages produced during the build.
     */
    public List<MavenBuildMessage> getBuildMessages( ) {
        return buildMessages;
    }

    public void addBuildMessage( MavenBuildMessage buildMessage ) {
        buildMessages.add( buildMessage );
    }
}