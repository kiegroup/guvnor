package com.google.gwt.user.client.ui;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class MultiWordSuggestOracle_MultiWordSuggestion_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getDisplayString(com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion instance) /*-{
    return instance.@com.google.gwt.user.client.ui.MultiWordSuggestOracle$MultiWordSuggestion::displayString;
  }-*/;
  
  private static native void setDisplayString(com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion instance, java.lang.String value) 
  /*-{
    instance.@com.google.gwt.user.client.ui.MultiWordSuggestOracle$MultiWordSuggestion::displayString = value;
  }-*/;
  
  private static native java.lang.String getReplacementString(com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion instance) /*-{
    return instance.@com.google.gwt.user.client.ui.MultiWordSuggestOracle$MultiWordSuggestion::replacementString;
  }-*/;
  
  private static native void setReplacementString(com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion instance, java.lang.String value) 
  /*-{
    instance.@com.google.gwt.user.client.ui.MultiWordSuggestOracle$MultiWordSuggestion::replacementString = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion instance) throws SerializationException {
    setDisplayString(instance, streamReader.readString());
    setReplacementString(instance, streamReader.readString());
    
  }
  
  public static com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion instance) throws SerializationException {
    streamWriter.writeString(getDisplayString(instance));
    streamWriter.writeString(getReplacementString(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.user.client.ui.MultiWordSuggestOracle_MultiWordSuggestion_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.user.client.ui.MultiWordSuggestOracle_MultiWordSuggestion_FieldSerializer.deserialize(reader, (com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.user.client.ui.MultiWordSuggestOracle_MultiWordSuggestion_FieldSerializer.serialize(writer, (com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion)object);
  }
  
}
