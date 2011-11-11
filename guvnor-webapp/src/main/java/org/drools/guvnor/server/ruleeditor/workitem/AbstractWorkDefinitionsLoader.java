/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.server.ruleeditor.workitem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.WorkDefinition;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.datatype.impl.type.BooleanDataType;
import org.drools.process.core.datatype.impl.type.EnumDataType;
import org.drools.process.core.datatype.impl.type.FloatDataType;
import org.drools.process.core.datatype.impl.type.IntegerDataType;
import org.drools.process.core.datatype.impl.type.ListDataType;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.drools.process.core.datatype.impl.type.UndefinedDataType;
import org.drools.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

/**
 * Class to load Work Definitions
 */
public abstract class AbstractWorkDefinitionsLoader
    implements
    WorkDefinitionsLoader {

    protected Map<String, WorkDefinition> workDefinitions = null;

    protected static final String         NEW_LINE        = System.getProperty( "line.separator" );

    /**
     * Load the Work Definitions from whatever resource
     * 
     * @return A List of Strings containing individual Work Definitions
     */
    public abstract List<String> loadWorkDefinitions() throws Exception;

    /**
     * Get the collection of Work Definitions
     * 
     * @returnException
     */
    public Map<String, WorkDefinition> getWorkDefinitions() throws Exception {
        if ( this.workDefinitions == null ) {
            this.workDefinitions = new HashMap<String, WorkDefinition>();
            List<String> workDefinitions = loadWorkDefinitions();
            populateWorkDefinitions( workDefinitions );
        }
        return this.workDefinitions;
    }

    //Parse String into WorkDefinitions
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Map<String, WorkDefinition> populateWorkDefinitions(List<String> workItemDefinitions) {

        //Add Data-type imports, in-case they are missing from definition
        ParserContext context = new ParserContext();
        context.addImport( "ObjectDataType",
                           ObjectDataType.class );
        context.addImport( "StringDataType",
                           StringDataType.class );
        context.addImport( "IntegerDataType",
                           IntegerDataType.class );
        context.addImport( "FloatDataType",
                           FloatDataType.class );
        context.addImport( "BooleanDataType",
                           BooleanDataType.class );
        context.addImport( "ListDataType",
                           ListDataType.class );
        context.addImport( "EnumDataType",
                           EnumDataType.class );
        context.addImport( "UndefinedDataType",
                           UndefinedDataType.class );

        //Compile expression and convert String
        for ( String workItemDefinition : workItemDefinitions ) {

            Serializable compiled = MVEL.compileExpression( workItemDefinition,
                                                            context );
            List<Map<String, Object>> workDefinitionsMap = (List<Map<String, Object>>) MVEL.executeExpression( compiled,
                                                                                                               new HashMap() );

            //Populate model
            if ( workDefinitionsMap != null ) {
                for ( Map<String, Object> workDefinitionMap : workDefinitionsMap ) {

                    if ( workDefinitionMap != null ) {
                        WorkDefinitionImpl workDefinition = new WorkDefinitionImpl();
                        workDefinition.setName( (String) workDefinitionMap.get( "name" ) );
                        workDefinition.setDisplayName( (String) workDefinitionMap.get( "displayName" ) );
                        workDefinition.setIcon( (String) workDefinitionMap.get( "icon" ) );
                        workDefinition.setCustomEditor( (String) workDefinitionMap.get( "customEditor" ) );
                        Set<ParameterDefinition> parameters = new HashSet<ParameterDefinition>();
                        if ( workDefinitionMap.get( "parameters" ) != null ) {
                            Map<String, DataType> parameterMap = (Map<String, DataType>) workDefinitionMap.get( "parameters" );
                            if ( parameterMap != null ) {
                                for ( Map.Entry<String, DataType> entry : parameterMap.entrySet() ) {
                                    parameters.add( new ParameterDefinitionImpl( entry.getKey(),
                                                                                 entry.getValue() ) );
                                }
                            }
                            workDefinition.setParameters( parameters );
                        }

                        if ( workDefinitionMap.get( "results" ) != null ) {
                            Set<ParameterDefinition> results = new HashSet<ParameterDefinition>();
                            Map<String, DataType> resultMap = (Map<String, DataType>) workDefinitionMap.get( "results" );
                            if ( resultMap != null ) {
                                for ( Map.Entry<String, DataType> entry : resultMap.entrySet() ) {
                                    results.add( new ParameterDefinitionImpl( entry.getKey(),
                                                                              entry.getValue() ) );
                                }
                            }
                            workDefinition.setResults( results );
                        }
                        if ( workDefinitionMap.get( "defaultHandler" ) != null ) {
                            workDefinition.setDefaultHandler( (String) workDefinitionMap.get( "defaultHandler" ) );
                        }
                        if ( workDefinitionMap.get( "dependencies" ) != null ) {
                            workDefinition.setDependencies( ((List<String>) workDefinitionMap.get( "dependencies" )).toArray( new String[0] ) );
                        }
                        workDefinitions.put( workDefinition.getName(),
                                             workDefinition );
                    }
                }
            }
        }
        return workDefinitions;
    }

}
