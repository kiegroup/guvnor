/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.drools.guvnor.importgenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.common.DroolsObjectOutputStream;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.InputType;
import org.drools.rule.Package;
import org.jboss.drools.guvnor.importgenerator.CmdArgsParser.Parameters;
import org.jboss.drools.guvnor.importgenerator.utils.DroolsHelper;
import org.jboss.drools.guvnor.importgenerator.utils.FileIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a drl package file found in the file system
 */
public class PackageFile implements Comparator<Integer> {

    private static final String PH_RULE_START = "rule ";
    private static final String PH_PACKAGE_START = "package ";
    private static final String PH_NEWLINE = "\n";
    private static final String[] RULE_START_MATCHERS = new String[]{"rule \"", "rule\""};
    private static final String[] RULE_END_MATCHERS = new String[]{"end\n", "\nend"};
    private static final String PACKAGE_DELIMETER = ".";
    private static String FUNCTIONS_FILE = null;

    private Package pkg;
    private String imports = ""; //default to no imports
    private String dependencyErrors = "";
    private String compilationErrors = "";
    private Map<String, Rule> rules = new HashMap<String, Rule>();
    private Map<String, File> ruleFiles = new HashMap<String, File>();
    private String name;
    private String modelContent;
    private List<Model> modelFiles = new ArrayList<Model>();


    private enum Format {
        DRL(".drl"),
        XLS(".xls");
        String value;

        Format(String value) {
            this.value = value;
        }
    }

    public List<Model> getModelFiles() {
        return modelFiles;
    }

    public void setModelFiles(File[] files) throws UnsupportedEncodingException {
        for (File modelFile : files) {
            if (!modelFile.exists()) {
                throw new RuntimeException("model file does not exist [" + modelFile.getAbsolutePath() + "]");
            }
            modelContent = FileIO.readAllAsBase64(modelFile);
            modelFiles.add(new Model(modelFile, modelContent));
        }
    }

    /**
     * goes through the file system calling extract to build a list of PackageFile objects
     *
     * @param options
     * @return
     * @throws Exception
     */
    public static Map<String, PackageFile> buildPackages(CmdArgsParser options) throws IOException {
        String path = options.getOption(Parameters.OPTIONS_PATH);
        FUNCTIONS_FILE = options.getOption(Parameters.OPTIONS_FUNCTIONS_FILE);
        Map<String, PackageFile> result = new HashMap<String, PackageFile>();
        File location = new File(path);
        if (!location.isDirectory()) {
            throw new IllegalStateException("path (" + path + ") must be a directory");
        }
        buildPackageForDirectory(result, location, options);
        return result;
    }

    public boolean containsAssets() {
        return this.modelFiles.size() > 0 || this.ruleFiles.size() > 0;
    }

    /**
     * Populates the <param>packages</param> parameter with PackageFile objects representing files within the specified <param>directory</param>
     *
     * @param packages
     * @param directory
     * @param options
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    private static void buildPackageForDirectory(Map<String, PackageFile> packages, File directory, CmdArgsParser options)
            throws IOException {
        boolean recurse = "true".equals(options.getOption(Parameters.OPTIONS_RECURSIVE));

        File[] files = directory.listFiles(
                new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return !name.startsWith(".");
                    }
                }
        );
        for (int i = 0; i < files.length; i++) {
            //if it's a directory with files then build a package
            if (files[i].isDirectory()) {
                File[] ruleFiles = getRuleFiles(files[i], options);
                File[] modelFiles = getJarFiles(files[i]);
                PackageFile packageFile = new PackageFile();
                packageFile.setName(getPackageName(files[i], options));

                if (modelFiles.length > 0) {
                    packageFile.setModelFiles(modelFiles);
                }
                if (ruleFiles.length > 0) {
                    packageFile = parseRuleFiles(packageFile, ruleFiles, options);
                }
                if (packageFile.containsAssets())
                    packages.put(packageFile.getName(), packageFile);

                if (recurse)
                    buildPackageForDirectory(packages, files[i], options);
            }
        }
    }

    private static File[] getJarFiles(File directory) {
        return directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.startsWith(".") && name.endsWith(".jar");
            }
        });
    }

    private static File[] getRuleFiles(File directory, CmdArgsParser options) {
        if (directory.isDirectory()) {
            final String extensionList = options.getOption(Parameters.OPTIONS_EXTENSIONS);
            File[] files = directory.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return !name.startsWith(".") && name.matches(buildRE(extensionList));
                }
            });
            List<File> result = new ArrayList<File>();
            for (File file : files) {
                if (file.isFile())
                    result.add(file);
            }
            return result.toArray(new File[result.size()]);
        }
        return new File[]{};
    }

    private static PackageFile parseRuleFiles(PackageFile result, File[] ruleFiles, CmdArgsParser options) throws IOException {
        for (int i = 0; i < ruleFiles.length; i++) {
            File file = ruleFiles[i];
            if (file.getName().endsWith(".drl")) {
                parseDrlFile(file, result, options);
            } else if (file.getName().endsWith(".xls")) {
                parseXlsFile(file, result, options);
            }
        }
        return result;
    }

    private static void parseXlsFile(File file, PackageFile packageFile, CmdArgsParser options) throws FileNotFoundException, UnsupportedEncodingException {
        String content = FileIO.readAllAsBase64(file);
        packageFile.getRules().put(file.getName(), new Rule(file.getName(), content, file));
        packageFile.getRuleFiles().put(file.getName(), file);
    }


    class ImportsComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            if (!o1.contains("package") && o2.contains("package")) {
                return 1;
            }
            return 0;
        }
    }

    private void cleanImports() {
        List<String> list = new ArrayList<String>();
        Set<String> set = new HashSet<String>();

        // separate out import and package lines
        StringTokenizer st = new StringTokenizer(getImports(), "\n");
        while (st.hasMoreTokens()) {
            list.add(st.nextToken().trim());
        }

        // ensure each line is unique
        set.addAll(list);
        list.clear();
        list.addAll(set);

        // sort so that package is at the top
        Collections.sort(list, new ImportsComparator());

        // push back into the package imports
        StringBuffer sb = new StringBuffer();
        for (String line : list) {
            sb.append(line).append("\n");
        }
        imports = sb.toString();
    }


    private static void parseDrlFile(File file, PackageFile packageFile, CmdArgsParser options) throws FileNotFoundException {
        String content = FileIO.readAll(new FileInputStream(file));
        int packageLoc = content.indexOf(PH_PACKAGE_START); // usually 0
        int ruleLoc = getRuleStart(content, 0);// variable
        //packageFile.setPackageName(getPackageName(file, options));
        if (ruleLoc < 0)
            return; // there are no rule's in this file (perhaps functions or other?)
        String imports = content.substring(packageLoc, ruleLoc);
        packageFile.addImports(imports);
        packageFile.cleanImports();
        try {
            boolean moreRules = true;
            while (moreRules) {
                int endLoc = getLoc(content, ruleLoc, RULE_END_MATCHERS) + 4;
                String ruleContents = content.substring(ruleLoc, endLoc);
                ruleLoc = getRuleStart(content, endLoc);
                moreRules = ruleLoc >= 0;
                Rule rule = new Rule(findRuleName(ruleContents, options), ruleContents, file);
                packageFile.getRules().put(rule.getRuleName(), rule);
                packageFile.getRuleFiles().put(rule.getRuleName(), file);
            }
        } catch (StringIndexOutOfBoundsException e) {
            System.err.print("Error with file: " + file.getName() + "\n");
        }
    }

    /**
     * compiles the rule files into a package and generates any error details
     *
     * @throws IOException
     */
    public void buildPackage() throws IOException {
        PackageBuilder pb = new PackageBuilder();
        for (String key : getRuleFiles().keySet()) {
            File file = getRuleFiles().get(key);

            try {
                if (FUNCTIONS_FILE != null) {
                    File functionsFile = new File(file.getParentFile().getPath(), FUNCTIONS_FILE);
                    if (functionsFile.exists()) {
                        pb.addPackageFromDrl(new FileReader(functionsFile));
                    }
                }

                if (file.getName().endsWith(Format.DRL.value)) {
                    pb.addPackageFromDrl(new FileReader(file));
                } else if (file.getName().endsWith(Format.XLS.value)) {
                    pb.addPackageFromDrl(new StringReader(DroolsHelper.compileDTabletoDRL(file, InputType.XLS)));
                }
            } catch (DroolsParserException e) {
                throw new IllegalArgumentException("File (" + file + ") throw a DroolsParserException.", e);
            }
        }
        this.pkg = pb.getPackage();
        if (pkg == null) { // compilation error - the rule is syntactically incorrect
            for (int i = 0; i < pb.getErrors().getErrors().length; i++) {
                DroolsError msg = pb.getErrors().getErrors()[i];
                addCompilationError(msg.getMessage());
            }
        } else if (pkg != null && !pkg.isValid()) {
            addDependencyError(pkg.getErrorSummary());
        }
    }

    /**
     * impl that determines whether you have dependency errors
     * - this is not completed - unsure how to display/count error packages if you get one comp error and one dep error in a simple package
     */
    public void buildPackageWithAccurateDependencyErrorDetection() throws IOException, DroolsParserException {
        PackageBuilder resBuilder = new PackageBuilder();
        for (String key : getRuleFiles().keySet()) {
            File file = getRuleFiles().get(key);
            PackageBuilder pb = new PackageBuilder();
            if (FUNCTIONS_FILE != null) {
                File functionsFile = new File(file.getParentFile().getPath(), FUNCTIONS_FILE);
                if (functionsFile.exists()) {
                    pb.addPackageFromDrl(new FileReader(functionsFile));
                }
            }
            if (isFormat(Format.DRL)) {
                pb.addPackageFromDrl(new FileReader(file));
            } else if (isFormat(Format.DRL)) {
                pb.addPackageFromDrl(new StringReader(DroolsHelper.compileDTabletoDRL(file, InputType.XLS)));
            }
            Package check = pb.getPackage();
            if (check == null) { // compilation error - the rule is syntactically incorrect
                for (int i = 0; i < pb.getErrors().getErrors().length; i++) {
                    DroolsError msg = pb.getErrors().getErrors()[i];
                    addCompilationError(msg.getMessage());
                }
            } else if (check != null && !check.isValid()) {
                addDependencyError(check.getErrorSummary());
                resBuilder.addPackage(pb.getPackage());
            }
            resBuilder.addPackage(check);
        }
        this.pkg = resBuilder.getPackage();
    }

    /**
     * @return
     * @throws IOException
     */
    public byte[] toByteArray() throws IOException {
        if (pkg != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DroolsObjectOutputStream doos = new DroolsObjectOutputStream(baos);
            doos.writeObject(pkg);
            return baos.toByteArray();
        } else {
            return new byte[]{};
        }
    }

    public void addRuleFile(String ruleName, File ruleFile) {
        ruleFiles.put(ruleName, ruleFile);
    }

    public Map<String, File> getRuleFiles() {
        return ruleFiles;
    }

    /**
     * Given a comma separated list of file extensions, this method returns a regular expression to match them
     *
     * @param extensions
     * @return
     */
    private static String buildRE(String extensions) {
        //String RE="[a-zA-Z0-9-_]+\\.({0})$";
        String RE = ".+\\.({0})$";
        String[] xtns = extensions.split(",");
        for (int i = 0; i < xtns.length; i++) {
            String xtn = "(" + xtns[i] + ")";
            if (i < xtns.length - 1)
                xtn += "|{0}";
            RE = MessageFormat.format(RE, xtn);
        }
        return RE;
    }

    /**
     * returns "approval.determine" where path is /home/mallen/workspace/rules/approval/determine/1.0.0.SNAPSHOT/file.xls
     * and options "start" is "rules" and end is "[0-9|.]*[SNAPSHOT]+[0-9|.]*" ie. any number, dot and word SNAPSHOT
     *
     * @param directory
     * @param options
     * @return
     */
    private static String getPackageName(File directory, CmdArgsParser options) {
        String startPath = directory.getPath();
        Matcher m = Pattern.compile("([^/|\\\\]+)").matcher(startPath); // quad-backslash is for windows paths
        List<String> lpath = new ArrayList<String>();
        while (m.find())
            lpath.add(m.group());
        String[] path = lpath.toArray(new String[lpath.size()]);
        StringBuffer sb = new StringBuffer();
        for (int i = path.length - 1; i >= 0; i--) {
            String dir = path[i];
            if ((dir.matches(options.getOption(Parameters.OPTIONS_PACKAGE_EXCLUDE))))
                continue;
            if ((dir.equals(options.getOption(Parameters.OPTIONS_PACKAGE_START))))
                break; //since we are working in reverse, it's time to exit
            sb.insert(0, PACKAGE_DELIMETER).insert(0, dir);
        }
        if (sb.substring(sb.length() - 1).equals(PACKAGE_DELIMETER))
            sb.delete(sb.length() - 1, sb.length());

        return sb.toString();
    }

    /**
     * Gets the start position of the next rule in the package (<param>contents</param>)
     *
     * @param contents
     * @param startLoc
     * @return
     */
    private static int getRuleStart(String contents, int startLoc) {
        return getLoc(contents, startLoc, RULE_START_MATCHERS);
    }

    private static int getLoc(String contents, int startLoc, String[] markers) {
        List<Integer> a = new ArrayList<Integer>();

        for (int i = 0; i < markers.length; i++) {
            a.add(Integer.valueOf(contents.indexOf(markers[i], startLoc)));
        }

        Collections.sort(a, new PackageFile());
        for (int k = 0; k < a.size(); k++) {
            if (a.get(k) >= 0)
                return a.get(k); // return the lowest non-negative number
        }
        return -1;
    }

    /**
     * returns the rule name given the entire rule content
     *
     * @param ruleContents
     * @return
     */
    private static String findRuleName(String ruleContents, CmdArgsParser options) {
        //TODO: this is incorrect - what if a rule starts 'rule"rule1"'??? use the getRuleStart method to find the beginning
        String name = ruleContents.substring(ruleContents.indexOf(PH_RULE_START) + PH_RULE_START.length(), ruleContents.indexOf(PH_NEWLINE)).replaceAll("\"", "").trim();
        if (!name.matches("[^'^/^<^>.]+")) { //Guvnor seems to not like some characters
            Logger logger = LoggerFactory.getLogger(PackageFile.class);
            logger.debug("WARNING: fixing invalid rule name [old name=" + name + "]");
            name = name.replaceAll("'", ""); //remove all ' chars since they are not valid in rule names
            name = name.replaceAll("/", "-"); //remove all / chars since they are not valid in rule names
            name = name.replaceAll("<", "&lt;"); //remove all < chars since they are not valid in rule names
            name = name.replaceAll(">", "&gt;"); //remove all > chars since they are not valid in rule names
        }
        return name;
    }

    // GETTERS/SETTERS for PackageFile object

    public boolean isFormat(Format isFormat) {
        if (ruleFiles != null && ruleFiles.size() > 0) {
            String name = ruleFiles.get(0).getName().toLowerCase();
            return name.endsWith(isFormat.value);
        }
        return false;
    }

    public String getDependencyErrors() {
        return dependencyErrors;
    }

    public void setDependencyErrors(String dependencyErrors) {
        this.dependencyErrors = dependencyErrors;
    }

    public boolean hasDependencyErrors() {
        return dependencyErrors.length() > 0;
    }

    public void addDependencyError(String dependencyError) {
        this.dependencyErrors += dependencyError + "\n";
    }

    public String getCompilationErrors() {
        return compilationErrors;
    }

    public void setCompilationErrors(String compilationErrors) {
        this.compilationErrors = compilationErrors;
    }

    public boolean hasCompilationErrors() {
        return compilationErrors.length() > 0;
    }

    public void addCompilationError(String compilationError) {
        this.compilationErrors += compilationError + "\n";
    }

    public boolean hasErrors() {
        return hasCompilationErrors() || hasDependencyErrors();
    }

    public Package getPkg() {
        return pkg;
    }

    public void setPkg(Package pkg) {
        this.pkg = pkg;
    }

    public String getImports() {
        return imports;
    }

    public void addImports(String imports) {
        //strip out any "package " lines from additional imports
        StringBuffer sb = new StringBuffer(imports);
        if (imports.length() > 0) {
            int posPackage = imports.indexOf("package ");
            sb.delete(posPackage, imports.indexOf("\n", posPackage));
        }
        this.imports = new StringBuffer().append(imports).append("\n").append(sb).toString();
    }

    public Map<String, Rule> getRules() {
        return rules;
    }

    public void setRules(Map<String, Rule> rules) {
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "PackageFile[name=" + name + "]";
    }

    public int compare(Integer o1, Integer o2) {
        return o1 - o2;
    }
}
