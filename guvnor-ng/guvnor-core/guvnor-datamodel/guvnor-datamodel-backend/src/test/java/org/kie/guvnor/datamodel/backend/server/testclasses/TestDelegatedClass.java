package org.kie.guvnor.datamodel.backend.server.testclasses;

/**
 * Test class to check data-types are extracted correctly by ProjectDataModelOracleBuilder for subclasses and delegated classes
 */
public class TestDelegatedClass {

    private TestSuperClass superClass;

    public TestDelegatedClass( final TestSuperClass superClass ) {
        this.superClass = superClass;
    }

    public String getField1() {
        return superClass.getField1();
    }

    public void setField1( final String field1 ) {
        this.superClass.setField1( field1 );
    }

}
