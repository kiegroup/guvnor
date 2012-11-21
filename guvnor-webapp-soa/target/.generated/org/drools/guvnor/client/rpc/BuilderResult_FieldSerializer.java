package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class BuilderResult_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getLines(org.drools.guvnor.client.rpc.BuilderResult instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.BuilderResult::lines;
  }-*/;
  
  private static native void setLines(org.drools.guvnor.client.rpc.BuilderResult instance, java.util.List value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.BuilderResult::lines = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.BuilderResult instance) throws SerializationException {
    setLines(instance, (java.util.List) streamReader.readObject());
    
  }
  
  public static org.drools.guvnor.client.rpc.BuilderResult instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.BuilderResult();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.BuilderResult instance) throws SerializationException {
    streamWriter.writeObject(getLines(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.BuilderResult_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.BuilderResult_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.BuilderResult)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.BuilderResult_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.BuilderResult)object);
  }
  
}
