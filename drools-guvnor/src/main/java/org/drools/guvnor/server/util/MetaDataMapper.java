package org.drools.guvnor.server.util;
/*
 * Copyright 2005 JBoss Inc
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



import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.guvnor.client.rpc.MetaData;
import org.drools.repository.RulesRepositoryException;

/**
 * This utility uses reflection to map from the MetaData DTO to 
 * the AssetItem back end class, to adhere to the DRY principle.
 * 
 * AssetItem is not a remotable instance, but MetaData is.
 * 
 * @author Michael Neale
 */
public class MetaDataMapper {

    
    private Map writeMappingsForClass = new HashMap();

    private Map readMappipngsForClass = new HashMap();
    
    public void copyFromMetaData(MetaData data, Object target) {
        Map writeMappings = getWriteMappings( data,
                          target );
        
        for ( Iterator iter = writeMappings.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry e = (Map.Entry) iter.next();
            Field f = (Field) e.getKey();
            Method m = (Method) e.getValue();

            try {
                m.invoke( target, new Object[] {f.get( data )} ) ;
            } catch ( IllegalArgumentException e1 ) {
                throw new RulesRepositoryException(e1);
            } catch ( IllegalAccessException e1 ) {
                throw new RulesRepositoryException(e1);
            } catch ( InvocationTargetException e1 ) {
                throw new RulesRepositoryException(e1);
            }

        }        
        
    }

    private Map getWriteMappings(MetaData data,
                                  Object target) {
        if (!this.writeMappingsForClass.containsKey( target.getClass() )) {
            Map writeMappings = loadWriteMappings( data,
                                                    target.getClass() );
            writeMappingsForClass.put( target.getClass(), writeMappings );
        }
        return (Map) writeMappingsForClass.get( target.getClass() );
    }
    
    private Map loadWriteMappings(MetaData data,
                                  Class bean) {
        Map mappings = new HashMap();
        Field fields[] = data.getClass().getFields();
        for ( int i = 0; i < fields.length; i++ ) {
            Field f = fields[i];
            String old = f.getName();
            String name = Character.toUpperCase( old.charAt( 0 ) ) + old.substring( 1 );

            name = "update" + name;


            Method m;
            try {
                m = bean.getMethod( name, new Class[] {f.getType()} );
                mappings.put( f,
                                  m );
            } catch ( SecurityException e ) {
                throw new RulesRepositoryException( "Unable to map meta data",
                                                    e );
            } catch ( NoSuchMethodException e ) {
                //ignore
            }

        }
        return mappings;
    }

    public void copyToMetaData(MetaData data,
                               Object source) {
        Map readMappings = getReadMappings( data,
                         source );

        for ( Iterator iter = readMappings.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry e = (Map.Entry) iter.next();
            Field f = (Field) e.getKey();
            Method m = (Method) e.getValue();

            try {
                f.set( data, m.invoke( source, null ) );
            } catch ( IllegalArgumentException e1 ) {
                throw new RulesRepositoryException(e1);
            } catch ( IllegalAccessException e1 ) {
                throw new RulesRepositoryException(e1);
            } catch ( InvocationTargetException e1 ) {
                throw new RulesRepositoryException(e1);
            }

        }

    }

    private Map getReadMappings(MetaData data,
                                 Object source) {
        if (!this.readMappipngsForClass.containsKey( source.getClass() )) {
            this.readMappipngsForClass.put( source.getClass(), loadReadMappings( data,
                                                                                 source.getClass() ) );
        }
        return (Map) this.readMappipngsForClass.get( source.getClass() );
    }

    private Map loadReadMappings(MetaData data,
                            Class bean) {

        Map mappings = new HashMap();
        Field fields[] = data.getClass().getFields();
        for ( int i = 0; i < fields.length; i++ ) {
            Field f = fields[i];
            String old = f.getName();
            String name = Character.toUpperCase( old.charAt( 0 ) ) + old.substring( 1 );

            if ( f.getType() == Boolean.class ) {
                name = "is" + name;
            } else {
                name = "get" + name;
            }

            Method m;
            try {
                m = bean.getMethod( name, null );
                if (f.getType() == m.getReturnType())
                {
                    mappings.put( f,
                                  m );
                }
            } catch ( SecurityException e ) {
                throw new RulesRepositoryException( "Unable to map meta data",
                                                    e );
            } catch ( NoSuchMethodException e ) {
                //ignore
            }

        }
        return mappings;

    }

}