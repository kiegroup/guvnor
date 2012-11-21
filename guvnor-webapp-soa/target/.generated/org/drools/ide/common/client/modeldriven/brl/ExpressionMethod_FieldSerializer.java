package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ExpressionMethod_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.Map getParams(org.drools.ide.common.client.modeldriven.brl.ExpressionMethod instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionMethod::params;
  }-*/;
  
  private static native void setParams(org.drools.ide.common.client.modeldriven.brl.ExpressionMethod instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionMethod::params = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ExpressionMethod instance) throws SerializationException {
    setParams(instance, (java.util.Map) streamReader.readObject());
    
    org.drools.ide.common.client.modeldriven.brl.ExpressionPart_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.ExpressionMethod instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.ExpressionMethod();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ExpressionMethod instance) throws SerializationException {
    streamWriter.writeObject(getParams(instance));
    
    org.drools.ide.common.client.modeldriven.brl.ExpressionPart_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.ExpressionMethod_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ExpressionMethod_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.ExpressionMethod)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ExpressionMethod_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.ExpressionMethod)object);
  }
  
}
