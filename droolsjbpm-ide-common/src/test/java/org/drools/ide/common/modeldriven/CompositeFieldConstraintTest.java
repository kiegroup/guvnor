/*
 * Copyright 2010 JBoss Inc
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

package org.drools.ide.common.modeldriven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.junit.Test;

public class CompositeFieldConstraintTest {

    @Test
    public void testCompositeType() {
        CompositeFieldConstraint con = new CompositeFieldConstraint();
        assertEquals(null, con.compositeJunctionType);
    }
    
    @Test
    public void testAddConstraint() {
        final CompositeFieldConstraint p = new CompositeFieldConstraint();
        final SingleFieldConstraint x = new SingleFieldConstraint( "x" );
        p.addConstraint( x );

        assertEquals( 1,
                      p.constraints.length );
        assertEquals( x,
                      p.constraints[0] );

        final SingleFieldConstraint y = new SingleFieldConstraint( "y" );

        p.addConstraint( y );
        assertEquals( 2,
                      p.constraints.length );
        assertEquals( x,
                      p.constraints[0] );
        assertEquals( y,
                      p.constraints[1] );

    }

    @Test
    public void testRemoveConstraint() {
        final CompositeFieldConstraint p = new CompositeFieldConstraint();
        final SingleFieldConstraint x = new SingleFieldConstraint( "x" );
        p.addConstraint( x );
        final CompositeFieldConstraint y = new CompositeFieldConstraint(  );
        p.addConstraint( y );

        assertEquals( 2,
                      p.constraints.length );

        p.removeConstraint( 1 );

        assertEquals( 1,
                      p.constraints.length );

        assertEquals( x,
                      p.constraints[0] );

    }
    
    @Test
    public void testRemoveConstraintWithNonNullParent() {
        final FactPattern fp = new FactPattern();
        final SingleFieldConstraint con1 = new SingleFieldConstraint( "parent" );
        fp.addConstraint( con1 );
        final SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setParent( con1 );
        fp.addConstraint( con2 );
        final SingleFieldConstraint con3 = new SingleFieldConstraint();
        con3.setParent( con2 );
        fp.addConstraint( con3 );

        assertEquals( 3,
                      fp.constraintList.constraints.length );

        fp.removeConstraint( 1 );

        assertEquals( 2,
                      fp.constraintList.constraints.length );

        assertEquals( con1,
                      fp.constraintList.constraints[0] );
        assertEquals( con3,
                      fp.constraintList.constraints[1] );
        assertNull( ((SingleFieldConstraint) fp.constraintList.constraints[0]).getParent() );
        assertEquals( con1,
                      ((SingleFieldConstraint) fp.constraintList.constraints[1]).getParent() );
    }
    
    @Test
    public void testRemoveConstraintWithNullParent() {
        final FactPattern fp = new FactPattern();
        final SingleFieldConstraint con1 = new SingleFieldConstraint( "parent" );
        fp.addConstraint( con1 );
        final SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setParent( con1 );
        fp.addConstraint( con2 );
        final SingleFieldConstraint con3 = new SingleFieldConstraint();
        con3.setParent( con2 );
        fp.addConstraint( con3 );

        assertEquals( 3,
                      fp.constraintList.constraints.length );

        fp.removeConstraint( 0 );

        assertEquals( 2,
                      fp.constraintList.constraints.length );

        assertEquals( con2,
                      fp.constraintList.constraints[0] );
        assertEquals( con3,
                      fp.constraintList.constraints[1] );
        assertNull( ((SingleFieldConstraint) fp.constraintList.constraints[0]).getParent() );
        assertEquals( con2,
                      ((SingleFieldConstraint) fp.constraintList.constraints[1]).getParent() );
    }

}
