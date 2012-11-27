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

package org.kie.projecteditor.server.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.projecteditor.shared.model.GroupArtifactVersionModel;
import org.kie.projecteditor.shared.model.KBaseModel;
import org.kie.projecteditor.shared.model.KProjectModel;

import java.util.HashMap;
import java.util.Map;

public class KProjectConverter
        extends AbstractXStreamConverter {

    public KProjectConverter() {
        super(KProjectModel.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        KProjectModel kProject = (KProjectModel) value;
        writeAttribute(writer, "kBasesPath", kProject.getKBasesPath());
        writeAttribute(writer, "kProjectPath", kProject.getKProjectPath());
        writeObject(writer, context, "groupArtifactVersion", kProject.getGroupArtifactVersion());
        writeObjectList(writer, context, "kbases", "kbase", kProject.getKBases().values());
    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final KProjectModel kProject = new KProjectModel();
        kProject.setKBasesPath(reader.getAttribute("kBasesPath"));
        kProject.setKProjectPath(reader.getAttribute("kProjectPath"));

        readNodes(reader, new AbstractXStreamConverter.NodeReader() {
            public void onNode(HierarchicalStreamReader reader, String name, String value) {
                if ("groupArtifactVersion".equals(name)) {
                    kProject.setGroupArtifactVersion((GroupArtifactVersionModel) context.convertAnother(reader.getValue(), GroupArtifactVersionModel.class));
                } else if ("kbases".equals(name)) {
                    Map<String, KBaseModel> kBases = new HashMap<String, KBaseModel>();
                    for (KBaseModel kBase : readObjectList(reader, context, KBaseModel.class)) {
                        kBases.put(kBase.getName(), kBase);
                    }
                    kProject.getKBases().putAll(kBases);
                }
            }
        });

        return kProject;
    }
}
