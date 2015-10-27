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

package org.guvnor.common.services.project.client;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class POMEditorPanelTest {

    @Mock
    private POMEditorPanelView view;

    @Mock
    private SyncBeanManager iocManager;

    private POMEditorPanel panel;
    private POMEditorPanelView.Presenter presenter;

    @Before
    public void setUp() throws Exception {
        panel = new POMEditorPanel( view, iocManager );
        presenter = panel;
    }

    @Test
    public void testLoad() throws Exception {
        POM gavModel = createTestModel( "group", "artifact", "1.1.1" );
        gavModel.setParent( new GAV( "org.parent", "parent", "1.1.1" ) );
        panel.setPOM( gavModel, false );

        verify( view ).setGAV( gavModel.getGav() );
        verify( view ).setTitleText( "artifact" );
        verify( view ).setParentGAV( gavModel.getParent() );
        verify( view ).disableGroupID( "" );
        verify( view ).disableVersion( "" );
        verify( view ).showParentGAV();

        gavModel = createTestModel( "pomName", "pomDescription", "group", "artifact", "1.1.1" );
        panel.setPOM( gavModel, false );

        verify( view ).setName( "pomName" );
        verify( view ).setDescription( "pomDescription" );
        verify( view ).enableGroupID();
        verify( view ).enableVersion();
        verify( view ).hideParentGAV();
    }

    @Test
    public void testProjectNameValidation() throws Exception {
        panel.setValidName( true );
        verify( view ).setValidName( true );

        panel.setValidName( false );
        verify( view ).setValidName( false );
    }

    @Test
    public void testGroupIDValidation() throws Exception {
        panel.setValidGroupID( true );
        verify( view ).setValidGroupID( true );

        panel.setValidGroupID( false );
        verify( view ).setValidGroupID( false );
    }

    @Test
    public void testArtifactIDValidation() throws Exception {
        panel.setValidArtifactID( true );
        verify( view ).setValidArtifactID( true );

        panel.setValidArtifactID( false );
        verify( view ).setValidArtifactID( false );
    }

    @Test
    public void testVersionValidation() throws Exception {
        panel.setValidVersion( true );
        verify( view ).setValidVersion( true );

        panel.setValidVersion( false );
        verify( view ).setValidVersion( false );
    }

    @Test
    public void testOpenProjectContext() throws Exception {
        IOCBeanDef iocBeanDef = mock( IOCBeanDef.class );
        PlaceManager placeManager = mock( PlaceManager.class );
        when( iocBeanDef.getInstance() ).thenReturn( placeManager );
        when( iocManager.lookupBean( eq( PlaceManager.class ) ) ).thenReturn( iocBeanDef );

        presenter.onOpenProjectContext();
        verify( placeManager ).goTo( "repositoryStructureScreen" );
    }

    private POM createTestModel( String group,
                                 String artifact,
                                 String version ) {
        return new POM( new GAV( group, artifact, version ) );
    }

    private POM createTestModel( String name,
                                 String description,
                                 String group,
                                 String artifact,
                                 String version ) {
        return new POM( name, description, new GAV( group, artifact, version ) );
    }
}
