package org.kie.guvnor.datamodel.oracle;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Data-type recognized by the DataModelOracle
 */
@Portable
public class DataModelTypes {

    /**
     * These are the explicit types supported
     */
    public static final String TYPE_COLLECTION = "Collection";
    public static final String TYPE_COMPARABLE = "Comparable";
    public static final String TYPE_STRING = "String";
    public static final String TYPE_NUMERIC = "Numeric";
    public static final String TYPE_NUMERIC_BIGDECIMAL = "BigDecimal";
    public static final String TYPE_NUMERIC_BIGINTEGER = "BigInteger";
    public static final String TYPE_NUMERIC_BYTE = "Byte";
    public static final String TYPE_NUMERIC_DOUBLE = "Double";
    public static final String TYPE_NUMERIC_FLOAT = "Float";
    public static final String TYPE_NUMERIC_INTEGER = "Integer";
    public static final String TYPE_NUMERIC_LONG = "Long";
    public static final String TYPE_NUMERIC_SHORT = "Short";
    public static final String TYPE_BOOLEAN = "Boolean";
    public static final String TYPE_DATE = "Date";
    public static final String TYPE_OBJECT = "Object";                                                                                                                                                      // for all other unknown
    public static final String TYPE_FINAL_OBJECT = "FinalObject";                                                                                                                                                 // for all other unknown
    public static final String TYPE_THIS = "this";

}
