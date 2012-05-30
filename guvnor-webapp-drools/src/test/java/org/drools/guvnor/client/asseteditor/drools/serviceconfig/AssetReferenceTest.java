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

import org.junit.Test;

import static org.junit.Assert.*;

public class AssetReferenceTest {

    @Test
    public void testAssetReferenceExplicitConstructor() {
        final AssetReference assetReference = new AssetReference("a", "b", "c", "d", "e");

        assertNotNull(assetReference);
        assertEquals("a", assetReference.getPackageRef());
        assertEquals("b", assetReference.getName());
        assertEquals("c", assetReference.getFormat());
        assertEquals("d", assetReference.getUrl());
        assertEquals("e", assetReference.getUuid());
    }

    @Test
    public void testAssetReferenceToValueConsistency() {
        final AssetReference assetReference = new AssetReference("a", "b", "c", "d", "e");

        assertEquals(assetReference, new AssetReference(assetReference));
        assertEquals(assetReference.hashCode(), new AssetReference(assetReference).hashCode());

        assertTrue(assetReference.equals(assetReference));
        assertTrue(assetReference.equals(new AssetReference("a", "b", "c", "d", "e")));
        assertFalse(assetReference.equals(new AssetReference("a", "b", "c", "d", "x")));
        assertFalse(assetReference.equals(new AssetReference("a", "b", "c", "x", "e")));
        assertFalse(assetReference.equals(new AssetReference("a", "b", "x", "d", "e")));
        assertFalse(assetReference.equals(new AssetReference("a", "x", "c", "d", "e")));
        assertFalse(assetReference.equals(new AssetReference("x", "b", "c", "d", "e")));
        assertFalse(assetReference.equals(null));
        assertFalse(assetReference.equals("?!"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceNullConstructor() {
        new AssetReference(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorNull1() {
        new AssetReference(null, "b", "c", "d", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorEmpty1() {
        new AssetReference("", "b", "c", "d", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorNull2() {
        new AssetReference("a", null, "c", "d", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorEmpty2() {
        new AssetReference("a", "", "c", "d", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorNull3() {
        new AssetReference("a", "b", null, "d", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorEmpty3() {
        new AssetReference("a", "b", "", "d", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorNull4() {
        new AssetReference("a", "b", "c", null, "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorEmpty4() {
        new AssetReference("a", "b", "c", "", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorNull5() {
        new AssetReference("a", "b", "c", "d", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorEmpty5() {
        new AssetReference("a", "b", "c", "d", "");
    }

}
