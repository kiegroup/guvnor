/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.repository;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class PerspectivesConfigurationItemTest extends RepositoryTestCase {

    private RulesRepository rulesRepository;

    @Before
    public void setUp() throws Exception {
        rulesRepository = getRepo();
    }

    @Test
    public void testCreateConfiguration() throws Exception {
        IFramePerspectiveConfigurationItem createdPerspectiveConfigurationItem = createTestItem("test name", "http://jboss.org/drools");

        assertConfigurationItem("test name", "http://jboss.org/drools", createdPerspectiveConfigurationItem);
    }

    @Test
    public void testLoad() throws Exception {
        IFramePerspectiveConfigurationItem createdPerspectiveConfigurationItem = createTestItem("test name", "http://jboss.org/drools");

        IFramePerspectiveConfigurationItem loadedPerspectiveConfigurationItem = rulesRepository.loadPerspectivesConfiguration(createdPerspectiveConfigurationItem.getUuid());

        assertConfigurationItem("test name", "http://jboss.org/drools", loadedPerspectiveConfigurationItem);
    }

    @Test
    public void testRemove() throws Exception {
        IFramePerspectiveConfigurationItem createdPerspectiveConfigurationItem = createTestItem("test name", "http://jboss.org/drools");

        createdPerspectiveConfigurationItem.remove();

        try {
            rulesRepository.loadPerspectivesConfiguration(createdPerspectiveConfigurationItem.getUuid());
            fail("Item should not be in the repository.");
        } catch (RulesRepositoryException e) {
            // Success
        }
    }

    @Test
    public void testLoadListWhenThereIsNoPerspectivesSaved() throws Exception {
        Collection<IFramePerspectiveConfigurationItem> perspectiveConfigurationItems = rulesRepository.listPerspectiveConfigurations();

        assertNotNull(perspectiveConfigurationItems);
        assertEquals(0, perspectiveConfigurationItems.size());
    }

    @Test
    public void testLoadList() throws Exception {
        createTestItem("test name", "http://jboss.org/drools");
        createTestItem("Runtime", "http://localhost/runtime");
        createTestItem("Manual", "http://localhost/manual");

        Collection<IFramePerspectiveConfigurationItem> perspectiveConfigurationItems = rulesRepository.listPerspectiveConfigurations();

        assertEquals(3, perspectiveConfigurationItems.size());
    }

    @Test(expected = RulesRepositoryException.class)
    public void testLoadingSomethingThatDoesNotExist() throws Exception {
        rulesRepository.loadPerspectivesConfiguration("does-not-exist");
    }

    private IFramePerspectiveConfigurationItem createTestItem(String name, String url) {
        return rulesRepository.createPerspectivesConfiguration(name, url);
    }

    private void assertConfigurationItem(String name, String url, IFramePerspectiveConfigurationItem loadedPerspectiveConfigurationItem) {
        assertNotNull(loadedPerspectiveConfigurationItem.getUuid());
        assertEquals(name, loadedPerspectiveConfigurationItem.getName());
        assertEquals(url, loadedPerspectiveConfigurationItem.getUrl());
    }

}
