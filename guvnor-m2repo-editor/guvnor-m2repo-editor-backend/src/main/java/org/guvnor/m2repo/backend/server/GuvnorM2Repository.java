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
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
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
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.drools.core.io.impl.ReaderInputStream;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.deployment.DeploymentException;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.util.artifact.SubArtifact;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.guvnor.common.services.project.model.GAV;
import org.kie.scanner.Aether;
import org.kie.scanner.embedder.MavenSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        Aether.getAether().getRepositories().add(getGuvnorM2Repository());
    }

    public String getM2RepositoryRootDir() {
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
                                final GAV gav,
                                final boolean includeAdditionalRepositories ) {
        //Write JAR to temporary file for deployment
        File jarFile = new File( System.getProperty( "java.io.tmpdir" ),
                                 toFileName( gav,
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

        //Write pom.xml to JAR if it doesn't already exist
        String pomXML = loadPOMFromJarInternal( new File( jarFile.getPath() ) );
        if ( pomXML == null ) {
            pomXML = generatePOM( gav );
            jarFile = appendPOMToJar( pomXML,
                                      jarFile.getPath(),
                                      gav );
        }

        //Write pom.properties to JAR if it doesn't already exist
        String pomProperties = loadGAVFromJarInternal( new File( jarFile.getPath() ) );
        if ( pomProperties == null ) {
            pomProperties = generatePomProperties( gav );
            jarFile = appendPomPropertiesToJar( pomProperties,
                                                jarFile.getPath(),
                                                gav );
        }

        deployArtifact( gav,
                        pomXML,
                        jarFile,
                        includeAdditionalRepositories );
    }

    /**
     * Convenience method for unit tests - to avoid deploying to additional (possibly external) repositories
     * @param is InputStream holding JAR
     * @param gav GAV representing the JAR
     */
    public void deployArtifactInternal( InputStream is,
                                        GAV gav ) {

    }

    private void deployArtifact( final GAV gav,
                                 final String pomXML,
                                 final File jarFile,
                                 final boolean includeAdditionalRepositories ) {
        //Write pom.xml to temporary file for deployment
        final File pomXMLFile = new File( System.getProperty( "java.io.tmpdir" ),
                                          toFileName( gav,
                                                      "pom.xml" ) );

        try {
            if ( !pomXMLFile.exists() ) {
                pomXMLFile.getParentFile().mkdirs();
                pomXMLFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream( pomXMLFile );
            IOUtils.write( pomXML,
                           fos );

            fos.flush();
            fos.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        //JAR Artifact
        Artifact jarArtifact = new DefaultArtifact( gav.getGroupId(),
                                                    gav.getArtifactId(),
                                                    "jar",
                                                    gav.getVersion() );
        jarArtifact = jarArtifact.setFile( jarFile );

        //pom.xml Artifact
        Artifact pomXMLArtifact = new SubArtifact( jarArtifact,
                                                   "",
                                                   "pom" );
        pomXMLArtifact = pomXMLArtifact.setFile( pomXMLFile );

        try {
            //Install into local repository
            final InstallRequest installRequest = new InstallRequest();
            installRequest
                    .addArtifact( jarArtifact )
                    .addArtifact( pomXMLArtifact );

            Aether.getAether().getSystem().install( Aether.getAether().getSession(),
                                                    installRequest );
        } catch ( InstallationException e ) {
            throw new RuntimeException( e );
        }

        //Deploy into Workbench's default remote repository
        try {
            final DeployRequest deployRequest = new DeployRequest();
            deployRequest
                    .addArtifact( jarArtifact )
                    .addArtifact( pomXMLArtifact )
                    .setRepository( getGuvnorM2Repository() );

            Aether.getAether().getSystem().deploy( Aether.getAether().getSession(),
                                                   deployRequest );

        } catch ( DeploymentException e ) {
            throw new RuntimeException( e );
        }

        //Only deploy to additional repositories if required. This flag is principally for Unit Tests
        if ( !includeAdditionalRepositories ) {
            return;
        }

        //Deploy into remote repository defined in <distributionManagement>
        try {
            final Model model = new MavenXpp3Reader().read( new StringReader( pomXML ) );
            final DistributionManagement distributionManagement = model.getDistributionManagement();

            if ( distributionManagement != null ) {

                final boolean isSnapshot = pomXMLArtifact.isSnapshot();
                DeploymentRepository remoteRepository = null;
                if ( isSnapshot ) {
                    remoteRepository = distributionManagement.getSnapshotRepository();
                } else {
                    remoteRepository = distributionManagement.getRepository();
                }

                //If the user has configured a distribution management module in the pom then we will attempt to deploy there.
                //If credentials are required those credentials must be provisioned in the user's settings.xml file
                if ( remoteRepository != null ) {
                    DeployRequest remoteRequest = new DeployRequest();
                    remoteRequest
                            .addArtifact( jarArtifact )
                            .addArtifact( pomXMLArtifact )
                            .setRepository( getRemoteRepoFromDeployment( remoteRepository ) );

                    Aether.getAether().getSystem().deploy( Aether.getAether().getSession(),
                                                           remoteRequest );
                }
            }

        } catch ( DeploymentException e ) {
            throw new RuntimeException( e );
        } catch ( XmlPullParserException xppe ) {
            throw new RuntimeException( xppe );
        } catch ( IOException ioe ) {
            throw new RuntimeException( ioe );
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
            return new RemoteRepository.Builder( "guvnor-m2-repo",
                                         "default",
                                         localRepositoryUrl )
                    .setSnapshotPolicy( new RepositoryPolicy( true,
                                                      RepositoryPolicy.UPDATE_POLICY_DAILY,
                                                      RepositoryPolicy.CHECKSUM_POLICY_WARN ) )
                    .setReleasePolicy(new RepositoryPolicy(true,
                            RepositoryPolicy.UPDATE_POLICY_ALWAYS,
                            RepositoryPolicy.CHECKSUM_POLICY_WARN))
                    .build();

        } catch ( MalformedURLException e ) {
            log.error( e.getMessage(),
                       e );
            throw new RuntimeException( e );
        }
    }

    private RemoteRepository getRemoteRepoFromDeployment( DeploymentRepository repo ) {

        RemoteRepository.Builder remoteRepoBuilder = new RemoteRepository.Builder( repo.getId(), repo.getLayout(), repo.getUrl() )
                .setSnapshotPolicy(new RepositoryPolicy(true,
                        RepositoryPolicy.UPDATE_POLICY_DAILY,
                        RepositoryPolicy.CHECKSUM_POLICY_WARN))
                .setReleasePolicy(new RepositoryPolicy(true,
                        RepositoryPolicy.UPDATE_POLICY_ALWAYS,
                        RepositoryPolicy.CHECKSUM_POLICY_WARN));

        Settings settings = MavenSettings.getSettings();
        Server server = settings.getServer( repo.getId() );

        if ( server != null ) {
            remoteRepoBuilder.setAuthentication(
                    new AuthenticationBuilder()
                            .addUsername(server.getUsername())
                            .addPassword(server.getPassword())
                            .build());
        }

        return remoteRepoBuilder.build();
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

    private static String loadPOMFromJarInternal( final File file ) {
        InputStream is = null;
        InputStreamReader isr = null;
        try {
            ZipFile zip = new ZipFile( file );

            for ( Enumeration e = zip.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();

                if ( entry.getName().startsWith( "META-INF/maven" ) && entry.getName().endsWith( "pom.xml" ) ) {
                    is = zip.getInputStream( entry );
                    isr = new InputStreamReader( is, "UTF-8" );
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
        } finally {
            if ( isr != null ) {
                try {
                    isr.close();
                } catch ( IOException e ) {
                }
            }
            if ( is != null ) {
                try {
                    is.close();
                } catch ( IOException e ) {
                }
            }
        }

        return null;
    }

    public GAV loadGAVFromJar( final String jarPath ) {
        File zip = new File( M2_REPO_DIR,
                             jarPath );

        try {
            final String pomProperties = loadGAVFromJarInternal( zip );

            final Properties props = new Properties();
            props.load( new StringReader( pomProperties ) );

            final String groupId = props.getProperty( "groupId" );
            final String artifactId = props.getProperty( "artifactId" );
            final String version = props.getProperty( "version" );

            return new GAV( groupId,
                            artifactId,
                            version );
        } catch ( IOException e ) {
            log.error( e.getMessage() );
        }

        return null;
    }

    private String loadGAVFromJarInternal( final File file ) {
        InputStream is = null;
        InputStreamReader isr = null;
        try {
            ZipFile zip = new ZipFile( file );

            for ( Enumeration e = zip.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();

                if ( entry.getName().startsWith( "META-INF/maven" ) && entry.getName().endsWith( "pom.properties" ) ) {
                    is = zip.getInputStream( entry );
                    isr = new InputStreamReader( is, "UTF-8" );
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
        } finally {
            if ( isr != null ) {
                try {
                    isr.close();
                } catch ( IOException e ) {
                }
            }
            if ( is != null ) {
                try {
                    is.close();
                } catch ( IOException e ) {
                }
            }
        }

        return null;
    }

    public static String loadPOMFromJar( final InputStream jarInputStream ) {
        try {

            InputStream is = getInputStreamFromJar( jarInputStream,
                                                    "META-INF/maven",
                                                    "pom.xml" );
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

    public static String loadPOMPropertiesFromJar( final InputStream jarInputStream ) {
        try {

            InputStream is = getInputStreamFromJar( jarInputStream,
                                                    "META-INF/maven",
                                                    "pom.properties" );
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

    private static InputStream getInputStreamFromJar( final InputStream jarInputStream,
                                                      final String prefix,
                                                      final String suffix ) throws IOException {
        ZipInputStream zis = new ZipInputStream( jarInputStream );
        ZipEntry entry;

        while ( ( entry = zis.getNextEntry() ) != null ) {
            final String entryName = entry.getName();
            if ( entryName.startsWith( prefix ) && entryName.endsWith( suffix ) ) {
                return new ReaderInputStream( new InputStreamReader( zis,
                                                                     "UTF-8" ) );
            }
        }

        throw new FileNotFoundException( "Could not find '" + prefix + "/*/" + suffix + "' in the jar." );
    }

    private File appendPOMToJar( final String pom,
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

            // append pom.xml
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

        return appendedJarFile;
    }

    private File appendPomPropertiesToJar( final String pomProperties,
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

            // append pom.properties
            ZipEntry e = new ZipEntry( getPomPropertiesPath( gav ) );
            append.putNextEntry( e );
            append.write( pomProperties.getBytes() );
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

    protected String toFileName( final GAV gav,
                                 final String fileName ) {
        return gav.getArtifactId() + "-" + gav.getVersion() + "." + fileName;
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


