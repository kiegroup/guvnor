/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.server;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rpc.ScenarioRunResult;
import org.drools.guvnor.client.rpc.SingleScenarioResult;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyField;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;
import org.drools.ide.common.server.util.ScenarioXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.junit.Test;

import static org.junit.Assert.*;

public class RepositoryScenarioTest extends GuvnorTestBase {
    @Test
    public void testRunScenario() throws Exception {
        RulesRepository repo = rulesRepository;

        System.out.println( "create package" );
        PackageItem pkg = repo.createPackage( "testScenarioRun",
                                              "" );
        DroolsHeader.updateDroolsHeader( "import org.drools.Person\n global org.drools.Cheese cheese\n",
                                                  pkg );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent("rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end");
        rule1.checkin("");
        repo.save();

        Scenario sc = new Scenario();
        FactData person = new FactData();
        person.setName( "p" );
        person.setType("Person");
        person.getFieldData().add(new FieldData("age",
                "40"));
        person.getFieldData().add( new FieldData( "name",
                                                  "michael" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        VerifyRuleFired vr = new VerifyRuleFired( "rule1",
                                                  1,
                                                  null );
        sc.getFixtures().add( vr );

        VerifyFact vf = new VerifyFact();
        vf.setName( "p" );
        vf.getFieldValues().add(new VerifyField("name",
                "michael",
                "=="));
        vf.getFieldValues().add( new VerifyField( "age",
                                                  "42",
                                                  "==" ) );
        sc.getFixtures().add( vf );

        FactData cheese = new FactData();
        cheese.setName( "cheese" );
        cheese.setType("Cheese");
        cheese.getFieldData().add(new FieldData("price",
                "42"));
        sc.getGlobals().add( cheese );

        ScenarioRunResult res = repositoryPackageService.runScenario( pkg.getName(),
                                                                      sc ).result;
        assertNull( res.getErrors() );
        assertNotNull(res.getScenario());
        assertTrue(vf.wasSuccessful());
        assertTrue( vr.wasSuccessful() );

        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;
        assertNull( res.getErrors() );
        assertNotNull(res.getScenario());
        assertTrue(vf.wasSuccessful());
        assertTrue( vr.wasSuccessful() );

        RuleBaseCache.getInstance().clearCache();
        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;
        assertNull( res.getErrors() );
        assertNotNull(res.getScenario());
        assertTrue(vf.wasSuccessful());
        assertTrue( vr.wasSuccessful() );

        //BuilderResult[] results = impl.buildPackage(pkg.getUUID(), null, true);
        //assertNull(results);

        rule1.updateContent( "Junk" );
        rule1.checkin("");

        RuleBaseCache.getInstance().clearCache();
        pkg.updateBinaryUpToDate(false);
        repo.save();
        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;
        assertNotNull( res.getErrors() );
        assertNull(res.getScenario());

        assertTrue(res.getErrors().size() > 0);

        repositoryCategoryService.createCategory("/",
                "sc",
                "");

        String scenarioId = serviceImplementation.createNewRule( "sc1",
                                                "s",
                                                "sc",
                                                pkg.getName(),
                                                AssetFormats.TEST_SCENARIO );

        RuleAsset asset = repositoryAssetService.loadRuleAsset( scenarioId );
        assertNotNull( asset.getContent() );
        assertTrue( asset.getContent() instanceof Scenario );

        Scenario sc_ = (Scenario) asset.getContent();
        sc_.getFixtures().add( new ExecutionTrace() );
        repositoryAssetService.checkinVersion( asset );
        asset = repositoryAssetService.loadRuleAsset( scenarioId );
        assertNotNull( asset.getContent() );
        assertTrue( asset.getContent() instanceof Scenario );
        sc_ = (Scenario) asset.getContent();
        assertEquals( 1,
                      sc_.getFixtures().size() );

    }

    @Test
    public void testRunScenarioWithGeneratedBeans() throws Exception {
        RulesRepository repo = rulesRepository;

        PackageItem pkg = repo.createPackage( "testScenarioRunWithGeneratedBeans",
                                              "" );
        DroolsHeader.updateDroolsHeader( "declare GenBean\n name: String \n age: int \nend\n",
                                                  pkg );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when \n p : GenBean(name=='mic') \n then \n p.setAge(42); \n end" );
        rule1.checkin( "" );
        repo.save();

        Scenario sc = new Scenario();
        FactData person = new FactData();
        person.setName( "c" );
        person.setType( "GenBean" );
        person.getFieldData().add( new FieldData( "age",
                                                  "40" ) );
        person.getFieldData().add( new FieldData( "name",
                                                  "mic" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        VerifyRuleFired vr = new VerifyRuleFired( "rule1",
                                                  1,
                                                  null );
        sc.getFixtures().add( vr );

        VerifyFact vf = new VerifyFact();
        vf.setName( "c" );
        vf.getFieldValues().add( new VerifyField( "name",
                                                  "mic",
                                                  "==" ) );
        vf.getFieldValues().add( new VerifyField( "age",
                                                  "42",
                                                  "==" ) );
        sc.getFixtures().add( vf );

        SingleScenarioResult res_ = repositoryPackageService.runScenario( pkg.getName(),
                                                                          sc );
        assertTrue( res_.auditLog.size() > 0 );

        String[] logEntry = res_.auditLog.get( 0 );
        assertNotNull( logEntry[0],
                       logEntry[1] );

        ScenarioRunResult res = res_.result;

        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );
        assertTrue( vf.wasSuccessful() );
        assertTrue( vr.wasSuccessful() );

    }

    @Test
    public void testRunPackageScenariosWithDeclaredFacts() throws Exception {
        RulesRepository repo = rulesRepository;

        PackageItem pkg = repo.createPackage( "testScenarioRunBulkWithDeclaredFacts",
                                              "" );
        DroolsHeader.updateDroolsHeader( "declare Wang \n age: Integer \n name: String \n end",
                                                  pkg );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when \np : Wang() \n then \np.setAge(42); \n end" );
        rule1.checkin( "" );

        //this rule will never fire
        AssetItem rule2 = pkg.addAsset( "rule_2",
                                        "" );
        rule2.updateFormat( AssetFormats.DRL );
        rule2.updateContent( "rule 'rule2' \n when \np : Wang(age == 1000) \n then \np.setAge(46); \n end" );
        rule2.checkin( "" );
        repo.save();

        //first, the green scenario
        Scenario sc = new Scenario();
        FactData person = new FactData();
        person.setName( "p" );
        person.setType( "Wang" );
        person.getFieldData().add( new FieldData( "age",
                                                  "40" ) );
        person.getFieldData().add( new FieldData( "name",
                                                  "michael" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        VerifyRuleFired vr = new VerifyRuleFired( "rule1",
                                                  1,
                                                  null );
        sc.getFixtures().add( vr );

        VerifyFact vf = new VerifyFact();
        vf.setName( "p" );
        vf.getFieldValues().add( new VerifyField( "name",
                                                  "michael",
                                                  "==" ) );
        vf.getFieldValues().add( new VerifyField( "age",
                                                  "42",
                                                  "==" ) );
        sc.getFixtures().add( vf );

        AssetItem scenario1 = pkg.addAsset( "scen1",
                                            "" );
        scenario1.updateFormat( AssetFormats.TEST_SCENARIO );
        scenario1.updateContent( ScenarioXMLPersistence.getInstance().marshal( sc ) );
        scenario1.checkin( "" );

        //now the bad scenario
        sc = new Scenario();
        person = new FactData();
        person.setName( "p" );
        person.setType( "Wang" );
        person.getFieldData().add( new FieldData( "age",
                                                  "40" ) );
        person.getFieldData().add( new FieldData( "name",
                                                  "michael" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        vr = new VerifyRuleFired( "rule2",
                                  1,
                                  null );
        sc.getFixtures().add( vr );

        AssetItem scenario2 = pkg.addAsset( "scen2",
                                            "" );
        scenario2.updateFormat( AssetFormats.TEST_SCENARIO );
        scenario2.updateContent( ScenarioXMLPersistence.getInstance().marshal( sc ) );
        scenario2.checkin( "" );

        BulkTestRunResult result = repositoryPackageService.runScenariosInPackage( pkg.getUUID() );
        assertNull( result.getResult() );

        assertEquals( 50,
                      result.getPercentCovered() );
        assertEquals( 1,
                      result.getRulesNotCovered().length );
        assertEquals( "rule2",
                      result.getRulesNotCovered()[0] );

        assertEquals( 2,
                      result.getResults().length );

        ScenarioResultSummary s1 = result.getResults()[0];
        assertEquals( 0,
                      s1.getFailures() );
        assertEquals( 3,
                      s1.getTotal() );
        assertEquals( scenario1.getUUID(),
                      s1.getUuid() );
        assertEquals( scenario1.getName(),
                      s1.getScenarioName() );

        ScenarioResultSummary s2 = result.getResults()[1];
        assertEquals( 1,
                      s2.getFailures() );
        assertEquals( 1,
                      s2.getTotal() );
        assertEquals( scenario2.getUUID(),
                      s2.getUuid() );
        assertEquals( scenario2.getName(),
                      s2.getScenarioName() );
    }

    @Test
    public void testRunScenarioWithJar() throws Exception {
        RulesRepository repo = rulesRepository;

        // create our package
        PackageItem pkg = repo.createPackage( "testRunScenarioWithJar",
                                              "" );
        AssetItem model = pkg.addAsset( "MyModel",
                                        "" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );

        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board",
                                                  pkg );

        AssetItem asset = pkg.addAsset( "testRule",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );
        asset.updateContent( "rule 'MyGoodRule' \n dialect 'mvel' \n when Board() then System.err.println(42); \n end" );
        asset.checkin( "" );
        repo.save();

        Scenario sc = new Scenario();
        FactData person = new FactData();
        person.setName( "p" );
        person.setType( "Board" );
        person.getFieldData().add( new FieldData( "cost",
                                                  "42" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        VerifyRuleFired vr = new VerifyRuleFired( "MyGoodRule",
                                                  1,
                                                  null );
        sc.getFixtures().add( vr );

        VerifyFact vf = new VerifyFact();
        vf.setName( "p" );

        vf.getFieldValues().add( new VerifyField( "cost",
                                                  "42",
                                                  "==" ) );
        sc.getFixtures().add( vf );

        ScenarioRunResult res = repositoryPackageService.runScenario( pkg.getName(),
                                                                      sc ).result;
        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );

        assertTrue( vf.wasSuccessful() );
        assertTrue( vr.wasSuccessful() );

        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;

        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );

        assertTrue( vf.wasSuccessful() );
        assertTrue( vr.wasSuccessful() );

        RuleBaseCache.getInstance().clearCache();

        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;
        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );
        assertTrue( vf.wasSuccessful() );
        assertTrue( vr.wasSuccessful() );

    }

    @Test
    public void testRunScenarioWithJarThatHasSourceFiles() throws Exception {
        RulesRepository repo = rulesRepository;

        // create our package
        PackageItem pkg = repo.createPackage( "testRunScenarioWithJarThatHasSourceFiles",
                                              "" );
        AssetItem model = pkg.addAsset( "MyModel",
                                        "" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/jarWithSourceFiles.jar" ) );
        model.checkin( "" );

        DroolsHeader.updateDroolsHeader( "import org.test.Person; \n import org.test.Banana; \n ",
                                                  pkg );

        AssetItem asset = pkg.addAsset( "testRule",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );
        asset.updateContent( "rule 'MyGoodRule' \n dialect 'mvel' \n when \n Person() \n then \n insert( new Banana() ); \n end" );
        asset.checkin( "" );
        repo.save();

        Scenario sc = new Scenario();
        FactData person = new FactData();
        person.setName( "p" );
        person.setType( "Person" );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        VerifyRuleFired vr = new VerifyRuleFired( "MyGoodRule",
                                                  1,
                                                  null );
        sc.getFixtures().add( vr );

        ScenarioRunResult res = null;
        try {
            res = repositoryPackageService.runScenario( pkg.getName(),
                                                        sc ).result;
        } catch ( ClassFormatError e ) {
            fail( "Probably failed when loading a source file instead of class file. " + e );
        }

        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );
        assertTrue( vr.wasSuccessful() );

        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;

        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );
        assertTrue( vr.wasSuccessful() );

        RuleBaseCache.getInstance().clearCache();

        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;

        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );
        assertTrue( vr.wasSuccessful() );

    }

    @Test
    public void testRunPackageScenarios() throws Exception {
        RulesRepository repo = rulesRepository;

        PackageItem pkg = repo.createPackage( "testScenarioRunBulk",
                                              "" );
        DroolsHeader.updateDroolsHeader( "import org.drools.Person",
                                                  pkg );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end" );
        rule1.checkin( "" );

        //this rule will never fire
        AssetItem rule2 = pkg.addAsset( "rule_2",
                                        "" );
        rule2.updateFormat( AssetFormats.DRL );
        rule2.updateContent( "rule 'rule2' \n when \np : Person(age == 1000) \n then \np.setAge(46); \n end" );
        rule2.checkin( "" );
        repo.save();

        //first, the green scenario
        Scenario sc = new Scenario();
        FactData person = new FactData();
        person.setName( "p" );
        person.setType( "Person" );
        person.getFieldData().add( new FieldData( "age",
                                                  "40" ) );
        person.getFieldData().add( new FieldData( "name",
                                                  "michael" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        VerifyRuleFired vr = new VerifyRuleFired( "rule1",
                                                  1,
                                                  null );
        sc.getFixtures().add( vr );

        VerifyFact vf = new VerifyFact();
        vf.setName( "p" );
        vf.getFieldValues().add( new VerifyField( "name",
                                                  "michael",
                                                  "==" ) );
        vf.getFieldValues().add( new VerifyField( "age",
                                                  "42",
                                                  "==" ) );
        sc.getFixtures().add( vf );

        AssetItem scenario1 = pkg.addAsset( "scen1",
                                            "" );
        scenario1.updateFormat( AssetFormats.TEST_SCENARIO );
        scenario1.updateContent( ScenarioXMLPersistence.getInstance().marshal( sc ) );
        scenario1.checkin( "" );

        //now the bad scenario
        sc = new Scenario();
        person = new FactData();
        person.setName( "p" );
        person.setType( "Person" );
        person.getFieldData().add( new FieldData( "age",
                                                  "40" ) );
        person.getFieldData().add( new FieldData( "name",
                                                  "michael" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        vr = new VerifyRuleFired( "rule2",
                                  1,
                                  null );
        sc.getFixtures().add( vr );

        AssetItem scenario2 = pkg.addAsset( "scen2",
                                            "" );
        scenario2.updateFormat( AssetFormats.TEST_SCENARIO );
        scenario2.updateContent( ScenarioXMLPersistence.getInstance().marshal( sc ) );
        scenario2.checkin( "" );

        AssetItem scenario3 = pkg.addAsset( "scenBOGUS",
                                            "" );
        scenario3.updateFormat( AssetFormats.TEST_SCENARIO );
        scenario3.updateContent( "SOME RUBBISH" );
        scenario3.updateDisabled( true );
        scenario3.checkin( "" );

        //love you
        long time = System.currentTimeMillis();
        BulkTestRunResult result = repositoryPackageService.runScenariosInPackage( pkg.getUUID() );
        System.err.println( "Time taken for runScenariosInPackage " + (System.currentTimeMillis() - time) );
        assertNull( result.getResult() );

        assertEquals( 50,
                      result.getPercentCovered() );
        assertEquals( 1,
                      result.getRulesNotCovered().length );
        assertEquals( "rule2",
                      result.getRulesNotCovered()[0] );

        assertEquals( 2,
                      result.getResults().length );

        ScenarioResultSummary s1 = result.getResults()[0];
        assertEquals( 0,
                      s1.getFailures() );
        assertEquals( 3,
                      s1.getTotal() );
        assertEquals( scenario1.getUUID(),
                      s1.getUuid() );
        assertEquals( scenario1.getName(),
                      s1.getScenarioName() );

        ScenarioResultSummary s2 = result.getResults()[1];
        assertEquals( 1,
                      s2.getFailures() );
        assertEquals( 1,
                      s2.getTotal() );
        assertEquals( scenario2.getUUID(),
                      s2.getUuid() );
        assertEquals( scenario2.getName(),
                      s2.getScenarioName() );
    }

}
