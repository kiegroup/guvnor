package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DSLSentence_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getDefinition(org.drools.ide.common.client.modeldriven.brl.DSLSentence instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.DSLSentence::definition;
  }-*/;
  
  private static native void setDefinition(org.drools.ide.common.client.modeldriven.brl.DSLSentence instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.DSLSentence::definition = value;
  }-*/;
  
  private static native java.lang.String getSentence(org.drools.ide.common.client.modeldriven.brl.DSLSentence instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.DSLSentence::sentence;
  }-*/;
  
  private static native void setSentence(org.drools.ide.common.client.modeldriven.brl.DSLSentence instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.DSLSentence::sentence = value;
  }-*/;
  
  private static native java.util.List getValues(org.drools.ide.common.client.modeldriven.brl.DSLSentence instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.DSLSentence::values;
  }-*/;
  
  private static native void setValues(org.drools.ide.common.client.modeldriven.brl.DSLSentence instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.DSLSentence::values = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.DSLSentence instance) throws SerializationException {
    setDefinition(instance, streamReader.readString());
    setSentence(instance, streamReader.readString());
    setValues(instance, (java.util.List) streamReader.readObject());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.DSLSentence instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.DSLSentence();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.DSLSentence instance) throws SerializationException {
    streamWriter.writeString(getDefinition(instance));
    streamWriter.writeString(getSentence(instance));
    streamWriter.writeObject(getValues(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.DSLSentence_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.DSLSentence_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.DSLSentence)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.DSLSentence_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.DSLSentence)object);
  }
  
}
