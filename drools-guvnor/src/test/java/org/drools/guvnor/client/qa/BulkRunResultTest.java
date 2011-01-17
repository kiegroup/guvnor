/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.qa;

import static org.mockito.Mockito.mock;

import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.user.client.Command;

/**
 * @author rikkola
 *
 */
public class BulkRunResultTest {

    private BulkRunResult     bulkRunResult;
    private BulkRunResultView displayMock;
    private BulkTestRunResult bulkTestRunResult;
    private EditItemEvent     editItemEvent;
    private Command           closeCommand;

    @Before
    public void setUp() {

        bulkTestRunResult = new BulkTestRunResult();

        displayMock = mock( BulkRunResultView.class );

        bulkRunResult = new BulkRunResult( bulkTestRunResult,
                                           displayMock );
        bulkRunResult.setEditItemEvent( editItemEvent );
        bulkRunResult.setCloseCommand( closeCommand );
    }

    @Test
    public void first() {
    }
}
