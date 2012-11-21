package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Artifact_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.Artifact instance) throws SerializationException {
    instance.checkinComment = streamReader.readString();
    instance.dateCreated = (java.util.Date) streamReader.readObject();
    instance.description = streamReader.readString();
    instance.format = streamReader.readString();
    instance.lastContributor = streamReader.readString();
    instance.lastModified = (java.util.Date) streamReader.readObject();
    instance.name = streamReader.readString();
    instance.readonly = streamReader.readBoolean();
    instance.state = streamReader.readString();
    instance.uuid = streamReader.readString();
    instance.versionNumber = streamReader.readLong();
    
  }
  
  public static org.drools.guvnor.client.rpc.Artifact instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.Artifact();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.Artifact instance) throws SerializationException {
    streamWriter.writeString(instance.checkinComment);
    streamWriter.writeObject(instance.dateCreated);
    streamWriter.writeString(instance.description);
    streamWriter.writeString(instance.format);
    streamWriter.writeString(instance.lastContributor);
    streamWriter.writeObject(instance.lastModified);
    streamWriter.writeString(instance.name);
    streamWriter.writeBoolean(instance.readonly);
    streamWriter.writeString(instance.state);
    streamWriter.writeString(instance.uuid);
    streamWriter.writeLong(instance.versionNumber);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.Artifact_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.Artifact_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.Artifact)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.Artifact_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.Artifact)object);
  }
  
}
