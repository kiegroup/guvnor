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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.rpc.RuleFlowContentModel;

import pl.balon.gwt.diagrams.client.connection.Connection;

import com.google.gwt.user.client.ui.AbsolutePanel;

public class RuleFlowViewer extends AbsolutePanel {

    private Map<Long, RuleFlowBaseNode> nodes       = new HashMap<Long, RuleFlowBaseNode>();
    private List<Connection>            connections = new ArrayList<Connection>();

    public RuleFlowViewer(RuleFlowContentModel rfcm,
                          FormStyleLayout parametersForm) {

        this.setHeight( "600px" );
        this.setHeight( "600px" );

        // Handle nodes
        for ( TransferNode tn : rfcm.getNodes() ) {

            RuleFlowBaseNode node = RuleFlowNodeFactory.createNode( tn );

            if ( node != null ) {

                if ( node instanceof ForEachNode ) {
                    addForEachSubNodes( (ForEachNode) node,
                                        parametersForm );
                }

                node.addParametersForm( parametersForm );

                add( node,
                     node.getX(),
                     node.getY() );

                nodes.put( node.getId(),
                           node );
            }
        }

        for ( TransferConnection c : rfcm.getConnections() ) {

            Connection connection = RuleFlowConnectionFactory.createConnection( c,
                                                                                nodes );

            connections.add( connection );

            connection.appendTo( this );

        }
    }

    private void addForEachSubNodes(ForEachNode fen,
                                    FormStyleLayout parametersForm) {
        for ( RuleFlowBaseNode node : fen.getNodes().values() ) {

            node.addParametersForm( parametersForm );

            if ( node instanceof ForEachNode ) {
                addForEachSubNodes( (ForEachNode) node,
                                    parametersForm );
            }
        }
    }

    /**
     * Update the connections that this graph has.
     */
    public void update() {
        if ( connections != null ) {
            for ( Connection c : connections ) {
                c.update();
            }
        }

        // For each nodes have connections too.
        for ( RuleFlowBaseNode node : nodes.values() ) {
            if ( node instanceof ForEachNode ) {
                ((ForEachNode) node).update();
            }
        }
    }

    public List<TransferNode> getTransferNodes() {

        List<TransferNode> transferNodes = new ArrayList<TransferNode>();

        for ( RuleFlowBaseNode node : nodes.values() ) {
            transferNodes.add( TransferRuleFlowNodeFactory.createNode( node ) );
        }

        return transferNodes;
    }
}
