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

package org.drools.guvnor.server.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import javax.jcr.Repository;

import org.drools.repository.JCRRepositoryConfigurator;
import org.junit.Ignore;
import org.junit.Test;

public class RepositoryStartupServiceTest {

    @Test
    @Ignore("RepositoryStartupService is a Seam managed application-wide Singleton, mocking it is not healthy to other tests.")
    public void testConfiguration() throws Exception {

        RepositoryStartupService config = new RepositoryStartupService();
        // TODO seam3upgrade
//        config.setHomeDirectory( "qed" );
        // TODO seam3upgrade
//        assertEquals( "qed",
//                      config.properties.get( JCRRepositoryConfigurator.REPOSITORY_ROOT_DIRECTORY ) );
        // TODO seam3upgrade
//        config.setRepositoryConfigurator( MockRepositoryConfigurator.class.getName() );

        Repository repository = config.getRepositoryInstance();

        assertEquals( MockRepo.class.getSimpleName(),
                      repository.getClass().getSimpleName() );

        assertNotNull( config.newSession( "foo",
                                          "password" ) );
        assertNotSame( config.newSession( "foo",
                                          "password" ),
                       config.newSession( "foo",
                                          "password" ) );
    }

}
