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
package org.jboss.bpm.console.client.util;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class JSONWalk
{
  private JSONValue root;


  private JSONWalk(JSONValue root)
  {
    this.root = root;
  }

  public static JSONWalk on(JSONValue root)
  {
    return new JSONWalk(root);
  }

  public JSONWrapper next(String name)
  {
    if (null == root || root.isObject() == null) return null;
    JSONObject rootObject = root.isObject();

    JSONWrapper match = null;
    Set<String> keySet = rootObject.keySet();

    Iterator it = keySet.iterator();
    while (it.hasNext())
    {
      String s = (String) it.next();
      JSONValue child = rootObject.get(s);
      if (name.equals(s))
      {
        match = new JSONWrapper(child);
        break;
      }
      else
      {
        match = JSONWalk.on(child).next(name);
      }
    }

    return match;
  }

  public class JSONWrapper
  {

    private JSONValue value;

    public JSONWrapper(JSONValue value)
    {
      this.value = value;
    }

    public int asInt()
    {
      if (value.isNumber() != null)
      {
        return new Double(value.isNumber().getValue()).intValue();
      }
      else
      {
        throw new IllegalArgumentException("Not a number: " + value);
      }
    }

    public long asLong()
    {
      if (value.isNumber() != null)
      {
        return new Double(value.isNumber().getValue()).longValue();
      }
      else
      {
        throw new IllegalArgumentException("Not a number: " + value);
      }
    }

    public double asDouble()
    {
      if (value.isNumber() != null)
      {
        return value.isNumber().getValue();
      }
      else
      {
        throw new IllegalArgumentException("Not a number: " + value);
      }
    }

    public String asString()
    {
      if (value.isString() != null)
      {
        return value.isString().stringValue();
      }
      else
      {
        throw new IllegalArgumentException("Not a string: " + value);
      }
    }

    public boolean asBool()
    {
      if (value.isBoolean() != null)
      {
        return value.isBoolean().booleanValue();
      }
      else
      {
        throw new IllegalArgumentException("Not a boolean: " + value);
      }
    }

    public Date asDate()
    {
      if (value.isString() != null)
      {
        SimpleDateFormat df = new SimpleDateFormat();
        return df.parse(value.isString().stringValue());
      }
      else
      {
        throw new IllegalArgumentException("Not a date string: " + value);
      }
    }


    public JSONArray asArray()
    {
      if (value.isArray() != null)
      {
        return value.isArray();
      }
      else
      {
        throw new IllegalArgumentException("Not a number: " + value);
      }
    }

    public JSONObject asObject()
    {
      if (value.isObject() != null)
      {
        return value.isObject();
      }
      else
      {
        throw new IllegalArgumentException("Not an object: " + value);
      }
    }


    public String toString() throws JSONException
    {
      return value.toString();
    }
  }
}
