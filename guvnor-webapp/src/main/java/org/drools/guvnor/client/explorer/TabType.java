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

package org.drools.guvnor.client.explorer;

public enum TabType {

    // Browse
    FIND_ID,
    CATEGORY_ROOT_ID,
    CATEGORY_ID,
    STATES_ID,
    STATES_ROOT_ID,
//    RECENT_EDITED_ID,
//    RECENT_VIEWED_ID,
//    INCOMING_ID,

    // QA
    TEST_SCENARIOS_ID,
    TEST_SCENARIOS_ROOT_ID,
    ANALYSIS_ID,
    ANALYSIS_ROOT_ID,

    // Table configurations
    RULE_LIST_TABLE_ID,
    PACKAGEVIEW_LIST_TABLE_ID,
    ARCHIVED_RULE_LIST_TABLE_ID,

    // Package snapshot
    PACKAGE_SNAPSHOTS

}
