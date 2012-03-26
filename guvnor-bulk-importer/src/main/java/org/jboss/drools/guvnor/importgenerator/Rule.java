/*
 * Copyright 2010 JBoss Inc
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

package org.jboss.drools.guvnor.importgenerator;

import java.io.File;

/**
 * Represents an individual rule within a drl package file found in the file system
 */
public class Rule {
    private String ruleName;
    private String content;
    private File file;

    /**
     * Std constructor used within the PackageFile parser
     *
     * @param ruleName
     * @param content
     */
    public Rule(String ruleName, String content, File file) {
        this.ruleName = ruleName;
        this.content = content;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
