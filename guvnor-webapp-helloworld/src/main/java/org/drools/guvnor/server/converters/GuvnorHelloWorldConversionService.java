package org.drools.guvnor.server.converters;

import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.client.rpc.ConversionResult;
import org.drools.repository.AssetItem;

@ApplicationScoped
public class GuvnorHelloWorldConversionService
        implements
        ConversionService {

    @Override
    public ConversionResult convert(AssetItem item, String targetFormat) {
        return null;  //TODO: Generated code -Rikkola-
    }
}
