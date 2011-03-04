package org.drools.guvnor.server;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.rpc.ConfigurationService;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;
import org.drools.repository.IFramePerspectiveConfigurationItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
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
    public void setUpGuvnorTestBase() {
        setUpSeam();
        setUpRepository();
        rulesRepository = spy(getRulesRepository());
        createConfigurationService();
    }

    private void createConfigurationService() {
        ConfigurationServiceImplementation configurationServiceImplementation = spy(new ConfigurationServiceImplementation());
        when(configurationServiceImplementation.getRepository()).thenReturn(rulesRepository);
        configurationService = configurationServiceImplementation;
    }

    @Test
    public void testLoadList() throws Exception {
        setUpMockIdentity();
        String authorUuid = save("Author", "http://localhost:8080/guvnor");
        String runtimeUuid = save("Runtime", "http://localhost:8080/runtime");
        String manualUuid = save("Manual", "http://localhost:8080/manual");

        Collection<IFramePerspectiveConfiguration> configurations = configurationService.loadPerspectiveConfigurations();

        assertEquals(3, configurations.size());
        assertConfigurationsContains(authorUuid, "Author", "http://localhost:8080/guvnor", configurations);
        assertConfigurationsContains(runtimeUuid, "Runtime", "http://localhost:8080/runtime", configurations);
        assertConfigurationsContains(manualUuid, "Manual", "http://localhost:8080/manual", configurations);
    }

    @Test(expected = SerializationException.class)
    public void testNullUuidLoad() throws Exception {
        setUpMockIdentity();
        configurationService.load(null);
    }

    @Test(expected = RulesRepositoryException.class)
    public void testNotFound() throws Exception {
        setUpMockIdentity();
        configurationService.load("ThereIsNothingInTheRepositoryWithThisId");
    }

    @Test
    public void testSaveNew() throws Exception {
        setUpMockIdentity();
        String uuid = save("Author", "http://jboss.org/drools");

        verify(rulesRepository).createPerspectivesConfiguration("Author", "http://jboss.org/drools");

        assertNotNull(uuid);
    }

    @Test
    public void testLoad() throws Exception {
        setUpMockIdentity();
        String uuid = save("Runtime", "http://jboss.com/brms");

        IFramePerspectiveConfiguration configuration = configurationService.load(uuid);

        verify(rulesRepository).loadPerspectivesConfiguration(uuid);

        assertEquals(uuid, configuration.getUuid());
        assertEquals("Runtime", configuration.getName());
        assertEquals("http://jboss.com/brms", configuration.getUrl());
    }

    @Test
    public void testModify() throws Exception {
        setUpMockIdentity();
        String uuid = save("oldName", "http://jboss.org/oldUrl");

        IFramePerspectiveConfiguration configuration = configurationService.load(uuid);
        configuration.setName("newName");
        configuration.setUrl("http://jboss.org/guvnor");

        String saveUuid = configurationService.save(configuration);

        verify(rulesRepository, never()).createPerspectivesConfiguration("newName", "http://jboss.org/guvnor");

        assertEquals(uuid, saveUuid);
    }

    @Test
    public void testRemove() throws Exception {
        setUpMockIdentity();
        String uuid = save("drools site", "http://drools.org");

        configurationService.remove(uuid);

        try {
            configurationService.load(uuid);
            fail("Item was not removed.");
        } catch (RulesRepositoryException e) {
            //Expected
        }
    }

    private String save(String name, String url) {
        IFramePerspectiveConfiguration configuration = new IFramePerspectiveConfiguration();
        configuration.setName(name);
        configuration.setUrl(url);

        return configurationService.save(configuration);
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
}
