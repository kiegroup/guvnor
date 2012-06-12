package org.drools.repository.utils;

import org.drools.repository.AssetItem;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
public class AssetValidator {
    private Validator defaultValidator;

    @Inject @Any
    private Instance<Validator> validators;

    public boolean validate(AssetItem assetItem){
        if (validators != null) {
            for(Validator validator: validators){
                if (validator.getFormat().equals("default")) {
                    defaultValidator = validator;
                }
                if(assetItem.getFormat().equals(validator.getFormat())) {
                    return validator.validate(assetItem);
                }
            }
            if (defaultValidator != null) {
                return defaultValidator.validate(assetItem);
            }
        }
        return true;
    }


}

