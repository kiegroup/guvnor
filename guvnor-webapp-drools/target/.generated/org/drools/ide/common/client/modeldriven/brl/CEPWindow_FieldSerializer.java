package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class CEPWindow_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.CEPWindow instance) throws SerializationException {
    instance.operator = streamReader.readString();
    instance.parameters = (java.util.Map) streamReader.readObject();
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.CEPWindow instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.CEPWindow();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.CEPWindow instance) throws SerializationException {
    streamWriter.writeString(instance.operator);
    streamWriter.writeObject(instance.parameters);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.CEPWindow_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.CEPWindow_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.CEPWindow)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.CEPWindow_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.CEPWindow)object);
  }
  
}
