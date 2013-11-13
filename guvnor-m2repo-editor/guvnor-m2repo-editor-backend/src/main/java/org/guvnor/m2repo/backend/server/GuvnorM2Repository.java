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

package org.guvnor.m2repo.backend.server;

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
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.IOUtil;
import org.drools.core.io.impl.ReaderInputStream;
import org.guvnor.common.services.project.model.GAV;
import org.kie.scanner.Aether;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.deployment.DeployRequest;
import org.sonatype.aether.deployment.DeploymentException;
import org.sonatype.aether.installation.InstallRequest;
import org.sonatype.aether.installation.InstallationException;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.repository.RepositoryPolicy;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.SubArtifact;

@ApplicationScoped
public class GuvnorM2Repository {

    private static final Logger log = LoggerFactory.getLogger( GuvnorM2Repository.class );

    public static final String M2_REPO_ROOT = "repositories" + File.separatorChar + "kie";
    public static String M2_REPO_DIR;

    private static final int BUFFER_SIZE = 1024;

    @PostConstruct
    protected void init() {
        setM2Repos();
    }

    private void setM2Repos() {
        final String meReposDir = System.getProperty( "org.guvnor.m2repo.dir" );

        if ( meReposDir == null || meReposDir.trim().isEmpty() ) {
            M2_REPO_DIR = M2_REPO_ROOT;
        } else {
            M2_REPO_DIR = meReposDir.trim();
        }
        log.info( "Maven Repository root set to: " + M2_REPO_DIR );

        //Ensure repository root has been created
        final File root = new File( getM2RepositoryRootDir() );
        if ( !root.exists() ) {
            log.info( "Creating Maven Repository root: " + M2_REPO_DIR );
            root.mkdirs();
        }

        Aether.DEFUALT_AETHER.getRepositories().add( getGuvnorM2Repository() );
    }

    protected String getM2RepositoryRootDir() {
        if ( !M2_REPO_DIR.endsWith( File.separator ) ) {
            return M2_REPO_DIR + File.separator;
        } else {
            return M2_REPO_DIR;
        }
    }

    public String getRepositoryURL() {
        File file = new File( getM2RepositoryRootDir() );
        return "file://" + file.getAbsolutePath();
    }

    public void deployArtifact( final InputStream inputStream,
                                final GAV gav ) {
        File jarFile = new File( System.getProperty( "java.io.tmpdir" ),
                                 toFileName( gav,
                                             null,
                                             "jar" ) );

        try {
            if ( !jarFile.exists() ) {
                jarFile.getParentFile().mkdirs();
                jarFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream( jarFile );

            final byte[] buf = new byte[ BUFFER_SIZE ];
            int byteRead = 0;
            while ( ( byteRead = inputStream.read( buf ) ) != -1 ) {
                fos.write( buf, 0, byteRead );
            }
            fos.flush();
            fos.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        //Prepare pom file
        String pom = loadPOMFromJarInternal( new File( jarFile.getPath() ) );
        if ( pom == null ) {
            pom = generatePOM( gav );
            jarFile = appendPOMToJar( pom,
                                      jarFile.getPath(),
                                      gav );
        }
        File pomFile = new File( System.getProperty( "java.io.tmpdir" ),
                                 toFileName( gav,
                                             null,
                                             "pom" ) );
        try {
            if ( !pomFile.exists() ) {
                pomFile.getParentFile().mkdirs();
                pomFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream( pomFile );
            IOUtils.write( pom, fos );

            fos.flush();
            fos.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        deployArtifact( gav, jarFile, pomFile );
    }

    public void deployArtifact( final GAV gav,
                                final File jarFile,
                                final File pomfile ) {
        Artifact jarArtifact = new DefaultArtifact( gav.getGroupId(),
                                                    gav.getArtifactId(),
                                                    "jar",
                                                    gav.getVersion() );
        jarArtifact = jarArtifact.setFile( jarFile );

        Artifact pomArtifact = new SubArtifact( jarArtifact,
                                                "",
                                                "pom" );
        pomArtifact = pomArtifact.setFile( pomfile );

        // install into local repository as it's preferred when loading kjars into KieContainer
        try {
            InstallRequest installRequest = new InstallRequest();
            installRequest
                    .addArtifact( jarArtifact )
                    .addArtifact( pomArtifact );

            Aether.DEFUALT_AETHER.getSystem().install( Aether.DEFUALT_AETHER.getSession(),
                                                       installRequest );
        } catch ( InstallationException e ) {
            throw new RuntimeException( e );
        }

        DeployRequest deployRequest = new DeployRequest();
        deployRequest
                .addArtifact( jarArtifact )
                .addArtifact( pomArtifact )
                .setRepository( getGuvnorM2Repository() );

        try {
            Aether.DEFUALT_AETHER.getSystem().deploy( Aether.DEFUALT_AETHER.getSession(),
                                                      deployRequest );
        } catch ( DeploymentException e ) {
            throw new RuntimeException( e );
        }
    }

    private RemoteRepository getGuvnorM2Repository() {
        File m2RepoDir = new File( M2_REPO_DIR );
        if ( !m2RepoDir.exists() ) {
            log.error( "Repository root does not exist: " + M2_REPO_DIR );
            throw new IllegalArgumentException( "Repository root does not exist: " + M2_REPO_DIR );
        }

        try {
            String localRepositoryUrl = m2RepoDir.toURI().toURL().toExternalForm();
            return new RemoteRepository( "guvnor-m2-repo",
                                         "default",
                                         localRepositoryUrl )
                    .setPolicy( true,
                                new RepositoryPolicy( false,
                                                      RepositoryPolicy.UPDATE_POLICY_NEVER,
                                                      RepositoryPolicy.CHECKSUM_POLICY_WARN ) )
                    .setPolicy( false,
                                new RepositoryPolicy( false,
                                                      RepositoryPolicy.UPDATE_POLICY_NEVER,
                                                      RepositoryPolicy.CHECKSUM_POLICY_WARN ) );

        } catch ( MalformedURLException e ) {
            log.error( e.getMessage(),
                       e );
            throw new RuntimeException( e );
        }
    }

    /**
     * Finds files within the repository with the given filters.
     * @return an collection of java.io.File with the matching files
     */
    public Collection<File> listFiles() {
        return listFiles( null );
    }

    /**
     * Finds files within the repository with the given filters.
     * @param filters filter to apply when finding files. The filter is used to create a wildcard matcher, ie., "*fileter*.*", in which "*" is
     * to represent a multiple wildcard characters.
     * @return an collection of java.io.File with the matching files
     */
    public Collection<File> listFiles( final String filters ) {
        String wildcard = "*.jar";
        if ( filters != null ) {
            wildcard = "*" + filters + "*.jar";
        }
        Collection<File> files = FileUtils.listFiles( new File( M2_REPO_DIR ),
                                                      new WildcardFileFilter( wildcard,
                                                                              IOCase.INSENSITIVE ),
                                                      DirectoryFileFilter.DIRECTORY );

        return files;
    }

    public InputStream loadFile( final String path ) {
        try {
            return new FileInputStream( new File( M2_REPO_DIR,
                                                  path ) );
        } catch ( FileNotFoundException e ) {
            log.error( e.getMessage() );
        }
        return null;
    }

    public String getFileName( final String path ) {
        return ( new File( M2_REPO_DIR,
                           path ) ).getName();
    }

    public static String loadPOMFromJar( final String jarPath ) {
        File zip = new File( M2_REPO_DIR,
                             jarPath );

        return loadPOMFromJarInternal( zip );
    }

    public static String loadPOMFromJarInternal( final File file ) {
        try {
            ZipFile zip = new ZipFile( file );

            for ( Enumeration e = zip.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();

                if ( entry.getName().startsWith( "META-INF/maven" ) && entry.getName().endsWith( "pom.xml" ) ) {
                    InputStream is = zip.getInputStream( entry );

                    InputStreamReader isr = new InputStreamReader( is, "UTF-8" );
                    StringBuilder sb = new StringBuilder();
                    for ( int c = isr.read(); c != -1; c = isr.read() ) {
                        sb.append( (char) c );
                    }
                    return sb.toString();
                }
            }
        } catch ( ZipException e ) {
            log.error( e.getMessage() );
        } catch ( IOException e ) {
            log.error( e.getMessage() );
        }

        return null;
    }

    public static InputStream loadPOMStreamFromJar( final InputStream jarInputStream ) throws IOException {
        ZipInputStream zis = new ZipInputStream( jarInputStream );
        ZipEntry entry;

        while ( ( entry = zis.getNextEntry() ) != null ) {
            if ( entry.getName().startsWith( "META-INF/maven" ) && entry.getName().endsWith( "pom.xml" ) ) {
                return new ReaderInputStream( new InputStreamReader( zis,
                                                                     "UTF-8" ) );
            }
        }

        throw new FileNotFoundException( "Could not find pom.xml from the jar." );
    }

    public static String loadPOMFromJar( final InputStream jarInputStream ) {
        try {

            InputStream is = loadPOMStreamFromJar( jarInputStream );
            StringBuilder sb = new StringBuilder();
            for ( int c = is.read(); c != -1; c = is.read() ) {
                sb.append( (char) c );
            }
            return sb.toString();
        } catch ( IOException e ) {
            log.error( e.getMessage() );
        }

        return null;
    }

    public File appendPOMToJar( final String pom,
                                final String jarPath,
                                final GAV gav ) {
        File originalJarFile = new File( jarPath );
        File appendedJarFile = new File( jarPath + ".tmp" );

        try {
            ZipFile war = new ZipFile( originalJarFile );

            ZipOutputStream append = new ZipOutputStream( new FileOutputStream( appendedJarFile ) );

            // first, copy contents from existing war
            Enumeration<? extends ZipEntry> entries = war.entries();
            while ( entries.hasMoreElements() ) {
                ZipEntry e = entries.nextElement();
                append.putNextEntry( e );
                if ( !e.isDirectory() ) {
                    IOUtil.copy( war.getInputStream( e ),
                                 append );
                }
                append.closeEntry();
            }

            // append pom.
            ZipEntry e = new ZipEntry( getPomXmlPath( gav ) );
            append.putNextEntry( e );
            append.write( pom.getBytes() );
            append.closeEntry();

            // close
            war.close();
            append.close();
        } catch ( ZipException e ) {
            log.error( e.getMessage() );
        } catch ( IOException e ) {
            log.error( e.getMessage() );
        }

        //originalJarFile.delete();
        //appendedJarFile.renameTo(originalJarFile);
        return appendedJarFile;
    }

    protected String toURL( final String repository,
                            final GAV gav,
                            final String classifier ) {
        final StringBuilder sb = new StringBuilder( repository );

        if ( !repository.endsWith( File.separator ) ) {
            sb.append( File.separator );
        }

        return sb.append( gav.getGroupId().replace( ".",
                                                    File.separator ) )
                .append( File.separator ).append( gav.getArtifactId() )
                .append( File.separator ).append( gav.getVersion() )
                .append( File.separator ).append( toFileName( gav,
                                                              classifier,
                                                              "jar" ) ).toString();
    }

    protected String toFileName( final GAV gav,
                                 final String classifier,
                                 final String fileType ) {
        if ( classifier != null ) {
            return gav.getArtifactId() + "-" + gav.getVersion() + "-" + classifier + "." + getFileExtension( fileType );
        }

        return gav.getArtifactId() + "-" + gav.getVersion() + "." + getFileExtension( fileType );
    }

    private String getFileExtension( final String type ) {
        if ( type.equalsIgnoreCase( "ear" ) ) {
            return "ear";
        } else if ( type.equalsIgnoreCase( "pom" ) ) {
            return "pom";
        } else if ( type.equalsIgnoreCase( "war" ) ) {
            return "war";
        }

        return "jar";
    }

    public String generatePOM( final GAV gav ) {
        Model model = new Model();
        model.setGroupId( gav.getGroupId() );
        model.setArtifactId( gav.getArtifactId() );
        model.setVersion( gav.getVersion() );
        model.setModelVersion( "4.0.0" );

/*        Repository repo = new Repository();
        repo.setId("guvnor-m2-repo");
        repo.setName("Guvnor M2 Repo");
        repo.setUrl(getRepositoryURL());
        model.addRepository(repo);*/

        StringWriter stringWriter = new StringWriter();
        try {
            new MavenXpp3Writer().write( stringWriter,
                                         model );
        } catch ( IOException e ) {
            log.error( e.getMessage() );
        }

        return stringWriter.toString();
    }

    public static String generatePomProperties( final GAV gav ) {
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

    public String getPomXmlPath( final GAV gav ) {
        return "META-INF/maven/" + gav.getGroupId() + "/" + gav.getArtifactId() + "/pom.xml";
    }

    public String getPomPropertiesPath( final GAV gav ) {
        return "META-INF/maven/" + gav.getGroupId() + "/" + gav.getArtifactId() + "/pom.properties";
    }
}


