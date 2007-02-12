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
    public static final String MODEL = "model";

    public static final String DSL = "dsl";

    /** Vanilla DRL "file" */
    public static String DRL = "drl";
    
    /** Use the rule modeller */
    public static String BUSINESS_RULE = "brxml";
    
    /** use vanilla text */
    public static String TECHNICAL_RULE    = "rule";
    
    /** use a DSL, not sure about this one - can use text or constrained editor */
    public static String DSL_TEMPLATE_RULE   = "trule";
    
    
    
}
