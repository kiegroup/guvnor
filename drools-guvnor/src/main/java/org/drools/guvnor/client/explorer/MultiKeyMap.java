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
public class MultiKeyMap<T> {
    private static final long       serialVersionUID = -3028001095889963235L;

    private HashMap<Set<String>, T> map              = new HashMap<Set<String>, T>();

    public boolean containsKey(String key) {

        for ( Set<String> keys : map.keySet() ) {
            for ( String string : keys ) {

                if ( string.equals( key ) ) {
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
        for ( Set<String> keys : map.keySet() ) {
            if ( keys.contains( key ) ) {
                return map.get( keys );
            }
        }

        return null;
    }

    public T put(final String key,
                 T value) {
        return map.put( new HashSet<String>() {
                            private static final long serialVersionUID = 6639904539329507948L;
                            {
                                add( key );
                            }
                        },
                        value );
    }

    public T put(String[] key,
                 T value) {
        return map.put( new HashSet<String>( Arrays.asList( key ) ),
                        value );
    }

    public T remove(String[] keys) {
        for ( String key : keys ) {
            T result = remove( key );
            if ( result != null ) {
                return result;
            }
        }

        return null;
    }

    public T remove(String key) {

        for ( Set<String> existingKeys : map.keySet() ) {
            if ( existingKeys.contains( key ) ) {
                return map.remove( existingKeys );
            }
        }

        return null;
    }

    public void clear() {
        map.clear();
    }

}
