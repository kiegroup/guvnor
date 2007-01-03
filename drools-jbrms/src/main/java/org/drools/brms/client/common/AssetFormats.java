package org.drools.brms.client.common;

/**
 * Keeps track of the different rule formats we support.
 * Each format type corresponds to the dublin core "format" attribute.
 * @author Michael Neale
 *
 */
public class AssetFormats {

    /** Vanilla DRL "file" */
    public static String DRL = "drl";
    
    /** Use the rule modeller */
    public static String BUSINESS_RULE = "brxml";
    
    /** use vanilla text */
    public static String TECHNICAL_RULE    = "rule";
    
    /** use a DSL, not sure about this one - can use text or constrained editor */
    public static String DSL_TEMPLATE_RULE   = "trule";
    
    
    
}
