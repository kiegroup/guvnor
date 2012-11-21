package org.drools.ide.common.client.modeldriven.dt;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DTColumnConfig_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt.DTColumnConfig instance) throws SerializationException {
    instance.defaultValue = streamReader.readString();
    instance.hideColumn = streamReader.readBoolean();
    instance.reverseOrder = streamReader.readBoolean();
    instance.useRowNumber = streamReader.readBoolean();
    instance.width = streamReader.readInt();
    
  }
  
  public static org.drools.ide.common.client.modeldriven.dt.DTColumnConfig instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt.DTColumnConfig();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt.DTColumnConfig instance) throws SerializationException {
    streamWriter.writeString(instance.defaultValue);
    streamWriter.writeBoolean(instance.hideColumn);
    streamWriter.writeBoolean(instance.reverseOrder);
    streamWriter.writeBoolean(instance.useRowNumber);
    streamWriter.writeInt(instance.width);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt.DTColumnConfig_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.DTColumnConfig_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt.DTColumnConfig)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.DTColumnConfig_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt.DTColumnConfig)object);
  }
  
}
