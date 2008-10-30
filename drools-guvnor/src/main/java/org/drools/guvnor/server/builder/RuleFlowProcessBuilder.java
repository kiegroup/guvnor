package org.drools.guvnor.server.builder;

import java.util.Collection;

import org.drools.guvnor.client.rulefloweditor.ForEachTransferNode;
import org.drools.guvnor.client.rulefloweditor.HumanTaskTransferNode;
import org.drools.guvnor.client.rulefloweditor.SplitNode;
import org.drools.guvnor.client.rulefloweditor.SplitTransferNode;
import org.drools.guvnor.client.rulefloweditor.TransferNode;
import org.drools.guvnor.client.rulefloweditor.WorkItemTransferNode;
import org.drools.knowledge.definitions.process.Node;
import org.drools.knowledge.definitions.process.NodeContainer;
import org.drools.process.core.Work;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.node.ForEachNode;
import org.drools.workflow.core.node.HumanTaskNode;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.workflow.core.node.Split.ConnectionRef;

public class RuleFlowProcessBuilder {

    public static void updateProcess(RuleFlowProcess process,
                                     Collection<TransferNode> contentNodes) {

        for ( TransferNode tn : contentNodes ) {

            Node node = process.getNode( tn.getId() );

            updateNode( tn,
                        node );
        }
    }

    public static void updateContainer(NodeContainer nodeContainer,
                                       Collection<TransferNode> contentNodes) {

        for ( TransferNode tn : contentNodes ) {

            Node node = nodeContainer.getNode( tn.getId() );

            updateNode( tn,
                        node );
        }
    }

    private static void updateNode(TransferNode tn,
                                   Node node) {
        /*
         * At this point only the parameters are editable
         */
        if ( tn instanceof HumanTaskTransferNode ) {

            updateHumanTask( (HumanTaskTransferNode) tn,
                             (HumanTaskNode) node );

        } else if ( tn instanceof WorkItemTransferNode ) {

            updateWorkItem( (WorkItemTransferNode) tn,
                            (WorkItemNode) node );

        } else if ( tn instanceof SplitTransferNode ) {

            updateSplitNode( (SplitTransferNode) tn,
                             (Split) node );

        } else if ( tn instanceof ForEachTransferNode ) {

            updateForEach( (ForEachTransferNode) tn,
                           (ForEachNode) node );
        }
    }

    private static void updateHumanTask(HumanTaskTransferNode httn,
                                        HumanTaskNode humanTaskNode) {
        Work work = humanTaskNode.getWork();

        if ( work != null ) {
            for ( String key : work.getParameters().keySet() ) {

                work.setParameter( key,
                                   httn.getParameters().get( key ) );
            }
        }
    }

    private static void updateWorkItem(WorkItemTransferNode witn,
                                       WorkItemNode workItemNode) {
        Work work = workItemNode.getWork();

        if ( work != null ) {

            for ( String key : work.getParameters().keySet() ) {

                work.setParameter( key,
                                   witn.getParameters().get( key ) );
            }
        }
    }

    private static void updateSplitNode(SplitTransferNode stn,
                                        Split splitNode) {
        for ( Split.ConnectionRef connection : splitNode.getConstraints().keySet() ) {

            final ConnectionRef ref1 = new ConnectionRef( connection.getNodeId(),
                                                          connection.getToType() );

            SplitNode.ConnectionRef ref2 = new SplitNode.ConnectionRef();
            ref2.setNodeId( connection.getNodeId() );
            ref2.setToType( connection.getToType() );

            Constraint c1 = splitNode.internalGetConstraint( ref1 );
            SplitNode.Constraint c2 = stn.getConstraints().get( ref2 );

            updateConstraint( c1,
                              c2 );
        }
    }

    private static void updateForEach(ForEachTransferNode tn,
                                      ForEachNode foreachNode) {

        for ( TransferNode subTn : tn.getContentModel().getNodes() ) {

            Node subNode = foreachNode.getNode( subTn.getId() );

            updateNode( subTn,
                        subNode );
        }
    }

    private static void updateConstraint(Constraint to,
                                         org.drools.guvnor.client.rulefloweditor.SplitNode.Constraint from) {

        to.setConstraint( from.getConstraint() );

        to.setDialect( from.getDialect() );

        to.setName( from.getName() );

        to.setPriority( from.getPriority() );

        to.setType( from.getType() );
    }
}
