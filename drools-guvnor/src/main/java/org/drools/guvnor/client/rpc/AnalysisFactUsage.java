package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AnalysisFactUsage implements IsSerializable {

	public String name;
	public AnalysisFieldUsage[] fields;

}
