package org.drools.guvnor.server.security;
/*
 * Copyright 2005 JBoss Inc
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



import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;

/**
 * This will let any user in, effectively removing any authentication (as the system 
 * will attempt to auto login the first time).
 * @author Michael Neale
 */
@Name("nilAuthenticator")
public class NilAuthenticator {
    
    private static final Logger log = Logger.getLogger( NilAuthenticator.class );
    
    public boolean authenticate() {
        log.info( "All users are guests.");
        return true;
    }
}