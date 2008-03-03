package org.jboss.security.acl;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.jboss.security.authorization.Resource;
import org.jboss.security.identity.Identity;

/**
 * <p>
 * Simple ACL implementation that keeps the entries in a Map whose keys are the
 * identities of the entries, to provide fast access.
 * </p>
 * 
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 */
@Entity
@Table(name = "ACL")
public class ACLImpl implements ACL, Serializable
{
   private static final long serialVersionUID = -6390609071167528812L;

   @Id
   @GeneratedValue
   private long aclID;

   @Transient
   private Resource resource;

   @Column(name = "resource")
   private String resourceAsString;

   @Transient
   private Map<Identity, ACLEntry> entriesMap;

   @OneToMany(mappedBy = "acl", fetch = FetchType.EAGER, cascade =
   {CascadeType.REMOVE, CascadeType.PERSIST})
   @Cascade(
   {org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
   private Collection<ACLEntryImpl> entries;

   /**
    * <p>
    * Builds an instance of {@code ACLImpl}. This constructor is required by the JPA specification.
    * </p>
    */
   ACLImpl()
   {
   }

   /**
    * <p>
    * Builds an instance of {@code ACLImpl} for the specified resource.
    * </p>
    * 
    * @param resource   a reference to the {@code Resource} associated with
    * the ACL being constructed.
    */
   public ACLImpl(Resource resource)
   {
      this(resource, new ArrayList<ACLEntry>());
   }

   /**
    * <p>
    * Builds an instance of {@code ACLImpl} for the specified resource, and initialize
    * it with the specified entries.
    * </p>
    * 
    * @param resource   a reference to the {@code Resource} associated with
    * the ACL being constructed.
    * @param entries    a {@code Collection} containing the ACL's initial entries.
    */
   public ACLImpl(Resource resource, Collection<ACLEntry> entries)
   {
      this.resource = resource;
      this.resourceAsString = Util.getResourceAsString(resource);
      this.entries = new ArrayList<ACLEntryImpl>();
      this.entriesMap = new HashMap<Identity, ACLEntry>();
      if (entries != null)
      {
         for (ACLEntry entry : entries)
         {
            ACLEntryImpl entryImpl = (ACLEntryImpl) entry;
            entryImpl.setAcl(this);
            this.entries.add(entryImpl);
            this.entriesMap.put(entryImpl.getIdentity(), entryImpl);
         }
      }
   }

   /**
    * <p>
    * Obtains the persistent id of this {@code ACLImpl}.
    * </p>
    * 
    * @return a {@code long} representing the persistent id this ACL.
    */
   public long getACLId()
   {
      return this.aclID;
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.security.acl.ACL#addEntry(org.jboss.security.acl.ACLEntry)
    */
   public boolean addEntry(ACLEntry entry)
   {
      // don't add a null entry or an entry that already existSELECT * FROM ACL_ENTRYs.
      if (entry == null || this.entriesMap.get(entry.getIdentity()) != null)
         return false;
      this.entries.add((ACLEntryImpl) entry);
      ((ACLEntryImpl) entry).setAcl(this);
      this.entriesMap.put(entry.getIdentity(), entry);
      return true;
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.security.acl.ACL#removeEntry(org.jboss.security.acl.ACLEntry)
    */
   public boolean removeEntry(ACLEntry entry)
   {
      this.entriesMap.remove(entry.getIdentity());
      return this.entries.remove(entry);
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.security.acl.ACL#getEntries()
    */
   public Collection<? extends ACLEntry> getEntries()
   {
      if (this.entriesMap == null)
      {
         this.entriesMap = new HashMap<Identity, ACLEntry>();
         for (ACLEntry entry : this.getEntries())
         {
            this.entriesMap.put(entry.getIdentity(), entry);
         }
      }
      return Collections.unmodifiableCollection(this.entries);
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.security.acl.ACL#getResource()
    */
   public Resource getResource()
   {
      return this.resource;
   }

   public void setResource(Resource resource)
   {
      if (this.resource != null)
         throw new IllegalStateException("ACL resource has already been set");
      this.resource = resource;
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.security.acl.ACL#isGranted(org.jboss.security.acl.ACLPermission, org.jboss.security.identity.Identity)
    */
   public boolean isGranted(ACLPermission permission, Identity identity)
   {
      // lookup the entry corresponding to the specified identity.
       
      getEntries();
       
      ACLEntry entry = this.entriesMap.get(identity);
      if (entry != null)
      {
         // check the permission associated with the identity.
         return entry.checkPermission(permission);
      }
      return false;
   }
}
