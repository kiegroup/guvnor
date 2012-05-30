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

package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import org.drools.guvnor.shared.api.PortableObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import static java.util.Collections.*;
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.ListenerType.*;
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.MarshallingOption.*;
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.ProtocolOption.*;
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.SessionType.*;
import static org.drools.guvnor.client.util.Preconditions.*;

public class ServiceKSessionConfig
        implements PortableObject {

    private static final long serialVersionUID = -7079787988244078946L;

    private String name;
    private String url;
    private ProtocolOption protocol;
    private MarshallingOption marshalling;
    private SessionType type;
    private ClockType clockType;
    private Boolean keepReference;
    private Map<ListenerType, Set<String>> listeners;

    public ServiceKSessionConfig() {
        //necessary for serialization
    }

    public ServiceKSessionConfig(final String name) {
        setupNewInstance(name, name, STATELESS, REST, XSTREAM, null, null, null);
    }

    public ServiceKSessionConfig(final ServiceKSessionConfig ksession) {
        checkNotNull("ksession", ksession);
        setupNewInstance(ksession.name, ksession.url, ksession.type, ksession.protocol,
                ksession.marshalling, ksession.clockType, ksession.keepReference,
                ksession.listeners);
    }

    public ServiceKSessionConfig(final String name,
            final String url,
            final SessionType type,
            final ProtocolOption protocol,
            final MarshallingOption marshalling,
            final ClockType clockType,
            final Boolean keepReference,
            final Map<ListenerType, Set<String>> listeners) {
        setupNewInstance(name, url, type, protocol, marshalling, clockType, keepReference, listeners);
    }

    public ServiceKSessionConfig(String name, ServiceKSessionConfig ksession) {
        checkNotNull("ksession", ksession);
        checkNotEmpty("name", name);
        setupNewInstance(name, ksession.url, ksession.type, ksession.protocol,
                ksession.marshalling, ksession.clockType, ksession.keepReference,
                ksession.listeners);
    }

    private void setupNewInstance(final String name,
            final String url,
            final SessionType type,
            final ProtocolOption protocol,
            final MarshallingOption marshalling,
            final ClockType clockType,
            final Boolean keepReference,
            final Map<ListenerType, Set<String>> listeners) {
        this.name = checkNotEmpty("name", name);
        if (url == null) {
            this.url = name;
        } else {
            this.url = url;
        }
        if (type == null) {
            this.type = STATELESS;
        } else {
            this.type = type;
        }
        if (protocol == null) {
            this.protocol = REST;
        } else {
            this.protocol = protocol;
        }
        if (marshalling == null) {
            this.marshalling = XSTREAM;
        } else {
            this.marshalling = marshalling;
        }
        this.clockType = clockType;
        this.keepReference = keepReference;
        if (listeners != null) {
            this.listeners = new HashMap<ListenerType, Set<String>>(listeners);
        } else {
            this.listeners = new HashMap<ListenerType, Set<String>>();
        }
    }

    public boolean hasConfig() {
        if (clockType != null || keepReference != null || getListeners().size() > 0) {
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public SessionType getType() {
        return type;
    }

    public ProtocolOption getProtocol() {
        return protocol;
    }

    public MarshallingOption getMarshalling() {
        return marshalling;
    }

    public ClockType getClockType() {
        return clockType;
    }

    public Boolean getKeepReference() {
        return keepReference;
    }

    public void setUrl(final String url) {
        this.url = checkNotEmpty("url", url);
    }

    public void setType(final SessionType type) {
        this.type = checkNotNull("type", type);
    }

    public void setProtocol(final ProtocolOption protocol) {
        this.protocol = checkNotNull("protocol", protocol);
    }

    public void setMarshalling(final MarshallingOption marshalling) {
        this.marshalling = checkNotNull("marshalling", marshalling);
    }

    public void setClockType(final ClockType clockType) {
        this.clockType = checkNotNull("clockType", clockType);
    }

    public void setClockTypeToNull() {
        this.clockType = null;
    }

    public void setKeepReference(final boolean keepReference) {
        this.keepReference = keepReference;
    }

    public void setKeepReferenceToNull() {
        this.keepReference = null;
    }

    public void addAgendaListener(final String className) {
        addListerner(AGENDA, className);
    }

    public void removeAgendaListener(final String className) {
        removeListerner(AGENDA, className);
    }

    public void addProcessListener(final String className) {
        addListerner(PROCESS, className);
    }

    public void removeProcessListener(final String className) {
        removeListerner(PROCESS, className);
    }

    public void addWorkingMemoryListener(final String className) {
        addListerner(WORKING_MEMORY, className);
    }

    public void removeWorkingMemoryListener(final String className) {
        removeListerner(WORKING_MEMORY, className);
    }

    public Collection<String> getAgendaListeners() {
        return getListeners(AGENDA);
    }

    public Collection<String> getProcessListeners() {
        return getListeners(PROCESS);
    }

    public Collection<String> getWorkingMemoryListeners() {
        return getListeners(WORKING_MEMORY);
    }

    public Collection<String> getListeners() {
        final Set<String> result = new HashSet<String>();
        for (Set<String> activeSet : listeners.values()) {
            result.addAll(activeSet);
        }
        return result;
    }

    private Collection<String> getListeners(final ListenerType type) {
        if (!listeners.containsKey(type)) {
            return emptyList();
        }
        return listeners.get(type);
    }

    private void addListerner(final ListenerType type, final String className) {
        checkNotNull("type", type);
        checkNotEmpty("className", className);
        if (!listeners.containsKey(type)) {
            listeners.put(type, new HashSet<String>());
        }
        listeners.get(type).add(className);
    }

    private void removeListerner(final ListenerType type, final String className) {
        if (className == null || className.trim().equals("")) {
            return;
        }
        checkNotNull("type", type);
        if (!listeners.containsKey(type)) {
            return;
        }
        listeners.get(type).remove(className);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ServiceKSessionConfig that = (ServiceKSessionConfig) o;

        if (clockType != that.clockType) {
            return false;
        }
        if (keepReference != null ? !keepReference.equals(that.keepReference) : that.keepReference != null) {
            return false;
        }
        if (listeners != null ? !listeners.equals(that.listeners) : that.listeners != null) {
            return false;
        }
        if (marshalling != that.marshalling) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }
        if (protocol != that.protocol) {
            return false;
        }
        if (type != that.type) {
            return false;
        }
        if (!url.equals(that.url)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + protocol.hashCode();
        result = 31 * result + marshalling.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (clockType != null ? clockType.hashCode() : 0);
        result = 31 * result + (keepReference != null ? keepReference.hashCode() : 0);
        result = 31 * result + (listeners != null ? listeners.hashCode() : 0);
        return result;
    }
}
