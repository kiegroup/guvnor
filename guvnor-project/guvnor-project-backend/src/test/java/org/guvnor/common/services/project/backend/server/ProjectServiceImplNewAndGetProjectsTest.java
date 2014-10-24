package org.guvnor.common.services.project.backend.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jgroups.util.UUID;
import org.junit.Test;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;

/**
 * Tests for ProjectServiceImpl resolveTestPackage
 */
public class ProjectServiceImplNewAndGetProjectsTest extends ProjectServiceImplBaseTest {

    @Test
    public void testNewAndGetProject() throws Exception {
        String baseUrl = "/";
        
        final ProjectService projectService = getService(ProjectService.class);
        final RepositoryService repositoryService = getService(RepositoryService.class);

        Repository repository = createNewTestRepository(repositoryService);
       
        Project newProject = createNewTestProject(projectService, repository, baseUrl);
        
        @SuppressWarnings("unused")
        Project randomOtherProject = createNewTestProject(projectService, repository, baseUrl);
        
        List<Project> projects = projectService.getProjects(repository, baseUrl);
        assertTrue( "Null project list", projects != null );
        assertFalse( "Empty project list", projects.isEmpty() );
        assertTrue( "Not enough projects found", projects.size() >= 2 );
        
        Project newProjectFound = null;
        for( Project project : projects ) { 
            if( project.getProjectName().equals(newProject.getProjectName()) ) { 
                newProjectFound = project;
            }
        }
        
        assertNotNull( "Newly created project could not be found.", newProjectFound );
        assertEquals( "imports path", newProject.getImportsPath(), newProjectFound.getImportsPath() );
        assertEquals( "KModule XML path", newProject.getKModuleXMLPath(), newProjectFound.getKModuleXMLPath() );
        assertEquals( "Pom XML path", newProject.getPomXMLPath(), newProjectFound.getPomXMLPath() );
        assertEquals( "Root path", newProject.getRootPath(), newProjectFound.getRootPath() );
        assertEquals( "Root path", newProject.getSignatureId(), newProjectFound.getSignatureId() );
        assertListContainsEquals( "Roles", newProject.getRoles(), newProjectFound.getRoles(), String.class );
        assertListContainsEquals( "Traits", newProject.getTraits(), newProjectFound.getTraits(), String.class );
    }

    private Repository createNewTestRepository(RepositoryService repositoryService) { 
        String repoName = UUID.randomUUID().toString();
        repoName = "repo-" + repoName.substring(0, repoName.indexOf("-"));
        final String scheme = "git";

        // username and password are optional
        final Map<String, Object> env = new HashMap<String, Object>( 1 );
        env.put( "init", true );

        return repositoryService.createRepository( scheme, repoName, env );
    }
  
    private Project createNewTestProject(ProjectService projectService, Repository repository, String baseUrl) { 
        String projectName = UUID.randomUUID().toString();
        projectName = "proj-" + projectName.substring(0, projectName.indexOf("-"));
        String projectGroup = UUID.randomUUID().toString();
        String version = String.valueOf(random.nextInt(1000)) + ".0";
        GAV gav = new GAV(projectGroup, projectName, version);
        POM projectPom = new POM(projectName, "test project: " + projectName, gav );
       
        return projectService.newProject(repository, projectName, projectPom, baseUrl );
    }
    
    private static <T> void assertListContainsEquals(String message, Collection<T> one, Collection<T> two, Class<T> type) { 
       List<T> listClone = new ArrayList<T>(one);
       
       for( Object twoElem : two )  { 
          assertTrue( message, listClone.remove(twoElem) );
       }
       assertTrue( message, listClone.isEmpty() );
    }

}
