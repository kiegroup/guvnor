/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.structure.backend.config;

import java.util.Iterator;
 
import org.guvnor.structure.server.config.ConfigType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private org.guvnor.structure.repositories.Repository systemRepository;

    @InjectMocks
    private ConfigurationServiceImpl configurationService = new ConfigurationServiceImpl();

    @Test
    public void onlyFirstRunCallsIOService() throws
            Exception {

        final DirectoryStream directoryStream = mock(DirectoryStream.class);

        when(directoryStream.iterator()).thenReturn(mock(Iterator.class));
        when(ioService.newDirectoryStream(any(Path.class),
                                          any(DirectoryStream.Filter.class))).thenReturn(directoryStream);

        configurationService.getConfiguration(ConfigType.EDITOR);

        verify(ioService,
               times(1)).newDirectoryStream(any(Path.class),
                                            any(DirectoryStream.Filter.class));

        reset(ioService);

        configurationService.getConfiguration(ConfigType.EDITOR);

        verify(ioService,
               never()).newDirectoryStream(any(Path.class),
                                            any(DirectoryStream.Filter.class));
    }
}