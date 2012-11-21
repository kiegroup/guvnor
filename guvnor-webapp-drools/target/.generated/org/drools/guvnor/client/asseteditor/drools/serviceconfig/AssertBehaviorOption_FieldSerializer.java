package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AssertBehaviorOption_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption instance) throws SerializationException {
    // Enum deserialization is handled via the instantiate method
  }
  
  public static org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption instantiate(SerializationStreamReader streamReader) throws SerializationException {
    int ordinal = streamReader.readInt();
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption[] values = org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption.values();
    assert (ordinal >= 0 && ordinal < values.length);
    return values[ordinal];
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption instance) throws SerializationException {
    assert (instance != null);
    streamWriter.writeInt(instance.ordinal());
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption)object);
  }
  
}
