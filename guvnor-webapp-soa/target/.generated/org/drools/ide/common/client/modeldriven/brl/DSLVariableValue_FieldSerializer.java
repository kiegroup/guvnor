package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DSLVariableValue_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getValue(org.drools.ide.common.client.modeldriven.brl.DSLVariableValue instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.DSLVariableValue::value;
  }-*/;
  
  private static native void setValue(org.drools.ide.common.client.modeldriven.brl.DSLVariableValue instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.DSLVariableValue::value = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.DSLVariableValue instance) throws SerializationException {
    setValue(instance, streamReader.readString());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.DSLVariableValue instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.DSLVariableValue();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.DSLVariableValue instance) throws SerializationException {
    streamWriter.writeString(getValue(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.DSLVariableValue_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.DSLVariableValue_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.DSLVariableValue)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.DSLVariableValue_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.DSLVariableValue)object);
  }
  
}
