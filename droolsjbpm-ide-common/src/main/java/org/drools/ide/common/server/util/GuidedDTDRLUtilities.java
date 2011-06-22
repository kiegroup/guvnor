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
                BigDecimal bdValue = dcv.getNumericValue();
                return (bdValue == null ? null : bdValue.toPlainString());
            default :
                return dcv.getStringValue();
        }
    }

}
