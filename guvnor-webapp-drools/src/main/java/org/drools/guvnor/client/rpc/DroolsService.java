/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;

public interface DroolsService
        extends
        RemoteService {

    /**
     * Creates a brand new Guided Decision Table rule with the initial category.
     * Return the UUID of the item created. This will not check in the rule, but
     * just leave it as saved in the repo.
     */
    public String createNewRule(NewGuidedDecisionTableAssetConfiguration configuration) throws SerializationException;

    /**
     * Validate module configuration
     *
     * @return A ValidatedReponse, with any errors to be reported. No payload is
     *         in the response. If there are any errors, the user should be
     *         given the option to review them, and correct them if needed (but
     *         a save will not be prevented this way - as its not an exception).
     */
    public ValidatedResponse validateModule(Module data) throws SerializationException;
}
