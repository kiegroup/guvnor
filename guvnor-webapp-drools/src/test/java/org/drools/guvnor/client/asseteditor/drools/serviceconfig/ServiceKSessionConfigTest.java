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

package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import java.util.HashMap;
import java.util.Set;

import org.junit.Test;

import static org.drools.ClockType.*;
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.MarshallingOption.*;
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.ProtocolOption.*;
import static org.junit.Assert.*;

public class ServiceKSessionConfigTest {

    @Test
    public void testDefaultValues() {
        final ServiceKSessionConfig ksession = new ServiceKSessionConfig("ksession1", null, null, null, null, null, null);

        assertNotNull(ksession);
        assertEquals("ksession1", ksession.getName());
        assertEquals("ksession1", ksession.getUrl());
        assertEquals(REST, ksession.getProtocol());
        assertEquals(XSTREAN, ksession.getMarshalling());
        assertNull(ksession.getClockType());
        assertNull(ksession.getKeepReference());

        assertNotNull(ksession.getListeners());
        assertEquals(0, ksession.getListeners().size());
        assertEquals(0, ksession.getAgendaListeners().size());
        assertEquals(0, ksession.getProcessListeners().size());
        assertEquals(0, ksession.getWorkingMemoryListeners().size());

        assertEquals(ksession, new ServiceKSessionConfig(ksession.getName()));
        assertEquals(ksession.hashCode(), new ServiceKSessionConfig(ksession.getName()).hashCode());

        assertEquals(ksession, new ServiceKSessionConfig(ksession.getName(), null, REST, XSTREAN, null, null, new HashMap<ListenerType, Set<String>>()));
        assertEquals(ksession.hashCode(), new ServiceKSessionConfig(ksession.getName(), null, REST, XSTREAN, null, null, new HashMap<ListenerType, Set<String>>()).hashCode());

        assertEquals(ksession, ksession);
        assertEquals(ksession, new ServiceKSessionConfig(ksession));
        assertEquals(ksession.hashCode(), new ServiceKSessionConfig(ksession).hashCode());
    }

    @Test
    public void testEqualsOnProtocol() {
        final ServiceKSessionConfig ksession1a = new ServiceKSessionConfig("ksession1", "url_ksession1", null, null, null, null, null);
        final ServiceKSessionConfig ksession1b = new ServiceKSessionConfig("ksession1", "url_ksession1", WEB_SERVICE, XSTREAN, null, null, null);
        final ServiceKSessionConfig ksession1c = new ServiceKSessionConfig("ksession1", "url_ksession1", REST, JSON, null, null, null);

        assertFalse(ksession1a.equals(ksession1b));
        assertFalse(ksession1a.equals(ksession1c));
        assertFalse(ksession1b.equals(ksession1c));

        ksession1b.setProtocol(REST);
        assertTrue(ksession1a.equals(ksession1b));

        ksession1c.setMarshalling(XSTREAN);
        assertTrue(ksession1a.equals(ksession1c));

        ksession1b.setUrl("custom_url");
        assertFalse(ksession1a.equals(ksession1b));

        ksession1c.setUrl("custom_url");
        assertFalse(ksession1a.equals(ksession1c));
        assertTrue(ksession1c.equals(ksession1b));
    }

    @Test
    public void testEqualsOnGeneral() {
        final ServiceKSessionConfig ksession1apn = new ServiceKSessionConfig("ksession1", "url_ksession1", null, null, PSEUDO_CLOCK, null, null);
        final ServiceKSessionConfig ksession1bnt = new ServiceKSessionConfig("ksession1", "url_ksession1", null, null, null, true, null);
        final ServiceKSessionConfig ksession1crf = new ServiceKSessionConfig("ksession1", "url_ksession1", null, null, REALTIME_CLOCK, false, null);
        final ServiceKSessionConfig ksession1dpf = new ServiceKSessionConfig("ksession1", "url_ksession1", null, null, PSEUDO_CLOCK, false, null);

        assertFalse(ksession1apn.equals(ksession1bnt));
        assertFalse(ksession1apn.equals(ksession1crf));
        assertFalse(ksession1bnt.equals(ksession1crf));
        assertFalse(ksession1bnt.equals(ksession1dpf));
        assertFalse(ksession1crf.equals(ksession1dpf));

        ksession1apn.setKeepReference(true);
        assertFalse(ksession1apn.equals(ksession1bnt));
        assertFalse(ksession1apn.equals(ksession1crf));
        assertFalse(ksession1apn.equals(ksession1dpf));

        ksession1apn.setKeepReference(false);
        assertTrue(ksession1apn.equals(ksession1dpf));

        ksession1bnt.setClockType(REALTIME_CLOCK);
        assertFalse(ksession1apn.equals(ksession1bnt));
        assertFalse(ksession1apn.equals(ksession1crf));
        assertFalse(ksession1bnt.equals(ksession1crf));
        assertFalse(ksession1bnt.equals(ksession1dpf));
        assertFalse(ksession1crf.equals(ksession1dpf));

        ksession1bnt.setKeepReference(false);
        assertFalse(ksession1apn.equals(ksession1bnt));
        assertFalse(ksession1apn.equals(ksession1crf));
        assertFalse(ksession1bnt.equals(ksession1dpf));
        assertFalse(ksession1crf.equals(ksession1dpf));
        assertTrue(ksession1bnt.equals(ksession1crf));
    }

    @Test
    public void simpleEquals() {
        final ServiceKSessionConfig ksession1 = new ServiceKSessionConfig("ksession1");

        assertTrue(ksession1.equals(ksession1));
        assertFalse(ksession1.equals(new ServiceKSessionConfig("mmm")));
        assertFalse(ksession1.equals(null));
        assertFalse(ksession1.equals("mm"));
    }

    @Test
    public void testAddRemoveListeners() {
        final ServiceKSessionConfig ksession = new ServiceKSessionConfig("ksession1");

        assertNotNull(ksession.getListeners());
        assertEquals(0, ksession.getListeners().size());
        assertEquals(0, ksession.getAgendaListeners().size());
        assertEquals(0, ksession.getProcessListeners().size());
        assertEquals(0, ksession.getWorkingMemoryListeners().size());

        ksession.addAgendaListener("a.b.Ccc");
        ksession.addAgendaListener("a.b.Ddd");
        ksession.addAgendaListener("a.b.Eee");
        ksession.addAgendaListener("a.b.Eee");
        assertEquals(3, ksession.getListeners().size());
        assertEquals(3, ksession.getAgendaListeners().size());
        assertEquals(0, ksession.getProcessListeners().size());
        assertEquals(0, ksession.getWorkingMemoryListeners().size());

        ksession.addProcessListener("a.b.Ccc");
        ksession.addProcessListener("a.b.Ddd");
        ksession.addProcessListener("a.b.Ddd");
        ksession.addProcessListener("a.b.Eee");
        assertEquals(3, ksession.getListeners().size());
        assertEquals(3, ksession.getAgendaListeners().size());
        assertEquals(3, ksession.getProcessListeners().size());
        assertEquals(0, ksession.getWorkingMemoryListeners().size());

        ksession.addWorkingMemoryListener("a.b.Ccc");
        ksession.addWorkingMemoryListener("a.b.Ccc");
        ksession.addWorkingMemoryListener("a.b.Ddd");
        ksession.addWorkingMemoryListener("a.b.Eee");
        assertEquals(3, ksession.getListeners().size());
        assertEquals(3, ksession.getAgendaListeners().size());
        assertEquals(3, ksession.getProcessListeners().size());
        assertEquals(3, ksession.getWorkingMemoryListeners().size());

        assertEquals(ksession, ksession);
        assertFalse(ksession.equals(new ServiceKSessionConfig(ksession.getName())));
        assertEquals(ksession, new ServiceKSessionConfig(ksession));
        assertEquals(ksession.hashCode(), new ServiceKSessionConfig(ksession).hashCode());

        ksession.removeAgendaListener("a.b.Ccc");
        ksession.removeAgendaListener("");
        ksession.removeAgendaListener(null);
        assertEquals(3, ksession.getListeners().size());
        assertEquals(2, ksession.getAgendaListeners().size());
        assertEquals(3, ksession.getProcessListeners().size());
        assertEquals(3, ksession.getWorkingMemoryListeners().size());

        ksession.removeProcessListener("a.b.Ddd");
        ksession.removeProcessListener("");
        ksession.removeProcessListener(null);
        assertEquals(3, ksession.getListeners().size());
        assertEquals(2, ksession.getAgendaListeners().size());
        assertEquals(2, ksession.getProcessListeners().size());
        assertEquals(3, ksession.getWorkingMemoryListeners().size());

        ksession.removeWorkingMemoryListener("a.b.Eee");
        ksession.removeWorkingMemoryListener("");
        ksession.removeWorkingMemoryListener(null);
        assertEquals(3, ksession.getListeners().size());
        assertEquals(2, ksession.getAgendaListeners().size());
        assertEquals(2, ksession.getProcessListeners().size());
        assertEquals(2, ksession.getWorkingMemoryListeners().size());

        ksession.removeAgendaListener("a.b.Ddd");
        ksession.removeProcessListener("a.b.Ddd");
        ksession.removeWorkingMemoryListener("a.b.Ddd");

        assertEquals(2, ksession.getListeners().size());
        assertEquals(2, ksession.getProcessListeners().size());
        assertEquals(1, ksession.getWorkingMemoryListeners().size());
        assertEquals(1, ksession.getAgendaListeners().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        new ServiceKSessionConfig((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyName() {
        new ServiceKSessionConfig("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOnCopy() {
        new ServiceKSessionConfig((ServiceKSessionConfig) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOnClockType() {
        final ServiceKSessionConfig ksession = new ServiceKSessionConfig("ksession1");
        ksession.setClockType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOnMarshalling() {
        final ServiceKSessionConfig ksession = new ServiceKSessionConfig("ksession1");
        ksession.setMarshalling(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOnProtocol() {
        final ServiceKSessionConfig ksession = new ServiceKSessionConfig("ksession1");
        ksession.setProtocol(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOnUrl() {
        final ServiceKSessionConfig ksession = new ServiceKSessionConfig("ksession1");
        ksession.setUrl(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyOnUrl() {
        final ServiceKSessionConfig ksession = new ServiceKSessionConfig("ksession1");
        ksession.setUrl("");
    }
}
