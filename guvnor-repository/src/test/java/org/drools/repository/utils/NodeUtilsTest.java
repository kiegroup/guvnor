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
package org.drools.repository.utils;

import static org.junit.Assert.assertEquals;

import org.drools.repository.utils.NodeUtils;
import org.junit.Test;

public class NodeUtilsTest {

    @Test
    public void testNodePathConversion1() {
        final String assetName = "one/two";
        final String pathName = NodeUtils.makeJSR170ComplaintName( assetName );
        assertEquals( "one_two",
                      pathName );
    }

    @Test
    public void testNodePathConversion2() {
        final String assetName = "one:two";
        final String pathName = NodeUtils.makeJSR170ComplaintName( assetName );
        assertEquals( "one_two",
                      pathName );
    }

    @Test
    public void testNodePathConversion3() {
        final String assetName = "one*two";
        final String pathName = NodeUtils.makeJSR170ComplaintName( assetName );
        assertEquals( "one_two",
                      pathName );
    }

    @Test
    public void testNodePathConversion4() {
        final String assetName = "one[two";
        final String pathName = NodeUtils.makeJSR170ComplaintName( assetName );
        assertEquals( "one_two",
                      pathName );
    }

    @Test
    public void testNodePathConversion5() {
        final String assetName = "one]two";
        final String pathName = NodeUtils.makeJSR170ComplaintName( assetName );
        assertEquals( "one_two",
                      pathName );
    }

    @Test
    public void testNodePathConversion6() {
        final String assetName = "one'two";
        final String pathName = NodeUtils.makeJSR170ComplaintName( assetName );
        assertEquals( "one_two",
                      pathName );
    }

    @Test
    public void testNodePathConversion7() {
        final String assetName = "one\"two";
        final String pathName = NodeUtils.makeJSR170ComplaintName( assetName );
        assertEquals( "one_two",
                      pathName );
    }

    @Test
    public void testNodePathConversion8() {
        final String assetName = "one|two";
        final String pathName = NodeUtils.makeJSR170ComplaintName( assetName );
        assertEquals( "one_two",
                      pathName );
    }

    @Test
    public void testNodePathConversion9() {
        final String assetName = "one two";
        final String pathName = NodeUtils.makeJSR170ComplaintName( assetName );
        assertEquals( "one two",
                      pathName );
    }

}
