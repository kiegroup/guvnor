package org.drools.guvnor.server.selector;

import org.drools.repository.AssetItem;

public class BuiltInSelector implements AssetSelector {
	private String status;
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	private String operator;

    public BuiltInSelector(String status, String operator) {
    	this.status = status;
    	this.operator = operator;
	}

    public BuiltInSelector() {
	}
    
	public boolean isAssetAllowed(AssetItem item) {
		if("=".equals(operator)) {
		    if (item.getStateDescription().equals(status))
			    return true;
		    else
			    return false;
		} else if ("!=".equals(operator)) {
			if (!item.getStateDescription().equals(status))
			    return true;
		    else
			    return false;
		}
	
		return false;
	}
}
