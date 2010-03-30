package org.drools.guvnor.client.rpc;

import java.io.Serializable;
import java.util.List;

import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.guvnor.client.modeldriven.brl.PortableObject;

/**
 * 
 * @author bauna
 */
public class WorkingSetConfigData implements PortableObject, Serializable {
	private static final long serialVersionUID = 501L;

	public String name;
	public String description;
	public List<ConstraintConfiguration> constraints;
	
	public String[] validFacts;
	public WorkingSetConfigData[] workingSets;	
}
