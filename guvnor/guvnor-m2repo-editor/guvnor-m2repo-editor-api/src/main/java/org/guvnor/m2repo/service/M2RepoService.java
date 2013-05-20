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

package org.guvnor.m2repo.service;

import org.guvnor.m2repo.model.JarListPageRow;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.services.project.service.model.GAV;
import org.uberfire.client.tables.PageRequest;
import org.uberfire.client.tables.PageResponse;

@Remote
public interface M2RepoService {

    public String getJarName( String path );

    public void deleteJar( String[] path );

    public String loadPOMStringFromJar( String path );

    public GAV loadGAVFromJar( String path );

    public PageResponse<JarListPageRow> listJars( PageRequest pageRequest,
                                                  String filters );

    public String getRepositoryURL( String baseURL );
}
