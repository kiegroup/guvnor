package org.drools.guvnor.client.factcontraints.dataprovider;

import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.factcontraints.ArgumentNotSetException;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public abstract class DefaultFieldDataProviderImpl implements FieldDataProvider {

    private String factType;
    private String fieldName;
    private Map<String, Object> arguments = new HashMap<String, Object>();

    public DefaultFieldDataProviderImpl() {

    }

    protected Object getMandatoryArgument(String key) throws ArgumentNotSetException {
        if (!this.arguments.containsKey(key)) {
            throw new ArgumentNotSetException("The argument " + key + " doesn't exist.");
        }

        Object value = this.getArgumentValue(key);

        if (value == null) {
            throw new ArgumentNotSetException("The argument " + key + " is null.");
        }

        return value;
    }

    public void setFactType(String factType) {
        this.factType = factType;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFactType() {
        return factType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String[] getArgumentKeys() {
        return this.arguments.keySet().toArray(new String[this.arguments.size()]);
    }

    public Object getArgumentValue(String key) {
        return this.arguments.get(key);
    }

    public void setArgumentValue(String key, Object value) {
        this.arguments.put(key, value);
    }

}
