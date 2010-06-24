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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.guvnor.client.common.Snapshot;

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

    private enum FileType {
        UNKNOWN, SOURCE, DOCUMENTATION
    }

    private FileType fileType = FileType.UNKNOWN;

    public PackageDeploymentURIHelper(String uri) throws UnsupportedEncodingException {
        new URIProcessor().parseUri( uri );
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
        return fileType == FileType.SOURCE;
    }

    public boolean isDocumentation() {
        return fileType == FileType.DOCUMENTATION;
    }

    public String getAssetName() {
        return this.assetName;
    }

    public boolean isAsset() {
        return assetName != null;
    }

    class URIProcessor {

        private static final String PDF  = ".pdf";
        private static final String BPMN = ".bpmn";
        private static final String DRL  = ".drl";

        private String              url;

        public void parseUri(String uri) throws UnsupportedEncodingException {
            url = URLDecoder.decode( uri,
                                     "UTF-8" );

            String extension = getFileExtensionIfAny();
            setFileTypeIfAny( extension );
            stripFileExtensionIfAny( extension );
            setPackageOrAssetData();
        }

        private void setPackageOrAssetData() {
            Pattern pattern = Pattern.compile( ".*/(package|asset)/(.*)" );
            Matcher matcher = pattern.matcher( url );
            if ( matcher.matches() ) {
                String result = matcher.group( 2 );
                String[] tokens = result.split( "/" );
                version = tokens[1];
                packageName = tokens[0];
                if ( tokens.length == 3 ) {
                    assetName = tokens[2];
                }
            }
        }

        private void setFileTypeIfAny(String extension) {
            if ( extension.equals( DRL ) || extension.equals( BPMN ) ) {
                fileType = FileType.SOURCE;
            } else if ( extension.equals( PDF ) ) {
                fileType = FileType.DOCUMENTATION;
            }
        }

        private void stripFileExtensionIfAny(String extension) {
            if ( extension.length() > 0 ) {
                url = url.substring( 0,
                                     url.length() - extension.length() );
            }
        }

        private String getFileExtensionIfAny() {

            if ( isFileType( DRL ) ) {
                return DRL;
            } else if ( isFileType( BPMN ) ) {
                return BPMN;
            } else if ( isFileType( PDF ) ) {
                return PDF;
            }

            return "";
        }

        private boolean isFileType(String extension) {
            return url.endsWith( extension );
        }

    }
}