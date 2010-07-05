package org.drools.factconstraints.client.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.factconstraints.client.customform.CustomFormConfiguration;
import org.drools.factconstraints.client.customform.predefined.DefaultCustomFormImplementation;

public class CustomFormsContainer {

    //because a FactType.field can only have one customForm,
    //this map is: <"FactType.field", CustomForm>
    private Map<String, CustomFormConfiguration> customForms = new HashMap<String, CustomFormConfiguration>();

    public CustomFormsContainer(CustomFormConfiguration[] customFormsConfigs) {
        this(Arrays.asList(customFormsConfigs));
    }

    public CustomFormsContainer(Collection<CustomFormConfiguration> customFormsConfigs) {
        if (customFormsConfigs != null && !customFormsConfigs.isEmpty()) {
            for (CustomFormConfiguration c : customFormsConfigs) {
                putCustomForm(c);
            }
        }
    }

//	public CustomFormsContainer() {
//
//	}
    public void removeCustomForm(CustomFormConfiguration cfc) {
        this.customForms.remove(this.createMapKey(cfc));
    }

    /**
     * If cfc.getCustomFormURL() is empty, the CustomFormConfiguration is removed.
     * @param cfc
     */
    public final void putCustomForm(CustomFormConfiguration cfc) {
        if (cfc.getCustomFormURL().trim().equals("")){
            this.customForms.remove(this.createMapKey(cfc));
        }else{
            this.customForms.put(this.createMapKey(cfc), cfc);
        }
    }

    public CustomFormConfiguration getCustomForm(String factType, String fieldName) {
        return this.customForms.get(this.createMapKey(factType, fieldName));
    }

    public List<CustomFormConfiguration> getCustomForms(){
        return new ArrayList<CustomFormConfiguration>(this.customForms.values());
    }

    public boolean containsCustomFormFor(String factType, String fieldName){
        return this.getCustomForm(factType, fieldName) != null;
    }

    private String createMapKey(String factType, String fieldName){
        return factType+"."+fieldName;
    }

    private String createMapKey(CustomFormConfiguration cfc) {
        return this.createMapKey(cfc.getFactType(), cfc.getFieldName());
    }

    public static CustomFormConfiguration getEmptyCustomFormConfiguration() {
        return new DefaultCustomFormImplementation();
    }

}
