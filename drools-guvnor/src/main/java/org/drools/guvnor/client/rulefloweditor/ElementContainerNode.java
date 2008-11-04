package org.drools.guvnor.client.rulefloweditor;

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
import java.util.HashMap;
import java.util.Map;

import pl.balon.gwt.diagrams.client.connection.Connection;

import com.google.gwt.user.client.ui.Widget;

public class ElementContainerNode extends RuleFlowBaseNode {

    private Map<Long, RuleFlowBaseNode> nodes       = new HashMap<Long, RuleFlowBaseNode>();
    private Collection<Connection>      connections = new ArrayList<Connection>();

    @Override
    public Corners getCorners() {
        return Corners.NONE;
    }

    @Override
    public String getImagePath() {
        return null;
    }

    @Override
    public String getStyle() {
        return WHITE_RULE_FLOW_NODE_STYLE;
    }

    public void update() {
        if ( connections != null ) {
            for ( Connection c : connections ) {
                c.update();
            }
        }

        // For each nodes have connections too.
        for ( RuleFlowBaseNode node : nodes.values() ) {
            if ( node instanceof ElementContainerNode ) {
                ((ElementContainerNode) node).update();
            }
        }
    }

    public Map<Long, RuleFlowBaseNode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<Long, RuleFlowBaseNode> nodes) {
        this.nodes = nodes;
    }

    public Collection<Connection> getConnections() {
        return connections;
    }

    public void setConnections(Collection<Connection> connections) {
        this.connections = connections;
    }

    @Override
    public void onClick(Widget arg0) {
        // TODO Auto-generated method stub
        //        super.onClick( arg0 );
    }
}
