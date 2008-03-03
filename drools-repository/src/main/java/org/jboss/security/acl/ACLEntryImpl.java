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

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.jboss.security.identity.Identity;

/**
 * <p>
  * This class represents an entry in the Access Control List (ACL), and associates a permission
 * to an identity. This implementation only stores permissions of type {@code BitMaskPermission},
 * and can also only check permissions of that type.
 * </p>
 * 
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 */
@Entity
@Table(name = "ACL_ENTRY")
public class ACLEntryImpl implements ACLEntry, Serializable
{
   private static final long serialVersionUID = -2985214023383451768L;

   @Id
   @GeneratedValue
   private long entryID;

   @Transient
   private BitMaskPermission permission;

   /* persist only the bitmask */
   private int bitMask;

   @Transient
   private Identity identity;

   /* persist the string representation of the identity */
   private String identityString;

   @ManyToOne
   private ACLImpl acl;

   /**
    * <p>
    * Builds an instance of {@code ACLEntryImpl}. This constructor is required by the JPA 
    * specification.
    * </p>
    */
   ACLEntryImpl()
   {
   }

   /**
    * <p>
    * Builds an instance of {@code ACLEntryImpl} with the specified permission and identity.
    * </p>
    * 
    * @param permission the {@code ACLPermission} granted to the associated identity.
    * @param identity   the {@code Identity} for which the permission is being granted.
    */
   public ACLEntryImpl(BitMaskPermission permission, Identity identity)
   {
      this.permission = permission;
      this.identity = identity;
   }

   /**
    * <p>
    * Obtains the persistent id of this {@code ACLEntryImpl}.
    * </p>
    * 
    * @return a {@code long} representing the persistent id this entry.
    */
   public long getACLEntryId()
   {
      return this.entryID;
   }

   /**
    * <p>
    * Method called by the JPA layer before persisting the fields.
    * </p>
    */
   @PrePersist
   @SuppressWarnings("unused")
   private void setPersistentFields()
   {
      if (this.permission != null)
         this.bitMask = this.permission.getMaskValue();
      this.identityString = Util.getIdentityAsString(this.identity);
   }

   /**
    * <p>
    * Method called by the JPA layer after loading the persisted object.
    * </p>
    */
   @PostLoad
   @SuppressWarnings("unused")
   private void loadState()
   {
      if (this.permission != null)
         throw new IllegalStateException("ACLEntry permission has already been set");
      this.permission = new CompositeACLPermission(this.bitMask);

      if (this.identity != null)
         throw new IllegalStateException("ACLEntry identity has already been set");
      this.identity = Util.getIdentityFromString(identityString);
   }

   public ACLImpl getAcl()
   {
      return this.acl;
   }

   public void setAcl(ACLImpl acl)
   {
      this.acl = acl;
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.security.acl.ACLEntry#getIdentity()
    */
   public Identity getIdentity()
   {
      return this.identity;
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.security.acl.ACLEntry#getPermission()
    */
   public ACLPermission getPermission()
   {
      return this.permission;
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.security.acl.ACLEntry#checkPermission(org.jboss.security.acl.ACLPermission)
    */
   public boolean checkPermission(ACLPermission permission)
   {
      if (!(permission instanceof BitMaskPermission))
         return false;
      BitMaskPermission bitmaskPermission = (BitMaskPermission) permission;
      // an empty permission is always part of another permission.
      if (bitmaskPermission.getMaskValue() == 0)
         return true;
      // simple implementation: if any bit matches, return true.
      return (this.permission.getMaskValue() & bitmaskPermission.getMaskValue()) != 0;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof ACLEntryImpl)
      {
         ACLEntryImpl entry = (ACLEntryImpl) obj;
         return entry.permission.getMaskValue() == this.permission.getMaskValue()
               && entry.getIdentity().getName().equals(this.identity.getName());
      }
      return false;
   }
}
