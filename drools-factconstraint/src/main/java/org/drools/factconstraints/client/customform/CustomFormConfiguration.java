package org.drools.factconstraints.client.customform;

import java.io.Serializable;

/**
 *
 * @author esteban
 */
public interface CustomFormConfiguration extends Serializable{

    String getFactType();
    void setFactType(String factType);

    String getFieldName();
    void setFieldName(String fieldName);

    String getCustomFormURL();
    void setCustomFormURL(String url);

    int getCustomFormHeight();
    void setCustomFormHeight(int height);

    int getCustomFormWidth();
    void setCustomFormWidth(int width);

}
