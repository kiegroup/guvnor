package org.drools.guvnor.server.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.xpath.DefaultXPath;
import org.drools.guvnor.server.repository.TestRepositoryStartupService;
import org.drools.repository.utils.IOUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.jboss.shrinkwrap.api.Filters;

public class Deployments {

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive webArchive = ShrinkWrap.create(MavenImporter.class).loadEffectivePom("pom.xml")
                .importBuildOutput().importTestBuildOutput()
                .as(WebArchive.class);
        addTestDependencies(webArchive);

        File explodedWarFile = new File("target/guvnor-webapp-drools-6.0.0-SNAPSHOT");
        if (!explodedWarFile.exists()) {
            throw new IllegalStateException("The exploded war file (" + explodedWarFile
                    + ") should exist, run \"mvn package\" first.");
        }
        filterWebXml(webArchive, explodedWarFile);
        removeExcludedFiles(webArchive, explodedWarFile);
       // dumpArchive(webArchive);
        return webArchive;
    }

    private static void filterWebXml(WebArchive webArchive, File explodedWarFile) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(explodedWarFile.getPath() + "/WEB-INF/web.xml"));
            // Keep in sync with guvnor-distribution-wars/src/main/config-processor-filter/jboss-as-7_0/web_xml-filter.xml
            DefaultXPath xpath = new DefaultXPath("//j:context-param[j:param-name/text()=\"resteasy.injector.factory\"]");
            Map<String,String> namespaces = new TreeMap<String,String>();
            namespaces.put("j","http://java.sun.com/xml/ns/javaee");
            xpath.setNamespaceURIs(namespaces);

            Node node =  xpath.selectSingleNode(document);
            node.detach();
            File filteredWebXml = new File("target/test/filtered/jboss-as-7_0/WEB-INF/web.xml");
            filteredWebXml.getParentFile().mkdirs();
            FileWriter writer = null;
            try {
                writer = new FileWriter(filteredWebXml);
                document.write(writer);
            } catch (IOException e) {
                throw new IllegalStateException("filterWebXml failed", e);
            } finally {
                IOUtils.closeQuietly(writer);
            }
            webArchive.delete(ArchivePaths.create("WEB-INF/web.xml"))   ;
            webArchive.addAsWebInfResource(filteredWebXml, ArchivePaths.create("web.xml"));
        } catch (DocumentException e) {
            throw new IllegalStateException("filterWebXml failed", e);
        }
    }

    private static void dumpArchive(WebArchive webArchive) {
        File shrinkwrapParentDir = new File("target/shrinkwrap");
        FileUtils.deleteQuietly(shrinkwrapParentDir);
        shrinkwrapParentDir.mkdirs();
        webArchive.as(ExplodedExporter.class).exportExploded(shrinkwrapParentDir);
        // System.out.println(webArchive.toString(org.jboss.shrinkwrap.api.formatter.Formatters.VERBOSE));
    }

//    @Deployment
//    public static WebArchive createDeploymentWithoutMavenImporter() {
//        // TODO FIXME do not hardcode the version number
//        File explodedWarFile = new File("target/guvnor-webapp-5.5.0-SNAPSHOT");
//        if (!explodedWarFile.exists()) {
//            throw new IllegalStateException("The exploded war file (" + explodedWarFile
//                    + ") should exist, run \"mvn package\" first.");
//        }
//        File mergedBeansXml = writeMergedBeansXmlFile();
//        WebArchive webArchive = ShrinkWrap.create(ExplodedImporter.class, explodedWarFile.getName() + ".war")
//                .importDirectory(explodedWarFile)
//                .as(WebArchive.class)
//                .addAsResource(new File("target/test-classes/"), ArchivePaths.create(""))
//                // Workaround for https://issues.jboss.org/browse/ARQ-585
//                .addAsWebInfResource(mergedBeansXml, ArchivePaths.create("beans.xml"))
////                .addAsLibraries(
////                        DependencyResolvers.use(MavenDependencyResolver.class)
////                                .includeDependenciesFromPom("pom.xml")
////                                // exclusions don't work after includeDependenciesFromPom
////                                // .exclusions("org.jboss.arquillian:*", "org.jboss.shrinkwrap:*")
////                                .resolveAsFiles(new ScopeFilter("test"))
//                ;
//        removeExcludedFiles(webArchive, explodedWarFile);
//        System.out.println(webArchive.toString(org.jboss.shrinkwrap.api.formatter.Formatters.VERBOSE));
//        return webArchive;
//    }

    private static void addTestDependencies(WebArchive webArchive) {
        // Adding all test scope dependencies is bad because it includes arquillian and shrinkwrap
        // For now, we just add what we need... this is not scalable
        webArchive.addClasses(
                // Replace the production one
                TestRepositoryStartupService.class

                // Stuff we need
        ).addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class)
                        .artifact("org.apache.abdera:abdera-core:1.1.1")
                        .artifact("org.apache.abdera:abdera-client:1.1.1")
                        .resolveAsFiles())
        .addAsLibrary(new File("target/test-classes/billasurf.jar"));
    }

    private static void removeExcludedFiles(WebArchive webArchive, File explodedWarFile) {
        // Workaround because guvnor-webapp and guvnor-gwt-client modules aren't split
        webArchive.delete(ArchivePaths.create("WEB-INF/classes/org/drools/guvnor/gwtutil"));
        // Workaround for JBoss 7 https://issues.jboss.org/browse/WELD-983
        File libDir = new File(explodedWarFile, "WEB-INF/lib");
        for (File file : libDir.listFiles()) {
            String fileName = file.getName();
            if (fileName.endsWith(".jar") && (fileName.startsWith("weld-") || fileName.startsWith("resteasy-"))) {
                webArchive.delete(ArchivePaths.create("WEB-INF/lib/" + fileName));
            }
        }
    }

//    @Deployment
//    public static WebArchive createDeployment() {
//        WebArchive webArchive = ShrinkWrap.create(WebArchive.class)
//                .addAsResource(new File("target/classes/"))
//                .addAsWebInfResource(new File("target/guvnor-webapp-5.3.0-SNAPSHOT/WEB-INF/web.xml"), "web.xml")
//                .addAsWebInfResource(new File("target/guvnor-webapp-5.3.0-SNAPSHOT/WEB-INF/beans.xml"), "beans.xml")
//                .addAsLibraries(
//                        DependencyResolvers.use(MavenDependencyResolver.class)
//                                .includeDependenciesFromPom("pom.xml")
//                                .resolveAsFiles(new ScopeFilter("", "compile", "runtime", "test")));
//
//        return webArchive;
//        // TODO use loadMetadataFromPom instead
//    }
}
