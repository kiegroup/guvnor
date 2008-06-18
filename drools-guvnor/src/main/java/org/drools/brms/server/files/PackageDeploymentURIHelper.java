package org.drools.brms.server.files;
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



import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.brms.client.common.Snapshot;

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
    private String assetName = null;
	private boolean source;

    public PackageDeploymentURIHelper(String uri) throws UnsupportedEncodingException {

        String url = URLDecoder.decode( uri, "UTF-8" );

        if (url.endsWith(".drl")) {
        	source = true;
        	url = url.substring(0, url.length() - 4);
        }

        Pattern pattern = Pattern.compile( ".*/(package|asset)/(.*)" );
        Matcher m = pattern.matcher( url );
        if ( m.matches() ) {
            String result = m.group(2);
            String []mtoks = result.split( "/" );
            this.version = mtoks[1];
            this.packageName = mtoks[0];
            if (mtoks.length == 3) {
            	this.assetName = mtoks[2];
            }
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersion() {
        return version;
    }

    public boolean isLatest() {
        return Snapshot.LATEST_SNAPSHOT.equals( version );
    }

	public boolean isSource() {

		return source;
	}

	public String getAssetName() {
		return this.assetName;

	}

	public boolean isAsset() {
		return assetName != null;
	}
}