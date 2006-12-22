package org.drools.brms.server.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.brms.client.rpc.MetaData;
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

    private Map readMappings;
    private Map writeMappings;
    
    
    public void copyFromMetaData(MetaData data, Object target) {
        if ( this.writeMappings == null ) {
            this.writeMappings = loadWriteMappings( data,
                                              target.getClass() );
        }
        
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
        if ( this.readMappings == null ) {
            this.readMappings = loadReadMappings( data,
                                              source.getClass() );
        }

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
