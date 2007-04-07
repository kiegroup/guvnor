package org.drools.brms.server.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.server.rules.SuggestionCompletionLoader;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
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
        List<DSLMappingFile> result = new ArrayList<DSLMappingFile>();
        AssetItemIterator it = pkg.listAssetsByFormat( new String[] {AssetFormats.DSL} );
        while(it.hasNext()) {
          AssetItem item = (AssetItem) it.next();
          String dslData = item.getContent();
          DSLMappingFile file = new DSLMappingFile();
          try {
            file.parseAndLoad( new StringReader( dslData ) );
            result.add( file );
          } catch ( IOException e ) {
            errors.add( e.getMessage() );
          }  
          
        }
        
        return result;
    }    
    
    private List<JarInputStream> getJars(PackageItem pkg) {
        List<JarInputStream> result = new ArrayList<JarInputStream>();
        AssetItemIterator ait = pkg.listAssetsByFormat( new String[]{AssetFormats.MODEL} );
        while ( ait.hasNext() ) {
            AssetItem item = (AssetItem) ait.next();
            if (item.getBinaryContentAttachment() != null) {
                try {
                    result.add( new JarInputStream( item.getBinaryContentAttachment(), false ) );
                } catch ( IOException e ) {
                    this.errors.add( e.getMessage() );
                }
            }
        }
        return result;
    }    
    
}
