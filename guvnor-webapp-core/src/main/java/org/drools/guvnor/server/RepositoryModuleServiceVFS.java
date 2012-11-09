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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.core.util.BinaryRuleBaseLoader;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleService;
import org.drools.guvnor.client.rpc.Path;
import org.drools.guvnor.client.rpc.PathImpl;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse;
import org.drools.guvnor.client.rpc.SnapshotDiffs;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.server.builder.ClassLoaderBuilder;
import org.drools.guvnor.server.contenthandler.ModelContentHandler;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.ModuleFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.ModuleItem;
import org.drools.repository.ModuleIterator;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.solder.core.Veto;


import com.google.gwt.user.client.rpc.SerializationException;

import org.uberfire.backend.Root;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.VFSTempUtil;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.security.annotations.Roles;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
@Veto
public class RepositoryModuleServiceVFS
        implements
    ModuleService {

    private static final long          serialVersionUID = 901123;

    private static final LoggingHelper log              = LoggingHelper.getLogger( RepositoryAssetService.class );

    @Inject @Preferred
    private RulesRepository            rulesRepository;

    @Inject
    private RepositoryModuleOperations repositoryModuleOperations;

    @Inject
    private RepositoryAssetOperations  repositoryAssetOperations;

    @Inject
    private ServiceImplementation      serviceImplementation;

    @Inject
    private RulesRepositoryVFS rulesRepositoryVFS;
    
    
    /**
     * Role-based Authorization check: This method only returns modules that the
     * user has permission to access. User has permission to access the
     * particular module when: The user has a package.readonly role or higher
     * (i.e., package.admin, package.developer) to this module.
     */
    public Module[] listModules() {    
    	List<Module> result =  rulesRepositoryVFS.listModules();
    	for(Module m : result) {
    		//Exclude the "globalArea" 
    		if("globalArea".equals(m.getName())) {
    			result.remove(m);
    		}
    	}
    	
        sortModules( result );
        
        return result.toArray( new Module[result.size()] );
    }
    
    void sortModules(List<Module> result) {
        Collections.sort( result,
                new Comparator<Module>() {

                    public int compare(final Module d1,
                                       final Module d2) {
                        return d1.getName().compareTo( d2.getName() );
                    }

                } );
    }
    
    //TODO: no more workspace
    public Module[] listModules(String workspace) {
        return listModules();
    }
    
    
    public Module[] listArchivedModules() {
        return listArchivedModules( null );
    }

    public Module[] listArchivedModules(String workspace) {
        return repositoryModuleOperations.listModules(
                                                       true,
                                                       workspace );
    }
    
    //TODO: In jcr repository, the global area is created during jcr repo initialization phase. shall we create global area folder when 
    //we clone git repo (if "globalAra" does not exist in the cloned git repo)?
    public Module loadGlobalModule() {
    	List<Module> result = rulesRepositoryVFS.listModules();
    	for(Module m : result) {
    		if("globalArea".equals(m.getName())) {
    			return rulesRepositoryVFS.loadModule(m.getPath());
    		}    	
    	}
    	
    	return null;
    }

    public void rebuildPackages() throws SerializationException {
        Iterator<ModuleItem> pkit = rulesRepository.listModules();
        StringBuilder errs = new StringBuilder();
        while ( pkit.hasNext() ) {
            ModuleItem pkg = pkit.next();
            try {
                Path path = new PathImpl();
                path.setUUID(pkg.getUUID());
                BuilderResult builderResult = this.buildPackage( path,
                                                                 true );
                if ( builderResult != null ) {
                    errs.append( "Unable to build package name [" ).append( pkg.getName() ).append( "]\n" );
                    StringBuilder buf = createStringBuilderFrom( builderResult );
                    log.warn( buf.toString() );
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                log.error( "An error occurred building package [" + pkg.getName() + "]\n" );
                errs.append( "An error occurred building package [" ).append( pkg.getName() ).append( "]\n" );
            }
        }
    }

    private StringBuilder createStringBuilderFrom(BuilderResult res) {
        StringBuilder buf = new StringBuilder();
        for ( int i = 0; i < res.getLines().size(); i++ ) {
            buf.append( res.getLines().get( i ).toString() );
            buf.append( '\n' );
        }
        return buf;
    }

    public String buildModuleSource(Path modulePath) throws SerializationException {
        return repositoryModuleOperations.buildModuleSource( modulePath );
    }

    @Roles({"ADMIN"})
    public Path copyModule(Path sourceModulePath,
                           String destModuleName) throws SerializationException {
        return rulesRepositoryVFS.copyModule(sourceModulePath, destModuleName);
    }

    public void removeModule(Path modulePath) {
    	rulesRepositoryVFS.removeModule( modulePath );
    }

    public Path renameModule(Path modulePath,
                             String newName) {
        return rulesRepositoryVFS.renameModule( modulePath, newName );
    }

    public byte[] exportModules(String moduleName) {
        return repositoryModuleOperations.exportModules( moduleName );
    }

    public void importPackages(byte[] byteArray,
                               boolean importAsNew) {
        repositoryModuleOperations.importPackages( byteArray,
                                                   importAsNew );
    }

    public Path createModule(String name,
                             String description,
                             String format) throws RulesRepositoryException {
        return rulesRepositoryVFS.createModule( name, description, format );
    }

    //No more workspace
    @Roles({"ADMIN"})
    public Path createModule(String name,
                               String description,
                               String format,
                               String[] workspace) throws RulesRepositoryException {
        return createModule(name, description, format);
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
    @Roles({"ADMIN"})
    public Path createSubModule(String name,
                                  String description,
                                  String parentNode) throws SerializationException {
        return repositoryModuleOperations.createSubModule( name,
                                                           description,
                                                           parentNode );
    }

    public Module loadModule(Path modulePath) {
    	return rulesRepositoryVFS.loadModule(modulePath);
    }

    public void saveModule(Module module) throws SerializationException {
    	rulesRepositoryVFS.saveModule( module );
    }

    public BuilderResult buildPackage(Path modulePath,
                                      boolean force) throws SerializationException {
        return buildPackage( modulePath,
                             force,
                             null,
                             null,
                             null,
                             false,
                             null,
                             null,
                             false,
                             null );
    }

    public BuilderResult buildPackage(Path modulePath,
                                      boolean force,
                                      String buildMode,
                                      String statusOperator,
                                      String statusDescriptionValue,
                                      boolean enableStatusSelector,
                                      String categoryOperator,
                                      String category,
                                      boolean enableCategorySelector,
                                      String customSelectorName) throws SerializationException {
        return repositoryModuleOperations.buildModule( modulePath,
                                                       force,
                                                       buildMode,
                                                       statusOperator,
                                                       statusDescriptionValue,
                                                       enableStatusSelector,
                                                       categoryOperator,
                                                       category,
                                                       enableCategorySelector,
                                                       customSelectorName );
    }

	public void createModuleSnapshot(String moduleName, String snapshotName,
			boolean replaceExisting, String comment)
			throws SerializationException {
		repositoryModuleOperations.createModuleSnapshot(moduleName,
				snapshotName, replaceExisting, comment, false);

	}

	public void createModuleSnapshot(String moduleName, String snapshotName,
			boolean replaceExisting, String comment,
			boolean checkIsBinaryUpToDate) throws SerializationException {
		repositoryModuleOperations.createModuleSnapshot(moduleName,
				snapshotName, replaceExisting, comment, checkIsBinaryUpToDate);
	}

    public void copyOrRemoveSnapshot(String moduleName,
                                     String snapshotName,
                                     boolean delete,
                                     String newSnapshotName) throws SerializationException {
        repositoryModuleOperations.copyOrRemoveSnapshot( moduleName,
                                                         snapshotName,
                                                         delete,
                                                         newSnapshotName );
    }

    public String[] listRulesInPackage(String packageName) throws SerializationException {
        return repositoryModuleOperations.listRulesInPackage( packageName );
    }

    public String[] listImagesInModule(String moduleName) throws SerializationException {
        return repositoryModuleOperations.listImagesInModule( moduleName );
    }

    @Roles({"ADMIN"})
    public void rebuildSnapshots() throws SerializationException {

        Iterator<ModuleItem> pkit = rulesRepository.listModules();
        while ( pkit.hasNext() ) {
            ModuleItem pkg = pkit.next();
            String[] snaps = rulesRepository.listModuleSnapshots( pkg.getName() );
            for ( String snapName : snaps ) {
                ModuleItem snap = rulesRepository.loadModuleSnapshot( pkg.getName(),
                                                                      snapName );
                Path path = new PathImpl();
                path.setUUID(snap.getUUID());                
                BuilderResult builderResult = this.buildPackage( path,
                                                                 true );
                if ( builderResult.hasLines() ) {
                    StringBuilder stringBuilder = createStringBuilderFrom( builderResult );
                    throw new DetailedSerializationException( "Unable to rebuild snapshot [" + snapName,
                                                              stringBuilder.toString() + "]" );
                }
            }
        }
    }

    public SnapshotInfo[] listSnapshots(String moduleName) {
        String[] snaps = rulesRepository.listModuleSnapshots( moduleName );
        SnapshotInfo[] snapshotInfos = new SnapshotInfo[snaps.length];
        for ( int i = 0; i < snaps.length; i++ ) {
            ModuleItem moduleItem = rulesRepository.loadModuleSnapshot( moduleName,
                                                                        snaps[i] );
            snapshotInfos[i] = moduleItemToSnapshotItem( snaps[i],
                                                         moduleItem );
        }
        return snapshotInfos;
    }

    public SnapshotInfo loadSnapshotInfo(String packageName,
                                         String snapshotName) {
        return moduleItemToSnapshotItem( snapshotName,
                                         rulesRepository.loadModuleSnapshot( packageName,
                                                                             snapshotName ) );
    }

    private SnapshotInfo moduleItemToSnapshotItem(String snapshotName,
                                                  ModuleItem packageItem) {
        SnapshotInfo snapshotInfo = new SnapshotInfo();
        snapshotInfo.setComment( packageItem.getCheckinComment() );
        snapshotInfo.setName( snapshotName );
        Path path = new PathImpl();
        path.setUUID(packageItem.getUUID());
        snapshotInfo.setPath(path);
        return snapshotInfo;
    }

    public String[] listTypesInPackage(Path modulePath) throws SerializationException {

        ModuleItem pkg = this.rulesRepository.loadModuleByUUID( modulePath.getUUID() );
        List<String> res = new ArrayList<String>();
        AssetItemIterator it = pkg.listAssetsByFormat( AssetFormats.MODEL,
                                                       AssetFormats.DRL_MODEL );

        JarInputStream jis = null;

        try {
            while ( it.hasNext() ) {
                AssetItem asset = it.next();
                if ( !asset.isArchived() ) {
                    if ( asset.getFormat().equals( AssetFormats.MODEL ) ) {
                        jis = typesForModel( res,
                                             asset );
                    } else {
                        typesForOthers( res,
                                        asset );
                    }

                }
            }
            return res.toArray( new String[res.size()] );
        } catch ( IOException e ) {
            log.error( "Unable to read the jar files in the package: " + e.getMessage() );
            throw new DetailedSerializationException( "Unable to read the jar files in the package.",
                                                      e.getMessage() );
        } finally {
            IOUtils.closeQuietly( jis );
        }

    }

    public void updateDependency(Path modulePath,
                                 String dependencyPath) {
        ModuleItem item = rulesRepository.loadModuleByUUID( modulePath.getUUID() );
        item.updateDependency( dependencyPath );
        item.checkin( "Update dependency" );
    }

    public String[] getDependencies(Path modulePath) {
        ModuleItem item = rulesRepository.loadModuleByUUID( modulePath.getUUID());
        return item.getDependencies();
    }

    private JarInputStream typesForModel(List<String> res,
                                         AssetItem asset) throws IOException {
        if ( !asset.isBinary() ) {
            return null;
        }
        if ( asset.getBinaryContentAttachment() == null ) {
            return null;
        }

        JarInputStream jis;
        jis = new JarInputStream( asset.getBinaryContentAttachment() );
        JarEntry entry = null;
        while ( (entry = jis.getNextJarEntry()) != null ) {
            if ( !entry.isDirectory() ) {
                if ( entry.getName().endsWith( ".class" ) && !entry.getName().endsWith( "package-info.class" ) ) {
                    res.add( ModelContentHandler.convertPathToName( entry.getName() ) );
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
            PackageDescr desc = parser.parse( asset.getContent() );
            List<TypeDeclarationDescr> types = desc.getTypeDeclarations();
            for ( TypeDeclarationDescr typeDeclarationDescr : types ) {
                res.add( typeDeclarationDescr.getTypeName() );
            }
        } catch ( DroolsParserException e ) {
            log.error( "An error occurred parsing rule: " + e.getMessage() );

        }
    }

    public void installSampleRepository() throws SerializationException {
        rulesRepository.importRepository( this.getClass().getResourceAsStream( "/mortgage-sample-repository.xml" ) );
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
        return repositoryModuleOperations.compareSnapshots( moduleName,
                                                            firstSnapshotName,
                                                            secondSnapshotName );
    }

    public SnapshotComparisonPageResponse compareSnapshots(SnapshotComparisonPageRequest request) {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        return repositoryModuleOperations.compareSnapshots( request );
    }

    private ClassLoaderBuilder createClassLoaderBuilder(ModuleItem packageItem) {
        return new ClassLoaderBuilder( packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat( AssetFormats.MODEL ) );
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

    private RuleBase loadRuleBase(ModuleItem item) throws DetailedSerializationException {
        try {
            return deserKnowledgebase( item,
                                       createClassLoaderBuilder( item ).buildClassLoader() );
        } catch ( ClassNotFoundException e ) {
            log.error( "Unable to load rule base.",
                       e );
            throw new DetailedSerializationException( "A required class was not found.",
                                                      e.getMessage() );
        } catch ( Exception e ) {
            log.error( "Unable to load rule base.",
                       e );
            log.info( "...but trying to rebuild binaries..." );
            try {
                BuilderResult builderResult = repositoryModuleOperations.buildModule(
                                                                                      item,
                                                                                      true );
                if ( builderResult != null && builderResult.getLines().size() > 0 ) {
                    log.error( "There were errors when rebuilding the knowledgebase." );
                    throw new DetailedSerializationException( "There were errors when rebuilding the knowledgebase.",
                                                              "" );
                }
            } catch ( Exception e1 ) {
                log.error( "Unable to rebuild the rulebase: " + e.getMessage() );
                throw new DetailedSerializationException( "Unable to rebuild the rulebase.",
                                                          e.getMessage() );
            }
            try {
                return deserKnowledgebase( item,
                                           createClassLoaderBuilder( item ).buildClassLoader() );
            } catch ( Exception e2 ) {
                log.error( "Unable to reload knowledgebase: " + e.getMessage() );
                throw new DetailedSerializationException( "Unable to reload knowledgebase.",
                                                          e.getMessage() );
            }

        }
    }

}
