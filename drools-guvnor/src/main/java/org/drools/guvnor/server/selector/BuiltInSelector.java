package org.drools.guvnor.server.selector;

import org.drools.guvnor.server.security.CategoryPathType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.jboss.seam.security.Identity;

public class BuiltInSelector implements AssetSelector {
	private String status;
	private String statusOperator;
	private String category;
	private String categoryOperator;
	private boolean enableStatusSelector;
	private boolean enableCategorySelector;	

    public BuiltInSelector() {
	}	
    
 	public boolean isEnableStatusSelector() {
		return enableStatusSelector;
	}

	public void setEnableStatusSelector(boolean enableStatusSelector) {
		this.enableStatusSelector = enableStatusSelector;
	}

	public boolean isEnableCategorySelector() {
		return enableCategorySelector;
	}

	public void setEnableCategorySelector(boolean enableCategorySelector) {
		this.enableCategorySelector = enableCategorySelector;
	}
	
    public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategoryOperator() {
		return categoryOperator;
	}

	public void setCategoryOperator(String categoryOperator) {
		this.categoryOperator = categoryOperator;
	}
		
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusOperator() {
		return statusOperator;
	}

	public void setStatusOperator(String statusOperator) {
		this.statusOperator = statusOperator;
	}
    
	public boolean isAssetAllowed(AssetItem item) {
		if(enableStatusSelector && enableCategorySelector) {
			return (isStatusAllowed(item) && isCategoryAllowed(item));
		} else if (enableStatusSelector) {
			return isStatusAllowed(item);
		} else if (enableCategorySelector) {
			return isCategoryAllowed(item);
		}
		
		//allow everything if none enabled.
		return true;
	}
	
	private boolean isStatusAllowed(AssetItem item) {
		if("=".equals(statusOperator)) {
		    if (item.getStateDescription().equals(status))
			    return true;
		    else
			    return false;
		} else if ("!=".equals(statusOperator)) {
			if (!item.getStateDescription().equals(status))
			    return true;
		    else
			    return false;
		}
	
		return false;		
	}
	
	private boolean isCategoryAllowed(AssetItem item) {
		if("=".equals(categoryOperator)) {			
            for ( CategoryItem cat : item.getCategories() ) {
            	if (cat.getName().equals(category)) {
            		return true;
            	}
            }
            return false;
		} else if ("!=".equals(categoryOperator)) {
            for ( CategoryItem cat : item.getCategories() ) {
            	if (!cat.getName().equals(category)) {
            		return true;
            	}
            }
            return false;
		}
	
		return false;		
	}
}
