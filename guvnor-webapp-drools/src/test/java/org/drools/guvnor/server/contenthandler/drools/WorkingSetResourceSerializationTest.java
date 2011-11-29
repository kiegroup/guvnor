/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.server.contenthandler.drools;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;
import org.junit.Test;


public class WorkingSetResourceSerializationTest{

    @Test
    public void testUnmarshalling() throws Exception{
        InputStream workingSetResource = WorkingSetResourceSerializationTest.class.getResourceAsStream("/org/drools/guvnor/server/contenthandler/serializedWorkingSet.xml");

        WorkingSetHandler wsh = new WorkingSetHandler();
        
        WorkingSetConfigData workingSetConfigData = wsh.unmarshallContent(IOUtils.toString(workingSetResource));
        
        Assert.assertNotNull(workingSetConfigData);
        
        List<String> validFacts = Arrays.asList(workingSetConfigData.validFacts);
        
        Assert.assertEquals(3, validFacts.size());
        
        Assert.assertTrue(validFacts.contains("Applicant"));
        Assert.assertTrue(validFacts.contains("Bankruptcy"));
        Assert.assertTrue(validFacts.contains("IncomeSource"));
        System.out.println("ESTEBAN!");
        
    }

}
