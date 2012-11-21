package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DSLComplexVariableValue_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getId(org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue::id;
  }-*/;
  
  private static native void setId(org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue::id = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue instance) throws SerializationException {
    setId(instance, streamReader.readString());
    
    org.drools.ide.common.client.modeldriven.brl.DSLVariableValue_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue instance) throws SerializationException {
    streamWriter.writeString(getId(instance));
    
    org.drools.ide.common.client.modeldriven.brl.DSLVariableValue_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue)object);
  }
  
}
