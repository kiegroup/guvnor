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
    public static String DRL = "drl";
    
    /** Use the rule modeller */
    public static String BUSINESS_RULE = "brxml";

    
    /** use a DSL, free text editor */
    public static String DSL_TEMPLATE_RULE   = "brule";

    
    /** Use a decision table.*/
    public static String DECISION_SPREADSHEET_XLS = "xls";
    
    /**
     * The following group the 
     */
    public static String[] BUSINESS_RULE_FORMATS = new String[] {AssetFormats.BUSINESS_RULE, AssetFormats.DSL_TEMPLATE_RULE, AssetFormats.DECISION_SPREADSHEET_XLS};
    public static String[] TECHNICAL_RULE_FORMATS = new String[] {AssetFormats.DRL};
}
