/*
 * Copyright 2013 JBoss Inc
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
package org.guvnor.common.services.backend.validation;

import java.io.InputStream;
import java.util.List;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.backend.vfs.Path;

/**
 * Validator capable of validating generic Kie assets (i.e those that are handled by KieBuilder)
 */
public interface GenericValidator {

    /**
     * Validate an asset. Validation needs to know the Project in which the resource resides to be able
     * to determine classes within the project's dependencies. The resourcePath is used to determine the
     * containing project. The resourcePath is also used to determine the destination Path in Kie VFS.
     * @param resourcePath The VFS Path of the resource
     * @param resource An InputStream containing the resource to be validated
     * @param supportingFileFilters An optional list of filters to add
     * supporting files for the validation (e.g. .dslr needs .dsl)
     * @return
     */
    List<ValidationMessage> validate( final Path resourcePath,
                                      final InputStream resource,
                                      final DirectoryStream.Filter<org.uberfire.java.nio.file.Path>... supportingFileFilters );

}
