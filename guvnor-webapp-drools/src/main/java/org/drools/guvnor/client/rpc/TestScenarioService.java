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
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.repository.ModuleItem;

public interface TestScenarioService
        extends
        RemoteService {

    /**
     * @param packageName The package name the scenario is to be run in.
     * @param scenario    The scenario to run.
     * @return The scenario, with the results fields populated.
     * @throws com.google.gwt.user.client.rpc.SerializationException
     */
    public SingleScenarioResult runScenario(String packageName,
                                            Scenario scenario) throws SerializationException;

    /**
     * This should be pretty obvious what it does !
     */
    public BulkTestRunResult runScenariosInPackage(String packageUUID) throws SerializationException;

}
