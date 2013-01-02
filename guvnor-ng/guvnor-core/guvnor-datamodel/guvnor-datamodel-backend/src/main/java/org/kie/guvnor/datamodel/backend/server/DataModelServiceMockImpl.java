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

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.model.ModelAnnotation;
import org.kie.guvnor.datamodel.model.ModelField;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.oracle.DataType;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class DataModelServiceMockImpl
        implements DataModelService {

    @Override
    public DataModelOracle getDataModel( final Path project ) {
        return makeMockModel();
    }

    private DataModelOracle makeMockModel() {
        DataModelOracle oracle = DataModelBuilder.newDataModelBuilder()
                .addFact( "Driver" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "name",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "date",
                                           Date.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_DATE ) )
                .addField( new ModelField( "approved",
                                           Boolean.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_BOOLEAN ) )
                .end()
                .addFact( "Incident" )
                .addAnnotation( new ModelAnnotation( "role",
                                                     "value",
                                                     "event" ) )
                .addField( new ModelField( "dateOfIncident",
                                           Date.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_DATE ) )
                .addField( new ModelField( "typeOfIncident",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "claimAmount",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .end()
                .build();
        return oracle;
    }
}
