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

package org.drools.guvnor.client.explorer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

public class PerspectiveLoaderTest {

    private PerspectiveLoader                          perspectiveLoader;
    private Collection<IFramePerspectiveConfiguration> result = new ArrayList<IFramePerspectiveConfiguration>();

    @Before
    public void setUp() throws Exception {
        ConfigurationServiceAsync configurationServiceAsync = mock( ConfigurationServiceAsync.class );
        perspectiveLoader = new PerspectiveLoaderMock( configurationServiceAsync );
    }

    @Test
    public void testLoadDefault() throws Exception {
        LoadPerspectives loadPerspectives = spy( new LoadPerspectivesTester( 1,
                                                                             "Author" ) );
        perspectiveLoader.loadPerspectives( loadPerspectives );

        verify( loadPerspectives ).loadPerspectives( Matchers.<Collection<Perspective>> anyObject() );
    }

    @Test
    public void testLoadDefaultAndFewHtmlPerspectives() throws Exception {
        setUpPerspectiveConfiguration( "Runtime" );
        setUpPerspectiveConfiguration( "I have spaces" );

        LoadPerspectives loadPerspectives = spy( new LoadPerspectivesTester( 3,
                                                                             "Author",
                                                                             "Runtime",
                                                                             "I have spaces" ) );
        perspectiveLoader.loadPerspectives( loadPerspectives );

        verify( loadPerspectives ).loadPerspectives( Matchers.<Collection<Perspective>> anyObject() );
    }

    private void setUpPerspectiveConfiguration(String name) {
        IFramePerspectiveConfiguration perspectivesConfiguration = new IFramePerspectiveConfiguration();
        perspectivesConfiguration.setName( name );
        result.add( perspectivesConfiguration );
    }

    private void assertContainsPerspective(String name,
                                           Collection<Perspective> perspectives) {
        boolean found = false;

        for ( Perspective perspective : perspectives ) {
            if ( perspective.getName().equals( name ) ) {
                found = true;
                break;
            }
        }

        assertTrue( String.format( "Could not find perspective %s",
                                   name ),
                    found );
    }

    class LoadPerspectivesTester
        implements
        LoadPerspectives {

        private int      amountOfPerspectives;
        private String[] perspectiveNames;

        LoadPerspectivesTester(int amountOfPerspectives,
                               String... perspectiveNames) {
            this.amountOfPerspectives = amountOfPerspectives;
            this.perspectiveNames = perspectiveNames;
        }

        public void loadPerspectives(Collection<Perspective> perspectives) {
            assertEquals( amountOfPerspectives,
                          perspectives.size() );
            for ( String perspectiveName : perspectiveNames ) {
                assertContainsPerspective( perspectiveName,
                                           perspectives );
            }
        }
    }

    class PerspectiveLoaderMock extends PerspectiveLoader {

        public PerspectiveLoaderMock(ConfigurationServiceAsync configurationService) {
            super( configurationService );
        }

        public void loadPerspectives(final LoadPerspectives loadPerspectives) {
            handleResult( result,
                          loadPerspectives );
        }
    }
}
