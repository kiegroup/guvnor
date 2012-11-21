package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Module_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.Module instance) throws SerializationException {
    instance.archived = streamReader.readBoolean();
    instance.catRules = (java.util.HashMap) streamReader.readObject();
    instance.dependencies = (java.lang.String[]) streamReader.readObject();
    instance.externalURI = streamReader.readString();
    instance.header = streamReader.readString();
    instance.isSnapshot = streamReader.readBoolean();
    instance.snapshotName = streamReader.readString();
    instance.subModules = (org.drools.guvnor.client.rpc.Module[]) streamReader.readObject();
    instance.workspaces = (java.lang.String[]) streamReader.readObject();
    
    org.drools.guvnor.client.rpc.Artifact_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.Module instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.Module();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.Module instance) throws SerializationException {
    streamWriter.writeBoolean(instance.archived);
    streamWriter.writeObject(instance.catRules);
    streamWriter.writeObject(instance.dependencies);
    streamWriter.writeString(instance.externalURI);
    streamWriter.writeString(instance.header);
    streamWriter.writeBoolean(instance.isSnapshot);
    streamWriter.writeString(instance.snapshotName);
    streamWriter.writeObject(instance.subModules);
    streamWriter.writeObject(instance.workspaces);
    
    org.drools.guvnor.client.rpc.Artifact_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.Module_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.Module_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.Module)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.Module_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.Module)object);
  }
  
}
