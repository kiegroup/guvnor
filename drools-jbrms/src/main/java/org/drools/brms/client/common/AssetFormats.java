package org.drools.brms.client.common;
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



/**
 * Keeps track of the different rule formats we support.
 * Each format type corresponds to the dublin core "format" attribute.
 *
 * This is used both by the UI, to determine what are valid formats, and also on the server.
 * If you are adding new types they need to be registered here.
 *
 * If an asset type is unknown, then it will be opened with the default editor.
 *
 *
 * @author Michael Neale
 *
 */
public class AssetFormats {

    /** For functions */
    public static final String FUNCTION = "function";

    /** For "model" assets */
    public static final String MODEL = "jar";

    /** For DSL language grammars */
    public static final String DSL = "dsl";

    /** Vanilla DRL "file" */
    public static final String DRL = "drl";

    /** Use the rule modeller */
    public static final String BUSINESS_RULE = "brl";


    /** use a DSL, free text editor */
    public static final String DSL_TEMPLATE_RULE   = "dslr";


    /** Use a decision table.*/
    public static final String DECISION_SPREADSHEET_XLS = "xls";

    /** Use a ruleflow.*/
    public static final String RULE_FLOW_RF = "rf";

    /** Use a data enum.*/
    public static final String ENUMERATION = "enumeration";

    /** For test scenarios.  */
    public static final String TEST_SCENARIO = "scenario";

    /**
     * The following group the assets together for lists, helpers etc...
     */
    public static final String[] BUSINESS_RULE_FORMATS = new String[] {AssetFormats.BUSINESS_RULE, AssetFormats.DSL_TEMPLATE_RULE, AssetFormats.DECISION_SPREADSHEET_XLS};
    public static final String[] TECHNICAL_RULE_FORMATS = new String[] {AssetFormats.DRL, AssetFormats.RULE_FLOW_RF, AssetFormats.ENUMERATION};

    /**
     * These define assets that are really package level "things"
     */
    private static final String[] PACKAGE_DEPENCENCIES = new String[] {AssetFormats.FUNCTION, AssetFormats.DSL, AssetFormats.MODEL, AssetFormats.ENUMERATION};


    /**
     * Will return true if the given asset format is a package dependency (eg a function, DSL, model etc).
     * Package dependencies are needed before the package is validated, and any rule assets are processed.
     */
    public static boolean isPackageDependency(String format) {
        for ( int i = 0; i < PACKAGE_DEPENCENCIES.length; i++ ) {
            if (PACKAGE_DEPENCENCIES[i].equals( format )) {
                return true;
            }
        }
        return false;
    }




}