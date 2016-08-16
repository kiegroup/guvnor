/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.pipeline;

import org.guvnor.ala.config.Config;

/*
 * Represents a generic Fluent PipelineBuilder factory
 * @param INPUT extends Config
 * @param OUTPUT extends Config
 */
public interface PipelineBuilder<INPUT extends Config, OUTPUT extends Config> {

    /*
     * This method allows you to add a new stage to the pipeline. 
     * @return the Fluent Builder
     */
    <T extends Config> PipelineBuilder<INPUT, T> andThen( final Stage<? super OUTPUT, T> nextStep );

    /*
     * This method builds the pipeline and return a new Pipeline instance.
     * @param String name for the pipeline to be built.
     * @return the constructed Pipeline
     */
    Pipeline buildAs( final String name );

    /*
     * This method builds the pipeline based on the provided PipelineConfig
     * @param PipelineConfig containing the pipeline configuration
     * @return the constructed Pipeline
     */
    Pipeline build( final PipelineConfig config );
}
