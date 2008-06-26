package org.drools.guvnor.client.factmodel;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.modeldriven.brl.PortableObject;

/**
 * Represents the GUI data for a fact model definition.
 *
 * @author Michael Neale
 */
public class FactMetaModel implements PortableObject {

	public String name;

	/**
	 * @gwt.typeArgs <org.drools.guvnor.client.factmodel.FieldMetaModel>
	 */
	public List fields = new ArrayList();

	public FactMetaModel() {}
	public FactMetaModel(String name, List fields) {
		this.name = name;
		this.fields = fields;
	}



}

