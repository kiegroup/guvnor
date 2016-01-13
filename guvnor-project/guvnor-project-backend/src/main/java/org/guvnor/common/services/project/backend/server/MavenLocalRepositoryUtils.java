/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.backend.server;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.guvnor.common.services.project.model.GAV;

public class MavenLocalRepositoryUtils {

    /**
     * Get a Path pointing to a transient M2 Local Repository
     * @param gav GAV (helper to name temporary folder)
     * @return
     * @throws IOException
     */
    public static File getRepositoryPath( final GAV gav ) throws IOException {
        final File tempLocalRepositoryBasePath = createTempDirectory( "m2-" + toFileName( gav ) );
        return tempLocalRepositoryBasePath;
    }

    private static String toFileName( final GAV gav ) {
        final StringBuilder sb = new StringBuilder();
        sb.append( "m2-" ).append( gav.getGroupId() + "-" + gav.getArtifactId() + "-" + gav.getVersion() );
        return sb.toString();
    }

    /**
     * Destroy the temporary local Maven Repository and all content.
     * @param m2Folder
     */
    public static void tearDownMavenRepository( final File m2Folder ) {
        FileUtils.deleteQuietly( m2Folder );
    }

    /**
     * Get a Path pointing to a transient folder
     * @param name Name of folder to create
     * @return
     * @throws IOException
     */
    public static File createTempDirectory( final String name ) throws IOException {
        final File temp = File.createTempFile( name,
                                               Long.toString( System.nanoTime() ) );

        if ( !( temp.delete() ) ) {
            throw new IOException( "Could not delete temp file: " + temp.getAbsolutePath() );
        }

        if ( !( temp.mkdir() ) ) {
            throw new IOException( "Could not create temp directory: " + temp.getAbsolutePath() );
        }

        return temp;
    }

}
