package org.drools.guvnor.server.files;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.common.HTMLFileManagerFields;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.ContentPackageAssembler;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.guvnor.server.contenthandler.ModelContentHandler;
import org.drools.guvnor.server.contenthandler.RuleFlowHandler;
import org.drools.guvnor.server.repository.MigrateRepository;
import org.drools.guvnor.server.security.AdminType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.ClassicDRLImporter;
import org.drools.guvnor.server.util.FormData;
import org.drools.guvnor.server.util.ClassicDRLImporter.Asset;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

/**
 * This assists the file manager servlets.
 * @author Fernando Meyer
 */
@Name("fileManager")
@Scope(ScopeType.EVENT)
@AutoCreate
public class FileManagerUtils {

    @In
    public RulesRepository repository;

    /**
     * This attach a file to an asset.
     */
    @Restrict("#{identity.loggedIn}")
    public void attachFile(FormData uploadItem) throws IOException {

        String uuid = uploadItem.getUuid();
        InputStream fileData = uploadItem.getFile().getInputStream();
        String fileName = uploadItem.getFile().getName();

        attachFileToAsset( uuid,
                           fileData,
                           fileName );
        uploadItem.getFile().getInputStream().close();

    }

    /**
     * This utility method attaches a file to an asset.
     * @throws IOException
     */
    @Restrict("#{identity.loggedIn}")
    public void attachFileToAsset(String uuid,
                                  InputStream fileData,
                                  String fileName) throws IOException {

        //here we should mark the binary data as invalid on the package (which means moving something into repo modle)

        AssetItem item = repository.loadAssetByUUID( uuid );

        item.updateBinaryContentAttachment( fileData );
        item.updateBinaryContentAttachmentFileName( fileName );
        item.getPackage().updateBinaryUpToDate( false );
        item.checkin( "Attached file: " + fileName );

        // Special treatment for model and ruleflow attachments.
        ContentHandler handler = ContentManager.getHandler( item.getFormat() );
        if ( handler instanceof ModelContentHandler ) {
            ((ModelContentHandler) handler).modelAttached( item );
        } else if ( handler instanceof RuleFlowHandler ) {
            ((RuleFlowHandler) handler).ruleFlowAttached( item );
        }

    }

    public RulesRepository getRepository() {
        return this.repository;
    }

    /**
     * The get returns files based on UUID of an asset.
     */
    @Restrict("#{identity.loggedIn}")
    public String loadFileAttachmentByUUID(String uuid,
                                           OutputStream out) throws IOException {

        AssetItem item = repository.loadAssetByUUID( uuid );

        byte[] data = item.getBinaryContentAsBytes();
        if ( data == null ) {
            data = new byte[0];
        }
        out.write( data );
        out.flush();

        return item.getName() + "." + item.getFormat();//item.getBinaryContentAttachmentFileName();
    }

    /**
     * Get the form data from the inbound request.
     */
    public static FormData getFormData(HttpServletRequest request) {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload( factory );

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
     * Load up the approproate package version.
     * @param packageName The name of the package.
     * @param packageVersion The version (if it is a snapshot).
     * @param isLatest true if the latest package binary will be used (ie NOT a snapshot).
     * @return The filename if its all good.
     */
    public String loadBinaryPackage(String packageName,
                                    String packageVersion,
                                    boolean isLatest,
                                    OutputStream out) throws IOException {
        PackageItem item = null;
        if ( isLatest ) {
            item = repository.loadPackage( packageName );
            byte[] data = item.getCompiledPackageBytes();
            out.write( data );
            out.flush();
            return packageName + ".pkg";
        } else {
            item = repository.loadPackageSnapshot( packageName,
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
        PackageItem item = null;
        if ( isLatest ) {
            item = repository.loadPackage( packageName );
            ContentPackageAssembler asm = new ContentPackageAssembler( item,
                                                                       false );
            String drl = asm.getDRL();
            out.write( drl.getBytes() );
            out.flush();
            return packageName + ".drl";
        } else {
            item = repository.loadPackageSnapshot( packageName,
                                                   packageVersion );
            ContentPackageAssembler asm = new ContentPackageAssembler( item,
                                                                       false );
            String drl = asm.getDRL();
            out.write( drl.getBytes() );
            out.flush();
            return packageName + "_" + URLEncoder.encode( packageVersion,
                                                          "UTF-8" ) + ".drl";
        }

    }

    public byte[] exportPackageFromRepository(String packageName) {
        try {
            return this.repository.exportPackageFromRepository( packageName );
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        } catch ( IOException e ) {
            throw new RulesRepositoryException( e );
        }
    }

    public void exportRulesRepository(OutputStream out) {
        this.repository.exportRulesRepositoryToStream( out );
    }

    @Restrict("#{identity.loggedIn}")
    public void importRulesRepository(InputStream in) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }
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
            ServiceImplementation.ruleBaseCache.clear();    
        } catch ( RepositoryException e ) {
            e.printStackTrace();
            throw new RulesRepositoryException( e );
        }
    }

    @Restrict("#{identity.loggedIn}")
    public void importPackageToRepository(byte[] data,
                                          boolean importAsNew) {

        repository.importPackageToRepository( data,
                                              importAsNew );

        //
        //Migrate v4 ruleflows to v5
        //This section checks if the repository contains drools v4
        //ruleflows that need to be migrated to drools v5
        //
        try {
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
     */
    @Restrict("#{identity.loggedIn}")
    public void importClassicDRL(InputStream drlStream) throws IOException,
                                                       DroolsParserException {

        ClassicDRLImporter imp = new ClassicDRLImporter( drlStream );
        PackageItem pkg = null;

        String packageName = imp.getPackageName();
        boolean existing = repository.containsPackage( packageName );

        // Check if the package is archived
        if ( existing && repository.isPackageArchived( packageName ) ) {
            // Remove the package so it can be created again.
            PackageItem item = repository.loadPackage( packageName );
            item.remove();
            existing = false;
        }

        if ( existing ) {
            pkg = repository.loadPackage( packageName );
            ServiceImplementation.updateDroolsHeader( ClassicDRLImporter.mergeLines( ServiceImplementation.getDroolsHeader( pkg ),
                                                                                     imp.getPackageHeader() ),
                                                      pkg );
            existing = true;
        } else {
            pkg = repository.createPackage( packageName,
                                            "<imported>" );
            ServiceImplementation.updateDroolsHeader( imp.getPackageHeader(),
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

    }

    /**
     * This will return the last time the package was built.
     */
    public long getLastModified(String name,
                                String version) {
        PackageItem item = null;
        if ( version.equals( "LATEST" ) ) {
            item = repository.loadPackage( name );
        } else {
            item = repository.loadPackageSnapshot( name,
                                                   version );
        }
        return item.getLastModified().getTimeInMillis();
    }

    public String loadSourceAsset(String packageName,
                                  String packageVersion,
                                  boolean isLatest,
                                  String assetName,
                                  ByteArrayOutputStream out) throws IOException {
        PackageItem pkg = null;
        if ( isLatest ) {
            pkg = repository.loadPackage( packageName );
        } else {
            pkg = repository.loadPackageSnapshot( packageName,
                                                  packageVersion );
        }

        AssetItem item = pkg.loadAsset( assetName );
        ContentHandler handler = ContentManager.getHandler( item.getFormat() );//new AssetContentFormatHandler();
        StringBuffer buf = new StringBuffer();
        if ( handler.isRuleAsset() ) {

            BRMSPackageBuilder builder = new BRMSPackageBuilder();
            //now we load up the DSL files
            builder.setDSLFiles( BRMSPackageBuilder.getDSLMappingFiles( item.getPackage(),
                                                                        new BRMSPackageBuilder.DSLErrorEvent() {
                                                                            public void recordError(AssetItem asset,
                                                                                                    String message) {
                                                                                //ignore at this point...
                                                                            }
                                                                        } ) );
            ((IRuleAsset) handler).assembleDRL( builder,
                                                item,
                                                buf );
            out.write( buf.toString().getBytes() );
            return item.getName() + ".drl";
        } else {
            out.write( item.getContent().getBytes() );
            return item.getName() + ".drl";
        }

    }

    @Destroy
    public void close() {
        repository.logout();
    }
}