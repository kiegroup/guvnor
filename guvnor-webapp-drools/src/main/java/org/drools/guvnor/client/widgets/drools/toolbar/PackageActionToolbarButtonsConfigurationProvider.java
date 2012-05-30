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

package org.drools.guvnor.client.widgets.drools.toolbar;

import org.drools.guvnor.client.widgets.toolbar.ActionToolbarButtonsConfigurationProvider;

public class PackageActionToolbarButtonsConfigurationProvider
        implements
        ActionToolbarButtonsConfigurationProvider {

    public PackageActionToolbarButtonsConfigurationProvider() {
    }

    public boolean showSaveButton() {
        return true;
    }

    public boolean showSaveAndCloseButton() {
        return false;
    }

    public boolean showCopyButton() {
        return true;
    }
    
    public boolean showRenameButton() {
        return true;
    }
    
    public boolean showPromoteToGlobalButton() {
        return false;
    }

    public boolean showArchiveButton() {
        return true;
    }

    public boolean showDeleteButton() {
        return false;
    }

    public boolean showChangeStatusButton() {
        return true;
    }

    public boolean showSelectWorkingSetsButton() {
        return false;
    }

    public boolean showValidateButton() {
    	return false;
    }

    public boolean showVerifyButton() {
    	return false;
    }

    public boolean showViewSourceButton() {
        return true;
    }

    public boolean showStateLabel() {
        return true;
    }
}
