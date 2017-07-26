/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.config;

import org.guvnor.ala.config.RuntimeConfig;

/*
 * This interface represents the basic information that we need for running and interacting. 
 * With an running application inside a OpenShift Instance.
 * @see OpenShiftRuntimeConfiguration
 * @see RuntimeConfig
 */
public interface OpenShiftRuntimeExecConfig extends OpenShiftRuntimeConfig, RuntimeConfig {

}
