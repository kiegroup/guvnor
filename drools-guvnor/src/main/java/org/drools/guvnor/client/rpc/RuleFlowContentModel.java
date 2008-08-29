package org.drools.guvnor.client.rpc;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Collection;

import org.drools.guvnor.client.rulefloweditor.TransferConnection;
import org.drools.guvnor.client.rulefloweditor.TransferNode;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RuleFlowContentModel
    implements
    IsSerializable {

    private Collection<TransferNode>       nodes       = new ArrayList<TransferNode>();
    private Collection<TransferConnection> connections = new ArrayList<TransferConnection>();
    private String                         xml;

    public void setNodes(Collection<TransferNode> nodes) {
        this.nodes = nodes;
    }

    public Collection<TransferNode> getNodes() {
        return nodes;
    }

    public void setConnections(Collection<TransferConnection> connections) {
        this.connections = connections;
    }

    public Collection<TransferConnection> getConnections() {
        return connections;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getXml() {
        return xml;
    }

}