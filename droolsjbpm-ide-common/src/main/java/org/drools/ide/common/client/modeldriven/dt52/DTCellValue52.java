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
    private BigDecimal        valueNumeric;
    private BigDecimal        valueBigDecimal;
    private BigInteger        valueBigInteger;
    private Byte              valueByte;
    private Double            valueDouble;
    private Float             valueFloat;
    private Integer           valueInteger;
    private Long              valueLong;
    private Short             valueShort;
    private String            valueString;
    private DTDataTypes52     dataType;

    //Does this cell represent "all other values" to those explicitly defined for the column
    private boolean           isOtherwise;

    public DTCellValue52() {
    }

    public DTCellValue52(DTCellValue52 sourceCell) {
        switch ( sourceCell.getDataType() ) {
            case BOOLEAN :
                setBooleanValue( sourceCell.getBooleanValue() );
                break;
            case DATE :
                setDateValue( sourceCell.getDateValue() );
                break;
            case NUMERIC :
                setNumericValue( sourceCell.getNumericValue() );
                break;
            case NUMERIC_BIGDECIMAL :
                setBigDecimalValue( sourceCell.getBigDecimalValue() );
                break;
            case NUMERIC_BIGINTEGER :
                setBigIntegerValue( sourceCell.getBigIntegerValue() );
                break;
            case NUMERIC_BYTE :
                setByteValue( sourceCell.getByteValue() );
                break;
            case NUMERIC_DOUBLE :
                setDoubleValue( sourceCell.getDoubleValue() );
                break;
            case NUMERIC_FLOAT :
                setFloatValue( sourceCell.getFloatValue() );
                break;
            case NUMERIC_INTEGER :
                setIntegerValue( sourceCell.getIntegerValue() );
                break;
            case NUMERIC_LONG :
                setLongValue( sourceCell.getLongValue() );
                break;
            case NUMERIC_SHORT :
                setShortValue( sourceCell.getShortValue() );
                break;
            default :
                setStringValue( sourceCell.getStringValue() );
        }
    }

    public DTCellValue52(DTDataTypes52 type) {
        switch ( type ) {
            case BOOLEAN :
                setBooleanValue( false );
                break;
            case DATE :
                setDateValue( new Date() );
                break;
            case NUMERIC :
                setNumericValue( new BigDecimal( "0" ) );
                break;
            case NUMERIC_BIGDECIMAL :
                setBigDecimalValue( new BigDecimal( "0" ) );
                break;
            case NUMERIC_BIGINTEGER :
                setBigIntegerValue( new BigInteger( "0" ) );
                break;
            case NUMERIC_BYTE :
                setByteValue( new Byte( "0" ) );
                break;
            case NUMERIC_DOUBLE :
                setDoubleValue( 0.0d );
                break;
            case NUMERIC_FLOAT :
                setFloatValue( 0.0f );
                break;
            case NUMERIC_INTEGER :
                setIntegerValue( 0 );
                break;
            case NUMERIC_LONG :
                setLongValue( 0l );
                break;
            case NUMERIC_SHORT :
                setShortValue( new Short( "0" ) );
                break;
            default :
                setStringValue( "" );
        }
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
            setBigDecimalValue( (BigDecimal) value );
            return;
        }
        if ( value instanceof BigInteger ) {
            setBigIntegerValue( (BigInteger) value );
            return;
        }
        if ( value instanceof Byte ) {
            setByteValue( (Byte) value );
            return;
        }
        if ( value instanceof Double ) {
            setDoubleValue( (Double) value );
            return;
        }
        if ( value instanceof Float ) {
            setFloatValue( (Float) value );
            return;
        }
        if ( value instanceof Integer ) {
            setIntegerValue( (Integer) value );
            return;
        }
        if ( value instanceof Long ) {
            setLongValue( (Long) value );
            return;
        }
        if ( value instanceof Short ) {
            setShortValue( (Short) value );
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

    public BigDecimal getBigDecimalValue() {
        return this.valueBigDecimal;
    }

    public BigInteger getBigIntegerValue() {
        return this.valueBigInteger;
    }

    public Byte getByteValue() {
        return this.valueByte;
    }

    public Double getDoubleValue() {
        return this.valueDouble;
    }

    public Float getFloatValue() {
        return this.valueFloat;
    }

    public Integer getIntegerValue() {
        return this.valueInteger;
    }

    public Long getLongValue() {
        return this.valueLong;
    }

    public Short getShortValue() {
        return this.valueShort;
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

    public void setBigDecimalValue(BigDecimal value) {
        clearValues();
        this.valueBigDecimal = value;
        this.dataType = DTDataTypes52.NUMERIC_BIGDECIMAL;
    }

    public void setBigIntegerValue(BigInteger value) {
        clearValues();
        this.valueBigInteger = value;
        this.dataType = DTDataTypes52.NUMERIC_BIGINTEGER;
    }

    public void setByteValue(Byte value) {
        clearValues();
        this.valueByte = value;
        this.dataType = DTDataTypes52.NUMERIC_BYTE;
    }

    public void setDoubleValue(Double value) {
        clearValues();
        this.valueDouble = value;
        this.dataType = DTDataTypes52.NUMERIC_DOUBLE;
    }

    public void setFloatValue(Float value) {
        clearValues();
        this.valueFloat = value;
        this.dataType = DTDataTypes52.NUMERIC_FLOAT;
    }

    public void setIntegerValue(Integer value) {
        clearValues();
        this.valueInteger = value;
        this.dataType = DTDataTypes52.NUMERIC_INTEGER;
    }

    public void setLongValue(Long value) {
        clearValues();
        this.valueLong = value;
        this.dataType = DTDataTypes52.NUMERIC_LONG;
    }

    public void setShortValue(Short value) {
        clearValues();
        this.valueShort = value;
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

    private void clearValues() {
        this.valueBoolean = null;
        this.valueDate = null;
        this.valueBigDecimal = null;
        this.valueBigInteger = null;
        this.valueByte = null;
        this.valueDouble = null;
        this.valueFloat = null;
        this.valueInteger = null;
        this.valueLong = null;
        this.valueShort = null;
        this.valueString = null;
        this.dataType = null;
    }

    public boolean hasValue() {
        return valueBoolean != null
               || valueDate != null
               || valueBigDecimal != null
               || valueBigInteger != null
               || valueByte != null
               || valueDouble != null
               || valueFloat != null
               || valueInteger != null
               || valueLong != null
               || valueShort != null
               || valueString != null
               || isOtherwise;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash + 31 * (valueBoolean == null ? 0 : valueBoolean.hashCode());
        hash = hash + 31 * (valueDate == null ? 0 : valueDate.hashCode());
        hash = hash + 31 * (valueNumeric == null ? 0 : valueNumeric.hashCode());
        hash = hash + 31 * (valueBigDecimal == null ? 0 : valueBigDecimal.hashCode());
        hash = hash + 31 * (valueBigInteger == null ? 0 : valueBigInteger.hashCode());
        hash = hash + 31 * (valueByte == null ? 0 : valueByte.hashCode());
        hash = hash + 31 * (valueDouble == null ? 0 : valueDouble.hashCode());
        hash = hash + 31 * (valueFloat == null ? 0 : valueFloat.hashCode());
        hash = hash + 31 * (valueInteger == null ? 0 : valueInteger.hashCode());
        hash = hash + 31 * (valueLong == null ? 0 : valueLong.hashCode());
        hash = hash + 31 * (valueShort == null ? 0 : valueShort.hashCode());
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
        if ( valueBigDecimal != null ? !valueBigDecimal.equals( that.valueBigDecimal ) : that.valueBigDecimal != null ) return false;
        if ( valueBigInteger != null ? !valueBigInteger.equals( that.valueBigInteger ) : that.valueBigInteger != null ) return false;
        if ( valueByte != null ? !valueByte.equals( that.valueByte ) : that.valueByte != null ) return false;
        if ( valueDouble != null ? !valueDouble.equals( that.valueDouble ) : that.valueDouble != null ) return false;
        if ( valueFloat != null ? !valueFloat.equals( that.valueFloat ) : that.valueFloat != null ) return false;
        if ( valueInteger != null ? !valueInteger.equals( that.valueInteger ) : that.valueInteger != null ) return false;
        if ( valueLong != null ? !valueLong.equals( that.valueLong ) : that.valueLong != null ) return false;
        if ( valueShort != null ? !valueShort.equals( that.valueShort ) : that.valueShort != null ) return false;
        if ( valueString != null ? !valueString.equals( that.valueString ) : that.valueString != null ) return false;
        if ( !dataType.equals( that.dataType ) ) return false;
        return true;
    }

}
