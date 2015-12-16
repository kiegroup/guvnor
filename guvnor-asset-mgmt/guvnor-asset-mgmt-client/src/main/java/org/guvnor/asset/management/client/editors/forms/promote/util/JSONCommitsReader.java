/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.guvnor.asset.management.client.editors.forms.promote.util;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import org.guvnor.asset.management.client.editors.forms.promote.SelectAssetsToPromotePresenter;

import javax.enterprise.context.Dependent;
import java.util.HashMap;
import java.util.Map;

@Dependent
public class JSONCommitsReader implements SelectAssetsToPromotePresenter.CommitsReader {

    @Override
    public Map<String, String> getCommitsPerFile( String commitsPerFileString ) {
        Map<String, String> commitsPerFile = new HashMap<String, String>();
        JSONObject jsonCommitsPerFile = JSONParser.parseStrict(commitsPerFileString).isObject();

        if ( jsonCommitsPerFile != null ) {
            for ( String file : jsonCommitsPerFile.keySet() ) {
                StringBuffer fileCommits = new StringBuffer();
                JSONArray commits = jsonCommitsPerFile.get( file ).isArray();
                if ( commits != null ) {
                    for ( int i = 0; i < commits.size(); i++ ) {
                        if ( fileCommits.length() > 0 ) fileCommits.append( "," );
                        fileCommits.append( commits.get( i ).isString().stringValue() );
                    }
                }
                commitsPerFile.put( file, fileCommits.toString() );
            }
        }
        return commitsPerFile;
    }
}
