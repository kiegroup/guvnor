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

import javax.transaction.UserTransaction;
import javax.transaction.SystemException;
import javax.naming.InitialContext;
import java.lang.reflect.Method;

/**
 * Decorates process engine invocations with common system aspects, i.e. transaction demarcation.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class InvocationProxy implements java.lang.reflect.InvocationHandler
{
  private Object obj;

  public static Object newInstance(Object obj) {
    return java.lang.reflect.Proxy.newProxyInstance(
        obj.getClass().getClassLoader(),
        obj.getClass().getInterfaces(),
        new InvocationProxy(obj));
  }

  private InvocationProxy(Object obj) {
    this.obj = obj;
  }

  public Object invoke(Object proxy, Method m, Object[] args)
      throws Throwable
  {
    Object result;

    InitialContext ctx = new InitialContext();
    UserTransaction tx = (UserTransaction)ctx.lookup("UserTransaction");
    

    try
    {
      // before method invocation
      tx.begin();

      // field target invocation
      result = m.invoke(obj, args);

      tx.commit();

    }
    catch (Exception e)
    {
      if(tx!=null)
      {
        try
        {
          tx.rollback();
        }
        catch (SystemException e1) {}
      }

      throw new RuntimeException("Unexpected invocation exception: " + e.getMessage(), e);

    }
    finally
    {
      // after method invocation

    }

    return result;
  }
}