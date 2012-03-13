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
import java.math.BigInteger;
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
    private Number            valueNumeric;
    private String            valueString      = "";
    private DTDataTypes52     dataType         = DTDataTypes52.STRING;

    //Does this cell represent "all other values" to those explicitly defined for the column
    private boolean           isOtherwise;

    public DTCellValue52() {
    }

    public DTCellValue52(DTCellValue52 sourceCell) {
        switch ( sourceCell.getDataType() ) {
            case BOOLEAN :
                setBooleanValue( sourceCell.getBooleanValue() );
                this.dataType = DTDataTypes52.BOOLEAN;
                break;
            case DATE :
                setDateValue( sourceCell.getDateValue() );
                this.dataType = DTDataTypes52.DATE;
                break;
            case NUMERIC :
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DTDataTypes52.NUMERIC;
                break;
            case NUMERIC_BIGDECIMAL :
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DTDataTypes52.NUMERIC_BIGDECIMAL;
                break;
            case NUMERIC_BIGINTEGER :
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DTDataTypes52.NUMERIC_BIGINTEGER;
                break;
            case NUMERIC_BYTE :
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DTDataTypes52.NUMERIC_BYTE;
                break;
            case NUMERIC_DOUBLE :
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DTDataTypes52.NUMERIC_DOUBLE;
                break;
            case NUMERIC_FLOAT :
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DTDataTypes52.NUMERIC_FLOAT;
                break;
            case NUMERIC_INTEGER :
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DTDataTypes52.NUMERIC_INTEGER;
                break;
            case NUMERIC_LONG :
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DTDataTypes52.NUMERIC_LONG;
                break;
            case NUMERIC_SHORT :
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DTDataTypes52.NUMERIC_SHORT;
                break;
            default :
                setStringValue( sourceCell.getStringValue() );
                this.dataType = DTDataTypes52.STRING;
        }
    }

    public DTCellValue52(DTDataTypes52 type) {
        switch ( type ) {
            case BOOLEAN :
                setBooleanValue( false );
                this.dataType = DTDataTypes52.BOOLEAN;
                break;
            case DATE :
                setDateValue( new Date() );
                this.dataType = DTDataTypes52.DATE;
                break;
            case NUMERIC :
                setNumericValue( new BigDecimal( "0" ) );
                this.dataType = DTDataTypes52.NUMERIC;
                break;
            case NUMERIC_BIGDECIMAL :
                setNumericValue( new BigDecimal( "0" ) );
                this.dataType = DTDataTypes52.NUMERIC_BIGDECIMAL;
                break;
            case NUMERIC_BIGINTEGER :
                setNumericValue( new BigInteger( "0" ) );
                this.dataType = DTDataTypes52.NUMERIC_BIGINTEGER;
                break;
            case NUMERIC_BYTE :
                setNumericValue( new Byte( "0" ) );
                this.dataType = DTDataTypes52.NUMERIC_BYTE;
                break;
            case NUMERIC_DOUBLE :
                setNumericValue( 0.0d );
                this.dataType = DTDataTypes52.NUMERIC_DOUBLE;
                break;
            case NUMERIC_FLOAT :
                setNumericValue( 0.0f );
                this.dataType = DTDataTypes52.NUMERIC_FLOAT;
                break;
            case NUMERIC_INTEGER :
                setNumericValue( new Integer( "0" ) );
                this.dataType = DTDataTypes52.NUMERIC_INTEGER;
                break;
            case NUMERIC_LONG :
                setNumericValue( 0l );
                this.dataType = DTDataTypes52.NUMERIC_LONG;
                break;
            case NUMERIC_SHORT :
                setNumericValue( new Short( "0" ) );
                this.dataType = DTDataTypes52.NUMERIC_SHORT;
                break;
            default :
                setStringValue( "" );
                this.dataType = DTDataTypes52.STRING;
        }
    }

    public DTCellValue52(Object value) {
        if ( value instanceof String ) {
            setStringValue( (String) value );
            this.dataType = DTDataTypes52.STRING;
            return;
        }
        if ( value instanceof Boolean ) {
            setBooleanValue( (Boolean) value );
            this.dataType = DTDataTypes52.BOOLEAN;
            return;
        }
        if ( value instanceof Date ) {
            setDateValue( (Date) value );
            this.dataType = DTDataTypes52.DATE;
            return;
        }
        if ( value instanceof BigDecimal ) {
            setNumericValue( (BigDecimal) value );
            this.dataType = DTDataTypes52.NUMERIC_BIGDECIMAL;
            return;
        }
        if ( value instanceof BigInteger ) {
            setNumericValue( (BigInteger) value );
            this.dataType = DTDataTypes52.NUMERIC_BIGINTEGER;
            return;
        }
        if ( value instanceof Byte ) {
            setNumericValue( (Byte) value );
            this.dataType = DTDataTypes52.NUMERIC_BYTE;
            return;
        }
        if ( value instanceof Double ) {
            setNumericValue( (Double) value );
            this.dataType = DTDataTypes52.NUMERIC_DOUBLE;
            return;
        }
        if ( value instanceof Float ) {
            setNumericValue( (Float) value );
            this.dataType = DTDataTypes52.NUMERIC_FLOAT;
            return;
        }
        if ( value instanceof Integer ) {
            setNumericValue( (Integer) value );
            this.dataType = DTDataTypes52.NUMERIC_INTEGER;
            return;
        }
        if ( value instanceof Long ) {
            setNumericValue( (Long) value );
            this.dataType = DTDataTypes52.NUMERIC_LONG;
            return;
        }
        if ( value instanceof Short ) {
            setNumericValue( (Short) value );
            this.dataType = DTDataTypes52.NUMERIC_SHORT;
            return;
        }
        setStringValue( null );
    }

    public DTCellValue52(BigDecimal value) {
        setNumericValue( value );
        this.dataType = DTDataTypes52.NUMERIC_BIGDECIMAL;
    }

    public DTCellValue52(BigInteger value) {
        setNumericValue( value );
        this.dataType = DTDataTypes52.NUMERIC_BIGINTEGER;
    }

    public DTCellValue52(Byte value) {
        setNumericValue( value );
        this.dataType = DTDataTypes52.NUMERIC_BYTE;
    }

    public DTCellValue52(Double value) {
        setNumericValue( value );
        this.dataType = DTDataTypes52.NUMERIC_DOUBLE;
    }

    public DTCellValue52(Float value) {
        setNumericValue( value );
        this.dataType = DTDataTypes52.NUMERIC_FLOAT;
    }

    public DTCellValue52(Integer value) {
        setNumericValue( value );
        this.dataType = DTDataTypes52.NUMERIC_INTEGER;
    }

    public DTCellValue52(Long value) {
        setNumericValue( value );
        this.dataType = DTDataTypes52.NUMERIC_LONG;
    }

    public DTCellValue52(Short value) {
        setNumericValue( value );
        this.dataType = DTDataTypes52.NUMERIC_SHORT;
    }

    public DTCellValue52(Boolean value) {
        setBooleanValue( value );
        this.dataType = DTDataTypes52.BOOLEAN;
    }

    public DTCellValue52(Date value) {
        setDateValue( value );
        this.dataType = DTDataTypes52.DATE;
    }

    public DTCellValue52(String value) {
        setStringValue( value );
        this.dataType = DTDataTypes52.STRING;
    }

    public DTDataTypes52 getDataType() {
        return this.dataType;
    }

    public Boolean getBooleanValue() {
        return valueBoolean;
    }

    public Date getDateValue() {
        return valueDate;
    }

    public Number getNumericValue() {
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

    public void setNumericValue(Number value) {
        clearValues();
        this.valueNumeric = value;
    }

    public void setNumericValue(BigDecimal value) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DTDataTypes52.NUMERIC_BIGDECIMAL;
    }

    public void setNumericValue(BigInteger value) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DTDataTypes52.NUMERIC_BIGINTEGER;
    }

    public void setNumericValue(Byte value) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DTDataTypes52.NUMERIC_BYTE;
    }

    public void setNumericValue(Double value) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DTDataTypes52.NUMERIC_DOUBLE;
    }

    public void setNumericValue(Float value) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DTDataTypes52.NUMERIC_FLOAT;
    }

    public void setNumericValue(Integer value) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DTDataTypes52.NUMERIC_INTEGER;
    }

    public void setNumericValue(Long value) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DTDataTypes52.NUMERIC_LONG;
    }

    public void setNumericValue(Short value) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DTDataTypes52.NUMERIC_SHORT;
    }

    public void setOtherwise(boolean isOtherwise) {
        this.isOtherwise = isOtherwise;
    }

    public void setStringValue(String value) {
        clearValues();
        this.valueString = value;
        this.dataType = DTDataTypes52.STRING;
    }

    public void clearValues() {
        this.valueBoolean = null;
        this.valueDate = null;
        this.valueNumeric = null;
        this.valueString = null;
        this.isOtherwise = false;
    }

    public boolean hasValue() {
        return valueBoolean != null
               || valueDate != null
               || valueNumeric != null
               || valueString != null
               || isOtherwise;
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
