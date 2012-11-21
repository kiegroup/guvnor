package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class QueryMetadataPageRequest_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.Date getCreatedAfter(org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryMetadataPageRequest::createdAfter;
  }-*/;
  
  private static native void setCreatedAfter(org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryMetadataPageRequest::createdAfter = value;
  }-*/;
  
  private static native java.util.Date getCreatedBefore(org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryMetadataPageRequest::createdBefore;
  }-*/;
  
  private static native void setCreatedBefore(org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryMetadataPageRequest::createdBefore = value;
  }-*/;
  
  private static native java.util.Date getLastModifiedAfter(org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryMetadataPageRequest::lastModifiedAfter;
  }-*/;
  
  private static native void setLastModifiedAfter(org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryMetadataPageRequest::lastModifiedAfter = value;
  }-*/;
  
  private static native java.util.Date getLastModifiedBefore(org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryMetadataPageRequest::lastModifiedBefore;
  }-*/;
  
  private static native void setLastModifiedBefore(org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryMetadataPageRequest::lastModifiedBefore = value;
  }-*/;
  
  private static native java.util.List getMetadata(org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryMetadataPageRequest::metadata;
  }-*/;
  
  private static native void setMetadata(org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance, java.util.List value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryMetadataPageRequest::metadata = value;
  }-*/;
  
  private static native boolean getSearchArchived(org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryMetadataPageRequest::searchArchived;
  }-*/;
  
  private static native void setSearchArchived(org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryMetadataPageRequest::searchArchived = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance) throws SerializationException {
    setCreatedAfter(instance, (java.util.Date) streamReader.readObject());
    setCreatedBefore(instance, (java.util.Date) streamReader.readObject());
    setLastModifiedAfter(instance, (java.util.Date) streamReader.readObject());
    setLastModifiedBefore(instance, (java.util.Date) streamReader.readObject());
    setMetadata(instance, (java.util.List) streamReader.readObject());
    setSearchArchived(instance, streamReader.readBoolean());
    
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.QueryMetadataPageRequest instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.QueryMetadataPageRequest();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.QueryMetadataPageRequest instance) throws SerializationException {
    streamWriter.writeObject(getCreatedAfter(instance));
    streamWriter.writeObject(getCreatedBefore(instance));
    streamWriter.writeObject(getLastModifiedAfter(instance));
    streamWriter.writeObject(getLastModifiedBefore(instance));
    streamWriter.writeObject(getMetadata(instance));
    streamWriter.writeBoolean(getSearchArchived(instance));
    
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.QueryMetadataPageRequest_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.QueryMetadataPageRequest_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.QueryMetadataPageRequest)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.QueryMetadataPageRequest_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.QueryMetadataPageRequest)object);
  }
  
}
