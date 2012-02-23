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

package org.drools.ide.common.server.testscenarios.executors;

import org.drools.Cheesery;
import org.drools.ide.common.client.modeldriven.testing.CallFieldValue;
import org.drools.ide.common.client.modeldriven.testing.CallMethod;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class MethodExecutorTest {

    @Test
    public void testCallMethodNoArgumentOnFact() throws Exception {

        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        MethodExecutor methodExecutor = new MethodExecutor(populatedData);


        Cheesery listChesse = new Cheesery();
        listChesse.setTotalAmount(1000);
        populatedData.put("cheese",
                listChesse);
        CallMethod mCall = new CallMethod();
        mCall.setVariable("cheese");
        mCall.setMethodName("setTotalAmountToZero");

        methodExecutor.executeMethod(mCall);

        assertTrue(listChesse.getTotalAmount() == 0);
    }

    @Test
    public void testCallMethodOnStandardArgumentOnFact() throws Exception {

        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        MethodExecutor methodExecutor = new MethodExecutor(populatedData);


        Cheesery listChesse = new Cheesery();
        listChesse.setTotalAmount(1000);
        populatedData.put("cheese",
                listChesse);
        CallMethod mCall = new CallMethod();
        mCall.setVariable("cheese");
        mCall.setMethodName("addToTotalAmount");
        CallFieldValue field = new CallFieldValue();
        field.value = "5";
        mCall.addFieldValue(field);

        methodExecutor.executeMethod(mCall);
        assertTrue(listChesse.getTotalAmount() == 1005);
    }

    @Test
    public void testCallMethodOnClassArgumentOnFact() throws Exception {

        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        MethodExecutor methodExecutor = new MethodExecutor(populatedData);

        Cheesery listChesse = new Cheesery();
        listChesse.setTotalAmount(1000);
        populatedData.put("cheese",
                listChesse);
        Cheesery.Maturity m = new Cheesery.Maturity();
        populatedData.put("m",
                m);
        CallMethod mCall = new CallMethod();
        mCall.setVariable("cheese");
        mCall.setMethodName("setGoodMaturity");
        CallFieldValue field = new CallFieldValue();
        field.value = "=m";
        mCall.addFieldValue(field);

        methodExecutor.executeMethod(mCall);

        assertTrue(listChesse.getMaturity().equals(m));
        assertTrue(listChesse.getMaturity() == m);
    }

    @Test
    public void testCallMethodOnClassArgumentAndOnArgumentStandardOnFact() throws Exception {


        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        MethodExecutor methodExecutor = new MethodExecutor(populatedData);

        Cheesery listChesse = new Cheesery();
        listChesse.setTotalAmount(1000);
        populatedData.put("cheese",
                listChesse);
        Cheesery.Maturity m = new Cheesery.Maturity("veryYoung");
        populatedData.put("m",
                m);
        CallMethod mCall = new CallMethod();
        mCall.setVariable("cheese");
        mCall.setMethodName("setAgeToMaturity");
        CallFieldValue field = new CallFieldValue();
        field.value = "=m";
        mCall.addFieldValue(field);
        CallFieldValue field2 = new CallFieldValue();
        field2.value = "veryold";
        mCall.addFieldValue(field2);

        methodExecutor.executeMethod(mCall);
        assertTrue(m.getAge().equals("veryold"));
    }
}
