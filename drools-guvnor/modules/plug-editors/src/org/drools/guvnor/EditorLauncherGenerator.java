package org.drools.guvnor;

/**
 *
 */
public class EditorLauncherGenerator extends Generator {

    public EditorLauncherGenerator() {
        className = "EditorLauncher";
        outPath = "src/main/java/org/drools/guvnor/client/ruleeditor";
        configuration = "guvnor-editors.properties";
    }

    protected void collectImports() {
        imports.add("java.util.HashMap");
        imports.add("java.util.Map");
        imports.add("com.google.gwt.user.client.ui.Widget");
        imports.add("org.drools.guvnor.client.rpc.RuleAsset");
        imports.add("org.drools.guvnor.client.ruleeditor.RuleViewer");
    }

    void processProperties(String key, String[] options) {
        configs.add(new EditorConfiguration(key, options[0], options[1], options[2]));
    }

    String generateClassSource() {
        StringBuffer sb = new StringBuffer("package org.drools.guvnor.client.ruleeditor;\n\n");

        addImports(sb);

        sb.append("\npublic class " + className + " {\n\n");
        sb.append("  public static final Map TYPE_IMAGES = getTypeImages();\n\n");

        generateGetEditorViewerMethod(sb);
        generateGetAssetFormatIcon(sb);
        generateGetTypeImagesMethod(sb);
        return sb.toString();
    }

    private void generateGetTypeImagesMethod(StringBuffer sb) {
        sb.append("  private static Map getTypeImages() {\n");
        sb.append("    Map result = new HashMap();\n");
        for (Object o : configs) {
            EditorConfiguration config = (EditorConfiguration) o;
            sb.append("    result.put( \"" + config.type + "\", \"" + config.icon + "\" );\n");
        }
        sb.append("    return result;\n");
        sb.append("  }\n\n"); // getTypeImages

        sb.append("}\n"); //class end
    }

    private void generateGetAssetFormatIcon(StringBuffer sb) {
        sb.append("  public static String getAssetFormatIcon(String format) {\n" +
                "    String result = (String) TYPE_IMAGES.get( format );\n" +
                "    if (result == null) {\n" +
                "      return \"rule_asset.gif\";\n" +
                "    } else {\n" +
                "      return result;\n" +
                "    }\n" +
                "  }\n\n"); // getAssetFormatIcon
    }

    private void generateGetEditorViewerMethod(StringBuffer sb) {
        sb.append("  public static Widget getEditorViewer(RuleAsset asset, RuleViewer viewer) {\n");
        for (Object o : configs) {
            EditorConfiguration config = (EditorConfiguration) o;
            sb.append("    if (asset.metaData.format.equals(\"" + config.type + "\")) {\n");

            String line = "      return new ";
            if (config.wrapper == null || "".equals(config.wrapper)) {
                line += config.widget + "(asset, viewer)" + ";\n";
            } else {
                line += config.wrapper + "(\n             new " + config.widget + "(asset, viewer), asset)" + ";\n";
            }

            sb.append(line);
            sb.append("    } else ");
        }

        sb.append("{\n      return new org.drools.guvnor.client.common.DefaultContentUploadEditor( asset, viewer );\n    }\n");
        sb.append("  }\n\n"); // getEditorViewer
    }

    public static void main(String[] args) throws Exception {
        new EditorLauncherGenerator().execute();
    }

    class EditorConfiguration {
        String type;
        String widget;
        String wrapper;
        String icon;

        EditorConfiguration(String type, String widget, String wrapper, String icon) {
            this.type = type;
            this.widget = widget;
            this.wrapper = wrapper;
            this.icon = icon;
        }
    }

}
