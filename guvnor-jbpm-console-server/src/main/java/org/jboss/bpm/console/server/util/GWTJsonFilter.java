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

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class GWTJsonFilter implements Filter
{

   public FilterConfig filterConfig;
   private static final String COLLECTION_PREFIX = "{\"wrapper\":";
   private static final String COLLECTION_SUFFIX = "}";

   public void init(FilterConfig filterConfig) throws ServletException
   {
      this.filterConfig = filterConfig;
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
     throws IOException, ServletException
   {

      boolean isSOPCallback = false;

      // identify GWT json requests
      if(request.getParameter("callback")!=null)
         isSOPCallback = true;

      // sneak in repsonse wrapper
      OutputStream out = response.getOutputStream();
      BufferedResponseWrapper wrapper =  new BufferedResponseWrapper((HttpServletResponse) response);

      // proceed chain
      chain.doFilter(request, wrapper);

      // add callback std. json output
      String contentType = response.getContentType() != null ? response.getContentType() : "application/octet-stream";
      boolean isJSONEncoding = contentType.equals("application/json");
      StringBuffer sb = null;
      if(isJSONEncoding)
      {
         String payload = new String(wrapper.getData());
         String gwtextFriendly = trimPayload(payload);

         sb = new StringBuffer();

         if(isSOPCallback)
         {
            sb.append(request.getParameter("callback"));
            sb.append("(");
         }

         // Strip wrapper when JSONRequest
         sb.append(gwtextFriendly);

         if(isSOPCallback)
         {
            sb.append(");");
         }

      }

      // flush
      if(sb!=null)
         out.write(sb.toString().getBytes());
      else
         out.write(wrapper.getData());

      out.flush();
      out.close();
   }

   private String trimPayload(String payload)
   {
      String s = payload;
      if(s.startsWith(COLLECTION_PREFIX))
      {
         s = payload.substring( COLLECTION_PREFIX.length(), payload.lastIndexOf(COLLECTION_SUFFIX));
      }
      return s;
   }

   public void destroy()
   {

   }
}
