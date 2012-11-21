package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class WorkingSetConfigData_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.WorkingSetConfigData instance) throws SerializationException {
    instance.constraints = (java.util.List) streamReader.readObject();
    instance.customForms = (java.util.List) streamReader.readObject();
    instance.description = streamReader.readString();
    instance.name = streamReader.readString();
    instance.validFacts = (java.lang.String[]) streamReader.readObject();
    instance.workingSets = (org.drools.guvnor.client.rpc.WorkingSetConfigData[]) streamReader.readObject();
    
  }
  
  public static org.drools.guvnor.client.rpc.WorkingSetConfigData instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.WorkingSetConfigData();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.WorkingSetConfigData instance) throws SerializationException {
    streamWriter.writeObject(instance.constraints);
    streamWriter.writeObject(instance.customForms);
    streamWriter.writeString(instance.description);
    streamWriter.writeString(instance.name);
    streamWriter.writeObject(instance.validFacts);
    streamWriter.writeObject(instance.workingSets);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.WorkingSetConfigData_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.WorkingSetConfigData_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.WorkingSetConfigData)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.WorkingSetConfigData_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.WorkingSetConfigData)object);
  }
  
}
