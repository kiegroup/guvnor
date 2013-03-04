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

package org.kie.guvnor.datamodel.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.commons.service.builder.model.BuildResults;
import org.kie.guvnor.datamodel.backend.server.cache.BuildException;
import org.kie.guvnor.datamodel.backend.server.cache.LRUDataModelOracleCache;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class DataModelServiceImpl
        implements DataModelService {

    @Inject
    @Named("PackageDataModelOracleCache")
    private LRUDataModelOracleCache cachePackages;

    @Inject
    private ProjectService projectService;

    @Inject
    private Event<BuildResults> messagesEvent;

    @Override
    public DataModelOracle getDataModel( final Path resourcePath ) {
        PortablePreconditions.checkNotNull( "resourcePath",
                                            resourcePath );
        final Path projectPath = resolveProjectPath( resourcePath );
        final Path packagePath = resolvePackagePath( resourcePath );

        //Resource was not within a Project structure
        if ( projectPath == null ) {
            return new PackageDataModelOracle();
        }

        //Retrieve (or build) oracle
        DataModelOracle oracle = new PackageDataModelOracle();
        try {
            oracle = cachePackages.assertPackageDataModelOracle( projectPath,
                                                                 packagePath );
        } catch ( BuildException be ) {
            messagesEvent.fire( be.getResults() );
        }
        return oracle;
    }

    private Path resolveProjectPath( final Path resourcePath ) {
        return projectService.resolveProject( resourcePath );
    }

    private Path resolvePackagePath( final Path resourcePath ) {
        return projectService.resolvePackage( resourcePath );
    }

}
