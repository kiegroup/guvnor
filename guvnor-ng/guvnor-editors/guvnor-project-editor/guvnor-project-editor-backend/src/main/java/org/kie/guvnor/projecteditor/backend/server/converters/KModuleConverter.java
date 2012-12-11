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

package org.kie.guvnor.projecteditor.backend.server.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.guvnor.projecteditor.model.GroupArtifactVersionModel;
import org.kie.guvnor.projecteditor.model.KBaseModel;
import org.kie.guvnor.projecteditor.model.KModuleModel;

import java.util.HashMap;
import java.util.Map;

public class KModuleConverter
        extends AbstractXStreamConverter {

    public KModuleConverter() {
        super(KModuleModel.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        KModuleModel kModule = (KModuleModel) value;
        writeAttribute(writer, "kBasesPath", kModule.getKBasesPath());
        writeAttribute(writer, "kModulePath", kModule.getKModulePath());
        writeObject(writer, context, "groupArtifactVersion", kModule.getGroupArtifactVersion());
        writeObjectList(writer, context, "kbases", "kbase", kModule.getKBases().values());
    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final KModuleModel kModule = new KModuleModel();
        kModule.setKBasesPath(reader.getAttribute("kBasesPath"));
        kModule.setKModulePath(reader.getAttribute("kModulePath"));

        readNodes(reader, new AbstractXStreamConverter.NodeReader() {
            public void onNode(HierarchicalStreamReader reader, String name, String value) {
                if ("groupArtifactVersion".equals(name)) {
                    kModule.setGroupArtifactVersion((GroupArtifactVersionModel) context.convertAnother(reader.getValue(), GroupArtifactVersionModel.class));
                } else if ("kbases".equals(name)) {
                    Map<String, KBaseModel> kBases = new HashMap<String, KBaseModel>();
                    for (KBaseModel kBase : readObjectList(reader, context, KBaseModel.class)) {
                        kBases.put(kBase.getName(), kBase);
                    }
                    kModule.getKBases().putAll(kBases);
                }
            }
        });

        return kModule;
    }
}
