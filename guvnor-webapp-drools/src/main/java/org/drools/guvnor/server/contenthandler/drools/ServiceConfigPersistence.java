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
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ListenerType;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.MarshallingOption;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ProtocolOption;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig;
import org.drools.guvnor.client.rpc.MavenArtifact;

public class ServiceConfigPersistence {

    private static final ServiceConfigPersistence INSTANCE = new ServiceConfigPersistence();

    private final XStream xt;

    private ServiceConfigPersistence() {
        xt = new XStream(new DomDriver());

        xt.alias("service-config", ServiceConfig.class);

        xt.alias("kbase-config", ServiceKBaseConfig.class);
        xt.alias("ksession-config", ServiceKSessionConfig.class);
        xt.alias("kagent-config", ServiceKAgentConfig.class);

        xt.alias("asset-reference", AssetReference.class);
        xt.alias("maven-artifact", MavenArtifact.class);

        xt.alias("listener-type", ListenerType.class);
        xt.alias("protocol", ProtocolOption.class);
        xt.alias("marshalling-option", MarshallingOption.class);
        xt.alias("protocol-option", ProtocolOption.class);

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
