/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.asseteditor.drools.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: raymondefa
 * Date: 6/19/12
 * Time: 9:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class EnumRowTest {

    @Test
    public void testEmpty() throws Exception {
        EnumRow enumRow = new EnumRow("");
        assertEquals("", enumRow.getFactName());
        assertEquals("", enumRow.getFieldName());
        assertEquals("", enumRow.getContext());
        assertEquals("", enumRow.getText());
    }

    @Test
    public void testInputOutPut() throws Exception {
        EnumRow enumRow = new EnumRow("'Applicant.creditRating': ['AA', 'OK', 'Sub prime']");


        assertEquals("Applicant", enumRow.getFactName());
        assertEquals("creditRating", enumRow.getFieldName());
        assertEquals("['AA', 'OK', 'Sub prime']", enumRow.getContext());
        assertEquals("'Applicant.creditRating': ['AA', 'OK', 'Sub prime']", enumRow.getText());
    }

    @Test
    public void testInputOutput2() throws Exception {

        EnumRow enumRow = new EnumRow("'Person.age': ['22', '23', '24']");

        assertEquals("Person", enumRow.getFactName());
        assertEquals("age", enumRow.getFieldName());
        assertEquals("['22', '23', '24']", enumRow.getContext());
        assertEquals("'Person.age': ['22', '23', '24']", enumRow.getText());
    }

    @Test
    public void testModify() throws Exception {

        EnumRow enumRow = new EnumRow("'Person.age': ['22', '23', '24']");

        enumRow.setFactName("Address");
        enumRow.setFieldName("street");
        enumRow.setContext("['carrotstreet', 'mystreet', 'bananastreet']");

        assertEquals("Address", enumRow.getFactName());
        assertEquals("street", enumRow.getFieldName());
        assertEquals("['carrotstreet', 'mystreet', 'bananastreet']", enumRow.getContext());
        assertEquals("'Address.street': ['carrotstreet', 'mystreet', 'bananastreet']", enumRow.getText());
    }
}
