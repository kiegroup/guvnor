/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.ide.common.server.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.drools.core.util.DateUtils;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;

/**
 * Utilities to support Guided Decision Table operations
 */
public class GuidedDTDRLUtilities {

    /**
     * Utility method to convert DTCellValues to their String representation
     * 
     * @param dcv
     * @return
     */
    public static String convertDTCellValueToString(DTCellValue52 dcv) {
        switch ( dcv.getDataType() ) {
            case BOOLEAN :
                Boolean booleanValue = dcv.getBooleanValue();
                return (booleanValue == null ? null : booleanValue.toString());
            case DATE :
                Date dateValue = dcv.getDateValue();
                return (dateValue == null ? null : DateUtils.format( dcv.getDateValue() ));
            case NUMERIC :
                BigDecimal numericValue = dcv.getNumericValue();
                return (numericValue == null ? null : numericValue.toPlainString());
            case NUMERIC_BIGDECIMAL :
                BigDecimal bigDecimalValue = dcv.getBigDecimalValue();
                return (bigDecimalValue == null ? null : bigDecimalValue.toPlainString());
            case NUMERIC_BIGINTEGER :
                BigInteger bigIntegerValue = dcv.getBigIntegerValue();
                return (bigIntegerValue == null ? null : bigIntegerValue.toString());
            case NUMERIC_BYTE :
                Byte byteValue = dcv.getByteValue();
                return (byteValue == null ? null : byteValue.toString());
            case NUMERIC_DOUBLE :
                Double doubleValue = dcv.getDoubleValue();
                return (doubleValue == null ? null : doubleValue.toString());
            case NUMERIC_FLOAT :
                Float floatValue = dcv.getFloatValue();
                return (floatValue == null ? null : floatValue.toString());
            case NUMERIC_INTEGER :
                Integer integerValue = dcv.getIntegerValue();
                return (integerValue == null ? null : integerValue.toString());
            case NUMERIC_LONG :
                Long longValue = dcv.getLongValue();
                return (longValue == null ? null : longValue.toString());
            case NUMERIC_SHORT :
                Short shortValue = dcv.getShortValue();
                return (shortValue == null ? null : shortValue.toString());
            default :
                return dcv.getStringValue();
        }
    }

}
