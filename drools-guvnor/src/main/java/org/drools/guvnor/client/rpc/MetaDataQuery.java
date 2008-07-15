package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MetaDataQuery implements IsSerializable {

	public String attribute;
	public String valueList;

	public MetaDataQuery() {}
	public MetaDataQuery(String attribute) { this.attribute = attribute; }

}
