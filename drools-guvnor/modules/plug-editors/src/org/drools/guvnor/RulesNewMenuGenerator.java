package org.drools.guvnor;

/**
 *
 */
public class RulesNewMenuGenerator extends Generator {

    public RulesNewMenuGenerator() {
        className = "RulesNewMenu";
        outPath = "src/main/java/org/drools/guvnor/client/explorer";
        configuration = "rules-new-menu.properties";
    }

    void processProperties(String key, String[] options) {
        configs.add(new ItemConfiguration(key, options[0], options[1], options[2]));
    }

    String generateClassSource() {
        StringBuffer sb = new StringBuffer("package org.drools.guvnor.client.explorer;\n\n");
        addImports(sb);
        sb.append("\npublic class " + className + " {\n\n");
        sb.append("  public static Menu getMenu(final ExplorerLayoutManager manager) {\n");
        sb.append("    Menu m = new Menu();\n\n");
        for (Object o : configs) {
            ItemConfiguration item = (ItemConfiguration) o;
            sb.append("    m.addItem(new Item(\"" + item.title + "\", new BaseItemListenerAdapter() {\n");
            sb.append("      public void onClick(BaseItem item, EventObject e) {\n");
            sb.append("        manager.launchWizard(\"" + item.type + "\", \"" + item.title + "\", " + item.showCategories + ");\n");
            sb.append("      }\n");
            sb.append("    }, \"" + item.icon + "\"));\n\n");
        }
        sb.append("    return m;\n");
        sb.append("  }\n");
        sb.append("}");
        return sb.toString();
    }

    void collectImports() {
        imports.add("com.gwtext.client.widgets.menu.Menu");
        imports.add("com.gwtext.client.widgets.menu.Item");
        imports.add("com.gwtext.client.widgets.menu.BaseItem");
        imports.add("com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter");
        imports.add("com.gwtext.client.core.EventObject");
        imports.add("org.drools.guvnor.client.common.AssetFormats");
    }

    class ItemConfiguration {
        String type;
        String title;
        String showCategories;
        String icon;

        ItemConfiguration(String type, String title, String showCategories, String icon) {
            this.type = type;
            this.title = title;
            this.showCategories = showCategories;
            this.icon = icon;
        }
    }

    public static void main(String[] args) {
        new RulesNewMenuGenerator().execute();
    }

}
