/*
 * Copyright 2014 JBoss Inc
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

package org.guvnor.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;

import org.guvnor.common.services.shared.test.TestService;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class TestServiceImpl
        implements TestService {

    @Override
    public void runAllTests(Path path) {

    }

    @Override
    public void runAllTests(Path path, Event<TestResultMessage> customTestResultEvent) {

    }
}
