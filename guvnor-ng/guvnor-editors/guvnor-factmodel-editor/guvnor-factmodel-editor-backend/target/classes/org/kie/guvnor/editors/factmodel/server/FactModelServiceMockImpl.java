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

package org.kie.guvnor.editors.factmodel.server;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.guvnor.editors.factmodel.model.FactMetaModel;
import org.kie.guvnor.editors.factmodel.model.FactModelContent;
import org.kie.guvnor.editors.factmodel.model.FieldMetaModel;
import org.kie.guvnor.editors.factmodel.service.FactModelService;
import org.uberfire.backend.vfs.Path;

/**
 *
 */
@Service
@ApplicationScoped
public class FactModelServiceMockImpl
        implements FactModelService {

    @Override
    public FactModelContent loadContent( Path path ) {
        final FactModelContent result = new FactModelContent();

        final FactMetaModel model = new FactMetaModel();
        model.setName( "Applicant" );
        model.getFields().add( new FieldMetaModel( "age", "Integer" ) );
        model.getFields().add( new FieldMetaModel( "applicationDate", "Date" ) );
        model.getFields().add( new FieldMetaModel( "name", "String" ) );

        result.getCurrentTypes().add( model );

        return result;
    }

    @Override
    public void save( final Path path,
                      final List<FactMetaModel> factModels ) {

    }
}
