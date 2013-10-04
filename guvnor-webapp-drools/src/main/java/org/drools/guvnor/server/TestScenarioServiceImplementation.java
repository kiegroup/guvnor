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

package org.drools.guvnor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.drools.ClockType;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.base.ClassTypeResolver;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.BinaryRuleBaseLoader;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rpc.ScenarioRunResult;
import org.drools.guvnor.client.rpc.SingleScenarioResult;
import org.drools.guvnor.client.rpc.TestScenarioService;
import org.drools.guvnor.client.rpc.WorkItemService;
import org.drools.guvnor.server.builder.AuditLogReporter;
import org.drools.guvnor.server.builder.ClassLoaderBuilder;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.server.testscenarios.RuleCoverageListener;
import org.drools.ide.common.server.testscenarios.ScenarioRunner;
import org.drools.ide.common.shared.workitems.PortableWorkDefinition;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.drools.rule.Package;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.rule.ConsequenceException;
import org.drools.util.CompositeClassLoader;
import org.jboss.seam.remoting.annotations.WebRemote;
import org.jboss.seam.security.annotations.LoggedIn;

import org.drools.compiler.DrlParser;
import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;

public class TestScenarioServiceImplementation
        implements TestScenarioService {


    private static final LoggingHelper log = LoggingHelper.getLogger(TestScenarioService.class);

    @Inject @Preferred
    private RulesRepository rulesRepository;

    @Inject
    private ServiceSecurity serviceSecurity;

    @Inject
    private RepositoryAssetOperations repositoryAssetOperations;

    @Inject
    private WorkItemService workItemService;

    @Inject
    private ServiceImplementation serviceImplementation;

    @Inject
    private RepositoryModuleOperations repositoryModuleOperations;

    @WebRemote
    @LoggedIn
    public SingleScenarioResult runScenario(String packageName,
                                            Scenario scenario) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName(packageName);

        return runScenario(packageName,
                scenario,
                null);
    }

    private SingleScenarioResult runScenario(
            String packageName,
            Scenario scenario,
            RuleCoverageListener coverage) throws SerializationException {
        try {
            return runScenario(
                    scenario,
                    this.rulesRepository.loadModule(packageName),
                    coverage);
        } catch (Exception e) {
            if (e instanceof DetailedSerializationException) {
                DetailedSerializationException exception = (DetailedSerializationException) e;
                if (exception.getErrs() != null) {
                    return new SingleScenarioResult(new ScenarioRunResult(exception.getErrs()));
                } else {
                    throw exception;
                }
            } else {
                throw new DetailedSerializationException("Unable to run the scenario.",
                        e.getMessage());
            }
        }
    }

    private SingleScenarioResult runScenario(Scenario scenario,
                                             ModuleItem item,
                                             RuleCoverageListener coverage) throws DetailedSerializationException {

        RuleBase ruleBase = loadCacheRuleBase(item);
        org.drools.rule.Package aPackage = ruleBase.getPackages()[0];

        SessionConfiguration sessionConfiguration = new SessionConfiguration();
        sessionConfiguration.setClockType(ClockType.PSEUDO_CLOCK);
        sessionConfiguration.setKeepReference(false);
        InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession(
                sessionConfiguration,
                null);

        if (coverage != null) {
            workingMemory.addEventListener(coverage);
        }

        //Add stub Work Item Handlers
        WorkItemHandler workItemHandlerStub = getWorkItemHandlerStub();
        for (PortableWorkDefinition portableWorkDefinition : workItemService.loadWorkItemDefinitions(item.getUUID())) {
            workingMemory.getWorkItemManager().registerWorkItemHandler(
                    portableWorkDefinition.getName(),
                    workItemHandlerStub);
        }

        //Run Test Scenario
        try {
            AuditLogReporter logger = new AuditLogReporter(workingMemory);
            CompositeClassLoader classLoader = ((InternalRuleBase) ruleBase).getRootClassLoader();
            StringBuilder buf = new StringBuilder();
            AssetItemIterator it = item.listAssetsByFormat(AssetFormats.DRL_MODEL);
            while (it.hasNext()) {
                AssetItem as = it.next();
                buf.append(as.getContent());
                buf.append('\n');
            }

            String droolsHeader = DroolsHeader.getDroolsHeader(item);
            DrlParser parser = new DrlParser();
            PackageDescr pkgItem =null;
            try {
                pkgItem = parser.parse( droolsHeader + "\n" + buf.toString() );
            } catch ( final DroolsParserException e1 ) {
                throw new IllegalStateException( "Serious error, unable to validate package." );
            }  
            List<ImportDescr> imports= new ArrayList<ImportDescr>( pkgItem.getImports() );
            new ScenarioRunner(
                    new ClassTypeResolver(
                            getAllImports(aPackage),
                            classLoader),
                    classLoader,
                    workingMemory,
                    imports
            ).run(scenario);

            return new SingleScenarioResult(
                    new ScenarioRunResult(scenario),
                    logger.buildReport());
        } catch (ClassNotFoundException e) {
            log.error("Unable to load a required class.",
                    e);
            throw new DetailedSerializationException("Unable to load a required class.",
                    e.getMessage());
        } catch (ConsequenceException e) {
            String messageShort = "There was an error executing the consequence of rule [" + e.getRule().getName() + "]";
            String messageLong = e.getMessage();
            if (e.getCause() != null) {
                messageLong += "\nCAUSED BY " + e.getCause().getMessage();
            }

            log.error(messageShort + ": " + messageLong,
                    e);
            throw new DetailedSerializationException(messageShort,
                    messageLong);
        } catch (Exception e) {
            log.error("Unable to run the scenario.",
                    e);
            throw new DetailedSerializationException("Unable to run the scenario.",
                    e.getMessage());
        }
    }


    private Set<String> getAllImports(Package aPackage) {
        Set<String> allImports = new HashSet<String>(aPackage.getImports().keySet());

        if (aPackage.getGlobals() != null) {
            for (Object o : aPackage.getGlobals().keySet()) {
                allImports.add(aPackage.getGlobals().get(o));
            }
        }
        // need this for Generated beans to work
        allImports.add(aPackage.getName() + ".*");
        return allImports;
    }

    /*
    * Set the Rule base in a cache
    */
    private RuleBase loadCacheRuleBase(ModuleItem packageItem) throws DetailedSerializationException {

        if (packageItem.isBinaryUpToDate() && RuleBaseCache.getInstance().contains(packageItem.getUUID())) {
            return RuleBaseCache.getInstance().get(packageItem.getUUID());
        } else {

            // we have to build the package, and try again.
            if (packageItem.isBinaryUpToDate()) {
                RuleBase ruleBase = loadRuleBase(packageItem);
                RuleBaseCache.getInstance().put(packageItem.getUUID(),
                        ruleBase);
                return ruleBase;
            } else {
                repositoryModuleOperations.buildModuleWithoutErrors(packageItem, false);
                RuleBase ruleBase = loadRuleBase(packageItem);
                RuleBaseCache.getInstance().put(packageItem.getUUID(),
                        ruleBase);
                return ruleBase;
            }

        }
    }

    public BulkTestRunResult runScenariosInPackage(ModuleItem packageItem) throws SerializationException {

        if (!packageItem.isBinaryUpToDate() || !RuleBaseCache.getInstance().contains(packageItem.getUUID())) {

            if (packageItem.isBinaryUpToDate()) {
                RuleBaseCache.getInstance().put(
                        packageItem.getUUID(),
                        loadRuleBase(packageItem));
            } else {
                BuilderResult result = repositoryModuleOperations.buildModule(packageItem,
                        false);
                if (result == null || result.getLines().size() == 0) {
                    RuleBaseCache.getInstance().put(
                            packageItem.getUUID(),
                            loadRuleBase(packageItem));
                } else {
                    return new BulkTestRunResult(
                            result,
                            null,
                            0,
                            null);
                }
            }
        }

        AssetItemIterator it = packageItem.listAssetsByFormat(AssetFormats.TEST_SCENARIO);
        List<ScenarioResultSummary> resultSummaries = new ArrayList<ScenarioResultSummary>();
        RuleBase rb = RuleBaseCache.getInstance().get(packageItem.getUUID());
        Package bin = rb.getPackages()[0];

        RuleCoverageListener coverage = new RuleCoverageListener(expectedRules(bin));

        while (it.hasNext()) {
            AssetItem as = it.next();
            if (!as.getDisabled()) {
                Asset asset = repositoryAssetOperations.loadAsset(as);
                Scenario sc = (Scenario) asset.getContent();
                runScenario(packageItem.getName(),
                        sc,
                        coverage);

                int[] totals = sc.countFailuresTotal();
                resultSummaries.add(new ScenarioResultSummary(totals[0],
                        totals[1],
                        asset.getName(),
                        asset.getDescription(),
                        asset.getUuid()));
            }
        }

        ScenarioResultSummary[] summaries = resultSummaries.toArray(new ScenarioResultSummary[resultSummaries.size()]);

        return new BulkTestRunResult(null,
                resultSummaries.toArray(summaries),
                coverage.getPercentCovered(),
                coverage.getUnfiredRules());

    }


    private HashSet<String> expectedRules(Package bin) {
        HashSet<String> h = new HashSet<String>();
        for (int i = 0; i < bin.getRules().length; i++) {
            h.add(bin.getRules()[i].getName());
        }
        return h;
    }

    //Creates a stub Work Item Handler that does nothing. A problem is that if the *real* Work Item Handler
    //sets a Result Parameter that is used in other rules the results of running the Test Scenario could (or
    //more likely would) be different than those expected. We can't use the *real* Work Item Handler as we
    //have no control what code it executes unless we look into using SecurityManagers...
    private WorkItemHandler getWorkItemHandlerStub() {
        return new WorkItemHandler() {

            public void executeWorkItem(WorkItem workItem,
                                        WorkItemManager manager) {
                //Does absolute nothing, however could log execution if needed
            }

            public void abortWorkItem(WorkItem workItem,
                                      WorkItemManager manager) {
            }

        };
    }

    private RuleBase loadRuleBase(ModuleItem item) throws DetailedSerializationException {
        try {
            return deserKnowledgebase(
                    item,
                    createClassLoaderBuilder(item).buildClassLoader());
        } catch (ClassNotFoundException e) {
            log.error("Unable to load rule base.",
                    e);
            throw new DetailedSerializationException("A required class was not found.",
                    e.getMessage());
        } catch (Exception e) {
            log.error("Unable to load rule base.",
                    e);
            log.info("...but trying to rebuild binaries...");
            try {
                BuilderResult builderResult = repositoryModuleOperations.buildModule(
                        item,
                        true);
                if (builderResult != null && builderResult.getLines().size() > 0) {
                    log.error("There were errors when rebuilding the knowledgebase.");
                    throw new DetailedSerializationException("There were errors when rebuilding the knowledgebase.",
                            "");
                }
            } catch (Exception e1) {
                log.error("Unable to rebuild the rulebase: " + e.getMessage());
                throw new DetailedSerializationException("Unable to rebuild the rulebase.",
                        e.getMessage());
            }
            try {
                return deserKnowledgebase(
                        item,
                        createClassLoaderBuilder(item).buildClassLoader());
            } catch (Exception e2) {
                log.error("Unable to reload knowledgebase: " + e.getMessage());
                throw new DetailedSerializationException("Unable to reload knowledgebase.",
                        e.getMessage());
            }

        }
    }

    private ClassLoaderBuilder createClassLoaderBuilder(ModuleItem packageItem) {
        return new ClassLoaderBuilder(packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat(AssetFormats.MODEL));
    }

    private RuleBase deserKnowledgebase(ModuleItem item,
                                        ClassLoader classloader) throws IOException,
                                                                ClassNotFoundException {
        RuleBase rulebase = RuleBaseFactory.newRuleBase( new RuleBaseConfiguration( classloader ) );
        BinaryRuleBaseLoader rbl = new BinaryRuleBaseLoader( rulebase,
                                                             classloader );
        rbl.addPackage( new ByteArrayInputStream( item.getCompiledBinaryBytes() ) );
        return rulebase;
    }

    @WebRemote
    @LoggedIn
    public BulkTestRunResult runScenariosInPackage(String packageUUID) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid(packageUUID);
        ModuleItem item = rulesRepository.loadModuleByUUID(packageUUID);
        return runScenariosInPackage(item);
    }


}
