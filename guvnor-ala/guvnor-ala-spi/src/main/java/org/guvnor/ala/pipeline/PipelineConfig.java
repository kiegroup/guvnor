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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import org.guvnor.ala.config.Config;

/*
 * Represent the configuration for a Pipeline containing each stage detailed configuration.
*/
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface PipelineConfig {

    /*
     * Get the pipeline name
     * @return the name of the pipeline that will be created based on this configuration.
    */
    String getName();

    /*
     * Get the list of configurations, each representing a Stage
     * @return List<Config> for all the stages in the pipeline that will 
     *   be built using this configuration
    */
    List<Config> getConfigStages();
}
