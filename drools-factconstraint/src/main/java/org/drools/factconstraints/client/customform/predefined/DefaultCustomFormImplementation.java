package org.drools.factconstraints.client.customform.predefined;

import java.io.Serializable;
import org.drools.factconstraints.client.customform.CustomFormConfiguration;

/**
 *
 * @author esteban
 */
public class DefaultCustomFormImplementation implements CustomFormConfiguration, Serializable{

    private String factType;
    private String fieldName;
    private String url;

    public String getFactType() {
        return this.factType;
    }

    public void setFactType(String factType) {
        this.factType = factType;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getCustomFormURL() {
        return this.url;
    }

    public void setCustomFormURL(String url) {
        this.url = url;
    }

}
