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

package org.kie.guvnor.m2repo.backend.server;

import java.io.InputStream;

import org.kie.guvnor.m2repo.service.M2RepoService;
import org.kie.guvnor.project.model.GAV;

public interface ExtendedM2RepoService extends M2RepoService {

    public void deployJar( InputStream is,
                           GAV gav );

    public InputStream loadJar( String path );

}
