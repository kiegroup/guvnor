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

import org.drools.guvnor.client.common.FormStyleLayout;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

abstract class RuleFlowBaseNode extends FocusPanel
    implements
    ClickListener {

    protected enum Corners {
        NONE, ROUNDED, ROUND
    }

    protected static final int    CORNER_HEIGHT               = 8;
    protected static final int    CORNER_ROUND_HEIGHT         = 16;

    protected static final String IMAGE_STYLE                 = "ruleflow-image";
    protected static final String WHITE_RULE_FLOW_NODE_STYLE  = "white-ruleflow-node";
    protected static final String YELLOW_RULE_FLOW_NODE_STYLE = "yellow-ruleflow-node";
    protected static final String BLUE_RULE_FLOW_NODE_STYLE   = "blue-ruleflow-node";

    protected static final String PROCESS_START_GIF           = "images/ruleflow/process_start.gif";
    protected static final String PROCESS_STOP_GIF            = "images/ruleflow/process_stop.gif";
    protected static final String HUMAN_TASK_GIF              = "images/ruleflow/human_task.gif";
    protected static final String IMPORT_STATEMENT_GIF        = "images/ruleflow/import_statement.gif";
    protected static final String ACTION_GIF                  = "images/ruleflow/action.gif";
    protected static final String PROCESS_GIF                 = "images/ruleflow/process.gif";
    protected static final String QUESTION_GIF                = "images/ruleflow/question.gif";
    protected static final String TIMER_GIF                   = "images/ruleflow/timer.gif";

    private long                  id;
    private int                   x;
    private int                   y;

    protected FormStyleLayout     parametersForm              = null;

    public abstract String getImagePath();

    public abstract String getStyle();

    public abstract Corners getCorners();

    /**
     * Clear parameters when clicked.
     */
    public void onClick(Widget arg0) {

        if ( parametersForm != null ) {

            parametersForm.clear();
        }
    }

    public void addParametersForm(FormStyleLayout parametersForm) {
        this.parametersForm = parametersForm;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }
}
