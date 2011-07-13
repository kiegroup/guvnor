package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.safehtml.shared.SafeHtml;
import org.drools.guvnor.client.util.Util;

public class KnowledgeModulesTreeItemViewImpl
        extends ModulesTreeItemBaseViewImpl
        implements KnowledgeModulesTreeItemView {


    @Override
    protected SafeHtml getTreeHeader() {
        return Util.getHeader(
                images.chartOrganisation(),
                constants.Packages() );
    }

    public void clearModulesTreeItem() {
        tree.clear();
    }
}
