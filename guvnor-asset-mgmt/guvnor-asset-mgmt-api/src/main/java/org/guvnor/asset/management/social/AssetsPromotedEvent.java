/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.asset.management.social;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AssetsPromotedEvent extends AssetManagementEvent {

    private String sourceBranch;

    private String targetBranch;

    List<String> assets = new ArrayList<String>(  );

    public AssetsPromotedEvent() {
    }

    public AssetsPromotedEvent( String processName,
            String repositoryAlias,
            String rootURI,
            String sourceBranch,
            String targetBranch,
            List<String> assets,
            String user,
            Long timestamp ) {
        super( processName, repositoryAlias, rootURI, user, timestamp );
        this.sourceBranch = sourceBranch;
        this.targetBranch = targetBranch;
        this.assets = assets;
    }

    public String getSourceBranch() {
        return sourceBranch;
    }

    public void setSourceBranch( String sourceBranch ) {
        this.sourceBranch = sourceBranch;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public void setTargetBranch( String targetBranch ) {
        this.targetBranch = targetBranch;
    }

    public List<String> getAssets() {
        return assets;
    }

    public void setAssets( List<String> assets ) {
        this.assets = assets;
    }
}