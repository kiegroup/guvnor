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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;

public class CompositeFactPatternTest {

    @Test
    public void testAddPattern() {
        final CompositeFactPattern pat = new CompositeFactPattern();
        final FactPattern x = new FactPattern();
        pat.addFactPattern( x );
        assertEquals( 1,
                      pat.patterns.length );

        final FactPattern y = new FactPattern();
        pat.addFactPattern( y );
        assertEquals( 2,
                      pat.patterns.length );
        assertEquals( x,
                      pat.patterns[0] );
        assertEquals( y,
                      pat.patterns[1] );
    }

}
