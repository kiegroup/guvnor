package org.drools.brms.server.builder;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.server.contenthandler.ContentHandler;
import org.drools.brms.server.contenthandler.IRuleAsset;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.descr.PackageDescr;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.VersionableItem;
import org.drools.rule.Package;

/**
 * This assembles packages in the BRMS into binary package objects, and deals with errors etc.
 * Each content type is responsible for contributing to the package. 
 * 
 * @author Michael Neale
 */
public class ContentPackageAssembler {

    private PackageItem pkg;
    private List<ContentAssemblyError> errors = new ArrayList<ContentAssemblyError>();

    BRMSPackageBuilder builder;
    
    public ContentPackageAssembler(PackageItem assetPackage) {
        this.pkg = assetPackage;
        
        if (preparePackage()) {
            buildPackage();
        }
        
    }
    
    /**
     * This will build the package. 
     */
    private void buildPackage() {
        Iterator it = pkg.getAssets();
        while (it.hasNext()) {
            AssetItem asset = (AssetItem) it.next();
            ContentHandler h = ContentHandler.getHandler( asset.getFormat() );
            if (h instanceof IRuleAsset) {
                try {
                    ((IRuleAsset) h).compile( builder, asset );
                    if (builder.hasErrors()) {
                        this.recordBuilderErrors( asset );
                        builder.clearErrors();
                    }
                } catch ( DroolsParserException e ) {
                    throw new RulesRepositoryException(e);
                } catch ( IOException e ) {
                    throw new RulesRepositoryException(e);
                }
            }
        }
    }

    /**
     * This prepares the package builder, loads the jars/classpath.
     * @return true if everything is good to go, false if its all gone horribly wrong, 
     * and we can't even get the package header up.
     */
    private boolean preparePackage() {
        List<JarInputStream> jars = BRMSPackageBuilder.getJars( pkg );
        builder = BRMSPackageBuilder.getInstance( jars );
        builder.addPackage( new PackageDescr(pkg.getName()) );
        addDrl(pkg.getHeader());
        if (builder.hasErrors()) {
            recordBuilderErrors(pkg);
            return false;
        }

        
        builder.setDSLFiles( BRMSPackageBuilder.getDSLMappingFiles( pkg, new BRMSPackageBuilder.ErrorEvent() {
            public void logError(String message) {
                errors.add( new ContentAssemblyError(pkg, message) );
            }
        }));
        
        AssetItemIterator it = this.pkg.listAssetsByFormat( new String[] {AssetFormats.FUNCTION} );
        while(it.hasNext()) {
            AssetItem func = (AssetItem) it.next();
            addDrl( func.getContent() );
            if (builder.hasErrors()) {
                recordBuilderErrors(func);
                return false;
            }
        }
        
        return errors.size() == 0;
    }

    /**
     * This will return true if there is an error in the package configuration or functions.
     * @return
     */
    public boolean isPackageConfigurationInError() {
        if (this.errors.size() > 0) {
            return this.errors.get( 0 ).itemInError instanceof PackageItem;
        } else {
            return false;
        }
    }
    
    private void addDrl(String drl) {
        try {
            builder.addPackageFromDrl( new StringReader(drl) );
        } catch ( DroolsParserException e ) {
            throw new RulesRepositoryException("Unexpected error when parsing package.", e);
        } catch ( IOException e ) {
            throw new RulesRepositoryException("IO Exception occurred when parsing package.", e);
        }
    }



    /**
     * This will accumulate the errors.
     */
    private void recordBuilderErrors(VersionableItem asset) {
        DroolsError[] errs = builder.getErrors();
        for ( int i = 0; i < errs.length; i++ ) {
            this.errors.add( new ContentAssemblyError(asset, errs[i].getMessage()) );
        }
        
    }

    /**
     * I've got a package people !
     */
    public Package getBinaryPackage() {
        if (this.hasErrors()) {
            throw new IllegalStateException("There is no package available, as there were errors.");
        }
        return builder.getPackage();
    }

    
    public boolean hasErrors() {
        return errors.size() > 0;
    }
    
    public List<ContentAssemblyError> getErrors() {
        return this.errors;
    }
    
    
}
