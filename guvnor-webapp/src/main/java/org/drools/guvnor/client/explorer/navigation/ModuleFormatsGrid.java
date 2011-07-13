package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.place.shared.Place;
import org.drools.guvnor.client.rpc.PackageConfigData;

public class ModuleFormatsGrid extends Place {

    private String[] formats;
    private PackageConfigData packageConfig;
    private String title;

    public ModuleFormatsGrid( PackageConfigData packageConfig,
                              String title,
                              String[] formats ) {
        this.packageConfig = packageConfig;
        this.title = title;
        this.formats = formats;
    }

    public String[] getFormats() {
        return formats;
    }

    public PackageConfigData getPackageConfigData() {
        return packageConfig;
    }

    public String getTitle() {
        return title;
    }
}
