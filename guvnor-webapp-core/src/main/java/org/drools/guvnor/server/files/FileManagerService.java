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

package org.drools.guvnor.server.files;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.common.HTMLFileManagerFields;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.DSLLoader;
import org.drools.guvnor.server.builder.PackageDRLAssembler;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.ICanHasAttachment;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.guvnor.server.repository.MigrateRepository;
import org.drools.guvnor.server.security.AdminType;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.util.ClassicDRLImporter;
import org.drools.guvnor.server.util.ClassicDRLImporter.Asset;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.guvnor.server.util.FormData;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import org.jboss.seam.security.annotations.LoggedIn;
import org.jboss.seam.security.Identity;

/**
 * This assists the file manager servlets.
 */
@Named("fileManager")
@ApplicationScoped
public class FileManagerService {

    @Inject
    private RulesRepository repository;

    @Inject
    private Identity identity;

    /**
     * This attach a file to an asset.
     */
    @LoggedIn
    public void attachFile(FormData uploadItem) throws IOException {

        String uuid = uploadItem.getUuid();
        InputStream fileData = uploadItem.getFile().getInputStream();
        String fileName = uploadItem.getFile().getName();

        attachFileToAsset(uuid,
                fileData,
                fileName);
        uploadItem.getFile().getInputStream().close();

    }

    /**
     * This utility method attaches a file to an asset.
     * @throws IOException
     */
    @LoggedIn
    public void attachFileToAsset(String uuid,
                                  InputStream fileData,
                                  String fileName) throws IOException {

        //here we should mark the binary data as invalid on the package (which means moving something into repo modle)

        AssetItem item = repository.loadAssetByUUID( uuid );

        item.updateBinaryContentAttachment( fileData );
        item.updateBinaryContentAttachmentFileName( fileName );
        item.getModule().updateBinaryUpToDate( false );
        item.checkin( "Attached file: " + fileName );

        // Special treatment for model and ruleflow attachments.
        ContentHandler handler = ContentManager.getHandler( item.getFormat() );
        if ( handler instanceof ICanHasAttachment ) {
            ((ICanHasAttachment) handler).onAttachmentAdded( item );
        }

    }

    public void setRepository(RulesRepository repository) {
        this.repository = repository;
    }

    /**
     * The get returns files based on UUID of an asset.
     */
    @LoggedIn
    public String loadFileAttachmentByUUID(String uuid,
                                           OutputStream out) throws IOException {

        AssetItem item = repository.loadAssetByUUID( uuid );

        byte[] data = item.getBinaryContentAsBytes();
        if ( data == null ) {
            data = new byte[0];
        }
        out.write( data );
        out.flush();

        return item.getName() + "." + item.getFormat();
    }

    /**
     * Get the form data from the inbound request.
     */
    @SuppressWarnings("rawtypes")
    public static FormData getFormData(HttpServletRequest request) {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload( factory );
        upload.setHeaderEncoding( "UTF-8" );

        FormData data = new FormData();
        try {
            List items = upload.parseRequest( request );
            Iterator it = items.iterator();
            while ( it.hasNext() ) {
                FileItem item = (FileItem) it.next();
                if ( item.isFormField() && item.getFieldName().equals( HTMLFileManagerFields.FORM_FIELD_UUID ) ) {
                    data.setUuid( item.getString() );
                } else if ( !item.isFormField() ) {
                    data.setFile( item );
                }
            }
            return data;
        } catch ( FileUploadException e ) {
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * Load up the appropriate package version.
     * @param packageName The name of the package.
     * @param packageVersion The version (if it is a snapshot).
     * @param isLatest true if the latest package binary will be used (ie NOT a snapshot).
     * @return The filename if its all good.
     */
    public String loadBinaryPackage(String packageName,
                                    String packageVersion,
                                    boolean isLatest,
                                    OutputStream out) throws IOException {
        ModuleItem item = null;
        if ( isLatest ) {
            item = repository.loadModule( packageName );
            byte[] data = item.getCompiledPackageBytes();
            out.write( data );
            out.flush();
            return packageName + ".pkg";
        } else {
            item = repository.loadModuleSnapshot( packageName,
                                                   packageVersion );
            byte[] data = item.getCompiledPackageBytes();
            out.write( data );
            out.flush();
            return packageName + "_" + URLEncoder.encode( packageVersion,
                                                          "UTF-8" ) + ".pkg";
        }

    }

    /**
     * Load up the approproate package version.
     * @param packageName The name of the package.
     * @param packageVersion The version (if it is a snapshot).
     * @param isLatest true if the latest package binary will be used (ie NOT a snapshot).
     * @return The filename if its all good.
     */
    public String loadSourcePackage(String packageName,
                                    String packageVersion,
                                    boolean isLatest,
                                    OutputStream out) throws IOException {
        ModuleItem item = null;
        if ( isLatest ) {
            item = repository.loadModule( packageName );
            PackageDRLAssembler asm = new PackageDRLAssembler( item );
            String drl = asm.getDRL();
            out.write( drl.getBytes() );
            out.flush();
            return packageName + ".drl";
        } else {
            item = repository.loadModuleSnapshot( packageName,
                                                   packageVersion );
            PackageDRLAssembler asm = new PackageDRLAssembler( item );
            String drl = asm.getDRL();
            out.write( drl.getBytes() );
            out.flush();
            return packageName + "_" + URLEncoder.encode( packageVersion,
                                                          "UTF-8" ) + ".drl";
        }

    }

    public byte[] exportPackageFromRepository(String packageName) {
        try {
            return this.repository.exportModuleFromRepository( packageName );
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        } catch ( IOException e ) {
            throw new RulesRepositoryException( e );
        }
    }

    public boolean isPackageExist(String packageName) {
        return this.repository.containsModule(packageName);
    }
    
    public void exportRulesRepository(OutputStream out) {
        this.repository.exportRepositoryToStream( out );
    }

    @LoggedIn
    public void importRulesRepository(InputStream in) {
        identity.checkPermission( new AdminType(),
                                                 RoleType.ADMIN.getName() );
        repository.importRulesRepositoryFromStream( in );

        //
        //Migrate v4 ruleflows to v5
        //This section checks if the repository contains drools v4
        //ruleflows that need to be migrated to drools v5
        //
        try {
            if ( MigrateRepository.needsRuleflowMigration( repository ) ) {
                MigrateRepository.migrateRuleflows( repository );
            }
            RuleBaseCache.getInstance().clearCache();
        } catch ( RepositoryException e ) {
            e.printStackTrace();
            throw new RulesRepositoryException( e );
        }
    }

    @LoggedIn
    public void importPackageToRepository(byte[] data,
                                          boolean importAsNew) {
        try {
            repository.importPackageToRepository( data,
                                                  importAsNew );

            //
            //Migrate v4 ruleflows to v5
            //This section checks if the repository contains drools v4
            //ruleflows that need to be migrated to drools v5
            //

            if ( MigrateRepository.needsRuleflowMigration( repository ) ) {
                MigrateRepository.migrateRuleflows( repository );
            }
        } catch ( RepositoryException e ) {
            e.printStackTrace();
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * This will import DRL from a drl file into a more normalised structure.
     * If the package does not exist, it will be created.
     * If it does, it will be "merged" in the sense that any new rules in the drl
     * will be created as new assets in the repo, everything else will stay as it was
     * in the repo.
     * 
     * @param packageName Name for this package. Overrides the one in the DRL.
     */
    @LoggedIn
    public String importClassicDRL(InputStream drlStream,
                                   String packageName) throws IOException,
                                                      DroolsParserException {
        ClassicDRLImporter imp = new ClassicDRLImporter( drlStream );
        ModuleItem pkg = null;

        if ( packageName == null ) {
            packageName = imp.getPackageName();
        }

        if ( packageName == null || "".equals( packageName ) ) {
            throw new IllegalArgumentException( "Missing package name." );
        }

        boolean existing = repository.containsModule( packageName );

        // Check if the package is archived
        if ( existing && repository.isModuleArchived( packageName ) ) {
            // Remove the package so it can be created again.
            ModuleItem item = repository.loadModule( packageName );
            item.remove();
            existing = false;
        }

        if ( existing ) {
            pkg = repository.loadModule( packageName );
            DroolsHeader.updateDroolsHeader( ClassicDRLImporter.mergeLines( DroolsHeader.getDroolsHeader( pkg ),
                                                                                     imp.getPackageHeader() ),
                                                      pkg );
            existing = true;
        } else {
            pkg = repository.createModule( packageName,
                                            "<imported>" );
            DroolsHeader.updateDroolsHeader( imp.getPackageHeader(),
                                                      pkg );
        }

        boolean newVer = Boolean.parseBoolean( System.getProperty( "drools.createNewVersionOnImport",
                                                                   "true" ) );

        for ( Asset as : imp.getAssets() ) {

            if ( existing && pkg.containsAsset( as.name ) ) {
                AssetItem asset = pkg.loadAsset( as.name );
                if ( asset.getFormat().equals( as.format ) ) {
                    asset.updateContent( as.content );
                    if ( newVer ) asset.checkin( "Imported change form external DRL" );
                } //skip it if not the right format

            } else {

                AssetItem asset = pkg.addAsset( as.name,
                                                "<imported>" );
                asset.updateFormat( as.format );

                asset.updateContent( as.content );
                asset.updateExternalSource( "Imported from external DRL" );
                if ( newVer ) asset.checkin( "Imported change form external DRL" );

            }
        }

        repository.save();

        /* Return the name of the new package to the caller */
        return packageName;
    }

    /**
     * This will return the last time the package was built.
     */
    public long getLastModified(String name,
                                String version) {
        ModuleItem item = null;
        if ( version.equals( "LATEST" ) ) {
            item = repository.loadModule( name );
        } else {
            item = repository.loadModuleSnapshot( name,
                                                   version );
        }
        return item.getLastModified().getTimeInMillis();
    }

    public String loadSourceAsset(String packageName,
                                  String packageVersion,
                                  boolean isLatest,
                                  String assetName,
                                  ByteArrayOutputStream out) throws IOException {
        ModuleItem pkg = null;
        if ( isLatest ) {
            pkg = repository.loadModule( packageName );
        } else {
            pkg = repository.loadModuleSnapshot( packageName,
                                                  packageVersion );
        }

        AssetItem item = pkg.loadAsset( assetName );
        ContentHandler handler = ContentManager.getHandler( item.getFormat() );
        StringBuilder stringBuilder = new StringBuilder();
        if ( handler.isRuleAsset() ) {

            BRMSPackageBuilder builder = new BRMSPackageBuilder();
            builder.setDSLFiles( DSLLoader.loadDSLMappingFiles( item.getModule() ) );
            ((IRuleAsset) handler).assembleDRL( builder,
                                                item,
                                                stringBuilder );
            out.write( stringBuilder.toString().getBytes() );
            return item.getName() + ".drl";
        } else {
            out.write( item.getContent().getBytes() );
            return item.getName() + ".drl";
        }

    }

}
