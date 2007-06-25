package org.drools.brms.server.repository;
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






import junit.framework.TestCase;

public class BRMSRepositoryConfigurationTest extends TestCase {

    public void testConfiguration() throws Exception {
        
        BRMSRepositoryConfiguration config = new BRMSRepositoryConfiguration();
        config.setHomeDirectory( "qed" );
        assertEquals("qed", config.repositoryHomeDirectory);
        config.setRepositoryConfigurator( MockRepositoryConfigurator.class.getName() );
        
        assertTrue(config.configurator instanceof MockRepositoryConfigurator);
        config.repository = new MockRepo();

        assertNotNull(config.newSession("foo"));
        assertNotSame(config.newSession("foo"), config.newSession("foo"));
        
    }

    
}