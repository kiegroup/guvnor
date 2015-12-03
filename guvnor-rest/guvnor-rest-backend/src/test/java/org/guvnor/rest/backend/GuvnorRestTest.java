/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.guvnor.rest.backend;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.junit.Test;
import org.kie.internal.remote.PermissionConstants;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

public class GuvnorRestTest {

    
    private static Reflections reflections = new Reflections(
            ClasspathHelper.forPackage("org.kie.remote.services.rest"),
            new TypeAnnotationsScanner(), new MethodAnnotationsScanner(), new SubTypesScanner());

    @Test
    public void allRestMethodsHaveRolesAssigned() { 
       Set<Method> restMethods = reflections.getMethodsAnnotatedWith(Path.class);
       restMethods.addAll(reflections.getMethodsAnnotatedWith(GET.class));
       restMethods.addAll(reflections.getMethodsAnnotatedWith(POST.class));
       restMethods.addAll(reflections.getMethodsAnnotatedWith(DELETE.class));
       restMethods.addAll(reflections.getMethodsAnnotatedWith(PUT.class));
       
       for( Method pathMethod : restMethods ) { 
          RolesAllowed rolesAllowedAnno = pathMethod.getAnnotation(RolesAllowed.class);
          assertNotNull( pathMethod.getDeclaringClass() + "." +  pathMethod.getName() + "(...) is missing a @RolesAllowed annotation!", 
                  rolesAllowedAnno);
          
          boolean basicRestRoleFound = false;
          for( String role : rolesAllowedAnno.value() ) { 
            if( PermissionConstants.REST_ROLE.equals(role) ) { 
                basicRestRoleFound = true;
                break;
            }
          }
          assertTrue( pathMethod.getDeclaringClass() + "." +  pathMethod.getName() + "(...) is does not have the " + PermissionConstants.REST_ROLE + " role",
                  basicRestRoleFound);
       }
    }
    
    @Test
    public void regexTest() {
        String regex = ProjectResource.REGEX;
        
        assertTrue( "a", "a".matches(regex));
        assertTrue( "aa", "aa".matches(regex));
        assertTrue( "aaa", "aaa".matches(regex));
        
        String [] badStrings = { 
                "/", "\n", "\r", "\t", "\0", "\f",
                "'", "?", "*", "<", ">", "|", "\"", ":",
                " ", "~", "^", "[", "]", "@"
        };
        for( String bad : badStrings ) { 
           assertFalse( bad, bad.matches(regex) );
        }
       
        String [] badDoubleAndSingleDotStrings =  { 
                // double dots
               "..", "a..", "..a",
               "a..a", "aa..a", "a..aa",
               // start or begin with . 
               ".", ".a", "a.",
               ".a.a", "a.a.", ".a.a.",
        };
        for( String bad : badDoubleAndSingleDotStrings ) { 
           assertFalse( bad, bad.matches(regex) );
        }
        
        String [] goodDotStrings = { 
                "a.a",
                "aa.a",
                "a.a.a",
                "aa.aa.aa",
                "a.a",
        };
        for( String bad : goodDotStrings ) { 
           assertTrue( bad, bad.matches(regex) );
        }
        
       
        // does the negative lookahead ( ?!\d{2} ) 
        // allow 2 of the bad characters?
        String [] badComplexStrings = { 
                "a//",
                "a//a"
        };
        for( String bad : badComplexStrings ) { 
           assertFalse( bad, bad.matches(regex) );
        }
    }
    
}
