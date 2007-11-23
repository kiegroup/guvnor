package org.drools.brms.server;
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



import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Session;

import junit.framework.TestCase;

import org.drools.brms.client.rpc.BuilderResult;
import org.drools.brms.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.VersionableItem;
import org.drools.rule.Package;

/**
 * A playground for performance analysing.
 * @author Michael Neale
 *
 */
public class PerfServiceTest extends TestCase {

    private long time;

    public void testDummy() {}

    public void XXXtestWarmup() throws Exception {

        ServiceImplementation impl = getService();
        RulesRepository repo = impl.repository;



        //create our package
        PackageItem pkg = repo.createPackage( "testBinaryPackageCompile", "" );
        pkg.updateHeader( "import org.drools.Person" );
        Session session = repo.getSession();
        Node pkgNode = repo.getSession().getNodeByUUID( pkg.getUUID() );

        Node assetNode = pkgNode.getNode( "assets" );



        for (int i = 0; i < 500; i++) {
            System.out.println("processing asset:" + i);
            Node ruleNode = assetNode.addNode( "" + i, "drools:assetNodeType" );

            ruleNode.setProperty( AssetItem.TITLE_PROPERTY_NAME,
                                  "" + i );

            ruleNode.setProperty( AssetItem.DESCRIPTION_PROPERTY_NAME,
                                  "" );

            ruleNode.setProperty( AssetItem.FORMAT_PROPERTY_NAME,
                                  "DRL" );


            ruleNode.setProperty( VersionableItem.CHECKIN_COMMENT,
                                  "Initial" );

            Calendar lastModified = Calendar.getInstance();

            ruleNode.setProperty( AssetItem.LAST_MODIFIED_PROPERTY_NAME, lastModified );
//            ruleNode.setProperty( AssetItem.PACKAGE_NAME_PROPERTY, this.getName() );
            ruleNode.setProperty( AssetItem.CREATOR_PROPERTY_NAME, session.getUserID() );



            repo.save();
            ruleNode.checkin();
//            AssetItem rule1 = pkg.addAsset( "rule_" + i, "" );
//            rule1.updateFormat( AssetFormats.DRL );
//            rule1.updateContent( "rule 'rule_" + i + "'  \n when p:Person() \n then p.setAge(" + i + "); \n end");
//            rule1.checkin( "" );

        }

        repo.save();


    }

    /**
     * This will test creating a package, check it compiles, and can exectute rules,
     * then take a snapshot, and check that it reports errors.
     */
    public void XXXtestBinaryPackageCompileAndExecute() throws Exception {


        ServiceImplementation impl = getService();
        RulesRepository repo = impl.repository;

        reset();

        PackageItem pkg = repo.loadPackage( "testBinaryPackageCompile" );

        time("loaded package"); reset();

        BuilderResult[] results = impl.buildPackage( pkg.getUUID(), null, true );

        time("built"); reset();
        assertNull(results);

        pkg = repo.loadPackage( "testBinaryPackageCompile" );
        byte[] binPackage = pkg.getCompiledPackageBytes();

        time("got bytes"); reset();

        assertNotNull(binPackage);

        ByteArrayInputStream bin = new ByteArrayInputStream(binPackage);
        ObjectInputStream in = new ObjectInputStream(bin);
        Package binPkg = (Package) in.readObject();

        assertNotNull(binPkg);
        assertTrue(binPkg.isValid());



        impl.createPackageSnapshot( "testBinaryPackageCompile", "SNAP1", false, "" );



    }

    private void time(String m) {
        System.out.println(m + " : " + (System.currentTimeMillis() - time) );

    }

    private void reset() {
        this.time = System.currentTimeMillis();

    }

    private ServiceImplementation getService() throws Exception {
        ServiceImplementation impl = new ServiceImplementation();
        impl.repository = new RulesRepository( TestEnvironmentSessionHelper.getSession() );
        return impl;
    }

}