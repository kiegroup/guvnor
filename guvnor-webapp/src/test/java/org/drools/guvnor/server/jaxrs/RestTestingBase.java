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
package org.drools.guvnor.server.jaxrs;

import org.drools.guvnor.server.GuvnorTestBase;

public class RestTestingBase extends GuvnorTestBase {
	
    public void setup() {
    	this.setUpGuvnorTestBase();
    }

    public void destroy() {
    	this.tearDownGuvnorTestBase();
    }
    
/*    public static String GetContent (InputStream is) throws IOException {
        StringAppender ret = new StringAppender();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            ret.append(line + "\n");
        }

        return ret.toString();
    }
    
    public static String GetContent (HttpURLConnection connection) throws IOException {
        StringAppender ret = new StringAppender();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            ret.append(line + "\n");
        }

        return ret.toString();
    }

    protected Package createTestPackage(String title) {
        Category c = new Category();
        c.setName("test");

        Package p = new Package();
        PackageMetadata metadata = new PackageMetadata();
        metadata.setCreated(new Date(System.currentTimeMillis()));
        metadata.setUuid(UUID.randomUUID().toString());
        metadata.setLastContributor("awaterma");
        metadata.setLastModified(new Date(System.currentTimeMillis()));

        p.setMetadata(metadata);
        p.setCategory(c);
        p.setCheckInComment("Check in comment for test package.");
        p.setTitle(title);
        p.setDescription("A simple test package with 0 assets.");
        return p;
    }*/
    
    //Put here to make the code compile. 
    public String generateBaseUrl() {
    	return null;
    }
}
