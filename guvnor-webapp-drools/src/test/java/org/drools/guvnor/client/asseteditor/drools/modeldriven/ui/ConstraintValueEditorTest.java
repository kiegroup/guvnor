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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

import org.drools.ide.common.client.modeldriven.ui.ConstraintValueEditorHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConstraintValueEditorTest {

    @Test
    public void testSplit() {
        String[] res = ConstraintValueEditorHelper.splitValue( "M=Male" );
        assertEquals( "M",
                      res[0] );
        assertEquals( "Male",
                      res[1] );
    }

    @Test
    public void testSplitComplex1() {
        String[] res = ConstraintValueEditorHelper.splitValue( "a\\=5*2=expression" );
        assertEquals( "a=5*2",
                      res[0] );
        assertEquals( "expression",
                      res[1] );
    }

    @Test
    public void testSplitComplex2() {
        String[] res = ConstraintValueEditorHelper.splitValue( "\\=\\==equals" );
        assertEquals( "==",
                      res[0] );
        assertEquals( "equals",
                      res[1] );
    }

    @Test
    public void testSplitComplex3() {
        String[] res = ConstraintValueEditorHelper.splitValue( "\\=\\=" );
        assertEquals( "==",
                      res[0] );
        assertEquals( "==",
                      res[1] );
    }

    @Test
    public void testSplitComplex4() {
        String[] res = ConstraintValueEditorHelper.splitValue( "!\\=" );
        assertEquals( "!=",
                      res[0] );
        assertEquals( "!=",
                      res[1] );
    }
    
}
