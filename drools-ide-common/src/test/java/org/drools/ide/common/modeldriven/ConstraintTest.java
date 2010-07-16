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

import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;

public class ConstraintTest extends TestCase {

    public void testAdd() {
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.addNewConnective();

        assertEquals( 1,
                      con.connectives.length );
        assertNotNull( con.connectives[0] );

        con.addNewConnective();

        assertEquals( 2,
                      con.connectives.length );
        assertNotNull( con.connectives[1] );

    }

}
