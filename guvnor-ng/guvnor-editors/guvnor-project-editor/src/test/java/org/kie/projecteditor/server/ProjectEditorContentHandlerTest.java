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

package org.kie.projecteditor.server;

import org.junit.Test;
import org.kie.guvnor.editors.projecteditor.server.ProjectEditorContentHandler;
import org.kie.guvnor.editors.projecteditor.shared.model.KProjectModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertNotNull;

public class ProjectEditorContentHandlerTest {

    @Test
    public void testMostSimpleOneICanThinkOf() throws Exception {
        KProjectModel model = ProjectEditorContentHandler.toModel( readResource( "simpleKProject.xml" ) );

        assertNotNull(model);
    }

    private String readResource(String name) {
        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(name)));
            String text = null;

            while ((text = reader.readLine()) != null) {
                contents.append(text);
            }

        } catch (Exception e) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            throw new IllegalStateException("Error while reading file.", e);
        }

        return contents.toString();
    }
}
