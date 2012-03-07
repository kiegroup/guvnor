package org.drools.ide.common.client.modeldriven.testing;

public class FactAssignmentField implements Field {

    private String fieldName;

    private Fact fact;

    public FactAssignmentField() {
    }

    public FactAssignmentField(String fieldName, String factType) {
        this.fieldName = fieldName;
        this.fact = new Fact(factType);
    }


    public void setName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setFact(Fact fact) {
        this.fact = fact;
    }

    @Override
    public String getName() {
        return fieldName;
    }

    public Fact getFact() {
        return fact;
    }
}
