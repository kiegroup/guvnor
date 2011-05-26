/*
 * Copyright 2011 JBoss Inc
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
package org.drools.ide.common.client.modeldriven.dt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * 
 */
public class ConditionCols extends ArrayList<ConditionCol52> {

    private Pattern pattern;

    public ConditionCols(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean add(ConditionCol52 col) {
        col.setPattern( this.pattern );
        return super.add( col );
    }

    @Override
    public void add(int index,
                    ConditionCol52 col) {
        col.setPattern( this.pattern );
        super.add( index,
                   col );
    }

    @Override
    public ConditionCol52 remove(int index) {
        ConditionCol52 col = super.remove( index );
        col.setPattern( null );
        return col;
    }

    @Override
    public boolean remove(Object o) {
        boolean wasRemoved = super.remove( o );
        if ( wasRemoved ) {
            ((ConditionCol52) o).setPattern( null );
        }
        return wasRemoved;
    }

    @Override
    public void clear() {
        for ( ConditionCol52 c : this ) {
            c.setPattern( null );
        }
        super.clear();
    }

    @Override
    public boolean addAll(Collection< ? extends ConditionCol52> c) {
        for ( ConditionCol52 cc : c ) {
            cc.setPattern( this.pattern );
        }
        return super.addAll( c );
    }

    @Override
    public boolean addAll(int index,
                          Collection< ? extends ConditionCol52> c) {
        for ( ConditionCol52 cc : c ) {
            cc.setPattern( this.pattern );
        }
        return super.addAll( index,
                             c );
    }

    @Override
    public boolean removeAll(Collection< ? > c) {
        Iterator< ? > e = c.iterator();
        while ( e.hasNext() ) {
            Object o = e.next();
            if ( this.contains( o ) ) {
                if ( o instanceof ConditionCol52 ) {
                    ((ConditionCol52) o).setPattern( null );
                }
            }
        }
        return super.removeAll( c );
    }

    @Override
    public boolean retainAll(Collection< ? > c) {
        // TODO Auto-generated method stub
        return super.retainAll( c );
    }

    @Override
    protected void removeRange(int fromIndex,
                               int toIndex) {
        for ( int i = fromIndex; i < toIndex; i++ ) {
            get( i ).setPattern( null );
        }
        super.removeRange( fromIndex,
                           toIndex );
    }

}
