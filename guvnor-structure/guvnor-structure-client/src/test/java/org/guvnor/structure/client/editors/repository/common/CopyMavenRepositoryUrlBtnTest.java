/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.client.editors.repository.common;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwtmockito.GwtMockito;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class CopyMavenRepositoryUrlBtnTest {

    private boolean isViewButton;

    private CopyRepositoryUrlBtn btn;

    @Parameterized.Parameters
    public static Collection<Boolean[]> caseSensitivity() {
        return Arrays.asList(new Boolean[][]{{true}, {false}});
    }

    public CopyMavenRepositoryUrlBtnTest(boolean isViewButton) {
        this.isViewButton = isViewButton;
    }

    @Before
    public void init() {
        GwtMockito.initMocks(this);
        btn = GWT.create(CopyRepositoryUrlBtn.class);

        doCallRealMethod().when(btn).init(anyBoolean(),
                                          anyString(),
                                          anyString());
    }

    @Test
    public void testButtonInit() {
        btn.init(isViewButton,
                 "abc",
                 "xyz");

        verify(btn,
               times(1)).setDataClipboardTargetAttribute("abc");
        verify(btn,
               times(1)).setDataClipboardTextAttribute("xyz");
        verify(btn,
               times(1)).setButtonAttribute(isViewButton,
                                            "abc");
        verify(btn,
               times(1)).setCopyRepositoryUrlTitle();
    }
}
