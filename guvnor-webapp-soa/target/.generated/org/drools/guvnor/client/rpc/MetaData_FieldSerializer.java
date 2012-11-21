package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class MetaData_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native boolean getBinary(org.drools.guvnor.client.rpc.MetaData instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.MetaData::binary;
  }-*/;
  
  private static native void setBinary(org.drools.guvnor.client.rpc.MetaData instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.MetaData::binary = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.MetaData instance) throws SerializationException {
    setBinary(instance, streamReader.readBoolean());
    instance.categories = (java.lang.String[]) streamReader.readObject();
    instance.coverage = streamReader.readString();
    instance.creator = streamReader.readString();
    instance.dateEffective = (java.util.Date) streamReader.readObject();
    instance.dateExpired = (java.util.Date) streamReader.readObject();
    instance.disabled = streamReader.readBoolean();
    instance.externalRelation = streamReader.readString();
    instance.externalSource = streamReader.readString();
    instance.hasPreceedingVersion = streamReader.readBoolean();
    instance.hasSucceedingVersion = streamReader.readBoolean();
    instance.moduleName = streamReader.readString();
    instance.moduleUUID = streamReader.readString();
    instance.publisher = streamReader.readString();
    instance.rights = streamReader.readString();
    instance.subject = streamReader.readString();
    instance.title = streamReader.readString();
    instance.type = streamReader.readString();
    
  }
  
  public static org.drools.guvnor.client.rpc.MetaData instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.MetaData();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.MetaData instance) throws SerializationException {
    streamWriter.writeBoolean(getBinary(instance));
    streamWriter.writeObject(instance.categories);
    streamWriter.writeString(instance.coverage);
    streamWriter.writeString(instance.creator);
    streamWriter.writeObject(instance.dateEffective);
    streamWriter.writeObject(instance.dateExpired);
    streamWriter.writeBoolean(instance.disabled);
    streamWriter.writeString(instance.externalRelation);
    streamWriter.writeString(instance.externalSource);
    streamWriter.writeBoolean(instance.hasPreceedingVersion);
    streamWriter.writeBoolean(instance.hasSucceedingVersion);
    streamWriter.writeString(instance.moduleName);
    streamWriter.writeString(instance.moduleUUID);
    streamWriter.writeString(instance.publisher);
    streamWriter.writeString(instance.rights);
    streamWriter.writeString(instance.subject);
    streamWriter.writeString(instance.title);
    streamWriter.writeString(instance.type);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.MetaData_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.MetaData_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.MetaData)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.MetaData_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.MetaData)object);
  }
  
}
