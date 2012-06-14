package org.drools.guvnor.server.jaxrs.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "uuid")
@XmlAccessorType(XmlAccessType.FIELD)
public class Uuid {
    @XmlElement
    private String value;
    public String getValue() {
        return value;
    }

    public void setValue(String uuid) {
        value = uuid;
    }
}
