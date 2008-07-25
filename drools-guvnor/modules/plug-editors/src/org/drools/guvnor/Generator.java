package org.drools.guvnor;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.*;
import java.util.*;

/**
 *
 */
public abstract class Generator extends Task {

    List<Object> configs = new ArrayList<Object>();
    Set<String> imports = new TreeSet<String>();

    String configuration;
    String outPath;
    String className;

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getOutPath() {
        return outPath;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    abstract void processProperties(String key, String[] options);

    abstract String generateClassSource();

    abstract void collectImports();

    public void loadEditorsConfig() throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(configuration));
        for (Object o : p.keySet()) {
            String key = (String) o;
            String[] options = p.getProperty(key).split(";");
            processProperties(key, options);
        }
    }

    public void execute() throws BuildException {
        System.out.println("--------- generating " + className + " ---------");
        try {
            System.out.println("loading configuration: " + configuration);
            loadEditorsConfig();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BuildException(e);
        }

        System.out.println("collecting required imports");
        collectImports();
        System.out.println("generating the class source");
        generate();
        System.out.println("new " + className + " is created in " + outPath);
        System.out.println("--------- " + className + " generated ---------");
    }

    protected void generate() {
        String source = generateClassSource();
        try {
            File file = new File(outPath, className + ".java");
            System.out.println("writing the new class to " + file.getAbsolutePath());
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter buffWriter = new BufferedWriter(fileWriter);
            buffWriter.write(source);
            buffWriter.flush();
            buffWriter.close();
            System.out.println("write complete");
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    void addImports(StringBuffer sb) {
        for (String classImport : imports) {
            sb.append("import " + classImport + ";\n");
        }
    }
}
