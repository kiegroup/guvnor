package org.drools.ide.common.client.modeldriven.testing;

public class FactAssignmentField implements Field {

    private String fieldName;

    private FactData factData;

    public FactAssignmentField(String fieldName, String factType) {
        this.fieldName = fieldName;
        this.factData = new FactData(factType, "", false);
    }


    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setFactData(FactData factData) {
        this.factData = factData;
    }

    @Override
    public String getName() {
        return fieldName;
    }

    public FactData getFactData() {
        return factData;
    }
}
