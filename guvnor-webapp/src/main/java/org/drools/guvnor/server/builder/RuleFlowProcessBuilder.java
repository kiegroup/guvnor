/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server.builder;

import java.util.Collection;

import org.drools.definition.process.Node;
import org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode;
import org.drools.guvnor.client.asseteditor.ruleflow.HumanTaskTransferNode;
import org.drools.guvnor.client.asseteditor.ruleflow.SplitNode;
import org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode;
import org.drools.guvnor.client.asseteditor.ruleflow.TransferNode;
import org.drools.guvnor.client.asseteditor.ruleflow.WorkItemTransferNode;
import org.drools.guvnor.client.asseteditor.ruleflow.TransferNode.Type;
import org.drools.process.core.Work;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.WorkItemNode;

public class RuleFlowProcessBuilder {

    public static void updateProcess(RuleFlowProcess process,
                                     Collection<TransferNode> contentNodes) {

        for ( TransferNode transferNode : contentNodes ) {

            Node node = process.getNode( transferNode.getId() );

            updateNode( transferNode,
                        node );
        }
    }

    private static void updateNode(TransferNode transferNode,
                                   Node node) {
        /*
         * At this point only the parameters are editable
         */
        if ( transferNode instanceof HumanTaskTransferNode ) {

            updateHumanTask( (HumanTaskTransferNode) transferNode,
                             (HumanTaskNode) node );

        } else if ( transferNode instanceof WorkItemTransferNode ) {

            updateWorkItem( (WorkItemTransferNode) transferNode,
                            (WorkItemNode) node );

        } else if ( transferNode instanceof SplitTransferNode ) {

            updateSplitNode( (SplitTransferNode) transferNode,
                             (Split) node );

        } else if ( transferNode instanceof ElementContainerTransferNode ) {

            if ( transferNode.getType() == Type.FOR_EACH ) {

                updateForEach( (ElementContainerTransferNode) transferNode,
                               (ForEachNode) node );

            } else if ( transferNode.getType() == Type.COMPOSITE ) {

                updateComposite( (ElementContainerTransferNode) transferNode,
                                 (CompositeNode) node );

            }
        }
    }

    private static void updateHumanTask(HumanTaskTransferNode transferNode,
                                        HumanTaskNode humanTaskNode) {
        Work work = humanTaskNode.getWork();

        if ( work != null ) {
            for ( String key : work.getParameters().keySet() ) {

                work.setParameter( key,
                                   transferNode.getParameters().get( key ) );
            }
        }
    }

    private static void updateWorkItem(WorkItemTransferNode transferNode,
                                       WorkItemNode workItemNode) {
        Work work = workItemNode.getWork();

        if ( work != null ) {

            for ( String key : work.getParameters().keySet() ) {

                work.setParameter( key,
                                   transferNode.getParameters().get( key ) );
            }
        }
    }

    private static void updateSplitNode(SplitTransferNode splitTransferNode,
                                        Split splitNode) {
        for ( ConnectionRef connection : splitNode.getConstraints().keySet() ) {

            final ConnectionRef connectionRef = new ConnectionRef( connection.getNodeId(),
                                                                   connection.getToType() );

            SplitNode.ConnectionRef splitNodeConnectionRef = new SplitNode.ConnectionRef();
            splitNodeConnectionRef.setNodeId( connection.getNodeId() );
            splitNodeConnectionRef.setToType( connection.getToType() );

            Constraint constraint = splitNode.internalGetConstraint( connectionRef );
            SplitNode.Constraint splitNodeConstraint = splitTransferNode.getConstraints().get( splitNodeConnectionRef );

            updateConstraint( constraint,
                              splitNodeConstraint );
        }
    }

    private static void updateForEach(ElementContainerTransferNode transferNode,
                                      ForEachNode foreachNode) {

        for ( TransferNode subTransferNode : transferNode.getContentModel().getNodes() ) {

            Node subNode = foreachNode.getNode( subTransferNode.getId() );

            updateNode( subTransferNode,
                        subNode );
        }
    }

    private static void updateComposite(ElementContainerTransferNode transferNode,
                                        CompositeNode compositeNode) {

        for ( TransferNode subTransferNode : transferNode.getContentModel().getNodes() ) {

            Node subNode = compositeNode.getNode( subTransferNode.getId() );

            updateNode( subTransferNode,
                        subNode );
        }
    }

    private static void updateConstraint(Constraint constraint,
                                         org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint from) {

        constraint.setConstraint( from.getConstraint() );

        constraint.setDialect( from.getDialect() );

        constraint.setName( from.getName() );

        constraint.setPriority( from.getPriority() );

        constraint.setType( from.getType() );
    }
}
