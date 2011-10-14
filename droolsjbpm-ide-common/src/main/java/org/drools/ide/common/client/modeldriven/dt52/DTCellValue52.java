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
package org.drools.ide.common.client.modeldriven.dt52;

import java.math.BigDecimal;
import java.util.Date;

import org.drools.ide.common.client.modeldriven.brl.PortableObject;

/**
 * Holder for cell value and other attributes. This is serialised by GWT RPC and
 * therefore does not contain a single property of type Serializable (that would
 * have been ideal). Instead the concrete data types are included separately.
 */
public class DTCellValue52
    implements
    PortableObject {

    private static final long serialVersionUID = -3547167997433925031L;

    //Type safe value of cell
    private Boolean           valueBoolean;
    private Date              valueDate;
    private BigDecimal        valueNumeric;
    private String            valueString;
    private DTDataTypes52     dataType;

    //Does this cell represent "all other values" to those explicitly defined for the column
    private boolean           isOtherwise;

    public DTCellValue52() {
    }

    public DTCellValue52(Object value) {
        if ( value instanceof String ) {
            setStringValue( (String) value );
            return;
        }
        if ( value instanceof Boolean ) {
            setBooleanValue( (Boolean) value );
            return;
        }
        if ( value instanceof Date ) {
            setDateValue( (Date) value );
            return;
        }
        if ( value instanceof BigDecimal ) {
            setNumericValue( (BigDecimal) value );
            return;
        }
        if ( value instanceof Double ) {
            setNumericValue( new BigDecimal( (Double) value ) );
            return;
        }
        if ( value instanceof Integer ) {
            setNumericValue( new BigDecimal( (Integer) value ) );
            return;
        }
        if ( value instanceof Long ) {
            setNumericValue( new BigDecimal( (Long) value ) );
            return;
        }
        setStringValue( null );
    }

    public DTCellValue52(BigDecimal value) {
        setNumericValue( value );
    }

    public DTCellValue52(Boolean value) {
        setBooleanValue( value );
    }

    public DTCellValue52(Date value) {
        setDateValue( value );
    }

    public DTCellValue52(String value) {
        setStringValue( value );
    }

    public Boolean getBooleanValue() {
        return valueBoolean;
    }

    public DTDataTypes52 getDataType() {
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
        this.dataType = DTDataTypes52.BOOLEAN;
    }

    public void setDateValue(Date value) {
        clearValues();
        this.valueDate = value;
        this.dataType = DTDataTypes52.DATE;
    }

    public void setNumericValue(BigDecimal value) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DTDataTypes52.NUMERIC;
    }

    public void setOtherwise(boolean isOtherwise) {
        this.isOtherwise = isOtherwise;
    }

    public void setStringValue(String value) {
        clearValues();
        this.valueString = value;
        this.dataType = DTDataTypes52.STRING;
    }

    private void clearValues() {
        this.valueBoolean = null;
        this.valueDate = null;
        this.valueNumeric = null;
        this.valueString = null;
    }

    public boolean hasValue() {
        return valueBoolean != null || valueDate != null || valueNumeric != null || valueString != null || isOtherwise;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash + 31 * (valueBoolean == null ? 0 : valueBoolean.hashCode());
        hash = hash + 31 * (valueDate == null ? 0 : valueDate.hashCode());
        hash = hash + 31 * (valueNumeric == null ? 0 : valueNumeric.hashCode());
        hash = hash + 31 * dataType.hashCode();
        return hash;
    }

    @Override
    //A clone of this class is used while editing a column. Overriding this method
    //allows us to easily compare the clone and the original to check if a change 
    //has been made
    public boolean equals(Object obj) {
        if ( !(obj instanceof DTCellValue52) ) {
            return false;
        }
        DTCellValue52 that = (DTCellValue52) obj;
        if ( valueBoolean != null ? !valueBoolean.equals( that.valueBoolean ) : that.valueBoolean != null ) return false;
        if ( valueDate != null ? !valueDate.equals( that.valueDate ) : that.valueDate != null ) return false;
        if ( valueNumeric != null ? !valueNumeric.equals( that.valueNumeric ) : that.valueNumeric != null ) return false;
        if ( valueString != null ? !valueString.equals( that.valueString ) : that.valueString != null ) return false;
        if ( !dataType.equals( that.dataType ) ) return false;
        return true;
    }

}
