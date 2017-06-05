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
package org.guvnor.ala.build.maven.config;

import org.guvnor.ala.config.BinaryConfig;

/*
 * Maven specific Project builds execution configuration. This interface represents the basic information needed
 *  to execute the build of a Maven Project. 
 * @see BinaryConfig
 */
public interface MavenBuildExecConfig extends BinaryConfig {

    String CAPTURE_ERRORS = "captureErrors";

    /**
     * Indicates if the maven build errors should be captured. When the errors are captured the pipeline will always
     * finish and the consumer will have the chance to look at the results to see if the build execution was successful.
     *
     * @return true if the error capture mode is enabled, false in any other case.
     */
    default String captureErrors() { return "${input." + CAPTURE_ERRORS + "}"; }
}