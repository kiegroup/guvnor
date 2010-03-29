package org.drools.guvnor.client.rpc;

import java.util.List;

import org.drools.factconstraints.client.Constraint;
import org.drools.guvnor.client.modeldriven.brl.PortableObject;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author bauna
 */
public class WorkingSetConfigData implements PortableObject, IsSerializable {
	public String name;
	public String description;
	public List<Constraint> constraints;
	
	public String[] validFacts;
	public WorkingSetConfigData[] workingSets;	
}
