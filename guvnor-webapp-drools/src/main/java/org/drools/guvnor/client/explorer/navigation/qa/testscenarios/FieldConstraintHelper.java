package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldConstraintHelper {

    private final Scenario scenario;
    private final ExecutionTrace executionTrace;
    private final SuggestionCompletionEngine sce;
    private final String factType;
    private Field field;
    private final Fact fact;

    public FieldConstraintHelper(Scenario scenario,
                                 ExecutionTrace executionTrace,
                                 SuggestionCompletionEngine sce,
                                 String factType,
                                 Field field,
                                 Fact fact) {
        this.scenario = scenario;
        this.executionTrace = executionTrace;
        this.sce = sce;
        this.factType = factType;
        this.field = field;
        this.fact = fact;
    }

    boolean isThereABoundVariableToSet() {
        List<?> vars = scenario.getFactNamesInScope(executionTrace, true);

        if (vars.size() > 0) {
            for (int i = 0; i < vars.size(); i++) {
                if (scenario.getFactTypes().get(vars.get(i)).getType().equals(resolveFieldType())) {
                    return true;
                }
            }
        }

        return false;
    }

    String resolveFieldType() {
        if (field instanceof FieldData && ((FieldData) field).collectionType != null) {
            return ((FieldData) field).collectionType;
        } else {
            return sce.getFieldType(factType, field.getName());
        }
    }

    boolean isItAList() {
        String fieldType = sce.getFieldType(factType, field.getName());

        if (fieldType != null && fieldType.equals("Collection")) {
            return true;
        }

        return false;
    }

    List<String> getFactNamesInScope() {
        return this.scenario.getFactNamesInScope(this.executionTrace, true);
    }

    FactData getFactTypeByVariableName(String var) {
        return this.scenario.getFactTypes().get(var);
    }

    String getFullFieldName() {
        return factType + "." + field.getName();
    }


    DropDownData getEnums() {
        Map<String, String> currentValueMap = new HashMap<String, String>();
        for (Field f : fact.getFieldData()) {
            if (f instanceof FieldData) {
                FieldData otherFieldData = (FieldData) f;
                currentValueMap.put(otherFieldData.getName(),
                        otherFieldData.getValue());
            }
        }
        return sce.getEnums(
                factType,
                field.getName(),
                currentValueMap);
    }

    String getFieldType() {
        return sce.getFieldType(getFullFieldName());
    }

    public String getParametricFieldType() {
        return sce.getParametricFieldType(
                factType,
                field.getName());
    }

    public FieldDataConstraintEditor createFieldDataConstraintEditor(final FieldData fieldData) {
        return new FieldDataConstraintEditor(
                fieldData.collectionType,
                fieldData,
                fact,
                sce,
                scenario,
                executionTrace);
    }

    public void replaceFieldWith(Field newField) {
        fact.getFieldData().set(
                fact.getFieldData().indexOf(field),
                newField);
        field = newField;
    }
}
