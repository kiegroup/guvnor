/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.common;

import com.google.gwt.resources.client.ImageResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StackItemHeaderTest {

    private StackItemHeader stackItemHeader;
    private StackItemHeaderView view;

    @Before
    public void setUp() throws Exception {
        view = mock(StackItemHeaderView.class);
        stackItemHeader = new StackItemHeader(view);
    }

    @Test
    public void testSetValues() throws Exception {
        stackItemHeader.setName("test");
        ImageResource imageResource = mock(ImageResource.class);
        stackItemHeader.setImageResource(imageResource);

        verify(view).setText("test");
        verify(view).setImageResource(imageResource);
    }
}
