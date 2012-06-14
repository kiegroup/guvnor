package org.drools.guvnor.server.jaxrs.jaxb;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "archived")
@XmlAccessorType(XmlAccessType.FIELD)
public class Archived {
    @XmlElement
    private boolean value;
    public boolean getValue() {
        return value;  //To change body of created methods use File | Settings | File Templates.
    }

    public void setValue(boolean archived) {
        value = archived;
    }
}