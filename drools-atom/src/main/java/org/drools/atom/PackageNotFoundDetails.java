package org.drools.atom;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PackageNotFoundDetails {
    private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
