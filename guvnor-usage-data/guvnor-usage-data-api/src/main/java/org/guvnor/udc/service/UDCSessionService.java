package org.guvnor.udc.service;

import org.guvnor.udc.model.UsageEventSummary;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface UDCSessionService extends UDCStorageService {

    UsageEventSummary getUsageDataByKey(String key);
    
}
