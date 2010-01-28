package org.drools.guvnor.client.explorer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Map that uses a collection of Strings as a key.
 * 
 * @author trikkola
 *
 */
public class MultiKeyMap<T> extends HashMap<Set<String>, T> {
    private static final long serialVersionUID = -3028001095889963235L;

    public boolean containsKey(String key) {

        for ( Set<String> keys : keySet() ) {
            for ( String string : keys ) {
            
                if(string.equals( key )){
                    continue;
                }
            }
            
            if ( keys.contains( key ) ) {
                return true;
            }
        }

        return false;
    }

    public T get(String key) {
        for ( Set<String> keys : keySet() ) {
            if ( keys.contains( key ) ) {
                return super.get( keys );
            }
        }

        return null;
    }

    public T put(final String key,
                 T value) {
        return put( new HashSet<String>() {
                        private static final long serialVersionUID = 6639904539329507948L;
                        {
                            add( key );
                        }
                    },
                    value );
    }

    public T put(String[] key,
                 T value) {
        return put( new HashSet<String>( Arrays.asList( key ) ),
                    value );
    }

    public T remove(String[] key) {
        return remove( new HashSet<String>( Arrays.asList( key ) ) );
    }

    public T remove(Object key) {
        for ( Set<String> keys : keySet() ) {
            if ( keys.equals( key ) || keys.contains( key ) ) {
                return super.remove( keys );
            }
        }

        return null;
    }

}
