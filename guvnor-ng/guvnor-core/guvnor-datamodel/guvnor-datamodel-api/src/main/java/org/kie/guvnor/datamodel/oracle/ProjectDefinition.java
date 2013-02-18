package org.kie.guvnor.datamodel.oracle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.guvnor.datamodel.model.MethodInfo;
import org.kie.guvnor.datamodel.model.ModelField;

/**
 * Container for everything needed to build a DataModelOracle for a Project
 */
@Portable
public class ProjectDefinition {

    // Details of Fact Types and their corresponding fields
    private Map<String, ModelField[]> modelFields = new HashMap<String, ModelField[]>();

    // A map of FactTypes {factType, isCollection} to determine which Fact Types are Collections.
    private Map<String, Boolean> collectionTypes = new HashMap<String, Boolean>();

    // A map of FactTypes {factType, isEvent} to determine which Fact Type can be treated as events.
    private Map<String, Boolean> eventTypes = new HashMap<String, Boolean>();

    // A map of { TypeName.field : String[] } - where a list is valid values to display in a drop down for a given Type.field combination.
    private Map<String, String[]> dataEnumLists = new HashMap<String, String[]>();

    // Details of Method information used (exclusively) by ExpressionWidget and ActionCallMethodWidget
    private Map<String, List<MethodInfo>> methodInformation = new HashMap<String, List<MethodInfo>>();

    // A map of the field that contains the parametrized type of a collection
    // for example given "List<String> name", key = "name" value = "String"
    private Map<String, String> fieldParametersType = new HashMap<String, String>();

    public void addFactsAndFields( final Map<String, ModelField[]> modelFields ) {
        this.modelFields.putAll( modelFields );
    }

    public Map<String, ModelField[]> getFactsAndFields() {
        return this.modelFields;
    }

    public void addEventType( final Map<String, Boolean> eventTypes ) {
        this.eventTypes.putAll( eventTypes );
    }

    public Map<String, Boolean> getEventTypes() {
        return this.eventTypes;
    }

    public void addMethodInformation( final Map<String, List<MethodInfo>> methodInformation ) {
        this.methodInformation.putAll( methodInformation );
    }

    public Map<String, List<MethodInfo>> getMethodInformation() {
        return methodInformation;
    }

    public void addFieldParametersType( final Map<String, String> fieldParametersType ) {
        this.fieldParametersType.putAll( fieldParametersType );
    }

    public Map<String, String> getFieldParametersTypes() {
        return this.fieldParametersType;
    }

    public void addEnumDefinitions( final Map<String, String[]> dataEnumLists ) {
        this.dataEnumLists.putAll( dataEnumLists );
    }

    public Map<String, String[]> getEnumDefinitions() {
        return dataEnumLists;
    }

    public void addCollectionType( final Map<String, Boolean> collectionTypes ) {
        this.collectionTypes.putAll( collectionTypes );
    }

    public Map<String, Boolean> getCollectionTypes() {
        return this.collectionTypes;
    }

}

