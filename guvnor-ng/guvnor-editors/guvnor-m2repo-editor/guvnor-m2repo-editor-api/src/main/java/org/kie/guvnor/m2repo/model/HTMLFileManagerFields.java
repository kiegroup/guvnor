/*
 * Copyright 2005 JBoss Inc
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

package org.kie.guvnor.m2repo.model;

/**
 * This is a central location for all form fields use in HTML forms for submission to the file servlet.
 * The all must be unique, of course.
 */
public class HTMLFileManagerFields {

    public static final String FORM_FIELD_REPOSITORY = "exportWholeRepository";
    public static final String FORM_FIELD_PATH = "attachmentPath";
    public static final String FILE_UPLOAD_FIELD_NAME_IMPORT = "importFile";
    public static final String UPLOAD_FIELD_NAME_ATTACH = "fileUploadElement";
    public static final String CLASSIC_DRL_IMPORT = "classicDRLFile";
    public static final String REPO_CONFIG_REPOSITORY = "exportRepositoryConfig";
    public static final String GROUP_ID = "groupId";   
    public static final String ARTIFACT_ID = "artifactId";   
    public static final String VERSION_ID = "versionId";   
   
}
