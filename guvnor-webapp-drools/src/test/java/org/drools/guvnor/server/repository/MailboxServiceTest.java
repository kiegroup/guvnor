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
import static org.junit.Assert.assertSame;

import java.util.List;

import javax.inject.Inject;

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.UserInfo.InboxEntry;
import org.junit.Test;

public class MailboxServiceTest extends GuvnorTestBase {

    @Inject
    private MailboxService mailboxService;

    @Test
    public void testMailbox() throws Exception {
        // TODO this tests fails because rulesRepository.getSession().getUserID() is "guest" because rulesRepository is created before loginAsAdmin()

        AssetItem asset = rulesRepository.loadDefaultModule().addAsset( "testMailbox",
                                                              "" );

        UserInbox mailman = new UserInbox( rulesRepository,
                                           GuvnorBootstrapConfiguration.MAILMAN_USERNAME_DEFAULT );
        assertEquals(0,
                mailman.loadIncoming().size());

        UserInbox ib = new UserInbox( rulesRepository,
                                      "mic" );
        ib.addToRecentEdited(asset.getUUID(),
                "hey");
        assertEquals( 0,
                      ib.loadIncoming().size() );

        UserInbox ib2 = new UserInbox( rulesRepository,
                                       "mic2" );
        ib2.addToRecentEdited(asset.getUUID(),
                "hey");
        assertEquals( 0,
                      ib2.loadIncoming().size() );

        mailboxService.recordItemUpdated(asset);

        Thread.sleep(300);

        List<InboxEntry> es = ib.loadIncoming();
        assertEquals(1,
                es.size());
        assertEquals( asset.getUUID(),
                      es.get( 0 ).assetUUID );

        es = ib2.loadIncoming();
        assertEquals( 1,
                      es.size() );
        assertEquals( asset.getUUID(),
                      es.get( 0 ).assetUUID );

        assertEquals( 0,
                      mailman.loadIncoming().size() );

        AssetItem ass2 = rulesRepository.loadDefaultModule().addAsset( "testMailbox2",
                                                             "XX" );

        ib2.addToRecentEdited( ass2.getUUID(),
                               "hey" );
        mailman.addToIncoming( ass2.getUUID(),
                               "whee",
                               "mic" );
        assertEquals( 1,
                      mailman.loadIncoming().size() );
        assertEquals( 1,
                      ib2.loadIncoming().size() );
        mailboxService.wakeUp();
        Thread.sleep( 250 );

        assertEquals( 2,
                      ib2.loadIncoming().size() );
        assertEquals( 0,
                      mailman.loadIncoming().size() );
        assertEquals(1,
                ib.loadIncoming().size());

        mailboxService.wakeUp();
        assertEquals( 2,
                      ib2.loadIncoming().size() );
        assertEquals( 0,
                      mailman.loadIncoming().size() );
        assertEquals( 1,
                      ib.loadIncoming().size() );

    }

    @Test
    public void testOneToMany() throws Exception {
        // TODO this tests fails because rulesRepository.getSession().getUserID() is "guest" because rulesRepository is created before loginAsAdmin()

        String sender = rulesRepository.getSession().getUserID();
        AssetItem asset = rulesRepository.loadDefaultModule().addAsset( "testMailboxOneToMany",
                                                              "" );
        UserInbox ib1 = new UserInbox( rulesRepository,
                                       sender );
        UserInbox ib2 = new UserInbox( rulesRepository,
                                       "dave" );
        UserInbox ib3 = new UserInbox( rulesRepository,
                                       "phil" );

        ib1.clearAll();
        ib2.clearAll();
        ib3.clearAll();

        ib1.addToRecentEdited( asset.getUUID(),
                               "hey" );
        ib2.addToRecentEdited( asset.getUUID(),
                               "hey" );
        ib3.addToRecentEdited( asset.getUUID(),
                               "hey" );

        assertEquals( 0,
                      ib1.loadIncoming().size() );
        assertEquals( 0,
                      ib2.loadIncoming().size() );
        assertEquals( 0,
                      ib3.loadIncoming().size() );

        mailboxService.recordItemUpdated(asset);

        Thread.sleep( 250 );

        assertEquals( 0,
                      ib1.loadIncoming().size() );
        assertEquals( 1,
                      ib2.loadIncoming().size() );
        assertEquals( 1,
                      ib3.loadIncoming().size() );

    }

}
