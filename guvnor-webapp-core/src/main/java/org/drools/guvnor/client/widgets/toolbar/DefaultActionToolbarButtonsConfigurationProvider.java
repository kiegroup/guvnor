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

import org.drools.guvnor.client.rpc.RuleAsset;

import static org.drools.guvnor.client.common.AssetFormats.*;

public class DefaultActionToolbarButtonsConfigurationProvider
        implements
        ActionToolbarButtonsConfigurationProvider {

    public static String[]  VALIDATING_FORMATS = new String[]{BUSINESS_RULE, DSL_TEMPLATE_RULE, DECISION_SPREADSHEET_XLS, DRL, ENUMERATION, DECISION_TABLE_GUIDED, DRL_MODEL, DSL, FUNCTION, RULE_TEMPLATE, SPRING_CONTEXT};
    private static String[] SOURCE_FORMATS     = new String[]{BUSINESS_RULE, DSL_TEMPLATE_RULE, DRL, DRL_MODEL, DECISION_SPREADSHEET_XLS, DECISION_TABLE_GUIDED, RULE_TEMPLATE, BPMN2_PROCESS, BPMN_PROCESS};

    public static String[] VERIFY_FORMATS = new String[]{BUSINESS_RULE, DECISION_SPREADSHEET_XLS, DRL, DECISION_TABLE_GUIDED, DRL_MODEL, RULE_TEMPLATE};

    private RuleAsset       asset;

    public DefaultActionToolbarButtonsConfigurationProvider(RuleAsset asset) {
        this.asset = asset;
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

    public boolean showRenameButton() {
        return true;
    }

    public boolean showPromoteToGlobalButton() {
        return true;
    }

    public boolean showArchiveButton() {
        return asset.getVersionNumber() != 0;
    }

    public boolean showDeleteButton() {
        return asset.getVersionNumber() == 0;
    }

    public boolean showChangeStatusButton() {
        return true;
    }

    public boolean showSelectWorkingSetsButton() {
        return BUSINESS_RULE.equals(asset.getFormat()) || RULE_TEMPLATE.equals(asset.getFormat());
    }

    public boolean showValidateButton() {
        return this.isValidatorTypeAsset();
    }

    public boolean showVerifyButton() {
        return this.isVerificationTypeAsset();
    }

    public boolean showViewSourceButton() {
        return isMemberOfFormats( asset.getFormat(),
                                  SOURCE_FORMATS );
    }

    public boolean showStateLabel() {
        return true;
    }

    private boolean isValidatorTypeAsset() {
        return isMemberOfFormats( asset.getFormat(),
                                  VALIDATING_FORMATS );
    }

    private boolean isVerificationTypeAsset() {
        return isMemberOfFormats( asset.getFormat(),
                                  VERIFY_FORMATS );
    }

    private boolean isMemberOfFormats(String format,
                                      String[] formats) {
        for ( String fmt : formats ) {
            if ( fmt.equals( format ) ) {
                return true;
            }
        }
        return false;
    }

}
