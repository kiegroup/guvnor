package org.drools.factconstraints.client;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author esteban.aliverti@gmail.com
 * @author baunax@gmail.com
 */
public interface ConstraintConfiguration extends Serializable {
	public String getFactType();
    public void setFactType(String factType);
    
    public String getFieldName();
    public void setFieldName(String fieldName);
    
    public Set<String> getArgumentKeys();
    public Object getArgumentValue(String key);
    public void setArgumentValue(String key, String value);
    public boolean containsArgument(String key);

	public String getConstraintName();
	public void setConstraintName(String name);
}
