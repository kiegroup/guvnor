package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Artifact_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getCheckinComment(org.drools.guvnor.client.rpc.Artifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Artifact::checkinComment;
  }-*/;
  
  private static native void setCheckinComment(org.drools.guvnor.client.rpc.Artifact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Artifact::checkinComment = value;
  }-*/;
  
  private static native java.util.Date getDateCreated(org.drools.guvnor.client.rpc.Artifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Artifact::dateCreated;
  }-*/;
  
  private static native void setDateCreated(org.drools.guvnor.client.rpc.Artifact instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Artifact::dateCreated = value;
  }-*/;
  
  private static native java.lang.String getDescription(org.drools.guvnor.client.rpc.Artifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Artifact::description;
  }-*/;
  
  private static native void setDescription(org.drools.guvnor.client.rpc.Artifact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Artifact::description = value;
  }-*/;
  
  private static native java.lang.String getFormat(org.drools.guvnor.client.rpc.Artifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Artifact::format;
  }-*/;
  
  private static native void setFormat(org.drools.guvnor.client.rpc.Artifact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Artifact::format = value;
  }-*/;
  
  private static native boolean getIsArchived(org.drools.guvnor.client.rpc.Artifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Artifact::isArchived;
  }-*/;
  
  private static native void setIsArchived(org.drools.guvnor.client.rpc.Artifact instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Artifact::isArchived = value;
  }-*/;
  
  private static native boolean getIsReadOnly(org.drools.guvnor.client.rpc.Artifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Artifact::isReadOnly;
  }-*/;
  
  private static native void setIsReadOnly(org.drools.guvnor.client.rpc.Artifact instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Artifact::isReadOnly = value;
  }-*/;
  
  private static native java.lang.String getLastContributor(org.drools.guvnor.client.rpc.Artifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Artifact::lastContributor;
  }-*/;
  
  private static native void setLastContributor(org.drools.guvnor.client.rpc.Artifact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Artifact::lastContributor = value;
  }-*/;
  
  private static native java.util.Date getLastModified(org.drools.guvnor.client.rpc.Artifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Artifact::lastModified;
  }-*/;
  
  private static native void setLastModified(org.drools.guvnor.client.rpc.Artifact instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Artifact::lastModified = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.guvnor.client.rpc.Artifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Artifact::name;
  }-*/;
  
  private static native void setName(org.drools.guvnor.client.rpc.Artifact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Artifact::name = value;
  }-*/;
  
  private static native java.lang.String getState(org.drools.guvnor.client.rpc.Artifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Artifact::state;
  }-*/;
  
  private static native void setState(org.drools.guvnor.client.rpc.Artifact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Artifact::state = value;
  }-*/;
  
  private static native java.lang.String getUuid(org.drools.guvnor.client.rpc.Artifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Artifact::uuid;
  }-*/;
  
  private static native void setUuid(org.drools.guvnor.client.rpc.Artifact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Artifact::uuid = value;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native long getVersionNumber(org.drools.guvnor.client.rpc.Artifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Artifact::versionNumber;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native void setVersionNumber(org.drools.guvnor.client.rpc.Artifact instance, long value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Artifact::versionNumber = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.Artifact instance) throws SerializationException {
    setCheckinComment(instance, streamReader.readString());
    setDateCreated(instance, (java.util.Date) streamReader.readObject());
    setDescription(instance, streamReader.readString());
    setFormat(instance, streamReader.readString());
    setIsArchived(instance, streamReader.readBoolean());
    setIsReadOnly(instance, streamReader.readBoolean());
    setLastContributor(instance, streamReader.readString());
    setLastModified(instance, (java.util.Date) streamReader.readObject());
    setName(instance, streamReader.readString());
    setState(instance, streamReader.readString());
    setUuid(instance, streamReader.readString());
    setVersionNumber(instance, streamReader.readLong());
    
  }
  
  public static org.drools.guvnor.client.rpc.Artifact instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.Artifact();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.Artifact instance) throws SerializationException {
    streamWriter.writeString(getCheckinComment(instance));
    streamWriter.writeObject(getDateCreated(instance));
    streamWriter.writeString(getDescription(instance));
    streamWriter.writeString(getFormat(instance));
    streamWriter.writeBoolean(getIsArchived(instance));
    streamWriter.writeBoolean(getIsReadOnly(instance));
    streamWriter.writeString(getLastContributor(instance));
    streamWriter.writeObject(getLastModified(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeString(getState(instance));
    streamWriter.writeString(getUuid(instance));
    streamWriter.writeLong(getVersionNumber(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.Artifact_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.Artifact_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.Artifact)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.Artifact_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.Artifact)object);
  }
  
}
