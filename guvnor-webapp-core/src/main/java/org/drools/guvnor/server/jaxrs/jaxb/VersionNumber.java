package org.drools.guvnor.server.jaxrs.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "versionNumber")
@XmlAccessorType(XmlAccessType.FIELD)
public class VersionNumber {
    @XmlElement
    private long value;
    public long getValue() {
        return value;  //To change body of created methods use File | Settings | File Templates.
    }

    public void setValue(long uuid) {
        value = uuid;
    }
}