package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FormContentModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getJson(org.drools.guvnor.client.rpc.FormContentModel instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.FormContentModel::json;
  }-*/;
  
  private static native void setJson(org.drools.guvnor.client.rpc.FormContentModel instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.FormContentModel::json = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.FormContentModel instance) throws SerializationException {
    setJson(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.rpc.FormContentModel instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.FormContentModel();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.FormContentModel instance) throws SerializationException {
    streamWriter.writeString(getJson(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.FormContentModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.FormContentModel_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.FormContentModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.FormContentModel_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.FormContentModel)object);
  }
  
}
