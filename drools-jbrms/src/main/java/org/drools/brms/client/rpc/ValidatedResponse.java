package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is used for services that validate.
 * Ie they may provide a validation error to be reported.
 * 
 * @author Michael Neale
 */
public class ValidatedResponse
    implements
    IsSerializable {

    /**
     * If this is true, then the errorHeader and messages should be displayed.
     */
    public boolean hasErrors;
    public String errorHeader;
    public String errorMessage;
    
    /**
     * Optional payload, if some more is needed.
     */
    public IsSerializable payload;
    
    
}
