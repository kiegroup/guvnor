package org.drools.factconstraints.client.dataprovider;

import java.util.Map;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public interface FieldDataProvider {
    public void setFactTYpe(String factType);
    public void setFieldName(String fieldName);
    
    public String[] getArgumentKeys();
    public Object getArgumentValue(String key);
    public void setArgumentValue(String key, Object value);

    public Map<Object,String> getData();
    public Object getDefault();
}
