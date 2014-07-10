package org.guvnor.common.services.shared.test;

import java.util.List;

public interface TestResultMessage {

    List<String> getResultStrings();
    boolean wasSuccessful();
    
}
