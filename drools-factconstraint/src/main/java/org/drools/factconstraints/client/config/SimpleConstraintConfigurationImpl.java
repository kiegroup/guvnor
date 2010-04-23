package org.drools.factconstraints.client.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.factconstraints.client.ConstraintConfiguration;

public class SimpleConstraintConfigurationImpl implements ConstraintConfiguration {

	private static final long serialVersionUID = 501L;
	private Map<String, String> args = new HashMap<String, String>();
	private String constraintName = null;
	private String factType;
	private String fieldName;

	public SimpleConstraintConfigurationImpl(ConstraintConfiguration constraintConfiguration) {
		copyFrom(constraintConfiguration);
	}

	public SimpleConstraintConfigurationImpl() {
	}

	public Set<String> getArgumentKeys() {
		return args.keySet();
	}

	public Object getArgumentValue(String key) {
		return args.get(key);
	}

	public String getConstraintName() {
		return constraintName;
	}

	public void setConstraintName(String constraintName) {
		this.constraintName = constraintName;
	}

	public String getFactType() {
		return factType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setArgumentValue(String key, String value) {
		args.put(key, value);
	}

	public void setFactType(String factType) {
		this.factType = factType;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public boolean containsArgument(String key) {
		return args.containsKey(key);
	}

	@Override
	public String toString() {
		return "SimpleConstraintConfigurationImpl [args=" + args + ", constraintName=" + constraintName + ", factType="
				+ factType + ", fieldName=" + fieldName + "]";
	}

	private void copyFrom(ConstraintConfiguration other) {
		if (constraintName != null) {
			throw new RuntimeException("can't copy configuration on a configured instance");
		}
		this.constraintName = other.getConstraintName();
		this.factType = other.getFactType();
		this.fieldName = other.getFieldName();
		this.args = new HashMap<String, String>();
		for (String argName : other.getArgumentKeys()) {
			this.args.put(argName, (String) other.getArgumentValue(argName));
		}
	}
}
