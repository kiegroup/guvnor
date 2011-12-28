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

package org.drools.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.junit.Ignore;
import org.junit.Test;

/**
 * This is a bit of a hacked scalability test. It will add 5000 odd rule nodes,
 * and then do some basic operations. It will take a LONG time to add these
 * nodes, and does it in batches.
 */
public class ScalabilityTest extends RepositoryTestCase {

    private static final int NUM = 5000;
    private RulesRepository  repo;

    @Test
    @Ignore("Legacy code, needs to be refactored.")
    //This test is not appropriate for unit tests. Actually I do not see how this can be justified as a 
    //test, as it does not check for any particular errors nor any performance numbers. 
    public void testRun() throws Exception {
        Properties properties = new Properties();
        properties.put( JCRRepositoryConfigurator.REPOSITORY_ROOT_DIRECTORY,
                        "./scalabilityTestRepo" );
        RulesRepositoryConfigurator config = RulesRepositoryConfigurator.getInstance( properties );
        Session session = config.getJCRRepository().login(
                                                           new SimpleCredentials( "alan_parsons",
                                                                                  "password".toCharArray() ) );
        config.setupRepository( session );
        repo = new RulesRepository( session );

        long start = System.currentTimeMillis();
        setupData( repo );
        System.out.println( "time to add, version and tag 5000: " + (System.currentTimeMillis() - start) );
        List list = listACat( repo );
        System.out.println( "list size is: " + list.size() );

        start = System.currentTimeMillis();
        AssetItem item = (AssetItem) list.get( 0 );
        item.updateContent( "this is a description" );
        item.checkin( "newer" );
        System.out.println( "time to update and version: " + (System.currentTimeMillis() - start) );

        start = System.currentTimeMillis();
        item = (AssetItem) list.get( 42 );
        item.updateContent( "this is a description" );
        item.updateContent( "wooooooooooooooooooooooooooooooooooot" );
        item.checkin( "latest" );
        System.out.println( "time to update and version: " + (System.currentTimeMillis() - start) );

    }

    private List listACat(RulesRepository repo) {
        long start = System.currentTimeMillis();
        List results = repo.findAssetsByCategory( "HR/CAT_1",
                                                  0,
                                                  -1 ).assets;
        System.out.println( "Time for listing a cat: " + (System.currentTimeMillis() - start) );

        start = System.currentTimeMillis();
        List results2 = repo.findAssetsByCategory( "HR/CAT_1",
                                                   0,
                                                   -1 ).assets;
        System.out.println( "Time for listing a cat: " + (System.currentTimeMillis() - start) );

        start = System.currentTimeMillis();
        results2 = repo.findAssetsByCategory( "HR/CAT_100",
                                              0,
                                              -1 ).assets;
        System.out.println( "Time for listing a cat: " + (System.currentTimeMillis() - start) );

        start = System.currentTimeMillis();
        results2 = repo.findAssetsByCategory( "HR/CAT_100",
                                              0,
                                              -1 ).assets;
        System.out.println( "Time for listing a cat: " + (System.currentTimeMillis() - start) );

        return results;
    }

    /** To run this, need to hack the addRule method to not save a session */
    private void setupData(RulesRepository repo) throws Exception {

        int count = 1;

        List list = new ArrayList();

        String prefix = "HR/";
        String cat = prefix + "CAT_1";
        for ( int i = 1; i <= NUM; i++ ) {

            if ( i % 500 == 0 ) {
                repo.getSession().save();
                for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
                    AssetItem element = (AssetItem) iter.next();
                    element.getNode().checkin();
                }
                list.clear();
            }

            if ( i > 2500 ) {
                prefix = "FINANCE/";
            }

            if ( count == 100 ) {
                count = 1;
                cat = prefix + "CAT_" + i;
                System.err.println( "changing CAT" );
                System.gc();

            } else {
                count++;
            }

            String ruleName = "rule_" + i + "_" + System.currentTimeMillis();
            System.out.println( "ADDING rule: " + ruleName );

            AssetItem item = repo.loadDefaultModule().addAsset( ruleName,
                                                                 "Foo(bar == " + i + ")panic(" + i + ");" );
            item.addCategory( cat );
            list.add( item );

        }
    }

}
