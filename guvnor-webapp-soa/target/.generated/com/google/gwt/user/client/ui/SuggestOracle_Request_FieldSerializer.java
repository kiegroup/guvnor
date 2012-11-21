package com.google.gwt.user.client.ui;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SuggestOracle_Request_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native int getLimit(com.google.gwt.user.client.ui.SuggestOracle.Request instance) /*-{
    return instance.@com.google.gwt.user.client.ui.SuggestOracle$Request::limit;
  }-*/;
  
  private static native void setLimit(com.google.gwt.user.client.ui.SuggestOracle.Request instance, int value) 
  /*-{
    instance.@com.google.gwt.user.client.ui.SuggestOracle$Request::limit = value;
  }-*/;
  
  private static native java.lang.String getQuery(com.google.gwt.user.client.ui.SuggestOracle.Request instance) /*-{
    return instance.@com.google.gwt.user.client.ui.SuggestOracle$Request::query;
  }-*/;
  
  private static native void setQuery(com.google.gwt.user.client.ui.SuggestOracle.Request instance, java.lang.String value) 
  /*-{
    instance.@com.google.gwt.user.client.ui.SuggestOracle$Request::query = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.google.gwt.user.client.ui.SuggestOracle.Request instance) throws SerializationException {
    setLimit(instance, streamReader.readInt());
    setQuery(instance, streamReader.readString());
    
  }
  
  public static com.google.gwt.user.client.ui.SuggestOracle.Request instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.google.gwt.user.client.ui.SuggestOracle.Request();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.google.gwt.user.client.ui.SuggestOracle.Request instance) throws SerializationException {
    streamWriter.writeInt(getLimit(instance));
    streamWriter.writeString(getQuery(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.user.client.ui.SuggestOracle_Request_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.user.client.ui.SuggestOracle_Request_FieldSerializer.deserialize(reader, (com.google.gwt.user.client.ui.SuggestOracle.Request)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.user.client.ui.SuggestOracle_Request_FieldSerializer.serialize(writer, (com.google.gwt.user.client.ui.SuggestOracle.Request)object);
  }
  
}
