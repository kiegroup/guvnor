/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.client.provider.status.runtime;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.guvnor.ala.ui.model.PipelineStatus;
import org.guvnor.ala.ui.model.RuntimeStatus;

public class RuntimePresenterHelper {

    /**
     * Builds the list of css classes for configuring the runtime icon given the pipeline status.
     * @param status pipeline status for the calculation.
     * @return a list of css classes.
     */
    public static Collection<String> buildStyle(final PipelineStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }
        switch (status) {
            case FINISHED:
                return Arrays.asList("pficon",
                                     "list-view-pf-icon-md",
                                     "pficon-ok",
                                     "list-view-pf-icon-success");
            case SCHEDULED:
            case RUNNING:
                return Arrays.asList("fa",
                                     "list-view-pf-icon-md",
                                     "fa-circle-o-notch",
                                     "fa-spin");
            case ERROR:
                return Arrays.asList("pficon",
                                     "list-view-pf-icon-md",
                                     "pficon-error-circle-o",
                                     "list-view-pf-icon-danger");
        }

        return Collections.emptyList();
    }

    /**
     * Builds the list of css classes for configuring the runtime icon given the runtime status.
     * @param status runtime status for the calculation.
     * @return a list of css classes.
     */
    public static Collection<String> buildStyle(final RuntimeStatus status) {
        switch (status) {
            case STARTED:
                return Arrays.asList("pficon",
                                     "list-view-pf-icon-md",
                                     "pficon-ok",
                                     "list-view-pf-icon-success");
            case LOADING:
                return Arrays.asList("fa",
                                     "list-view-pf-icon-md",
                                     "fa-circle-o-notch",
                                     "fa-spin");
            case WARN:
                return Arrays.asList("pficon",
                                     "list-view-pf-icon-md",
                                     "pficon-warning-triangle-o",
                                     "list-view-pf-icon-warning");
            case STOPPED:
                return Arrays.asList("pficon",
                                     "list-view-pf-icon-md",
                                     "pficon-close",
                                     "list-view-pf-icon-info");
            case ERROR:
                return Arrays.asList("pficon",
                                     "list-view-pf-icon-md",
                                     "pficon-error-circle-o",
                                     "list-view-pf-icon-danger");
        }

        return Collections.emptyList();
    }
}
