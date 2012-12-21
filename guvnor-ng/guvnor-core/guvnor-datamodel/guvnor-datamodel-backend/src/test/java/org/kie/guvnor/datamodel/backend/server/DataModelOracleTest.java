package org.kie.guvnor.datamodel.backend.server;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.oracle.DataType;

import static org.junit.Assert.*;

/**
 * Tests for the DefaultDataModel
 */
public class DataModelOracleTest {

    @Test
    @Ignore("Need to add loading Model from .class file")
    public void testDataTypes() {
        final DataModelOracle dmo = DataModelBuilder.newDataModelBuilder().build();
//        final DataModelOracle dmo = DataModelBuilder.newDataModelBuilder()
//                .addFact( TestDataTypes.class )
//                .end()
//                .build();

        assertEquals( DataType.TYPE_STRING,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldString" ) );
        assertEquals( DataType.TYPE_BOOLEAN,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldBooleanObject" ) );
        assertEquals( DataType.TYPE_DATE,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldDate" ) );
        assertEquals( DataType.TYPE_NUMERIC_BIGDECIMAL,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldNumeric" ) );
        assertEquals( DataType.TYPE_NUMERIC_BIGDECIMAL,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldBigDecimal" ) );
        assertEquals( DataType.TYPE_NUMERIC_BIGINTEGER,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldBigInteger" ) );
        assertEquals( DataType.TYPE_NUMERIC_BYTE,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldByteObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_DOUBLE,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldDoubleObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_FLOAT,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldFloatObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldIntegerObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_LONG,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldLongObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_SHORT,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldShortObject" ) );

        assertEquals( DataType.TYPE_BOOLEAN,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldBooleanPrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_BYTE,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldBytePrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_DOUBLE,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldDoublePrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_FLOAT,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldFloatPrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldIntegerPrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_LONG,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldLongPrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_SHORT,
                      dmo.getFieldType( "TestDataTypes",
                                        "fieldShortPrimitive" ) );

    }

}
