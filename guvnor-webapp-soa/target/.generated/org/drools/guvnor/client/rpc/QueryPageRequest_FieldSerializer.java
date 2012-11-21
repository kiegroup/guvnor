package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class QueryPageRequest_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.Boolean getIsCaseSensitive(org.drools.guvnor.client.rpc.QueryPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryPageRequest::isCaseSensitive;
  }-*/;
  
  private static native void setIsCaseSensitive(org.drools.guvnor.client.rpc.QueryPageRequest instance, java.lang.Boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryPageRequest::isCaseSensitive = value;
  }-*/;
  
  private static native java.lang.Boolean getSearchArchived(org.drools.guvnor.client.rpc.QueryPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryPageRequest::searchArchived;
  }-*/;
  
  private static native void setSearchArchived(org.drools.guvnor.client.rpc.QueryPageRequest instance, java.lang.Boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryPageRequest::searchArchived = value;
  }-*/;
  
  private static native java.lang.String getSearchText(org.drools.guvnor.client.rpc.QueryPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryPageRequest::searchText;
  }-*/;
  
  private static native void setSearchText(org.drools.guvnor.client.rpc.QueryPageRequest instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryPageRequest::searchText = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.QueryPageRequest instance) throws SerializationException {
    setIsCaseSensitive(instance, (java.lang.Boolean) streamReader.readObject());
    setSearchArchived(instance, (java.lang.Boolean) streamReader.readObject());
    setSearchText(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.QueryPageRequest instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.QueryPageRequest();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.QueryPageRequest instance) throws SerializationException {
    streamWriter.writeObject(getIsCaseSensitive(instance));
    streamWriter.writeObject(getSearchArchived(instance));
    streamWriter.writeString(getSearchText(instance));
    
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.QueryPageRequest_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.QueryPageRequest_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.QueryPageRequest)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.QueryPageRequest_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.QueryPageRequest)object);
  }
  
}
