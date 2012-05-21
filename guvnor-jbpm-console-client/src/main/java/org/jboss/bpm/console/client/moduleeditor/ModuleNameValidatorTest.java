/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.bpm.console.client.moduleeditor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.bpm.console.client.moduleeditor.ModuleNameValidator;
import org.junit.Test;

public class ModuleNameValidatorTest {

    @Test
    public void testPackageNameValidation() {
        Assert.assertTrue(ModuleNameValidator.validatePackageName("foo.bar"));
        Assert.assertTrue(ModuleNameValidator.validatePackageName("whee.waa2"));
        Assert.assertTrue(ModuleNameValidator.validatePackageName("whee.waa2.whee.waa2.whee.waa2.whee.waa2"));
        Assert.assertTrue(ModuleNameValidator.validatePackageName("こんにちは.世界"));
        Assert.assertFalse(ModuleNameValidator.validatePackageName(" hey DJ "));
        Assert.assertFalse(ModuleNameValidator.validatePackageName(""));
        Assert.assertFalse(ModuleNameValidator.validatePackageName(" "));
        Assert.assertFalse(ModuleNameValidator.validatePackageName("test\ning"));
        Assert.assertFalse(ModuleNameValidator.validatePackageName("\ttesting"));
        Assert.assertFalse(ModuleNameValidator.validatePackageName(null));

    }

}
