package org.drools.guvnor.client.factmodel;

import org.drools.guvnor.client.modeldriven.brl.PortableObject;

public class FieldMetaModel implements PortableObject {

	public String name;
	public String type;

	public FieldMetaModel() {}
	public FieldMetaModel(String name, String type) {
		this.name = name;
		this.type = type;
	}

}
