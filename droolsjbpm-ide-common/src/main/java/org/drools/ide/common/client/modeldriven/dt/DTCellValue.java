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
package org.drools.ide.common.client.modeldriven.dt;

import java.math.BigDecimal;
import java.util.Date;

import org.drools.ide.common.client.modeldriven.brl.PortableObject;

/**
 * Holder for cell value and other attributes. This is serialised by GWT RPC and
 * therefore does not contain a single property of type Serializable (that would
 * have been ideal). Instead the concrete data types are included separately.
 */
public class DTCellValue
    implements
    PortableObject {

    private static final long serialVersionUID = -3547167997433925031L;

    //Type safe value of cell
    private Boolean           valueBoolean;
    private Date              valueDate;
    private BigDecimal        valueNumeric;
    private String            valueString;
    private DTDataTypes       dataType;

    //Does this cell represent "all other values" to those explicitly defined for the column
    private boolean           isOtherwise;

    public DTCellValue() {
    }

    public DTCellValue(BigDecimal value) {
        setNumericValue( value );
    }

    public DTCellValue(Boolean value) {
        setBooleanValue( value );
    }

    public DTCellValue(Date value) {
        setDateValue( value );
    }

    public DTCellValue(double value) {
        setNumericValue( new BigDecimal( value ) );
    }

    public DTCellValue(int value) {
        setNumericValue( new BigDecimal( value ) );
    }

    public DTCellValue(long value) {
        setNumericValue( new BigDecimal( value ) );
    }

    public DTCellValue(String value) {
        setStringValue( value );
    }

    public Boolean getBooleanValue() {
        return valueBoolean;
    }

    public DTDataTypes getDataType() {
        return this.dataType;
    }

    public Date getDateValue() {
        return valueDate;
    }

    public BigDecimal getNumericValue() {
        return valueNumeric;
    }

    public String getStringValue() {
        return valueString;
    }

    public boolean isOtherwise() {
        return isOtherwise;
    }

    public void setBooleanValue(Boolean value) {
        clearValues();
        this.valueBoolean = value;
        this.dataType = DTDataTypes.BOOLEAN;
    }

    public void setDateValue(Date value) {
        clearValues();
        this.valueDate = value;
        this.dataType = DTDataTypes.DATE;
    }

    public void setNumericValue(BigDecimal value) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DTDataTypes.NUMERIC;
    }

    public void setOtherwise(boolean isOtherwise) {
        this.isOtherwise = isOtherwise;
    }

    public void setStringValue(String value) {
        clearValues();
        this.valueString = value;
        this.dataType = DTDataTypes.STRING;
    }

    private void clearValues() {
        this.valueBoolean = null;
        this.valueDate = null;
        this.valueNumeric = null;
        this.valueString = null;
    }

}