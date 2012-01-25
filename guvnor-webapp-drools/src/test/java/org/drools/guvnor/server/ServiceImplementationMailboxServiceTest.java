/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.server.repository.MailboxService;
import org.drools.guvnor.server.repository.RepositoryStartupService;
import org.drools.guvnor.server.repository.UserInbox;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.UserInfo.InboxEntry;
import org.junit.Before;
import org.junit.Test;

/**
 * MailboxService tests in their own Arquillian managed environment to ensure
 * MailboxService is shutdown completely as it appears to intefer with other
 * tests in the same class
 */
public class ServiceImplementationMailboxServiceTest extends GuvnorTestBase {

    @Inject
    private RepositoryStartupService repositoryStartupService;

    @Inject
    private MailboxService    mailboxService;

    @Before
    public void startMailboxService() {
        // Need to reference @ApplicationScoped bean to force load 
        // in the absence of @ManagedBean( eager=true ) in JDK1.5
        mailboxService.wakeUp();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testInboxEvents() throws Exception {

        assertNotNull( serviceImplementation.loadInbox( ExplorerNodeConfig.RECENT_EDITED_ID ) );

        //this should trigger the fact that the first user edited something
        AssetItem as = rulesRepository.loadDefaultModule().addAsset( "testLoadInbox",
                                                                                "" );
        as.checkin( "" );
        Asset ras = repositoryAssetService.loadRuleAsset( as.getUUID() );

        TableDataResult res = serviceImplementation.loadInbox( ExplorerNodeConfig.RECENT_EDITED_ID );
        boolean found = false;
        for ( TableDataRow row : res.data ) {
            if ( row.id.equals( ras.getUuid() ) ) found = true;
        }
        assertTrue( found );

        //but should not be in "incoming" yet
        found = false;
        res = serviceImplementation.loadInbox( ExplorerNodeConfig.INCOMING_ID );
        for ( TableDataRow row : res.data ) {
            if ( row.id.equals( as.getUUID() ) ) found = true;
        }
        assertFalse( found );

        //Now, the second user comes along, makes a change...
        RulesRepository repo2 = new RulesRepository( repositoryStartupService.newSession( "seconduser" ) );
        AssetItem as2 = repo2.loadDefaultModule().loadAsset( "testLoadInbox" );
        as2.updateContent( "hey" );
        as2.checkin( "here we go again !" );

        Thread.sleep( 250 );

        //now check that it is in the first users inbox
        TableDataRow rowMatch = null;
        res = serviceImplementation.loadInbox( ExplorerNodeConfig.INCOMING_ID );
        for ( TableDataRow row : res.data ) {
            if ( row.id.equals( as.getUUID() ) ) {
                rowMatch = row;
                break;
            }
        }
        assertNotNull( rowMatch );
        assertEquals( as.getName(),
                      rowMatch.values[0] );
        assertEquals( "seconduser",
                      rowMatch.values[2] ); //should be "from" that user name...

        //shouldn't be in second user's inbox
        UserInbox secondUsersInbox = new UserInbox( repo2 );
        secondUsersInbox.loadIncoming();
        assertEquals( 0,
                      secondUsersInbox.loadIncoming().size() );
        assertEquals( 1,
                      secondUsersInbox.loadRecentEdited().size() );

        //ok lets create a third user...
        RulesRepository repo3 = new RulesRepository( repositoryStartupService.newSession( "thirduser" ) );
        AssetItem as3 = repo3.loadDefaultModule().loadAsset( "testLoadInbox" );
        as3.updateContent( "hey22" );
        as3.checkin( "here we go again 22!" );

        Thread.sleep( 250 );

        //so should be in second user's inbox
        assertEquals( 1,
                      secondUsersInbox.loadIncoming().size() );

        //and also still in the first user's...
        found = false;
        res = serviceImplementation.loadInbox( ExplorerNodeConfig.INCOMING_ID );
        for ( TableDataRow row : res.data ) {
            if ( row.id.equals( as.getUUID() ) ) found = true;
        }
        assertTrue( found );

        //now lets open it with first user, and check that it disappears from the incoming...
        repositoryAssetService.loadRuleAsset( as.getUUID() );
        found = false;
        res = serviceImplementation.loadInbox( ExplorerNodeConfig.INCOMING_ID );
        for ( TableDataRow row : res.data ) {
            if ( row.id.equals( as.getUUID() ) ) found = true;
        }
        assertFalse( found );
    }

    @Test
    public void testTrackRecentOpenedChanged() throws Exception {

        UserInbox ib = new UserInbox( rulesRepository );
        ib.clearAll();
        rulesRepository.createModule( "testTrackRecentOpenedChanged",
                                      "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testTrackRecentOpenedChanged",
                                                  "this is a cat" );

        String id = serviceImplementation.createNewRule( "myrule",
                                                         "desc",
                                                         "testTrackRecentOpenedChanged",
                                                         "testTrackRecentOpenedChanged",
                                                         "drl" );

        Asset ass = repositoryAssetService.loadRuleAsset( id );

        repositoryAssetService.checkinVersion( ass );

        List<InboxEntry> es = ib.loadRecentEdited();
        assertEquals( 1,
                      es.size() );
        assertEquals( ass.getUuid(),
                      es.get( 0 ).assetUUID );
        assertEquals( ass.getName(),
                      es.get( 0 ).note );

        ib.clearAll();

        repositoryAssetService.loadRuleAsset( ass.getUuid() );
        es = ib.loadRecentEdited();
        assertEquals( 0,
                      es.size() );

        //now check they have it in their opened list...
        es = ib.loadRecentOpened();
        assertEquals( 1,
                      es.size() );
        assertEquals( ass.getUuid(),
                      es.get( 0 ).assetUUID );
        assertEquals( ass.getName(),
                      es.get( 0 ).note );

        assertEquals( 0,
                      ib.loadRecentEdited().size() );
    }

}
