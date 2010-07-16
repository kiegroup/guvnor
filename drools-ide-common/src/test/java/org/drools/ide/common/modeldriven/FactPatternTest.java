/**
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

import junit.framework.TestCase;

import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;

public class FactPatternTest extends TestCase {

    public void testAddConstraint() {
        final FactPattern p = new FactPattern();
        final SingleFieldConstraint x = new SingleFieldConstraint( "x" );
        p.addConstraint( x );

        assertEquals( 1,
                      p.constraintList.constraints.length );
        assertEquals( x,
                      p.constraintList.constraints[0] );

        final SingleFieldConstraint y = new SingleFieldConstraint( "y" );

        p.addConstraint( y );
        assertEquals( 2,
                      p.constraintList.constraints.length );
        assertEquals( x,
                      p.constraintList.constraints[0] );
        assertEquals( y,
                      p.constraintList.constraints[1] );

    }
    
    public void testWithCompositeNesting() {
        final FactPattern p = new FactPattern();
        final SingleFieldConstraint x = new SingleFieldConstraint( "x" );
        p.addConstraint( x );

        assertEquals( 1,
                      p.constraintList.constraints.length );
        assertEquals( x,
                      p.constraintList.constraints[0] );

        final CompositeFieldConstraint y = new CompositeFieldConstraint();

        y.addConstraint( new SingleFieldConstraint("y") );
        y.addConstraint( new SingleFieldConstraint("z") );        
        p.addConstraint( y );
        
        assertEquals( 2,
                      p.constraintList.constraints.length );
        assertEquals( x,
                      p.constraintList.constraints[0] );
        assertEquals( y,
                      p.constraintList.constraints[1] );     
        
       
        
    }

    public void testRemoveConstraint() {
        final FactPattern p = new FactPattern();
        final SingleFieldConstraint x = new SingleFieldConstraint( "x" );
        p.addConstraint( x );
        final SingleFieldConstraint y = new SingleFieldConstraint( "y" );
        p.addConstraint( y );

        assertEquals( 2,
                      p.constraintList.constraints.length );

        p.removeConstraint( 1 );

        assertEquals( 1,
                      p.constraintList.constraints.length );

        assertEquals( x,
                      p.constraintList.constraints[0] );

        
        
    }
    
    public void testIsBound() {
        FactPattern pat = new FactPattern();
        pat.boundName = "x";
        assertTrue(pat.isBound());
        
        pat = new FactPattern();
        assertFalse(pat.isBound());
    }
    
    public void testGetFieldConstraints() {
        FactPattern pat = new FactPattern();
        assertEquals(0, pat.getFieldConstraints().length);
        assertNull(pat.constraintList);
        
        pat.addConstraint( new SingleFieldConstraint() );
        assertNotNull(pat.constraintList);
        assertEquals(1, pat.getFieldConstraints().length);
    }

}
