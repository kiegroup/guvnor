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

import org.drools.guvnor.client.rpc.RuleFlowContentModel;
import org.drools.guvnor.client.rulefloweditor.TransferNode.Type;

public class TransferRuleFlowNodeFactory {

    public static TransferNode createNode(RuleFlowBaseNode node) {

        TransferNode tn;

        if ( node instanceof StartNode ) {

            tn = new TransferNode();
            tn.setType( Type.START );

        } else if ( node instanceof HumanTaskNode ) {

            tn = createHumanTask( (HumanTaskNode) node );

        } else if ( node instanceof JoinNode ) {

            tn = new TransferNode();
            tn.setType( Type.JOIN );

        } else if ( node instanceof SubProcessNode ) {

            tn = new TransferNode();
            tn.setType( Type.SUB_PROCESS );

        } else if ( node instanceof MileStoneNode ) {

            tn = new TransferNode();
            tn.setType( Type.MILESTONE );

        } else if ( node instanceof TimerNode ) {

            tn = new TransferNode();
            tn.setType( Type.TIMER );

        } else if ( node instanceof ActionNode ) {

            tn = new TransferNode();
            tn.setType( Type.ACTION_NODE );

        } else if ( node instanceof WorkItemNode ) {

            tn = createWorkItemNode( (WorkItemNode) node );

        } else if ( node instanceof RuleSetNode ) {

            tn = new TransferNode();
            tn.setType( Type.RULESET );

        } else if ( node instanceof SplitNode ) {

            tn = createSplit( (SplitNode) node );

        } else if ( node instanceof ForEachNode ) {

            tn = createForEach( (ForEachNode) node );

        } else if ( node instanceof EndNode ) {

            tn = new TransferNode();
            tn.setType( Type.END );

        } else {

            throw new IllegalArgumentException();

        }

        tn.setId( node.getId() );

        return tn;
    }

    private static TransferNode createForEach(ForEachNode node) {

        ForEachTransferNode fetn = new ForEachTransferNode();
        Collection<TransferNode> baseNodes = new ArrayList<TransferNode>();

        for ( RuleFlowBaseNode subNode : node.getNodes().values() ) {
            baseNodes.add( createNode( subNode ) );
        }

        RuleFlowContentModel model = new RuleFlowContentModel();
        model.setNodes( baseNodes );
        fetn.setContentModel( model );

        return fetn;
    }

    private static WorkItemTransferNode createWorkItemNode(WorkItemNode node) {

        WorkItemTransferNode tn = null;

        if ( node instanceof EmailNode ) {

            tn = new WorkItemTransferNode();
            tn.setWorkName( WorkItemNode.EMAIL );

        } else if ( node instanceof LogNode ) {

            tn = new WorkItemTransferNode();
            tn.setWorkName( WorkItemNode.LOG );

        }

        tn.setParameters( node.attributes );

        return tn;
    }

    private static SplitTransferNode createSplit(SplitNode node) {

        SplitTransferNode splitNode = new SplitTransferNode();

        splitNode.setSplitType( node.type );

        splitNode.setConstraints( node.constraints );

        return splitNode;
    }

    private static TransferNode createHumanTask(HumanTaskNode node) {
        HumanTaskTransferNode n = new HumanTaskTransferNode();

        n.setParameters( node.getParameters() );

        return n;
    }

}
