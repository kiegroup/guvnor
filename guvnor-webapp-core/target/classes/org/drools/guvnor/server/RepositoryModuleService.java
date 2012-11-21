/*
 * Copyright 2011 JBoss Inc
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.drools.ClockType;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.base.ClassTypeResolver;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.core.util.BinaryRuleBaseLoader;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleService;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rpc.ScenarioRunResult;
import org.drools.guvnor.client.rpc.SingleScenarioResult;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse;
import org.drools.guvnor.client.rpc.SnapshotDiffs;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.server.builder.AuditLogReporter;
import org.drools.guvnor.server.builder.ClassLoaderBuilder;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.contenthandler.ModelContentHandler;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.server.testscenarios.RuleCoverageListener;
import org.drools.ide.common.server.testscenarios.ScenarioRunner;
import org.drools.ide.common.shared.workitems.PortableWorkDefinition;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.ModuleItem;
import org.drools.repository.RepositoryFilter;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.drools.rule.Package;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.rule.ConsequenceException;
import org.jboss.seam.remoting.annotations.WebRemote;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.LoggedIn;

import com.google.gwt.user.client.rpc.SerializationException;

@ApplicationScoped
@Named("org.drools.guvnor.client.rpc.PackageService")
public class RepositoryModuleService
        implements
        ModuleService {

    private static final long serialVersionUID = 901123;

    private static final LoggingHelper log = LoggingHelper.getLogger(RepositoryAssetService.class);

    @Inject
    private RulesRepository rulesRepository;

    @Inject
    private ServiceSecurity serviceSecurity;

    @Inject
    private Identity identity;

    @Inject
    private RepositoryModuleOperations repositoryModuleOperations;

    @Inject
    private RepositoryAssetOperations repositoryAssetOperations;

    @Inject
    private ServiceImplementation serviceImplementation;

    /**
     * Role-based Authorization check: This method only returns modules that
     * the user has permission to access. User has permission to access the
     * particular module when: The user has a package.readonly role or higher
     * (i.e., package.admin, package.developer) to this module.
     */
    @WebRemote
    @LoggedIn
    public Module[] listModules() {
        return listModules(null);
    }

    @WebRemote
    @LoggedIn
    public Module[] listModules(String workspace) {
        RepositoryFilter pf = new ModuleFilter(identity);
        return repositoryModuleOperations.listModules(
                false,
                workspace,
                pf);
    }

    @WebRemote
    @LoggedIn
    public Module[] listArchivedModules() {
        return listArchivedModules(null);
    }

    @WebRemote
    @LoggedIn
    public Module[] listArchivedModules(String workspace) {
        return repositoryModuleOperations.listModules(
                true,
                workspace,
                new ModuleFilter(identity));
    }

    public Module loadGlobalModule() {
        return repositoryModuleOperations.loadGlobalModule();
    }

    @WebRemote
    @LoggedIn
    public void rebuildPackages() throws SerializationException {
        Iterator<ModuleItem> pkit = rulesRepository.listModules();
        StringBuilder errs = new StringBuilder();
        while (pkit.hasNext()) {
            ModuleItem pkg = pkit.next();
            try {
                BuilderResult builderResult = this.buildPackage(pkg.getUUID(),
                        true);
                if (builderResult != null) {
                    errs.append("Unable to build package name [").append(pkg.getName()).append("]\n");
                    StringBuilder buf = createStringBuilderFrom(builderResult);
                    log.warn(buf.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("An error occurred building package [" + pkg.getName() + "]\n");
                errs.append("An error occurred building package [").append(pkg.getName()).append("]\n");
            }
        }
    }

    private StringBuilder createStringBuilderFrom(BuilderResult res) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < res.getLines().size(); i++) {
            buf.append(res.getLines().get(i).toString());
            buf.append('\n');
        }
        return buf;
    }

    @WebRemote
    @LoggedIn
    public String buildModuleSource(String moduleUUID) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid(moduleUUID);
        return repositoryModuleOperations.buildModuleSource(moduleUUID);
    }

    @WebRemote
    public String copyModule(String sourceModuleName,
                             String destModuleName) throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();
        return repositoryModuleOperations.copyModules(sourceModuleName,
                destModuleName);
    }

    @WebRemote
    @LoggedIn
    public void removeModule(String uuid) {
        serviceSecurity.checkSecurityIsPackageAdminWithPackageUuid(uuid);
        repositoryModuleOperations.removeModule(uuid);
    }

    @WebRemote
    @LoggedIn
    public String renameModule(String uuid,
                               String newName) {
        serviceSecurity.checkSecurityIsPackageAdminWithPackageUuid(uuid);

        return repositoryModuleOperations.renameModule(uuid,
                newName);
    }

    @WebRemote
    @LoggedIn
    public byte[] exportModules(String moduleName) {
        serviceSecurity.checkSecurityIsPackageAdminWithPackageName(moduleName);
        return repositoryModuleOperations.exportModules(moduleName);
    }

    @WebRemote
    @LoggedIn
    public void importPackages(byte[] byteArray,
                               boolean importAsNew) {
        repositoryModuleOperations.importPackages(byteArray,
                importAsNew);
    }

    @WebRemote
    public String createModule(String name,
                               String description,
                               String format) throws RulesRepositoryException {
        return repositoryModuleOperations.createModule(name,
                description,
                format);
    }

    @WebRemote
    public String createModule(String name,
                               String description,
                               String format,
                               String[] workspace) throws RulesRepositoryException {
        serviceSecurity.checkSecurityIsAdmin();
        return repositoryModuleOperations.createModule(name,
                description,
                format,
                workspace);
    }

    /*
     * @WebRemote public String createPackage(String name, String description,
     * String format) throws RulesRepositoryException {
     * serviceSecurity.checkSecurityIsAdmin(); return
     * repositoryPackageOperations.createPackage( name, description, new
     * String[]{} ); }
     */
    /*
     * @WebRemote public String createPackage(String name, String description,
     * String format, String[] workspace) throws RulesRepositoryException {
     * return createPackage( name, description, new String[]{} ); }
     */
    @WebRemote
    public String createSubModule(String name,
                                  String description,
                                  String parentNode) throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();
        return repositoryModuleOperations.createSubModule(name,
                description,
                parentNode);
    }

    @WebRemote
    @LoggedIn
    public Module loadModule(String uuid) {
        ModuleItem moduleItem = rulesRepository.loadModuleByUUID(uuid);
        // the uuid passed in is the uuid of that deployment bundle, not the
        // module uudi.
        // we have to figure out the module name.
        serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName(moduleItem.getName());
        return repositoryModuleOperations.loadModule(moduleItem);
    }

    @WebRemote
    @LoggedIn
    public ValidatedResponse validateModule(Module data) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid(data.getUuid());
        return repositoryModuleOperations.validateModule(data);
    }

    @WebRemote
    @LoggedIn
    public void saveModule(Module data) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid(data.getUuid());
        repositoryModuleOperations.saveModule(data);
    }

    @WebRemote
    @LoggedIn
    public BuilderResult buildPackage(String packageUUID,
                                      boolean force) throws SerializationException {
        return buildPackage(packageUUID,
                force,
                null,
                null,
                null,
                false,
                null,
                null,
                false,
                null);
    }

    @WebRemote
    @LoggedIn
    public BuilderResult buildPackage(String packageUUID,
                                      boolean force,
                                      String buildMode,
                                      String statusOperator,
                                      String statusDescriptionValue,
                                      boolean enableStatusSelector,
                                      String categoryOperator,
                                      String category,
                                      boolean enableCategorySelector,
                                      String customSelectorName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid(packageUUID);
        return repositoryModuleOperations.buildModule(packageUUID,
                force,
                buildMode,
                statusOperator,
                statusDescriptionValue,
                enableStatusSelector,
                categoryOperator,
                category,
                enableCategorySelector,
                customSelectorName);
    }

    @WebRemote
    @LoggedIn
    public void createModuleSnapshot(String moduleName,
                                     String snapshotName,
                                     boolean replaceExisting,
                                     String comment) {
        serviceSecurity.checkSecurityIsPackageAdminWithPackageName(moduleName);
        repositoryModuleOperations.createModuleSnapshot(moduleName,
                snapshotName,
                replaceExisting,
                comment);

    }

    @WebRemote
    @LoggedIn
    public void copyOrRemoveSnapshot(String moduleName,
                                     String snapshotName,
                                     boolean delete,
                                     String newSnapshotName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageAdminWithPackageName(moduleName);
        repositoryModuleOperations.copyOrRemoveSnapshot(moduleName,
                snapshotName,
                delete,
                newSnapshotName);
    }

    @WebRemote
    @LoggedIn
    public String[] listRulesInPackage(String packageName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName(packageName);
        return repositoryModuleOperations.listRulesInPackage(packageName);
    }

    @WebRemote
    @LoggedIn
    public String[] listImagesInModule(String moduleName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName(moduleName);
        return repositoryModuleOperations.listImagesInModule(moduleName);
    }

    @WebRemote
    public void rebuildSnapshots() throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();

        Iterator<ModuleItem> pkit = rulesRepository.listModules();
        while (pkit.hasNext()) {
            ModuleItem pkg = pkit.next();
            String[] snaps = rulesRepository.listModuleSnapshots(pkg.getName());
            for (String snapName : snaps) {
                ModuleItem snap = rulesRepository.loadModuleSnapshot(pkg.getName(),
                        snapName);
                BuilderResult builderResult = this.buildPackage(snap.getUUID(),
                        true);
                if (builderResult.hasLines()) {
                    StringBuilder stringBuilder = createStringBuilderFrom(builderResult);
                    throw new DetailedSerializationException("Unable to rebuild snapshot [" + snapName,
                            stringBuilder.toString() + "]");
                }
            }
        }
    }

    @WebRemote
    @LoggedIn
    public SnapshotInfo[] listSnapshots(String moduleName) {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName(moduleName);

        String[] snaps = rulesRepository.listModuleSnapshots(moduleName);
        SnapshotInfo[] snapshotInfos = new SnapshotInfo[snaps.length];
        for (int i = 0; i < snaps.length; i++) {
            ModuleItem moduleItem = rulesRepository.loadModuleSnapshot(moduleName,
                    snaps[i]);
            snapshotInfos[i] = moduleItemToSnapshotItem(snaps[i],
                    moduleItem);
        }
        return snapshotInfos;
    }

    @LoggedIn
    public SnapshotInfo loadSnapshotInfo(String packageName,
                                         String snapshotName) {
        serviceSecurity.checkSecurityIsPackageAdminWithPackageName(packageName);

        return moduleItemToSnapshotItem(
                snapshotName,
                rulesRepository.loadModuleSnapshot(
                        packageName,
                        snapshotName));
    }

    private SnapshotInfo moduleItemToSnapshotItem(String snapshotName,
                                                  ModuleItem packageItem) {
        SnapshotInfo snapshotInfo = new SnapshotInfo();
        snapshotInfo.setComment(packageItem.getCheckinComment());
        snapshotInfo.setName(snapshotName);
        snapshotInfo.setUuid(packageItem.getUUID());
        return snapshotInfo;
    }

    @WebRemote
    @LoggedIn
    public String[] listTypesInPackage(String packageUUID) throws SerializationException {
        serviceSecurity.checkSecurityPackageReadOnlyWithPackageUuid(packageUUID);

        ModuleItem pkg = this.rulesRepository.loadModuleByUUID(packageUUID);
        List<String> res = new ArrayList<String>();
        AssetItemIterator it = pkg.listAssetsByFormat(AssetFormats.MODEL,
                AssetFormats.DRL_MODEL);

        JarInputStream jis = null;

        try {
            while (it.hasNext()) {
                AssetItem asset = it.next();
                if (!asset.isArchived()) {
                    if (asset.getFormat().equals(AssetFormats.MODEL)) {
                        jis = typesForModel(res,
                                asset);
                    } else {
                        typesForOthers(res,
                                asset);
                    }

                }
            }
            return res.toArray(new String[res.size()]);
        } catch (IOException e) {
            log.error("Unable to read the jar files in the package: " + e.getMessage());
            throw new DetailedSerializationException("Unable to read the jar files in the package.",
                    e.getMessage());
        } finally {
            IOUtils.closeQuietly(jis);
        }

    }

    @WebRemote
    @LoggedIn
    public void updateDependency(String uuid,
                                 String dependencyPath) {
        ModuleItem item = rulesRepository.loadModuleByUUID(uuid);
        item.updateDependency(dependencyPath);
        item.checkin("Update dependency");
    }

    public String[] getDependencies(String uuid) {
        ModuleItem item = rulesRepository.loadModuleByUUID(uuid);
        return item.getDependencies();
    }

    private JarInputStream typesForModel(List<String> res,
                                         AssetItem asset) throws IOException {
        if (!asset.isBinary()) {
            return null;
        }
        if (asset.getBinaryContentAttachment() == null) {
            return null;
        }

        JarInputStream jis;
        jis = new JarInputStream(asset.getBinaryContentAttachment());
        JarEntry entry = null;
        while ((entry = jis.getNextJarEntry()) != null) {
            if (!entry.isDirectory()) {
                if (entry.getName().endsWith(".class")) {
                    res.add(ModelContentHandler.convertPathToName(entry.getName()));
                }
            }
        }
        return jis;
    }

    private void typesForOthers(List<String> res,
                                AssetItem asset) {
        // its delcared model
        DrlParser parser = new DrlParser();
        try {
            PackageDescr desc = parser.parse(asset.getContent());
            List<TypeDeclarationDescr> types = desc.getTypeDeclarations();
            for (TypeDeclarationDescr typeDeclarationDescr : types) {
                res.add(typeDeclarationDescr.getTypeName());
            }
        } catch (DroolsParserException e) {
            log.error("An error occurred parsing rule: " + e.getMessage());

        }
    }

    @LoggedIn
    public void installSampleRepository() throws SerializationException {
        rulesRepository.importRepository(this.getClass().getResourceAsStream("/mortgage-sample-repository.xml"));
        this.rebuildPackages();
        this.rebuildSnapshots();
    }

    /**
     * @deprecated in favour of
     *             {@link #compareSnapshots(SnapshotComparisonPageRequest)}
     */
    public SnapshotDiffs compareSnapshots(String moduleName,
                                          String firstSnapshotName,
                                          String secondSnapshotName) {
        return repositoryModuleOperations.compareSnapshots(moduleName,
                firstSnapshotName,
                secondSnapshotName);
    }

    public SnapshotComparisonPageResponse compareSnapshots(SnapshotComparisonPageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        if (request.getPageSize() != null && request.getPageSize() < 0) {
            throw new IllegalArgumentException("pageSize cannot be less than zero.");
        }

        return repositoryModuleOperations.compareSnapshots(request);
    }

    @WebRemote
    @LoggedIn
    public SingleScenarioResult runScenario(String packageName,
                                            Scenario scenario) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName(packageName);

        return runScenario(packageName,
                scenario,
                null);
    }

    private SingleScenarioResult runScenario(String packageName,
                                             Scenario scenario,
                                             RuleCoverageListener coverage) throws SerializationException {
        try {
            return runScenario( scenario,
                                this.rulesRepository.loadModule( packageName ),
                                coverage );
        } catch ( Exception e ) {
            if ( e instanceof DetailedSerializationException ) {
                DetailedSerializationException exception = (DetailedSerializationException) e;
                if ( exception.getErrs() != null ) {
                    //err.getErrs() returns a Collections$UnmodifiablerRandomAccessList that cannot be serialised 
                    //by GWT. Hence we need to convert the errors into a List that can be serialised by GWT
                    List<BuilderResultLine> errors = new ArrayList<BuilderResultLine>();
                    for ( BuilderResultLine line : exception.getErrs() ) {
                        errors.add( line );
                    }
                    return new SingleScenarioResult( new ScenarioRunResult( errors ) );
                } else {
                    throw exception;
                }
            } else {
                throw new DetailedSerializationException( "Unable to run the scenario.",
                                                          e.getMessage() );
            }
        }
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
                BuilderResult result = repositoryModuleOperations.buildModule(packageItem,
                        false);
                if (result == null || result.getLines().size() == 0) {
                    RuleBase ruleBase = loadRuleBase(packageItem);
                    RuleBaseCache.getInstance().put(packageItem.getUUID(),
                            ruleBase);
                    return ruleBase;
                } else {
                    throw new DetailedSerializationException("Build error",
                            result.getLines());
                }
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

    private SingleScenarioResult runScenario(Scenario scenario,
                                             ModuleItem item,
                                             RuleCoverageListener coverage) throws DetailedSerializationException {

        RuleBase ruleBase = loadCacheRuleBase(item);
        Package aPackage = ruleBase.getPackages()[0];

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
        for (PortableWorkDefinition portableWorkDefinition : serviceImplementation.loadWorkItemDefinitions(item.getUUID())) {
            workingMemory.getWorkItemManager().registerWorkItemHandler(portableWorkDefinition.getName(),
                    workItemHandlerStub);
        }

        //Run Test Scenario
        try {
            AuditLogReporter logger = new AuditLogReporter(workingMemory);
            ClassLoader classLoader = ((InternalRuleBase) ruleBase).getRootClassLoader();
            new ScenarioRunner(
                    new ClassTypeResolver(getAllImports(aPackage),
                                          classLoader ),
                                          classLoader, 
                                          workingMemory).run(scenario);

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

    @WebRemote
    @LoggedIn
    public BulkTestRunResult runScenariosInPackage(String packageUUID) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid(packageUUID);
        ModuleItem item = rulesRepository.loadModuleByUUID(packageUUID);
        return runScenariosInPackage(item);
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

}
