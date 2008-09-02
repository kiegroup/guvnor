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

import org.cobogw.gwt.user.client.ui.RoundedPanel;
import org.drools.guvnor.client.rulefloweditor.RuleFlowBaseNode.Corners;
import org.drools.guvnor.client.rulefloweditor.TransferNode.Type;

import pl.balon.gwt.diagrams.client.connection.Connection;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class RuleFlowNodeFactory {

    public static RuleFlowBaseNode createNode(TransferNode tn) {

        RuleFlowBaseNode n;

        if ( tn.getType() == Type.START ) {

            n = new StartNode();

        } else if ( tn.getType() == Type.HUMANTASK ) {

            n = createHumanTask( (HumanTaskTransferNode) tn );

        } else if ( tn.getType() == Type.JOIN ) {

            n = new JoinNode();

        } else if ( tn.getType() == Type.SUB_PROCESS ) {

            n = new SubProcessNode();

        } else if ( tn.getType() == Type.MILESTONE ) {

            n = new MileStoneNode();

        } else if ( tn.getType() == Type.TIMER ) {

            n = new TimerNode();

        } else if ( tn.getType() == Type.ACTION_NODE ) {

            n = new ActionNode();

        } else if ( tn.getType() == Type.WORK_ITEM ) {

            n = createWorkItemNode( (WorkItemTransferNode) tn );

        } else if ( tn.getType() == Type.RULESET ) {

            n = new RuleSetNode();

        } else if ( tn.getType() == Type.SPLIT ) {

            n = createSplit( (SplitTransferNode) tn );

        } else if ( tn.getType() == Type.FOR_EACH ) {

            n = createForEach( (ForEachTransferNode) tn );

        } else if ( tn.getType() == Type.END ) {

            n = new EndNode();

        } else {

            throw new IllegalArgumentException();
        }

        fillRuleFlowBaseNode( n,
                              tn );
        return n;
    }

    private static RuleFlowBaseNode createForEach(ForEachTransferNode tn) {

        ForEachNode node = new ForEachNode();

        for ( TransferNode subNode : tn.getContentModel().getNodes() ) {
            RuleFlowBaseNode baseNode = createNode( subNode );

            node.getNodes().put( baseNode.getId(),
                                 baseNode );
        }

        for ( TransferConnection c : tn.getContentModel().getConnections() ) {
            node.getConnections().add( RuleFlowConnectionFactory.createConnection( c,
                                                                                   node.getNodes() ) );
        }

        return node;
    }

    private static RuleFlowBaseNode createWorkItemNode(WorkItemTransferNode node) {

        WorkItemNode workItemNode = null;

        if ( node.getWorkName().equals( WorkItemNode.EMAIL ) ) {

            workItemNode = new EmailNode();

        } else if ( node.getWorkName().equals( WorkItemNode.LOG ) ) {

            workItemNode = new LogNode();
        }

        workItemNode.setAttributes( node.getParameters() );

        return workItemNode;
    }

    private static RuleFlowBaseNode createSplit(SplitTransferNode tn) {

        SplitNode splitNode = new SplitNode();

        splitNode.type = tn.getSplitType();

        splitNode.constraints = tn.getConstraints();

        return splitNode;
    }

    private static RuleFlowBaseNode createHumanTask(HumanTaskTransferNode node) {
        HumanTaskNode n = new HumanTaskNode();

        n.setAttributes( node.getParameters() );

        return n;
    }

    private static void fillRuleFlowBaseNode(RuleFlowBaseNode node,
                                             TransferNode tn) {
        fillIdAndCoordinates( node,
                              tn );

        Widget panel;
        if ( node.getImagePath() == null ) {
            panel = createContentWithoutImage( tn.getName(),
                                               node,
                                               tn.getWidth() );
        } else {
            panel = createContentWithImage( tn.getName(),
                                            node,
                                            tn.getWidth() );
        }

        if ( node.getCorners() == Corners.ROUNDED ) {
            int cornerHeight = LayoutCalculator.calculateCornerHeight( tn.getWidth() );
            panel.setHeight( LayoutCalculator.calculateNodeHeight( tn.getHeight(),
                                                                   cornerHeight ) + "px" );
            RuleFlowRoundedPanel rp = new RuleFlowRoundedPanel( RoundedPanel.ALL,
                                                                RuleFlowBaseNode.CORNER_HEIGHT );

            rp.setTopAndBottomRowColor( "Black" );
            rp.setCornerStyleName( node.getStyle() );

            rp.add( panel );
            node.add( rp );

        } else if ( node.getCorners() == Corners.ROUND ) {
            int cornerHeight = LayoutCalculator.calculateRoundCornerHeight( tn.getWidth() );
            panel.setHeight( "1px" );

            RuleFlowRoundPanel rp = new RuleFlowRoundPanel( cornerHeight );

            rp.setTopAndBottomRowColor( "Black" );
            rp.setCornerStyleName( node.getStyle() );

            rp.add( panel );
            node.add( rp );

        } else {
            panel.setHeight( tn.getHeight() + "px" );

            if ( node instanceof ForEachNode ) {

                // Add nodes that are in for each node 
                AbsolutePanel ap = new AbsolutePanel();

                ForEachNode fen = (ForEachNode) node;

                for ( RuleFlowBaseNode baseNode : fen.getNodes().values() ) {
                    ap.add( baseNode,
                            baseNode.getX(),
                            baseNode.getY() );
                }

                // Add connections that are in for each node
                for ( Connection c : fen.getConnections() ) {
                    c.appendTo( ap );
                }

                ap.add( panel );

                node.add( ap );

            } else {
                node.add( panel );
            }
        }
    }

    private static Widget createContentWithoutImage(String name,
                                                    RuleFlowBaseNode node,
                                                    int width) {
        Label label = new Label( name );

        label.setStyleName( node.getStyle() );

        label.setWidth( width + "px" );

        return label;
    }

    private static Widget createContentWithImage(String name,
                                                 RuleFlowBaseNode node,
                                                 int width) {
        Label label = new Label( name );

        Image image = new Image();
        image.setStyleName( RuleFlowBaseNode.IMAGE_STYLE );
        image.setUrl( node.getImagePath() );

        HorizontalPanel panel = new HorizontalPanel();

        panel.setStyleName( node.getStyle() );

        panel.add( image );
        panel.add( label );
        panel.setWidth( width + "px" );

        return panel;
    }

    protected static void fillIdAndCoordinates(RuleFlowBaseNode node,
                                               TransferNode tn) {

        node.addClickListener( node );

        node.setId( tn.getId() );
        node.setX( tn.getX() );
        node.setY( tn.getY() );
    }

    protected static class LayoutCalculator {
        /**
         * Subtracts the border height from nodes height.
         * 
         * @return node height
         */
        static int calculateNodeHeight(int height,
                                       int cornerHeight) {
            int newHeight = 0;

            if ( height < (2 * cornerHeight) ) {
                newHeight = height;
            } else {
                newHeight = height - (2 * cornerHeight);
            }

            return newHeight;
        }

        /**
         * Calculates the corner height. If the width is too short, adding the
         * corner divs go over the node edges.
         * 
         * @param width
         * @return The fixed corner height
         */
        static int calculateCornerHeight(int width) {
            int newWidth = 1;

            if ( width < RuleFlowBaseNode.CORNER_HEIGHT ) {
                newWidth = width - 4;
            } else if ( width <= 20 ) {
                newWidth = 6;
            } else {
                newWidth = RuleFlowBaseNode.CORNER_HEIGHT;
            }

            if ( newWidth <= 0 ) {
                return 1;
            } else {
                return newWidth;
            }
        }

        static int calculateRoundCornerHeight(int width) {
            if ( width < 44 ) {
                return width / 2 - 1;
            } else {
                return 16;
            }
        }
    }
}
