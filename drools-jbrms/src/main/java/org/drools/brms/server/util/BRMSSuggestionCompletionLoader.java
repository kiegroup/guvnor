package org.drools.brms.server.util;

import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.server.builder.BRMSPackageBuilder;
import org.drools.brms.server.rules.SuggestionCompletionLoader;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.repository.PackageItem;

/**
 * This decorates the suggestion completion loader with BRMS specific stuff.
 * 
 * @author Michael Neale
 */
public class BRMSSuggestionCompletionLoader extends SuggestionCompletionLoader {
    
    public SuggestionCompletionEngine getSuggestionEngine(PackageItem pkg) {
            return super.getSuggestionEngine( pkg.getHeader(), getJars( pkg ), getDSLMappingFiles( pkg ));
    }
    
    
    private List<DSLMappingFile> getDSLMappingFiles(PackageItem pkg) {
        return BRMSPackageBuilder.getDSLMappingFiles( pkg, new BRMSPackageBuilder.ErrorEvent() {
            public void logError(String message) {
                errors.add( message );
            }
        });
    }    
    
    private List<JarInputStream> getJars(PackageItem pkg) {
        return BRMSPackageBuilder.getJars( pkg );
    }    
    
}
