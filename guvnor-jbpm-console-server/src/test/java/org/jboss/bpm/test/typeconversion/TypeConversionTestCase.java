/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.bpm.test.typeconversion;

import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import org.jboss.bpm.console.server.util.Payload2XML;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class TypeConversionTestCase extends TestCase
{
  public void testComplexTypes() throws Exception
  {
    Map<String, Object> javaPayload = new HashMap<String,Object>();
    javaPayload.put("not annotated", new ComplexType("Hello World"));
    javaPayload.put("a simple type", "Just a java String");
    javaPayload.put("a date field", new Date());
    javaPayload.put("annotation present", new AnnotatedComplexType());
    javaPayload.put("illegal type", new IllegalType());
    javaPayload.put("77illegal name % $$ - 1", "Hi There");
    Payload2XML t = new Payload2XML();
    System.out.println(t.convert("SampleProcess", javaPayload).toString());  
  }

}
