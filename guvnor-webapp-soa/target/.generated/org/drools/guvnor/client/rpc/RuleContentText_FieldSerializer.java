package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class RuleContentText_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.RuleContentText instance) throws SerializationException {
    instance.content = streamReader.readString();
    
  }
  
  public static org.drools.guvnor.client.rpc.RuleContentText instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.RuleContentText();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.RuleContentText instance) throws SerializationException {
    streamWriter.writeString(instance.content);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.RuleContentText_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.RuleContentText_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.RuleContentText)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.RuleContentText_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.RuleContentText)object);
  }
  
}
