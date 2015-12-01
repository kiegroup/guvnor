/*
 * Copyright 2015 JBoss Inc
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

package org.guvnor.common.services.project.client;

import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GAVEditorPanelTest {

    private GAVEditorView view;
    private GAVEditor editor;

    @Before
    public void setUp() throws Exception {
        view = mock( GAVEditorView.class );
        editor = new GAVEditor( view );
    }

    @Test
    public void testSetArtifactID() throws Exception {
        final GAV gav = new GAV( "groupId",
                                 "artifactId",
                                 "version" );

        editor.setGAV( gav );
        editor.setArtifactID( "changed" );

        verify( view,
                times( 1 ) ).setArtifactId( eq( "changed" ) );

        assertEquals( "changed",
                      gav.getArtifactId() );
    }

}
