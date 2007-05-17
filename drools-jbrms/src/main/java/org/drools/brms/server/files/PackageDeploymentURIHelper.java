package org.drools.brms.server.files;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.brms.client.packages.PackageSnapshotView;

/**
 * Works out from the path URI what package is being requested.
 * Uses Regular expression Pattern matching to recover packagename and version
 * it works both with gwt hosted mode and application server standalone. 
 * 
 * @author Michael Neale
 * @author Fernando Meyer
 */
public class PackageDeploymentURIHelper {

    private String version;
    private String packageName;

    public PackageDeploymentURIHelper(String uri) throws UnsupportedEncodingException {
        
        String url = URLDecoder.decode( uri, "UTF-8" );
        
        Pattern pattern = Pattern.compile( ".*/(package|asset)/(.*)" );
        Matcher m = pattern.matcher( url );
        if ( m.matches() ) {
            String result = m.group(2);
            String []mtoks = result.split( "/" );
            this.version = mtoks[1];
            this.packageName = mtoks[0];
        }
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
