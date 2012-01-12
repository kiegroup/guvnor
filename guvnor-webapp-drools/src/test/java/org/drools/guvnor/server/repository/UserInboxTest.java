/*
 * Copyright 2010 JBoss Inc
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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.UserInfo.InboxEntry;
import org.junit.Test;

public class UserInboxTest extends GuvnorTestBase {

    @Test
    public void testInboxen() throws Exception {

        RulesRepository repo = rulesRepository;

        UserInbox inb = new UserInbox( repo );
        inb.clearAll();
        inb.addToRecentEdited( "ABC",
                               "This is a note" );
        Thread.sleep( 100 );
        inb.addToRecentEdited( "QED",
                               "Here we go..." );
        repo.save();

        List<InboxEntry> entries = inb.loadRecentEdited();
        assertEquals( 2,
                      entries.size() );

        InboxEntry e1 = entries.get( 0 );
        assertEquals( "ABC",
                      e1.assetUUID );
        assertEquals( "This is a note",
                      e1.note );

        InboxEntry e2 = entries.get( 1 );
        assertEquals( "QED",
                      e2.assetUUID );
        assertTrue( e2.timestamp > e1.timestamp );

        inb.clearAll();

        for ( int i = 0; i < UserInbox.MAX_RECENT_EDITED; i++ ) {
            inb.addToRecentEdited( "X" + i,
                                   "NOTE" + i );
        }

        assertEquals( "X0",
                      inb.loadRecentEdited().get( 0 ).assetUUID );

        inb.addToRecentEdited( "Y1",
                               "NOTE" );

        List<InboxEntry> res = inb.loadRecentEdited();
        assertEquals( "X1",
                      inb.loadRecentEdited().get( 0 ).assetUUID );
        assertEquals( "Y1",
                      res.get( res.size() - 1 ).assetUUID );

        assertTrue( res.get( res.size() - 2 ).assetUUID.startsWith( "X" ) );

        inb.addToRecentEdited( "Y2",
                               "NOTE" );

        res = inb.loadRecentEdited();
        assertEquals( "X2",
                      inb.loadRecentEdited().get( 0 ).assetUUID );
        assertEquals( "Y2",
                      res.get( res.size() - 1 ).assetUUID );
        assertEquals( "Y1",
                      res.get( res.size() - 2 ).assetUUID );

    }

    @Test
    public void testLoadEntriesRecentlyOpened() {

        RulesRepository repo = rulesRepository;

        UserInbox inb = new UserInbox( repo );
        inb.clearAll();
        inb.addToRecentOpened( "QED",
                               "hey" );
        List<InboxEntry> es = inb.loadEntries( ExplorerNodeConfig.RECENT_VIEWED_ID );
        assertEquals( 1,
                      es.size() );
        assertEquals( "QED",
                      es.get( 0 ).assetUUID );
    }

    @Test
    public void testLoadEntriesRecentlyEdited() throws Exception {

        RulesRepository repo = rulesRepository;

        UserInbox inb = new UserInbox( repo );
        inb.clearAll();
        inb.addToRecentEdited( "ABC",
                               "This is a note" );

        List<InboxEntry> es = inb.loadEntries( ExplorerNodeConfig.RECENT_EDITED_ID );
        assertEquals( 1,
                      es.size() );
        assertEquals( "ABC",
                      es.get( 0 ).assetUUID );

    }

    @Test
    public void testLoadEntriesIncoming() throws Exception {

        RulesRepository repo = rulesRepository;

        AssetItem asset = repo.loadDefaultModule().addAsset( "testLoadEntriesIncoming",
                                                              "" );
        UserInbox ib = new UserInbox( repo );
        ib.clearAll();
        ib.addToIncoming( asset.getUUID(),
                          "hey",
                          "mic" );

        List<InboxEntry> es = ib.loadIncoming();
        assertEquals( 1,
                      es.size() );
        assertEquals( asset.getUUID(),
                      es.get( 0 ).assetUUID );

    }

    @Test
    public void testRead() throws Exception {

        RulesRepository repo = rulesRepository;

        UserInbox inb = new UserInbox( repo );
        inb.clearAll();
        inb.addToRecentOpened( "QED",
                               "hey" );
        inb.addToRecentEdited( "ABC",
                               "This is a note" );

        List<InboxEntry> es = inb.loadRecentOpened();
        assertEquals( 1,
                      es.size() );
        assertEquals( "QED",
                      es.get( 0 ).assetUUID );

    }

    @Test
    public void testDupes() throws Exception {

        RulesRepository repo = rulesRepository;

        UserInbox inb = new UserInbox( repo );
        inb.clearAll();

        for ( int i = 0; i < 120; i++ ) {
            inb.addToRecentOpened( "A" + i,
                                   "NOTE" );
        }

        List<InboxEntry> res = inb.loadRecentOpened();
        assertEquals( 120,
                      res.size() );
        inb.addToRecentOpened( "XX",
                               "hey" );

        assertEquals( res.size() + 1,
                      inb.loadRecentOpened().size() );
        InboxEntry firstOld = inb.loadRecentOpened().get( 0 );
        assertEquals( "A0",
                      firstOld.assetUUID );

        Thread.sleep( 30 );
        //shouldn't add another one... 
        inb.addToRecentOpened( "A0",
                               "hey22" );

        List<InboxEntry> finalList = inb.loadRecentOpened();
        assertEquals( res.size() + 1,
                      finalList.size() );
        assertEquals( "A1",
                      finalList.get( 0 ).assetUUID );

        InboxEntry lastEntry = finalList.get( finalList.size() - 1 );
        assertEquals( "A0",
                      lastEntry.assetUUID );

        assertTrue( lastEntry.timestamp > firstOld.timestamp );

    }

    @Test
    public void testHelper() throws Exception {

        RulesRepository repo = rulesRepository;

        UserInbox ib = new UserInbox( repo );
        ib.clearAll();
        AssetItem asset = repo.loadDefaultModule().addAsset( "InBoxTestHelper",
                                                              "hey" );
        UserInbox.recordOpeningEvent( asset );

        List<InboxEntry> es = ib.loadRecentOpened();
        assertEquals( 1,
                      es.size() );
        assertEquals( asset.getUUID(),
                      es.get( 0 ).assetUUID );
        assertEquals( "InBoxTestHelper",
                      es.get( 0 ).note );

        UserInbox.recordUserEditEvent( asset );
        es = ib.loadRecentEdited();
        assertEquals( 1,
                      es.size() );
        assertEquals( asset.getUUID(),
                      es.get( 0 ).assetUUID );
    }

    @Test
    public void testIncoming() throws Exception {

        RulesRepository repo = rulesRepository;

        AssetItem asset = repo.loadDefaultModule().addAsset( "testIncoming",
                                                              "" );
        UserInbox ib = new UserInbox( repo );
        ib.clearAll();
        ib.addToIncoming( asset.getUUID(),
                          "hey",
                          "mic" );
        ib.addToIncoming( "YYY",
                          "hey2",
                          "mic" );

        List<InboxEntry> es = ib.loadIncoming();
        assertEquals( 2,
                      es.size() );
        assertEquals( asset.getUUID(),
                      es.get( 0 ).assetUUID );
        assertEquals( "YYY",
                      es.get( 1 ).assetUUID );
        UserInbox.recordOpeningEvent( asset );

        es = ib.loadIncoming();
        assertEquals( 1,
                      es.size() );
        assertEquals( "YYY",
                      es.get( 0 ).assetUUID );

    }

}
