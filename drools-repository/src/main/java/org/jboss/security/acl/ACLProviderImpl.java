/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.security.acl;

import java.util.Map;
import java.util.Set;

import org.jboss.security.authorization.AuthorizationException;
import org.jboss.security.authorization.Resource;
import org.jboss.security.identity.Identity;
import org.jboss.util.NotImplementedException;

/**
 * <p>
 * This class is a simple {@code ACLProvider} implementation that maintains the ACLs in memory. It is
 * used mainly for testing purposes.
 * </p>
 * 
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 */
public class ACLProviderImpl implements ACLProvider
{

   /** persistence strategy used to retrieve the ACLs */
   private ACLPersistenceStrategy strategy;

   /**
    * @see org.jboss.security.acl.ACLProvider#initialize(java.util.Map, java.util.Map)
    */
   public void initialize(Map<String, Object> sharedState, Map<String, Object> options)
   {
   }

   /**
    * @see org.jboss.security.acl.ACLProvider#getEntitlements(java.lang.Class, org.jboss.security.authorization.Resource,
    *           org.jboss.security.identity.Identity)
    */
   public <T> Set<T> getEntitlements(Class<T> clazz, Resource resource, Identity identity)
         throws AuthorizationException
   {
      throw new NotImplementedException();
   }

   /**
    * @see org.jboss.security.acl.ACLProvider#getPersistenceStrategy()
    */
   public ACLPersistenceStrategy getPersistenceStrategy()
   {
      return this.strategy;
   }

   /**
    * @see org.jboss.security.acl.ACLProvider#setPersistenceStrategy(org.jboss.security.acl.ACLPersistenceStrategy)
    */
   public void setPersistenceStrategy(ACLPersistenceStrategy strategy)
   {
      this.strategy = strategy;
   }

   /**
    * @see org.jboss.security.acl.ACLProvider#isAccessGranted(org.jboss.security.authorization.Resource, 
    *           org.jboss.security.identity.Identity, org.jboss.security.acl.ACLPermission)
    */
   public boolean isAccessGranted(Resource resource, Identity identity, ACLPermission permission)
         throws AuthorizationException
   {
      if (this.strategy != null)
      {
         ACL acl = strategy.getACL(resource);
         if (acl != null)
            return acl.isGranted(permission, identity);
         else
            throw new AuthorizationException("Unable to locate an ACL for the resource " + resource);
      }
      throw new AuthorizationException("Unable to retrieve ACL: persistece strategy not set");
   }

   /**
    * @see org.jboss.security.acl.ACLProvider#tearDown()
    */
   public boolean tearDown()
   {
      return true;
   }

}