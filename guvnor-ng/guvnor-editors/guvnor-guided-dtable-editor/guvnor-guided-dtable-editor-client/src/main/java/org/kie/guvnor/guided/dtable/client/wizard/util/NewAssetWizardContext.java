/*
 * Copyright 2011 JBoss Inc
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
package org.kie.guvnor.guided.dtable.client.wizard.util;

import org.kie.guvnor.commons.ui.client.wizards.WizardContext;
import org.uberfire.backend.vfs.Path;

/**
 * A container for the details required to create a new Asset on the repository
 */
public abstract class NewAssetWizardContext implements WizardContext {

    private final String baseFileName;
    private final Path contextPath;

    public NewAssetWizardContext( final String baseFileName,
                                  final Path contextPath ) {
        this.baseFileName = baseFileName;
        this.contextPath = contextPath;
    }

    public String getBaseFileName() {
        return baseFileName;
    }

    public Path getContextPath() {
        return contextPath;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash + 31 * ( baseFileName == null ? 0 : baseFileName.hashCode() );
        hash = hash + 31 * ( contextPath == null ? 0 : contextPath.hashCode() );
        return hash;
    }

    @Override
    public boolean equals( Object o ) {
        if ( !( o instanceof NewAssetWizardContext ) ) {
            return false;
        }
        NewAssetWizardContext that = (NewAssetWizardContext) o;

        if ( baseFileName != null ? !baseFileName.equals( that.baseFileName ) : that.baseFileName != null ) {
            return false;
        }
        if ( contextPath != null ? !contextPath.equals( that.contextPath ) : that.contextPath != null ) {
            return false;
        }
        return true;
    }

}
