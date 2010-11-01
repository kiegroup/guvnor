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

package org.drools.guvnor.client.ruleeditor.toolbar;

import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.client.security.CapabilitiesManager;
import org.drools.guvnor.client.modeldriven.ui.RuleModelEditor;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.rpc.RuleAsset;
import static org.drools.guvnor.client.common.AssetFormats.*;

/**
 *
 * @author esteban.aliverti
 */
public class DefaultActionToolbarButtonsConfigurationProvider implements ActionToolbarButtonsConfigurationProvider {

    private static String[] VALIDATING_FORMATS = new String[]{BUSINESS_RULE, DSL_TEMPLATE_RULE, DECISION_SPREADSHEET_XLS, DRL, ENUMERATION, DECISION_TABLE_GUIDED, DRL_MODEL, DSL, FUNCTION, RULE_TEMPLATE};
    private static String[] VERIFY_FORMATS = new String[]{BUSINESS_RULE, DECISION_SPREADSHEET_XLS, DRL, DECISION_TABLE_GUIDED, DRL_MODEL, RULE_TEMPLATE};
    
    private RuleAsset asset;
    private Widget editor;

    public DefaultActionToolbarButtonsConfigurationProvider(RuleAsset asset,Widget editor) {
        this.asset = asset;
        this.editor = editor;
    }
    
    public boolean showSaveButton() {
        return true;
    }

    public boolean showSaveAndCloseButton() {
        return true;
    }

    public boolean showCopyButton() {
        return true;
    }

    public boolean showPromoteToGlobalButton() {
        return true;
    }

    public boolean showArchiveButton() {
        return true;
    }

    public boolean showDeleteButton() {
        return true;
    }

    public boolean showChangeStatusButton() {
        return true;
    }

    public boolean showSelectWorkingSetsButton() {
        return this.isValidatorTypeAsset() && editor instanceof RuleModelEditor;
    }

    public boolean showValidateButton() {
        return this.isValidatorTypeAsset();
    }

    public boolean showVerifyButton() {
        return this.isValidatorTypeAsset() && this.isVerificationTypeAsset();
    }

    public boolean showViewSourceButton() {
        return shouldShowViewSource();
    }
    
    public boolean showStateLabel() {
        return true;
    }
    
    private boolean isValidatorTypeAsset() {
        String format = asset.metaData.format;
        for (String fmt : VALIDATING_FORMATS) {
            if (fmt.equals(format)) {
                return true;
            }
        }
        return false;
    }

    private boolean isVerificationTypeAsset() {
        String format = asset.metaData.format;
        for (String fmt : VERIFY_FORMATS) {
            if (fmt.equals(format)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean shouldShowViewSource() {
        return CapabilitiesManager.getInstance().shouldShow(Capabilities.SHOW_PACKAGE_VIEW);
    }


}
