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
package org.drools.ide.common.modeldriven.dt;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern;
import org.junit.Test;

/**
 * 
 */
public class ConditionColsTest {

    @Test
    public void testAdd() {
        Pattern p = new Pattern();
        ConditionCol52 c = new ConditionCol52();
        c.setHeader( "c" );
        p.getConditions().add( c );

        assertEquals( 1,
                      p.getConditions().size() );

        assertEquals( p,
                      c.getPattern() );
    }

    @Test
    public void testAdd2() {
        Pattern p = new Pattern();
        ConditionCol52 c1 = new ConditionCol52();
        c1.setHeader( "c1" );
        ConditionCol52 c2 = new ConditionCol52();
        c2.setHeader( "c2" );
        p.getConditions().add( c1 );
        p.getConditions().add( c2 );

        assertEquals( 2,
                      p.getConditions().size() );

        assertEquals( p,
                      c1.getPattern() );
        assertEquals( p,
                      c2.getPattern() );
    }

    @Test
    public void testRemove() {
        Pattern p = new Pattern();
        ConditionCol52 c = new ConditionCol52();
        c.setHeader( "c" );
        p.getConditions().add( c );
        p.getConditions().remove( c );

        assertEquals( null,
                      c.getPattern() );
    }

    @Test
    public void testRemove2() {
        Pattern p = new Pattern();
        ConditionCol52 c1 = new ConditionCol52();
        c1.setHeader( "c1" );
        ConditionCol52 c2 = new ConditionCol52();
        c2.setHeader( "c2" );
        p.getConditions().add( c1 );
        p.getConditions().add( c2 );
        p.getConditions().remove( c2 );

        assertEquals( 1,
                      p.getConditions().size() );

        assertEquals( c1,
                      p.getConditions().get( 0 ) );

        assertEquals( p,
                      c1.getPattern() );
        assertEquals( null,
                      c2.getPattern() );
    }

    @Test
    public void testAddIndexed() {
        Pattern p = new Pattern();
        ConditionCol52 c = new ConditionCol52();
        c.setHeader( "c" );
        p.getConditions().add( 0,
                               c );

        assertEquals( p,
                      c.getPattern() );
    }

    @Test
    public void testRemoveIndexed() {
        Pattern p = new Pattern();
        ConditionCol52 c = new ConditionCol52();
        c.setHeader( "c" );
        p.getConditions().add( 0,
                               c );
        p.getConditions().remove( 0 );

        assertEquals( null,
                      c.getPattern() );
    }

    @Test
    public void testAddAll() {
        Pattern p = new Pattern();
        ConditionCol52 c1 = new ConditionCol52();
        c1.setHeader( "c1" );
        ConditionCol52 c2 = new ConditionCol52();
        c2.setHeader( "c2" );
        List<ConditionCol52> cs = new ArrayList<ConditionCol52>();
        cs.add( c1 );
        cs.add( c2 );

        p.getConditions().addAll( cs );

        assertEquals( p,
                      c1.getPattern() );
        assertEquals( p,
                      c2.getPattern() );
    }

    @Test
    public void testRemoveAll() {
        Pattern p = new Pattern();
        ConditionCol52 c1 = new ConditionCol52();
        c1.setHeader( "c1" );
        ConditionCol52 c2 = new ConditionCol52();
        c2.setHeader( "c2" );
        ConditionCol52 c3 = new ConditionCol52();
        c3.setHeader( "c3" );
        List<ConditionCol52> cs1 = new ArrayList<ConditionCol52>();
        cs1.add( c1 );
        cs1.add( c2 );
        cs1.add( c3 );

        List<ConditionCol52> cs2 = new ArrayList<ConditionCol52>();
        cs2.add( c1 );
        cs2.add( c2 );

        p.getConditions().addAll( cs1 );

        assertEquals( 3,
                      p.getConditions().size() );

        assertEquals( p,
                      c1.getPattern() );
        assertEquals( p,
                      c2.getPattern() );
        assertEquals( p,
                      c3.getPattern() );

        p.getConditions().removeAll( cs2 );

        assertEquals( 1,
                      p.getConditions().size() );

        assertEquals( null,
                      c1.getPattern() );
        assertEquals( null,
                      c2.getPattern() );
        assertEquals( p,
                      c3.getPattern() );
    }

}
