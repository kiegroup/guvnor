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
package org.guvnor.ala.openshift.model;

import org.guvnor.ala.runtime.base.BaseRuntimeState;

/**
 * OpenShift runtime state.
 */
public class OpenShiftRuntimeState extends BaseRuntimeState {

    /** Runtime is not available. */
    public static final String NA = "NA";

    /** Runtime is ready but not started. */
    public static final String READY = "Ready";

    /** Runtime has been started and can service requests. */
    public static final String STARTED = "Started";

    public OpenShiftRuntimeState() {
    }

    public OpenShiftRuntimeState(String state, String startedAt) {
        super(state, startedAt);
    }

}
