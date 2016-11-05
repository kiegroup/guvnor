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

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.config.Config;

/*
 * Base implementation for the Fluent PipelineBuilder Factory. 
 */
public final class PipelineFactory {

    private PipelineFactory() {

    }

    public static <INPUT extends Config, OUTPUT extends Config> PipelineBuilder<INPUT, OUTPUT> startFrom( final Stage<INPUT, OUTPUT> stage ) {
        return new PipelineBuilder<INPUT, OUTPUT>() {
            private final List<Stage> stages = new ArrayList<>();

            {
                stages.add( stage );
            }

            @Override
            public <T extends Config> PipelineBuilder<INPUT, T> andThen( final Stage<? super OUTPUT, T> nextStep ) {
                stages.add( nextStep );
                return ( PipelineBuilder<INPUT, T> ) this;
            }

            @Override
            public Pipeline buildAs( final String name ) {
                return new BasePipeline( name, stages );
            }

            @Override
            public Pipeline build( final PipelineConfig config ) {
                stages.clear();
                for ( final Config c : config.getConfigStages() ) {
                    stages.add( StageUtil.config( c.toString(), f -> c ) );
                }
                return new BasePipeline( config.getName(), stages, config );
            }

        };
    }

}
