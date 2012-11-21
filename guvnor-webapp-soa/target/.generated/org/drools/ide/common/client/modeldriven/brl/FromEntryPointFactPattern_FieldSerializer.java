package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FromEntryPointFactPattern_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getEntryPointName(org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern::entryPointName;
  }-*/;
  
  private static native void setEntryPointName(org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern::entryPointName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern instance) throws SerializationException {
    setEntryPointName(instance, streamReader.readString());
    
    org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern instance) throws SerializationException {
    streamWriter.writeString(getEntryPointName(instance));
    
    org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern)object);
  }
  
}
