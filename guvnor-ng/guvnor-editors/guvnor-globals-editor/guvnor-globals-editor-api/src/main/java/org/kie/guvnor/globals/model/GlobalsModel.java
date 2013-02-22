package org.kie.guvnor.globals.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.ArrayList;
import java.util.List;

/**
 * The model for Globals
 */
@Portable
public class GlobalsModel {

    private List<Global> globals = new ArrayList<Global>();

    public List<Global> getGlobals() {
        return globals;
    }

    public void setGlobals( List<Global> globals ) {
        this.globals = globals;
    }

}
