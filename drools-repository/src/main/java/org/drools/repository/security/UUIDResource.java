package org.drools.repository.security;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Id;

import org.jboss.security.authorization.Resource;
import org.jboss.security.authorization.ResourceType;

public class UUIDResource implements Resource, Serializable {

	private static final long serialVersionUID = 400l;


	public final String UUID;

	public String resourceName;
	
	public UUIDResource(String uuid) {
		this(uuid, null);
	}

	public UUIDResource(String uuid, String resourceName) {
		this.UUID = uuid;
		this.resourceName = resourceName;
	}
    
	@Id
	public String getUUID() {
		return  UUID;
	}

	public String getId() {
        return  UUID;
    }
	
	public String getResourceName() {
		return this.resourceName;
	}

	public void setResourceName(String name) {
		this.resourceName = name;
	}

	public ResourceType getLayer() {
		return ResourceType.ACL;
	}

	public Map<String, Object> getMap() {
		return null;
	}

}
