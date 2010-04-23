package org.drools.factconstraints.client.helper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.config.SimpleConstraintConfigurationImpl;

public class ConstraintsContainer {
	private static final Map<String, ConstraintConfiguration> constraintConfigs = new HashMap<String, ConstraintConfiguration>();
	
	static {
		ConstraintConfiguration config = new SimpleConstraintConfigurationImpl();
		config.setConstraintName("NotNull");
		constraintConfigs.put(config.getConstraintName(), config);
		
		config = new SimpleConstraintConfigurationImpl();
		config.setConstraintName("IntegerConstraint");
		constraintConfigs.put(config.getConstraintName(), config);
		
		config = new SimpleConstraintConfigurationImpl();
		config.setConstraintName("RangeConstraint");
		config.setArgumentValue("Min.value", "0");
		config.setArgumentValue("Max.value", "0");
		constraintConfigs.put(config.getConstraintName(), config);
		
		config = new SimpleConstraintConfigurationImpl();
		config.setConstraintName("NotMatches");
		config.setArgumentValue("matches", "");
		constraintConfigs.put(config.getConstraintName(), config);
		
		config = new SimpleConstraintConfigurationImpl();
		config.setConstraintName("Matches");
		config.setArgumentValue("matches", "");
		constraintConfigs.put(config.getConstraintName(), config);
	}
	
	private Map<String, List<ConstraintConfiguration>> constraints = new HashMap<String, List<ConstraintConfiguration>>();
	
	public ConstraintsContainer(ConstraintConfiguration[] constraints) {
		this(Arrays.asList(constraints));
	}
	
	public ConstraintsContainer(Collection<ConstraintConfiguration> constraints) {
		if (constraints != null && !constraints.isEmpty()) {
			for (ConstraintConfiguration c : constraints) {
				addConstraint(c);
			}
		}
	}

	public ConstraintsContainer() {
		
	}
	
	public List<ConstraintConfiguration> removeConstraint(ConstraintConfiguration c) {
		List<ConstraintConfiguration> list = constraints.get(c.getFactType());
		if (list != null) {
			list.remove(c);
		}
		return list;
	}
	
	public void addConstraint(ConstraintConfiguration c) {
		List<ConstraintConfiguration> list = constraints.get(c.getFactType());
		if (list == null) {
			list = new LinkedList<ConstraintConfiguration>();
			constraints.put(c.getFactType(), list);
		}
		list.add(c);
	}

	public List<ConstraintConfiguration> getConstraints(String factType) {
		return Collections.unmodifiableList(constraints.get(factType));
	}
	
	public List<ConstraintConfiguration> getConstraints(String factType, String fieldName) {
		
		List<ConstraintConfiguration> list = constraints.get(factType);
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}
		List<ConstraintConfiguration> res = new LinkedList<ConstraintConfiguration>();
		for (ConstraintConfiguration c : list) {
			if (fieldName.equals(c.getFieldName())) {
				res.add(c);
			}
		} 
		return res;
	}
	
	public boolean hasConstraints(String FactType) {
		return constraints.containsKey(FactType);
	}
	
	public static Map<String, ConstraintConfiguration> getAllConfigurations() {
		return constraintConfigs;
	}
 	
	public static ConstraintConfiguration getEmptyConfiguration(String constraintName) {
		return copyConfig(getAllConfigurations().get(constraintName));
	}

	private static ConstraintConfiguration copyConfig(ConstraintConfiguration constraintConfiguration) {
		return new SimpleConstraintConfigurationImpl(constraintConfiguration);
	}
}
