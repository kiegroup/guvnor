package org.guvnor.asset.management.backend.model;

import java.io.Serializable;

public class BranchInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String branchId;
	private String name;
	
	public BranchInfo(String branchId, String name) {
		this.branchId = branchId;
		this.name = name;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "BranchInfo [branchId=" + branchId + ", name=" + name + "]";
	}
}
