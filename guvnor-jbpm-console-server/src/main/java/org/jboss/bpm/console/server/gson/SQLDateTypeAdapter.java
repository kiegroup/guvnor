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
package org.jboss.bpm.console.server.gson;

import com.google.gson.*;

import java.util.Date;
import java.text.*;
import java.lang.reflect.Type;


/**
 * A default type adapter for a {@link java.util.Date} object.<br>
 * Create a GSON instance that can serialize/deserialize "java.util.Date" objects:
 * <pre>
 * Gson gson = new GsonBuilder()
 * .registerTypeAdapter(new DateTypeAdapter())
 * .create();
 * </pre>
 *
 * @author Joel Leitch
 */
public class SQLDateTypeAdapter implements JsonSerializer<java.sql.Timestamp>, JsonDeserializer<Date>
{
   private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   public JsonElement serialize(java.sql.Timestamp src, Type typeOfSrc, JsonSerializationContext context)
   {
      String dateFormatAsString = format.format(src);
      return new JsonPrimitive(dateFormatAsString);
   }

   public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
     throws JsonParseException
   {
      if (!(json instanceof JsonPrimitive)) {
         throw new JsonParseException("The date should be a string value");
      }

      try
      {
         return format.parse(json.getAsString());
      }
      catch (ParseException e)
      {
         throw new JsonParseException(e);
      }

   }
}
