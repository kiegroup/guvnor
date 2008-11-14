package org.drools.repository;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;

public class MetaData {   
	String metaDataName;
	String metaDataType;
	String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMetaDataName() {
		return metaDataName;
	}
	public void setMetaDataName(String metaDataName) {
		this.metaDataName = metaDataName;
	}
	public String getMetaDataType() {
		return metaDataType;
	}
	public void setMetaDataType(String metaDataType) {
		this.metaDataType = metaDataType;
	}
	
}
