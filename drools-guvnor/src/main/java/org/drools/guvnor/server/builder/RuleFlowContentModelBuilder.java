package org.drools.guvnor.server.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.guvnor.client.rpc.RuleFlowContentModel;
import org.drools.guvnor.client.rulefloweditor.ForEachTransferNode;
import org.drools.guvnor.client.rulefloweditor.HumanTaskTransferNode;
import org.drools.guvnor.client.rulefloweditor.SplitNode;
import org.drools.guvnor.client.rulefloweditor.SplitTransferNode;
import org.drools.guvnor.client.rulefloweditor.TransferConnection;
import org.drools.guvnor.client.rulefloweditor.TransferNode;
import org.drools.guvnor.client.rulefloweditor.WorkItemTransferNode;
import org.drools.process.core.Work;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.CompositeNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.EventNode;
import org.drools.workflow.core.node.ForEachNode;
import org.drools.workflow.core.node.HumanTaskNode;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.workflow.core.node.RuleSetNode;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.workflow.core.node.TimerNode;
import org.drools.workflow.core.node.WorkItemNode;

public class RuleFlowContentModelBuilder {

    public RuleFlowContentModel createModel(RuleFlowProcess process) {

        RuleFlowContentModel model = new RuleFlowContentModel();

        Collection<TransferNode> transferNodes = createNodesAndConnections( process.getNodes(),
                                                                            model );

        model.getNodes().addAll( transferNodes );

        return model;
    }

    /**
     * Creates transfer nodes from RuleFlowProcess nodes.
     * 
     * @param nodes from the rule flow XML.
     * @param model RuleFlowContentModel that contains the transfer nodes for client side.  
     */
    private Collection<TransferNode> createNodesAndConnections(Node[] nodes,
                                                               RuleFlowContentModel model) {

        Collection<TransferNode> transferNodes = new ArrayList<TransferNode>();

        for ( int i = 0; i < nodes.length; i++ ) {
            Node node = nodes[i];
            TransferNode.Type type = getType( node );
            TransferNode tn = null;

            if ( type == TransferNode.Type.WORK_ITEM ) {

                tn = createWorkItemTransfernode( (WorkItemNode) node );

            } else if ( type == TransferNode.Type.HUMANTASK ) {

                tn = createHumanTaskTransferNode( (HumanTaskNode) node );

            } else if ( type == TransferNode.Type.SPLIT ) {

                tn = createSplitNode( (Split) node );

            } else if ( type == TransferNode.Type.FOR_EACH ) {

                tn = createForEachNodeNode( (ForEachNode) node );

            } else {

                tn = new TransferNode();

            }

            /*
             * Creates connections for from and to this node.
             */
            createConnections( model,
                               node );

            tn.setId( node.getId() );

            tn.setX( (Integer) node.getMetaData( "x" ) );
            tn.setY( (Integer) node.getMetaData( "y" ) );

            Integer height = (Integer) node.getMetaData( "height" );
            if ( height == null ) {
                tn.setHeight( 40 );
            } else {
                tn.setHeight( height );
            }

            Integer width = (Integer) node.getMetaData( "width" );
            if ( width == null ) {
                tn.setWidth( 80 );
            } else {
                tn.setWidth( width );
            }

            tn.setName( node.getName() );
            tn.setType( type );

            transferNodes.add( tn );
        }

        return transferNodes;
    }

    private ForEachTransferNode createForEachNodeNode(ForEachNode node) {

        ForEachTransferNode fetn = new ForEachTransferNode();
        RuleFlowContentModel model = new RuleFlowContentModel();

        Collection<TransferNode> transferNodes = createNodesAndConnections( node.getNodes(),
                                                                            model );

        model.setNodes( transferNodes );
        fetn.setContentModel( model );

        return fetn;
    }

    /**
     * Checks this node for out and in going connections and creates a transfer object from them.
     * 
     * @param model
     * @param node
     */
    private void createConnections(RuleFlowContentModel model,
                                   Node node) {

        for ( List<org.drools.workflow.core.Connection> inConnections : node.getIncomingConnections().values() ) {
            for ( org.drools.workflow.core.Connection connection : inConnections ) {
                createConnection( model,
                                  connection );
            }
        }

        for ( List<org.drools.workflow.core.Connection> outConnections : node.getOutgoingConnections().values() ) {
            for ( org.drools.workflow.core.Connection connection : outConnections ) {
                createConnection( model,
                                  connection );
            }
        }
    }

    /**
     * Creates a single connection to be transferred.
     * 
     * @param model
     * @param connection
     */
    private void createConnection(RuleFlowContentModel model,
                                  org.drools.workflow.core.Connection connection) {
        TransferConnection tc = new TransferConnection();

        tc.setFromId( connection.getFrom().getId() );
        tc.setToId( connection.getTo().getId() );

        model.getConnections().add( tc );
    }

    private TransferNode createHumanTaskTransferNode(HumanTaskNode node) {
        HumanTaskTransferNode httn = new HumanTaskTransferNode();

        Work work = node.getWork();

        if ( work != null ) {
            for ( String key : work.getParameters().keySet() ) {

                Object o = work.getParameters().get( key );
                String value;

                if ( o == null ) {
                    value = "";
                } else {
                    value = o.toString();
                }

                httn.getParameters().put( key,
                                          value );
            }
        }

        return httn;
    }

    private SplitTransferNode createSplitNode(Split s) {
        SplitTransferNode sn = new SplitTransferNode();

        sn.setSplitType( SplitTransferNode.Type.getType( s.getType() ) );

        for ( Split.ConnectionRef connection : s.getConstraints().keySet() ) {

            SplitNode.ConnectionRef ref = new SplitNode.ConnectionRef();
            ref.setNodeId( connection.getNodeId() );
            ref.setToType( connection.getToType() );

            SplitNode.Constraint constraint = getConstraint( s.getConstraints().get( connection ) );

            sn.getConstraints().put( ref,
                                     constraint );
        }

        return sn;
    }

    private WorkItemTransferNode createWorkItemTransfernode(WorkItemNode node) {
        WorkItemTransferNode tn = new WorkItemTransferNode();

        Work work = node.getWork();

        if ( work != null ) {
            tn.setWorkName( work.getName() );

            for ( String key : work.getParameters().keySet() ) {

                Object o = work.getParameters().get( key );
                String value;

                if ( o == null ) {
                    value = "";
                } else {
                    value = o.toString();
                }

                tn.getParameters().put( key,
                                        value );
            }
        }

        return tn;
    }

    private org.drools.guvnor.client.rulefloweditor.SplitNode.Constraint getConstraint(Constraint constraint) {

        SplitNode.Constraint c = new SplitNode.Constraint();

        c.setConstraint( constraint.getConstraint() );

        c.setDialect( constraint.getDialect() );

        c.setName( constraint.getName() );

        c.setPriority( constraint.getPriority() );

        c.setType( constraint.getType() );

        return c;
    }

    private TransferNode.Type getType(Node node) {
        if ( node instanceof StartNode ) {
            return TransferNode.Type.START;
        } else if ( node instanceof EndNode ) {
            return TransferNode.Type.END;
        } else if ( node instanceof ActionNode ) {
            return TransferNode.Type.ACTION_NODE;
        } else if ( node instanceof RuleSetNode ) {
            return TransferNode.Type.RULESET;
        } else if ( node instanceof SubProcessNode ) {
            return TransferNode.Type.SUB_PROCESS;
        } else if ( node instanceof ForEachNode ) {
            return TransferNode.Type.FOR_EACH;
        } else if ( node instanceof CompositeNode ) {
            return TransferNode.Type.COMPOSITE;
        } else if ( node instanceof Join ) {
            return TransferNode.Type.JOIN;
        } else if ( node instanceof Split ) {
            return TransferNode.Type.SPLIT;
        } else if ( node instanceof MilestoneNode ) {
            return TransferNode.Type.MILESTONE;
        } else if ( node instanceof TimerNode ) {
            return TransferNode.Type.TIMER;
        } else if ( node instanceof HumanTaskNode ) {
            return TransferNode.Type.HUMANTASK;
        } else if ( node instanceof WorkItemNode ) {
            return TransferNode.Type.WORK_ITEM;
        } else if ( node instanceof EventNode ) {
            return TransferNode.Type.EVENT;
        }

        throw new IllegalArgumentException();
    }
}
