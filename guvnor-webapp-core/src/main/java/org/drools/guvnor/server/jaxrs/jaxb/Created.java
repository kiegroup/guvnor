package org.drools.guvnor.server.jaxrs.jaxb;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "created")
@XmlAccessorType(XmlAccessType.FIELD)
public class Created {
    @XmlElement
    private Date value;
    public Date getValue() {
        return value;  //To change body of created methods use File | Settings | File Templates.
    }

    public void setValue(Date created) {
        value = created;
    }
}