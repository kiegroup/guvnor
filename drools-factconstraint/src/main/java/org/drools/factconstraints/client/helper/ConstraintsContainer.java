package org.drools.factconstraints.client.helper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.factconstraints.client.Constraint;

public class ConstraintsContainer {
	Map<String, List<Constraint>> constraints = new HashMap<String, List<Constraint>>();
	
	public ConstraintsContainer(Constraint[] constraints) {
		this(Arrays.asList(constraints));
	}
	
	public ConstraintsContainer(Collection<Constraint> constraints) {
		if (constraints != null && !constraints.isEmpty()) {
			for (Constraint c : constraints) {
				addConstraint(c);
			}
		}
	}

	public ConstraintsContainer() {
		
	}
	
	public void removeConstraint(Constraint c) {
		List<Constraint> list = constraints.get(c.getFactType());
		if (list != null) {
			list.remove(c);
		}
	}
	
	public void addConstraint(Constraint c) {
		List<Constraint> list = constraints.get(c.getFactType());
		if (list == null) {
			list = new LinkedList<Constraint>();
			constraints.put(c.getFactType(), list);
		}
		list.add(c);
	}

	public List<Constraint> getConstraints(String factType) {
		return Collections.unmodifiableList(constraints.get(factType));
	}
	
	public List<Constraint> getConstraints(String factType, String fieldName) {
		
		List<Constraint> list = constraints.get(factType);
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}
		List<Constraint> res = new LinkedList<Constraint>();
		for (Constraint c : list) {
			if (fieldName.equals(c.getFieldName())) {
				res.add(c);
			}
		} 
		return res;
	}
	
	public boolean hasConstraints(String FactType) {
		return constraints.containsKey(FactType);
	}
}
