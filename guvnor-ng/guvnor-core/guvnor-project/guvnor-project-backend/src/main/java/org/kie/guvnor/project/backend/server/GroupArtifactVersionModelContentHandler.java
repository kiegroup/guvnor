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

package org.kie.guvnor.project.backend.server;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.kie.guvnor.project.model.GroupArtifactVersionModel;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class GroupArtifactVersionModelContentHandler {

    public String toString(GroupArtifactVersionModel gavModel)
            throws IOException {

        Model model = new Model();
        model.setGroupId(gavModel.getGroupId());
        model.setArtifactId(gavModel.getArtifactId());
        model.setVersion(gavModel.getVersion());

        StringWriter stringWriter = new StringWriter();
        new MavenXpp3Writer().write(stringWriter, model);

        return stringWriter.toString();
    }

    public GroupArtifactVersionModel toModel(String propertiesString)
            throws IOException, XmlPullParserException {
        Model model = new MavenXpp3Reader().read(new StringReader(propertiesString));

        return new GroupArtifactVersionModel(
                model.getGroupId(),
                model.getArtifactId(),
                model.getVersion());
    }
}
