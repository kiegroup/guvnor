/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.server.maven;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.apache.commons.codec.digest.DigestUtils.shaHex;
import static org.drools.guvnor.server.maven.cache.FileDownloadUtil.downloadFile;
import static org.drools.guvnor.server.maven.cache.GuvnorArtifactCacheSupport.getURLtoLocalUserMavenRepo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import org.drools.guvnor.client.rpc.MavenArtifact;
import org.junit.Test;

public class FileDownloadUtilTest {

    @Test
    public void testDownloadRemoteRepository() throws IOException {
        final String jbossReleaseRepo = "http://repository.jboss.org/nexus/content/repositories/releases/";
        final MavenArtifact artifact = new MavenArtifact( "org.kie:knowledge-api:jar:5.3.1.Final:compile" );

        final HttpURLConnection connection = (HttpURLConnection) new URL( artifact.toURL( jbossReleaseRepo ) ).openConnection();
        connection.setRequestMethod( "HEAD" );
        try {
            final int responseCode = connection.getResponseCode();
            if ( responseCode != 200 ) {
                //won't fail if you can't get access to external resource
                return;
            }
        } catch ( UnknownHostException ex ) {
            //won't fail if you can't reach jboss repo
            return;
        }

        final String finalFileName = System.getProperty( "java.io.tmpdir" ) + "/" + artifact.toFileName();
        final File result = downloadFile( artifact.toURL( jbossReleaseRepo ),
                                          finalFileName );

        assertNotNull( result );
        assertEquals( "47c645f0f605790b5d505c7d2f27b745",
                      md5Hex( getBytes( result ) ) );
    }

    @Test
    public void testLocalFile() throws IOException {
        final String localMavenRepo = getURLtoLocalUserMavenRepo();
        final MavenArtifact artifact = new MavenArtifact( "org.antlr:antlr-runtime:jar:3.3:compile" );

        final String finalFileName = System.getProperty( "java.io.tmpdir" ) + "/" + artifact.toFileName();

        final File result = downloadFile( artifact.toURL( localMavenRepo ),
                                          finalFileName );

        assertNotNull( result );
        assertEquals( "ccd65b08cbc9b7e90b9facd4d125a133c6f87228",
                      shaHex( getBytes( result ) ) );
    }

    @Test
    public void testNonExistentFile() throws IOException {
        final String localMavenRepo = getURLtoLocalUserMavenRepo();
        final MavenArtifact artifact = new MavenArtifact( "org.antlr:antlr-some:jar:3.3:compile" );

        final String finalFileName = System.getProperty( "java.io.tmpdir" ) + "/" + artifact.toFileName();

        final File result = downloadFile( artifact.toURL( localMavenRepo ),
                                          finalFileName );

        assertNull( result );
    }

    private byte[] getBytes(File file) throws IOException {
        byte[] bytes = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream( file );
        fileInputStream.read( bytes );
        return bytes;
    }
}