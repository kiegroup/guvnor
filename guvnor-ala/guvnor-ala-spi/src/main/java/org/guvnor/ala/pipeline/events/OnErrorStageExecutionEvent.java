/*
 * Copyright 2016 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.pipeline.events;

import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.Stage;

/*
 * Event emitted by the PipelineExecutor in the case of an Error in a speicific execution
*/
public class OnErrorStageExecutionEvent implements PipelineEvent {

    private final Pipeline pipeline;
    private final Stage stage;
    private final Throwable error;

    public OnErrorStageExecutionEvent( final Pipeline pipeline,
                                       final Stage stage,
                                       final Throwable error ) {
        this.pipeline = pipeline;
        this.stage = stage;
        this.error = error;
    }

    public Stage getStage() {
        return stage;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public Throwable getError() {
        return error;
    }
}