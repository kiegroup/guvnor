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

import java.util.List;

import javax.inject.Inject;

import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.server.repository.MailboxService;
import org.drools.guvnor.server.repository.UserInbox;
import org.drools.repository.UserInfo.InboxEntry;
import org.junit.Ignore;
import org.junit.Test;

/**
 * MailboxService tests in their own Arquillian managed environment to ensure
 * MailboxService is shutdown completely as it appears to intefer with other
 * tests in the same class
 */
public class ServiceImplementationMailboxService1Test extends GuvnorTestBase {

    @Inject
    private MailboxService    mailboxService;

    //@Before
    public void startMailboxService() {
        // Need to reference @ApplicationScoped bean to force load 
        // in the absence of @ManagedBean( eager=true ) in JDK1.5
        mailboxService.wakeUp();
    }
    
    //@After
    public void stopMailboxService() {
        mailboxService.stopExecutor();
    }

    @Test
    @Ignore("Is this the cause of our pain?")
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
