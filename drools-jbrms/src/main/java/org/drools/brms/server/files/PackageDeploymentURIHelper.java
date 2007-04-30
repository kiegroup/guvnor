package org.drools.brms.server.files;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import org.drools.brms.client.packages.PackageSnapshotView;

/**
 * Works out from the path URI what package is being requested.
 * 
 * @author Michael Neale
 */
public class PackageDeploymentURIHelper {
    

    
    private String version;
    private String packageName;

    public PackageDeploymentURIHelper(String uri) throws UnsupportedEncodingException {
        StringTokenizer tok = new StringTokenizer( URLDecoder.decode( uri, "UTF-8" ), "/" );
        String[] toks = new String[tok.countTokens()];
        int i = 0;
        while ( tok.hasMoreTokens() ) {
            toks[i] = tok.nextToken();
            i++;
        }
        
        if ( i < 3 ) {
            throw new IllegalArgumentException( "Bad URI - can't get a package binary for " + uri );
        }
        
        this.version = toks[i - 1];
        this.packageName = toks[i -2];
    }

    public String getPackageName() {
        return packageName;
    }


    public String getVersion() {
        return version;
    }

    public boolean isLatest() {
        return PackageSnapshotView.LATEST_SNAPSHOT.equals( version );
    }


    
    
    
    
}
