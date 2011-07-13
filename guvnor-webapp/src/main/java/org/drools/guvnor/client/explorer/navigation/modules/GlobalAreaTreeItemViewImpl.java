package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.safehtml.shared.SafeHtml;
import org.drools.guvnor.client.util.Util;

public class GlobalAreaTreeItemViewImpl
        extends ModulesTreeItemBaseViewImpl
        implements GlobalAreaTreeItemView {

    @Override
    protected SafeHtml getTreeHeader() {
        return Util.getHeader(
                images.chartOrganisation(),
                constants.GlobalArea() );
    }
}
