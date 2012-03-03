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

package org.drools.guvnor.server.contenthandler.drools;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig;
import org.drools.guvnor.client.rpc.MavenArtifact;

public class ServiceConfigPersistence {

    private static final ServiceConfigPersistence INSTANCE = new ServiceConfigPersistence();

    private final XStream xt;

    private ServiceConfigPersistence() {
        xt = new XStream(new DomDriver());

        xt.alias("service-config", ServiceConfig.class);
        xt.alias("protocol", ServiceConfig.Protocol.class);
        xt.alias("asset-reference", ServiceConfig.AssetReference.class);
        xt.alias("maven-artifact", MavenArtifact.class);
        xt.omitField(MavenArtifact.class, "child");
    }

    public static ServiceConfigPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal(final ServiceConfig serviceConfig) {
        if (serviceConfig == null) {
            return "";
        }
        return xt.toXML(serviceConfig);
    }

    public ServiceConfig unmarshal(final String xml) {
        if (xml == null || xml.trim().equals("")) {
            return new ServiceConfig();
        }

        return (ServiceConfig) xt.fromXML(xml);
    }

}
