/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.m2repo.backend.server;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.IOUtil;
import org.kie.guvnor.project.model.GAV;
import org.kie.scanner.Aether;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.deployment.DeployRequest;
import org.sonatype.aether.deployment.DeploymentException;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.SubArtifact;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


@ApplicationScoped
public class GuvnorM2Repository {

    public static final String M2_REPO_ROOT = "repository";
    public static final String REPO_ID_SNAPSHOTS = "snapshots";
    public static final String REPO_ID_RELEASES = "releases";
    private static final int BUFFER_SIZE = 1024;

    private File snapshotsRepository;
    private File releasesRepository;

    @PostConstruct
    protected void init() {
        setM2Repos();
    }

    private void setM2Repos() {
        snapshotsRepository = new File(getM2RepositoryRootDir() + File.separator + REPO_ID_SNAPSHOTS);
        if (!snapshotsRepository.exists()) {
            snapshotsRepository.mkdirs();
        }
        releasesRepository = new File(getM2RepositoryRootDir() + File.separator + REPO_ID_RELEASES);
        if (!releasesRepository.exists()) {
            releasesRepository.mkdirs();
        }
    }

    protected String getM2RepositoryRootDir() {
        if (!M2_REPO_ROOT.endsWith(File.separator)) {
            return M2_REPO_ROOT + File.separator;
        } else {
            return M2_REPO_ROOT;
        }
    }

    public String getRepositoryURL() {
        File file = new File(getM2RepositoryRootDir());
        return "file://" + file.getAbsolutePath();
    }

    public void deployArtifact(InputStream is, GAV gav) {
        File jarFile = new File( System.getProperty( "java.io.tmpdir" ), toFileName(gav, null, "jar"));

        try {
            if (!jarFile.exists()) {
                jarFile.getParentFile().mkdirs();
                jarFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(jarFile);

            final byte[] buf = new byte[BUFFER_SIZE];
            int byteRead = 0;
            while ((byteRead = is.read(buf)) != -1) {
                fos.write(buf, 0, byteRead);
            }
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Prepare pom file
        String pom = loadPOMFromJar(jarFile.getPath());
        if(pom == null) {
            pom =  generatePOM(gav);
            jarFile = appendPOMToJar(pom, jarFile.getPath(), gav);
        }
        File pomFile = new File( System.getProperty( "java.io.tmpdir" ), toFileName(gav, null, "pom"));
        try {
            if (!pomFile.exists()) {
                pomFile.getParentFile().mkdirs();
                pomFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(pomFile);
            IOUtils.write(pom, fos);

            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        deployArtifact(gav, jarFile, pomFile);
    }

    public void deployArtifact(GAV gav, File jarFile, File pomfile) {
        Artifact jarArtifact = new DefaultArtifact( gav.getGroupId(), gav.getArtifactId(), "jar", gav.getVersion() );
        jarArtifact = jarArtifact.setFile( jarFile );

        Artifact pomArtifact = new SubArtifact( jarArtifact, "", "pom" );
        pomArtifact = pomArtifact.setFile( pomfile );

        DeployRequest deployRequest = new DeployRequest();
        deployRequest
                .addArtifact( jarArtifact )
                .addArtifact( pomArtifact )
                .setRepository(getGuvnorM2Repository());

        try {
            Aether.DEFUALT_AETHER.getSystem().deploy(Aether.DEFUALT_AETHER.getSession(), deployRequest);
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        }
        
        //MavenRepository.getMavenRepository().deployArtifact(new ReleaseIdImpl(gav.getGroupId(), gav.getArtifactId(), gav.getVersion()), jarFile, pomfile);
    }

    private RemoteRepository getGuvnorM2Repository() {
        File m2RepoDir = new File( M2_REPO_ROOT );
        if (!m2RepoDir.exists()) {
            return null;
        }
        try {
            String localRepositoryUrl = m2RepoDir.toURI().toURL().toExternalForm();
            return new RemoteRepository( "guvnor-m2-repo", "default", localRepositoryUrl );
        } catch (MalformedURLException e) { }
        return null;
    }

    public boolean deleteFile(String[] fullPaths) {
        for (String fullPath : fullPaths) {
            final File file = new File(fullPath);
            if (file.exists()) {
                file.delete();
            }
        }
        return true;
    }

    /**
     * Finds files within the repository with the given filters.
     *
     * @return an collection of java.io.File with the matching files
     */
    public Collection<File> listFiles() {
        return listFiles(null);
    }

    /**
     * Finds files within the repository with the given filters.
     *
     * @param filters filter to apply when finding files. The filter is used to create a wildcard matcher, ie., "*fileter*.*", in which "*" is
     * to represent a multiple wildcard characters.
     * @return an collection of java.io.File with the matching files
     */
    public Collection<File> listFiles(String filters) {
        String wildcard = "*.jar";
        if(filters != null) {
            wildcard = "*" + filters + "*.jar";
        }
        Collection<File> files = FileUtils.listFiles(
                new File(M2_REPO_ROOT),
                new WildcardFileFilter(wildcard, IOCase.INSENSITIVE),
                DirectoryFileFilter.DIRECTORY
              );

        return files;
    }

    public InputStream loadFile(String path) {
        try {
            return new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public String getFileName(String path) {
        return (new File(path)).getName();
    }

    public static String loadPOMFromJar(String jarPath) {
        try {
            ZipFile zip = new ZipFile(new File(jarPath));

            for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry)e.nextElement();

                if(entry.getName().startsWith("META-INF/maven") &&  entry.getName().endsWith("pom.xml")) {
                    InputStream is = zip.getInputStream(entry);

                    InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                    StringBuilder sb = new StringBuilder();
                    for (int c = isr.read(); c != -1; c = isr.read()) {
                        sb.append((char)c);
                    }
                    return sb.toString();
                }
            }
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static String loadPOMFromJar(InputStream jarInputStream) {
        try {
            ZipInputStream zis = new ZipInputStream(jarInputStream);
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null)  {
                //System.out.println("entry: " + entry.getName() + ", " + entry.getSize());
                        // consume all the data from this entry
                if(entry.getName().startsWith("META-INF/maven") &&  entry.getName().endsWith("pom.xml")) {
                    InputStreamReader isr = new InputStreamReader(zis, "UTF-8");
                    StringBuilder sb = new StringBuilder();
                    for (int c = isr.read(); c != -1; c = isr.read()) {
                        sb.append((char)c);
                    }
                    return sb.toString();
                }
            }
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public File appendPOMToJar(String pom, String jarPath, GAV gav) {
        File originalJarFile = new File(jarPath);
        File appendedJarFile = new File(jarPath+".tmp");

        try {
            ZipFile war = new ZipFile(originalJarFile);

            ZipOutputStream append = new ZipOutputStream(new FileOutputStream(
                    appendedJarFile));

            // first, copy contents from existing war
            Enumeration<? extends ZipEntry> entries = war.entries();
            while (entries.hasMoreElements()) {
                ZipEntry e = entries.nextElement();
                // System.out.println("copy: " + e.getName());
                append.putNextEntry(e);
                if (!e.isDirectory()) {
                    IOUtil.copy(war.getInputStream(e), append);
                }
                append.closeEntry();
            }

            // append pom.
            ZipEntry e = new ZipEntry(getPomXmlPath(gav));
            // System.out.println("append: " + e.getName());
            append.putNextEntry(e);
            append.write(pom.getBytes());
            append.closeEntry();

            // close
            war.close();
            append.close();
        } catch (ZipException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //originalJarFile.delete();
        //appendedJarFile.renameTo(originalJarFile);
        return appendedJarFile;
    }

    protected String toURL(final String repository, GAV gav, String classifier) {
        final StringBuilder sb = new StringBuilder(repository);

        if (!repository.endsWith(File.separator)) {
            sb.append(File.separator);
        }

        return sb.append(gav.getGroupId().replace(".", File.separator))
                .append(File.separator).append(gav.getArtifactId())
                .append(File.separator).append(gav.getVersion())
                .append(File.separator).append(toFileName(gav, classifier, "jar")).toString();
    }

    protected String toFileName(GAV gav, String classifier, String fileType) {
        if (classifier != null) {
            return gav.getArtifactId() + "-" + gav.getVersion() + "-" + classifier + "." + getFileExtension(fileType);
        }

        return gav.getArtifactId() + "-" + gav.getVersion() + "." + getFileExtension(fileType);
    }

    private String getFileExtension(String type) {
        if (type.equalsIgnoreCase("ear")) {
            return "ear";
        } else if (type.equalsIgnoreCase("pom")) {
            return "pom";
        } else if (type.equalsIgnoreCase("war")) {
            return "war";
        }

        return "jar";
    }

    public String generatePOM(GAV gav) {
        Model model = new Model();
        model.setGroupId(gav.getGroupId());
        model.setArtifactId(gav.getArtifactId());
        model.setVersion(gav.getVersion());
        model.setModelVersion( "4.0.0" );

/*        Repository repo = new Repository();
        repo.setId("guvnor-m2-repo");
        repo.setName("Guvnor M2 Repo");
        repo.setUrl(getRepositoryURL());
        model.addRepository(repo);*/

        StringWriter stringWriter = new StringWriter();
        try {
            new MavenXpp3Writer().write(stringWriter, model);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return stringWriter.toString();
    }

    public static String generatePomProperties(GAV gav) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append( "groupId=" );
        sBuilder.append( gav.getGroupId() );
        sBuilder.append( "\n" );

        sBuilder.append( "artifactId=" );
        sBuilder.append( gav.getArtifactId() );
        sBuilder.append( "\n" );

        sBuilder.append( "version=" );
        sBuilder.append( gav.getVersion() );
        sBuilder.append( "\n" );

        return sBuilder.toString();
    }

    public String getPomXmlPath(GAV gav) {
        return "META-INF/maven/" + gav.getGroupId() + "/" + gav.getArtifactId() + "/pom.xml";
    }

    public String getPomPropertiesPath(GAV gav) {
        return "META-INF/maven/" + gav.getGroupId() + "/" + gav.getArtifactId() + "/pom.properties";
    }
}
