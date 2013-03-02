/*
 * Copyright 2012 JBoss Inc
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

package org.kie.guvnor.commons.service.builder.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Portable
public class BuildResults
        implements Iterable<BuildMessage> {

    private ArrayList<BuildMessage> buildMessages = new ArrayList<BuildMessage>();
    private String artifactID;

    public List<BuildMessage> getBuildMessages() {
        return buildMessages;
    }

    public boolean isEmpty() {
        return buildMessages.isEmpty();
    }

    @Override
    public Iterator<BuildMessage> iterator() {
        return buildMessages.iterator();
    }

    public String getArtifactID() {
        return artifactID;
    }

    public void setArtifactID(String artifactID) {
        this.artifactID = artifactID;
    }
}
