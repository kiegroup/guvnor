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
package org.guvnor.common.services.shared.builder;

import java.util.List;

import org.uberfire.backend.vfs.Path;

/**
 * Helper to provide validation of assets that are not validated by KIE, or need additional validation
 */
public interface BuildValidationHelper {

    /**
     * Does this helper support the specified path
     * @param path
     * @return
     */
    boolean accepts( final Path path );

    /**
     * Validate the content at the specified Path
     * @param path
     * @return
     */
    List<BuildMessage> validate( final Path path );

}
