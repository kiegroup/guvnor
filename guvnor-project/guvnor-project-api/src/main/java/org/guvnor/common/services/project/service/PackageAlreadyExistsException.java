/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.service;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.java.nio.file.FileSystemException;

/**
 * Exception for when a new package request fails; due to it already existing. FileAlreadyExistsException cannot
 * be used as this is presented to the User as "File already exists"... which is not technically correct for packages
 */
@Portable
public class PackageAlreadyExistsException extends FileSystemException {

    public PackageAlreadyExistsException() {
        super();
    }

    public PackageAlreadyExistsException(String file) {
        super(file);
    }

    public PackageAlreadyExistsException(String file,
                                         String other,
                                         String reason) {
        super(file,
              other,
              reason);
    }
}
