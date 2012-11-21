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

package org.drools.guvnor.client.widgets.toolbar;

public class StandaloneEditorIndividualActionToolbarButtonsConfigurationProvider implements ActionToolbarButtonsConfigurationProvider {

    public boolean showSaveButton() {
        return false;
    }

    public boolean showSaveAndCloseButton() {
        return false;
    }

    public boolean showCopyButton() {
        return false;
    }
    
    public boolean showRenameButton() {
        return false;
    }
    
    public boolean showPromoteToGlobalButton() {
        return false;
    }

    public boolean showArchiveButton() {
        return false;
    }

    public boolean showDeleteButton() {
        return false;
    }

    public boolean showChangeStatusButton() {
        return false;
    }

    public boolean showSelectWorkingSetsButton() {
        return false;
    }

    public boolean showValidateButton() {
        return true;
    }

    public boolean showVerifyButton() {
        return true;
    }

    public boolean showViewSourceButton() {
        return true;
    }
    
    public boolean showStateLabel() {
        return false;
    }

}
