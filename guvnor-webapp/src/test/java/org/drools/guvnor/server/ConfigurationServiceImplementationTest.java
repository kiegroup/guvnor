package org.drools.guvnor.server;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.rpc.ConfigurationService;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.IFramePerspectiveConfigurationItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ConfigurationServiceImplementationTest extends GuvnorTestBase {

    private ConfigurationService configurationService;
    private RulesRepository rulesRepository;

    @Before
    public void setUp() throws Exception {
        createConfigurationService();
    }

    private void createConfigurationService() {
        ConfigurationServiceImplementation configurationServiceImplementation = spy(new ConfigurationServiceImplementation());
        when(configurationServiceImplementation.getRepository()).thenReturn(rulesRepository);
        configurationService = configurationServiceImplementation;
    }

    protected RulesRepository getRulesRepository() {
        return mock(RulesRepository.class);
    }


    @Test(expected = SerializationException.class)
    public void testNullUuidLoad() throws Exception {
//        setUpIdentity().logInAdmin();
        configurationService.load(null);
    }

    @Test(expected = RulesRepositoryException.class)
    public void testNotFound() throws Exception {
//        setUpIdentity().logInAdmin();
        setUpLoadForEmptyRepository();
        configurationService.load("ThereIsNothingInTheRepositoryWithThisId");
    }

    @Test
    public void testSaveNew() throws Exception {
//        setUpIdentity().logInAdmin();

        setUpSave("mockUuid", "Author", "http://jboss.org/drools");


        IFramePerspectiveConfiguration configuration = new IFramePerspectiveConfiguration();
        configuration.setName("Author");
        configuration.setUrl("http://jboss.org/drools");

        String uuid = configurationService.save(configuration);

        verify(rulesRepository).createPerspectivesConfiguration("Author", "http://jboss.org/drools");

        assertEquals("mockUuid", uuid);
    }

    @Test
    public void testLoad() throws Exception {
//        setUpIdentity().logInAdmin();
        setUpLoad("yetAnotherMock", "Runtime", "http://jboss.com/brms");

        IFramePerspectiveConfiguration configuration = configurationService.load("yetAnotherMock");

        verify(rulesRepository).loadPerspectivesConfiguration("yetAnotherMock");

        assertEquals("yetAnotherMock", configuration.getUuid());
        assertEquals("Runtime", configuration.getName());
        assertEquals("http://jboss.com/brms", configuration.getUrl());
    }

    @Test
    public void testModify() throws Exception {
//        setUpIdentity().logInAdmin();
        setUpSave("modifyMockUuid", "newName", "http://jboss.org/guvnor");
        setUpLoad("modifyMockUuid", "oldName", "http://jboss.org/oldUrl");

        IFramePerspectiveConfiguration configuration = configurationService.load("modifyMockUuid");
        configuration.setName("newName");
        configuration.setUrl("http://jboss.org/guvnor");

        String saveUuid = configurationService.save(configuration);

        verify(rulesRepository, never()).createPerspectivesConfiguration(anyString(), anyString());

        assertEquals("modifyMockUuid", saveUuid);
    }

    @Test
    public void testRemove() throws Exception {
//        setUpIdentity().logInAdmin();

        IFramePerspectiveConfigurationItem perspectiveConfigurationItem = createConfigurationItem("test-uuid", "drools site", "http://drools.org");
        setUpLoad("test-uuid", perspectiveConfigurationItem);

        configurationService.remove("test-uuid");
        verify(perspectiveConfigurationItem).remove();
    }

    @Test
    public void testLoadList() throws Exception {
        IFramePerspectiveConfigurationItem author = createConfigurationItem("auth-1234-1234", "Author", "http://localhost:8080/guvnor");
        IFramePerspectiveConfigurationItem runtime = createConfigurationItem("runt-1234-1234", "Runtime", "http://localhost:8080/runtime");
        IFramePerspectiveConfigurationItem manual = createConfigurationItem("manu-1234-1234", "Manual", "http://localhost:8080/manual");
        setUpLoadList(author, runtime, manual);

        Collection<IFramePerspectiveConfiguration> configurations = configurationService.loadPerspectiveConfigurations();

        assertEquals(3, configurations.size());
        assertConfigurationsContains("auth-1234-1234", "Author", "http://localhost:8080/guvnor", configurations);
        assertConfigurationsContains("runt-1234-1234", "Runtime", "http://localhost:8080/runtime", configurations);
        assertConfigurationsContains("manu-1234-1234", "Manual", "http://localhost:8080/manual", configurations);
    }

    private void assertConfigurationsContains(String uuid, String name, String url, Collection<IFramePerspectiveConfiguration> configurations) {
        boolean found = false;
        for (IFramePerspectiveConfiguration perspectivesConfiguration : configurations) {
            if (perspectivesConfiguration.getUuid().equals(uuid) && perspectivesConfiguration.getName().equals(name) && perspectivesConfiguration.getUrl().equals(url)) {
                {
                    found = true;
                    break;
                }
            }
        }
        assertTrue(String.format("Could not find perspective uuid:%s name:%s url:%s", uuid, name, url), found);
    }

    @Test(expected = IllegalStateException.class)
    public void testOnlyAdminHasPermissionToSave() throws Exception {
        configurationService.save(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testOnlyAdminHasPermissionToLoad() throws Exception {
        configurationService.load("Doesn't_matter_I_don_havePermissions_for_this");
    }

    @Test(expected = IllegalStateException.class)
    public void testOnlyAdminHasPermissionToRemove() throws Exception {
        configurationService.remove("test-uuid");
    }

    private void setUpSave(String returnUuid, String name, String url) {
        IFramePerspectiveConfigurationItem perspectiveConfigurationItem = mock(IFramePerspectiveConfigurationItem.class);
        when(perspectiveConfigurationItem.getUuid()).thenReturn(returnUuid);
        perspectiveConfigurationItem.setName(name);
        perspectiveConfigurationItem.setUrl(url);

        when(
                rulesRepository.createPerspectivesConfiguration(name, url)
        ).thenReturn(
                perspectiveConfigurationItem
        );
    }


    private void setUpLoadForEmptyRepository() {
        setUpLoad(any(String.class), null);
    }

    private void setUpLoad(String uuid, String name, String url) {
        IFramePerspectiveConfigurationItem perspectiveConfigurationItem = createConfigurationItem(
                uuid,
                name,
                url
        );
        setUpLoad(uuid, perspectiveConfigurationItem);
    }

    private void setUpLoad(String uuid, IFramePerspectiveConfigurationItem perspectiveConfigurationItem) {
        when(
                rulesRepository.loadPerspectivesConfiguration(uuid)
        ).thenReturn(
                perspectiveConfigurationItem
        );
    }

    private void setUpLoadList(IFramePerspectiveConfigurationItem... perspectiveConfigurationItems) {
        when(
                rulesRepository.listPerspectiveConfigurations()
        ).thenReturn(
                Arrays.asList(perspectiveConfigurationItems)
        );
    }


    private IFramePerspectiveConfigurationItem createConfigurationItem(String uuid, String name, String url) {
        IFramePerspectiveConfigurationItem perspectiveConfigurationItem = mock(IFramePerspectiveConfigurationItem.class);
        when(perspectiveConfigurationItem.getUuid()).thenReturn(uuid);
        when(perspectiveConfigurationItem.getName()).thenReturn(name);
        when(perspectiveConfigurationItem.getUrl()).thenReturn(url);
        return perspectiveConfigurationItem;
    }
}
