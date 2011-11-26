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
package org.drools.guvnor.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface FlowImages
    extends
    ClientBundle {

    @Source("images/ruleflow/process_start.gif")
    ImageResource processStart();

    @Source("images/ruleflow/process_stop.gif")
    ImageResource processStop();

    @Source("images/ruleflow/human_task.gif")
    ImageResource humanTask();

    @Source("images/ruleflow/import_statement.gif")
    ImageResource importStatement();

    @Source("images/ruleflow/action.gif")
    ImageResource action();

    @Source("images/ruleflow/process.gif")
    ImageResource process();

    @Source("images/ruleflow/question.gif")
    ImageResource question();

    @Source("images/ruleflow/timer.gif")
    ImageResource timer();

    @Source("images/ruleflow/fault.gif")
    ImageResource fault();

    @Source("images/ruleflow/event.gif")
    ImageResource event();
}
