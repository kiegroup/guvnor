package com.google.gwt.user.client.ui;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SuggestOracle_Response_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native boolean getMoreSuggestions(com.google.gwt.user.client.ui.SuggestOracle.Response instance) /*-{
    return instance.@com.google.gwt.user.client.ui.SuggestOracle$Response::moreSuggestions;
  }-*/;
  
  private static native void setMoreSuggestions(com.google.gwt.user.client.ui.SuggestOracle.Response instance, boolean value) 
  /*-{
    instance.@com.google.gwt.user.client.ui.SuggestOracle$Response::moreSuggestions = value;
  }-*/;
  
  private static native int getNumMoreSuggestions(com.google.gwt.user.client.ui.SuggestOracle.Response instance) /*-{
    return instance.@com.google.gwt.user.client.ui.SuggestOracle$Response::numMoreSuggestions;
  }-*/;
  
  private static native void setNumMoreSuggestions(com.google.gwt.user.client.ui.SuggestOracle.Response instance, int value) 
  /*-{
    instance.@com.google.gwt.user.client.ui.SuggestOracle$Response::numMoreSuggestions = value;
  }-*/;
  
  private static native java.util.Collection getSuggestions(com.google.gwt.user.client.ui.SuggestOracle.Response instance) /*-{
    return instance.@com.google.gwt.user.client.ui.SuggestOracle$Response::suggestions;
  }-*/;
  
  private static native void setSuggestions(com.google.gwt.user.client.ui.SuggestOracle.Response instance, java.util.Collection value) 
  /*-{
    instance.@com.google.gwt.user.client.ui.SuggestOracle$Response::suggestions = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.google.gwt.user.client.ui.SuggestOracle.Response instance) throws SerializationException {
    setMoreSuggestions(instance, streamReader.readBoolean());
    setNumMoreSuggestions(instance, streamReader.readInt());
    setSuggestions(instance, (java.util.Collection) streamReader.readObject());
    
  }
  
  public static com.google.gwt.user.client.ui.SuggestOracle.Response instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.google.gwt.user.client.ui.SuggestOracle.Response();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.google.gwt.user.client.ui.SuggestOracle.Response instance) throws SerializationException {
    streamWriter.writeBoolean(getMoreSuggestions(instance));
    streamWriter.writeInt(getNumMoreSuggestions(instance));
    streamWriter.writeObject(getSuggestions(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.user.client.ui.SuggestOracle_Response_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.user.client.ui.SuggestOracle_Response_FieldSerializer.deserialize(reader, (com.google.gwt.user.client.ui.SuggestOracle.Response)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.user.client.ui.SuggestOracle_Response_FieldSerializer.serialize(writer, (com.google.gwt.user.client.ui.SuggestOracle.Response)object);
  }
  
}
