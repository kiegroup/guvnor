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

import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SplitNode extends RuleFlowBaseNode {

    SplitTransferNode.Type                type;

    public Map<ConnectionRef, Constraint> constraints;

    @Override
    public Corners getCorners() {
        return Corners.ROUND;
    }

    @Override
    public String getImagePath() {
        return null;
    }

    @Override
    public String getStyle() {
        return BLUE_RULE_FLOW_NODE_STYLE;
    }

    /**
     * Show parameters when clicked.
     */
    public void onClick(Widget arg0) {

        if ( parametersForm != null ) {

            parametersForm.clear();

            // Add Type:
            parametersForm.addAttribute( "Type",
                                         new Label( type.toString() ) );

            for ( final ConnectionRef connectionRef : constraints.keySet() ) {

                final Constraint constraint = constraints.get( connectionRef );

                final TextBox priorityTextBox = new TextBox();
                priorityTextBox.setWidth( "30px" );
                priorityTextBox.setText( constraint.getPriority() + "" );

                priorityTextBox.addFocusListener( new FocusListener() {
                    public void onFocus(Widget arg1) {
                        priorityTextBox.selectAll();
                    }

                    public void onLostFocus(Widget arg1) {

                        final Constraint constraint = constraints.get( connectionRef );
                        constraint.setPriority( Integer.parseInt( priorityTextBox.getText() ) );
                        constraints.put( connectionRef,
                                         constraint );
                    }
                } );

                final TextBox constraintTextBox = new TextBox();
                constraintTextBox.setWidth( "300px" );
                constraintTextBox.setText( constraint.getConstraint() );

                constraintTextBox.addFocusListener( new FocusListener() {
                    public void onFocus(Widget arg1) {
                        constraintTextBox.selectAll();
                    }

                    public void onLostFocus(Widget arg1) {

                        final Constraint constraint = constraints.get( connectionRef );
                        constraint.setConstraint( constraintTextBox.getText() );
                        constraints.put( connectionRef,
                                         constraint );
                    }
                } );

                Panel panel = new HorizontalPanel();
                panel.add( new Label( " Priority: " ) );
                panel.add( priorityTextBox );
                panel.add( new Label( " Value: " ) );
                panel.add( constraintTextBox );

                parametersForm.addAttribute( constraint.getName(),
                                             panel );
            }
        }
    }

    public static class Constraint
        implements
        IsSerializable {

        private String constraint;
        private String dialect;
        private int    priority;
        private String name;
        private String type;

        public String getConstraint() {
            return this.constraint;
        }

        public String getDialect() {
            return this.dialect;
        }

        public String getName() {
            return this.name;
        }

        public int getPriority() {
            return this.priority;
        }

        public String getType() {
            return this.type;
        }

        public void setConstraint(String constraint) {
            this.constraint = constraint;
        }

        public void setDialect(String dialect) {
            this.dialect = dialect;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

    public static class ConnectionRef
        implements
        IsSerializable {

        private String toType;
        private long   nodeId;

        public void setToType(String toType) {
            this.toType = toType;
        }

        public void setNodeId(long nodeId) {
            this.nodeId = nodeId;
        }

        public String getToType() {
            return toType;
        }

        public long getNodeId() {
            return nodeId;
        }

        public boolean equals(Object o) {
            if ( o instanceof ConnectionRef ) {
                ConnectionRef c = (ConnectionRef) o;
                return toType.equals( c.toType ) && nodeId == c.nodeId;
            }
            return false;
        }

        public int hashCode() {
            return 7 * toType.hashCode() + (int) nodeId;
        }

    }
}
