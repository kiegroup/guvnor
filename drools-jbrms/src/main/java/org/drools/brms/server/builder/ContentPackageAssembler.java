package org.drools.brms.server.builder;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepositoryException;
import org.drools.rule.Package;

/**
 * This assembles packages in the BRMS into binary package objects, and deals with errors etc.
 * Each content type is responsible for contributing to the package. 
 * 
 * @author Michael Neale
 */
public class ContentPackageAssembler {

    private PackageItem pkg;
    private Package binaryPackage;
    private List errors = new ArrayList();

    private BRMSPackageBuilder builder;
    private List<DSLMappingFile> dslFiles;
    
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
        try {
            builder.addPackageFromDrl( new StringReader(pkg.getHeader()) );
            if (builder.hasErrors()) {
                recordBuilderErrors();
                return false;
            }
        } catch ( DroolsParserException e ) {
            throw new RulesRepositoryException("A serious error occurred trying to parser the package header.", e);
        } catch ( IOException e ) {
            throw new RulesRepositoryException(e);
        }
        
        this.dslFiles = BRMSPackageBuilder.getDSLMappingFiles( pkg, new BRMSPackageBuilder.ErrorEvent() {
            public void logError(String message) {
                errors.add( new ContentAssemblyError(pkg, message) );
            }
        });
        return errors.size() == 0;
    }



    /**
     * This will accumulate the errors.
     */
    private void recordBuilderErrors() {
        DroolsError[] errs = builder.getErrors();
        for ( int i = 0; i < errs.length; i++ ) {
            this.errors.add( new ContentAssemblyError(pkg, errs[i].getMessage()) );
        }
        
    }

    /**
     * I've got a package people !
     */
    public Package getBinaryPackage() {
        if (this.hasErrors()) {
            throw new IllegalStateException("There is no package available, as there were errors.");
        }
        return binaryPackage;
    }

    
    public boolean hasErrors() {
        return errors.size() > 0;
    }
    
    
}
