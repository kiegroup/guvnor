package org.kie.guvnor.service;

import java.util.Map;

/**
 * A repository definition
 */
public interface Repository {

    String getAlias();

    String getScheme();

    Map<String, String> getEnvironment();

    void addEnvironmentParameter( final String key,
                                  final String value );

    boolean getBootstrap();

    void setBootstrap( final boolean bootstrap );

    boolean isValid();

}
