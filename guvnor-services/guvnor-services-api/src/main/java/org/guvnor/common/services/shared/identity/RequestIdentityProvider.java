package org.guvnor.common.services.shared.identity;

import java.util.List;

public interface RequestIdentityProvider {

    String getName();
    
    List<String> getRoles();
    
}