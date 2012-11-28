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

package org.kie.guvnor.projecteditor.backend.server;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.kie.guvnor.projecteditor.backend.server.converters.KBaseConverter;
import org.kie.guvnor.projecteditor.backend.server.converters.KProjectConverter;
import org.kie.guvnor.projecteditor.backend.server.converters.KSessionConverter;
import org.kie.guvnor.projecteditor.model.ClockTypeOption;
import org.kie.guvnor.projecteditor.model.KBaseModel;
import org.kie.guvnor.projecteditor.model.KProjectModel;
import org.kie.guvnor.projecteditor.model.KSessionModel;

public class ProjectEditorContentHandler {


    public static KProjectModel toModel(String xml) {
        return (KProjectModel) createXStream().fromXML(xml);
    }

    public static String toString(KProjectModel model) {
        return createXStream().toXML(model);
    }

    private static XStream createXStream() {
        XStream xStream = new XStream(new DomDriver());

        xStream.alias("kproject", KProjectModel.class);
        xStream.alias("kbase", KBaseModel.class);
        xStream.alias("ksession", KSessionModel.class);
        xStream.alias("clockType", ClockTypeOption.class);

        xStream.registerConverter(new KProjectConverter());
        xStream.registerConverter(new KBaseConverter());
        xStream.registerConverter(new KSessionConverter());
        xStream.registerConverter(new ClockTypeConverter());
        return xStream;
    }
}
