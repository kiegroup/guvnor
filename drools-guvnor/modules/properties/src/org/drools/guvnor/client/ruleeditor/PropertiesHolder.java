package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.modeldriven.brl.PortableObject;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 */
public class PropertiesHolder implements PortableObject {

    /**
	 * @gwt.typeArgs <org.drools.guvnor.client.ruleeditor.PropertyHolder>
	 */
    List<PropertyHolder> list = new ArrayList<PropertyHolder>();

    public PropertiesHolder() {
        list.add(new PropertyHolder("x1", "yyy1"));
        list.add(new PropertyHolder("x2", "yyy2"));
        list.add(new PropertyHolder("x3", "yyy3"));
        list.add(new PropertyHolder("x4", "yyy4"));
        list.add(new PropertyHolder("x5", "yyy5"));
        list.add(new PropertyHolder("x6", "yyy6"));
        list.add(new PropertyHolder("x7", "yyy7"));
    }
}