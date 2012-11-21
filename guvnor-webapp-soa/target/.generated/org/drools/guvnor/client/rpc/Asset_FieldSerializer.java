package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Asset_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.Asset instance) throws SerializationException {
    instance.content = (org.drools.ide.common.client.modeldriven.brl.PortableObject) streamReader.readObject();
    instance.metaData = (org.drools.guvnor.client.rpc.MetaData) streamReader.readObject();
    
    org.drools.guvnor.client.rpc.Artifact_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.Asset instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.Asset();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.Asset instance) throws SerializationException {
    streamWriter.writeObject(instance.content);
    streamWriter.writeObject(instance.metaData);
    
    org.drools.guvnor.client.rpc.Artifact_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.Asset_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.Asset_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.Asset)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.Asset_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.Asset)object);
  }
  
}
