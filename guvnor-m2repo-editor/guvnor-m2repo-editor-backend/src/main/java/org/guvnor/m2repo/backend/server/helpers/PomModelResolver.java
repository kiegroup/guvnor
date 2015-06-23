/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.m2repo.backend.server.helpers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.xml.PomModel;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.kie.api.builder.ReleaseId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PomModelResolver {

    private static final Logger log = LoggerFactory.getLogger( PomModelResolver.class );

    /**
     * Construct a PomModel from a JAR by parsing first the pom.xml file within the JAR
     * and if not present a pom.properties file in the JAR.
     * @param jarStream InputStream to the JAR
     * @return a populated PomModel or null if neither pom.xml or pom.properties existed in the JAR
     */
    public static PomModel resolveFromJar( InputStream jarStream ) {
        //Attempt to load JAR's POM information from it's pom.xml file
        PomModel pomModel = null;
        try {
            String pomXML = GuvnorM2Repository.loadPomFromJar( jarStream );
            if ( pomXML != null ) {
                pomModel = PomModel.Parser.parse( "pom.xml",
                                                  new ByteArrayInputStream( pomXML.getBytes() ) );
            }
        } catch ( Exception e ) {
            log.info( "Failed to parse pom.xml for GAV information. Falling back to pom.properties.",
                      e );
        }

        //Attempt to load JAR's POM information from it's pom.properties file
        if ( pomModel == null ) {
            try {
                jarStream.reset();
                String pomProperties = GuvnorM2Repository.loadPomPropertiesFromJar( jarStream );
                if ( pomProperties != null ) {
                    final ReleaseId releaseId = ReleaseIdImpl.fromPropertiesString( pomProperties );
                    if ( releaseId != null ) {
                        pomModel = new PomModel.InternalModel();
                        ( (PomModel.InternalModel) pomModel ).setReleaseId( releaseId );
                    }
                }
            } catch ( Exception e ) {
                log.info( "Failed to parse pom.properties for GAV information." );
            }
        }
        return pomModel;
    }

    /**
     * Construct a PomModel from a pom.xml file
     * @param pomStream InputStream to the pom.xml file
     * @return a populated PomModel or null if the file could not be parsed
     */
    public static PomModel resolveFromPom( InputStream pomStream ) {
        PomModel pomModel = null;
        try {
            pomModel = PomModel.Parser.parse( "pom.xml",
                                              pomStream );
        } catch ( Exception e ) {
            log.info( "Failed to parse pom.xml for GAV information.",
                      e );
        }

        return pomModel;
    }

}
