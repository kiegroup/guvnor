package org.drools.brms.client.common;

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
    public static final String BUSINESS_RULE = "brxml";

    
    /** use a DSL, free text editor */
    public static final String DSL_TEMPLATE_RULE   = "brule";

    
    /** Use a decision table.*/
    public static final String DECISION_SPREADSHEET_XLS = "xls";

    /** Use a ruleflow.*/
    public static final String RULE_FLOW_RF = "rf";
    
    
    /**
     * The following group the assets together for lists, helpers etc... 
     */
    public static final String[] BUSINESS_RULE_FORMATS = new String[] {AssetFormats.BUSINESS_RULE, AssetFormats.DSL_TEMPLATE_RULE, AssetFormats.DECISION_SPREADSHEET_XLS};
    public static final String[] TECHNICAL_RULE_FORMATS = new String[] {AssetFormats.DRL, AssetFormats.RULE_FLOW_RF};
    
    /**
     * These define assets that are really package level "things" 
     */
    private static final String[] PACKAGE_DEPENCENCIES = new String[] {AssetFormats.FUNCTION, AssetFormats.DSL, AssetFormats.MODEL};

    
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
