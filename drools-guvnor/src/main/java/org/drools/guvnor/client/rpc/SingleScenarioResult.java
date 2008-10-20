package org.drools.guvnor.client.rpc;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SingleScenarioResult implements IsSerializable {

	public ScenarioRunResult result;
	
	/**
	 * Maps from event type to message to display.
	 */
	public List<String[]> auditLog = new ArrayList<String[]>();
	
	
}
