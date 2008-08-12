package org.drools.guvnor.server.util;

import org.drools.guvnor.client.ruleeditor.PropertiesHolder;
import org.drools.guvnor.client.ruleeditor.PropertyHolder;

import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public class PropertiesPersistence {

    private static PropertiesPersistence INSTANCE = new PropertiesPersistence();

    private PropertiesPersistence() {
    }

    public static PropertiesPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal(PropertiesHolder holder) {
        StringBuilder sb = new StringBuilder();
        for (PropertyHolder propertyHolder : holder.list) {
            sb.append(propertyHolder.name).append("=").append(propertyHolder.value).append("\n");
        }
        return sb.toString();
    }

    public PropertiesHolder unmarshal(String properties) {
        List<PropertyHolder> list = new ArrayList<PropertyHolder>();
        String[] props = properties.split("\n");
        for (String s : props) {
            String[] pair = s.split("=");
            list.add(new PropertyHolder(pair[0], pair[1]));
        }
        PropertiesHolder result = new PropertiesHolder();
        result.list = list;
        return result;
    }

}
