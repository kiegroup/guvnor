package org.kie.guvnor.datamodel.backend.server;

import java.io.IOException;

import org.junit.Test;
import org.kie.guvnor.datamodel.backend.server.testclasses.TestDataTypes;
import org.kie.guvnor.datamodel.backend.server.testclasses.TestDelegatedClass;
import org.kie.guvnor.datamodel.backend.server.testclasses.TestSubClass;
import org.kie.guvnor.datamodel.backend.server.testclasses.TestSuperClass;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.oracle.DataType;

import static org.junit.Assert.*;

/**
 * Tests for the DefaultDataModel
 */
public class DataModelOracleTest {

    @Test
    public void testDataTypes() throws IOException {
        final DataModelOracle dmo = DataModelBuilder.newDataModelBuilder()
                .addClass( TestDataTypes.class )
                .build();

        assertEquals( 1,
                      dmo.getFactTypes().length );
        assertEquals( "TestDataTypes",
                      dmo.getFactTypes()[ 0 ] );

        assertEquals( 20,
                      dmo.getFieldCompletions( "TestDataTypes" ).length );

        assertEquals( DataType.TYPE_THIS,
                      dmo.getFieldType( "TestDataTypes",
                                        "this" ) );
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

    @Test
    public void testSuperClass() throws IOException {
        final DataModelOracle dmo = DataModelBuilder.newDataModelBuilder()
                .addClass( TestSuperClass.class )
                .build();

        assertEquals( 1,
                      dmo.getFactTypes().length );
        assertEquals( "TestSuperClass",
                      dmo.getFactTypes()[ 0 ] );

        assertEquals( 2,
                      dmo.getFieldCompletions( "TestSuperClass" ).length );

        assertEquals( DataType.TYPE_THIS,
                      dmo.getFieldType( "TestSuperClass",
                                        "this" ) );
        assertEquals( DataType.TYPE_STRING,
                      dmo.getFieldType( "TestSuperClass",
                                        "field1" ) );
    }

    @Test
    public void testSubClass() throws IOException {
        final DataModelOracle dmo = DataModelBuilder.newDataModelBuilder()
                .addClass( TestSubClass.class )
                .build();

        assertEquals( 1,
                      dmo.getFactTypes().length );
        assertEquals( "TestSubClass",
                      dmo.getFactTypes()[ 0 ] );

        assertEquals( 3,
                      dmo.getFieldCompletions( "TestSubClass" ).length );

        assertEquals( DataType.TYPE_THIS,
                      dmo.getFieldType( "TestSubClass",
                                        "this" ) );
        assertEquals( DataType.TYPE_STRING,
                      dmo.getFieldType( "TestSubClass",
                                        "field1" ) );
        assertEquals( DataType.TYPE_STRING,
                      dmo.getFieldType( "TestSubClass",
                                        "field2" ) );
    }

    @Test
    public void testDelegatedClass() throws IOException {
        final DataModelOracle dmo = DataModelBuilder.newDataModelBuilder()
                .addClass( TestDelegatedClass.class )
                .build();

        assertEquals( 1,
                      dmo.getFactTypes().length );
        assertEquals( "TestDelegatedClass",
                      dmo.getFactTypes()[ 0 ] );

        assertEquals( 2,
                      dmo.getFieldCompletions( "TestDelegatedClass" ).length );

        assertEquals( DataType.TYPE_THIS,
                      dmo.getFieldType( "TestDelegatedClass",
                                        "this" ) );
        assertEquals( DataType.TYPE_STRING,
                      dmo.getFieldType( "TestDelegatedClass",
                                        "field1" ) );
    }

}
