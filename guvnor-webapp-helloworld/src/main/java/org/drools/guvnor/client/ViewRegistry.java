package org.drools.guvnor.client;

import com.google.gwt.resources.client.ImageResource;

import java.util.HashMap;

public class ViewRegistry extends HashMap<String, ViewDescriptor> {

    public ViewRegistry() {
        put("helloworld", new ViewDescriptor() {
            @Override
            public String getTitle() {
                return "Hello world";
            }

            @Override
            public ViewPart getWidget() {
                return new HelloWorldViewPart();
            }
        });
    }
}
