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
package org.jboss.bpm.console.server.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Simple java to xml conversion for displaying process data
 * within the console.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class Payload2XML
{

  public StringBuffer convert(String refId, Map<String, Object> javaPayload)
  {

    StringBuffer sb = new StringBuffer();

    try
    {
      List<Class> clz = new ArrayList<Class>(javaPayload.size()+2);
      clz.add(PayloadCollection.class);
      clz.add(PayloadEntry.class);

      List<PayloadEntry> data = new ArrayList<PayloadEntry>();

      for(String key : javaPayload.keySet())
      {
        Object payload = javaPayload.get(key);
        clz.add(payload.getClass());
        data.add(new PayloadEntry(key, payload));
      }

      PayloadCollection dataset = new PayloadCollection(refId, data);
      JAXBContext jaxbContext = JAXBContext.newInstance(clz.toArray(new Class[]{}));
      ByteArrayOutputStream bout = new ByteArrayOutputStream();

      Marshaller m = jaxbContext.createMarshaller();
      //m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
      m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

      m.marshal(dataset, bout);
      sb.append(new String(bout.toByteArray()));

    }
    catch (JAXBException e)
    {
      throw new RuntimeException("Payload2XML conversion failed",e );
    }

    return sb;
  }
}

