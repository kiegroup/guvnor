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

package org.kie.guvnor.testscenario.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.kie.guvnor.testscenario.model.Scenario;
import org.kie.guvnor.testscenario.service.ScenarioTestEditorService;
import org.kie.guvnor.testscenario.service.TestService;
import org.kie.guvnor.testscenario.type.TestScenarioResourceTypeDefinition;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class TestServiceImpl
        implements
        TestService {

    @Inject
    private TestScenarioResourceTypeDefinition tsType;

    @Inject
    private ScenarioTestEditorService          editorService;

    @Override
    public void run(Path resource,
                    RunListener listener) {
        // in the future we need to add support to run java unit tests as well
        if ( tsType.accept( resource ) ) {
            try {
                // execute the test scenario
                Scenario scenario = editorService.load( resource );
                ScenarioRunner4JUnit runner = new ScenarioRunner4JUnit( scenario );
                JUnitCore junit = new JUnitCore();
                junit.addListener( listener );
                junit.run( runner );
            } catch ( Exception e ) {
                reportUnrecoverableError( "Error running scenario " + resource.getFileName(),
                                          listener,
                                          e );
            }
        } else {
            reportUnrecoverableError( "Unknown resource type. Unable to execute as a test: " + resource.getFileName(),
                                      listener,
                                      new IllegalArgumentException( "Unknown resource type" ) );
        }
    }

    private void reportUnrecoverableError(String message,
                                          RunListener listener,
                                          Exception e) {
        try {
            Description description = Description.createSuiteDescription( message );
            listener.testFailure( new Failure( description,
                                               e ) );
        } catch ( Exception e2 ) {
            // intentionally left empty as there is nothing to do
        }
    }

}
