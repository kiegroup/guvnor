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

package org.kie.projecteditor.server;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.kie.projecteditor.server.converters.KBaseConverter;
import org.kie.projecteditor.server.converters.KProjectConverter;
import org.kie.projecteditor.server.converters.KSessionConverter;
import org.kie.projecteditor.shared.model.ClockTypeOption;
import org.kie.projecteditor.shared.model.KBaseModel;
import org.kie.projecteditor.shared.model.KProjectModel;
import org.kie.projecteditor.shared.model.KSessionModel;

public class ProjectEditorContentHandler {

    public static KProjectModel toModel(String xml) {

        XStream xStream = new XStream(new DomDriver());

        xStream.alias("kproject", KProjectModel.class);
        xStream.alias("kbase", KBaseModel.class);
        xStream.alias("ksession", KSessionModel.class);
        xStream.alias("clockType", ClockTypeOption.class);

        xStream.registerConverter(new KProjectConverter());
        xStream.registerConverter(new KBaseConverter());
        xStream.registerConverter(new KSessionConverter());
        xStream.registerConverter(new ClockTypeConverter());

        return (KProjectModel) xStream.fromXML(xml);
    }

}
