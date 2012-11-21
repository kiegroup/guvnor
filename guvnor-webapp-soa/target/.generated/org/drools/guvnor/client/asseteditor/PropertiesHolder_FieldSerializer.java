package org.drools.guvnor.client.asseteditor;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PropertiesHolder_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.PropertiesHolder instance) throws SerializationException {
    instance.list = (java.util.List) streamReader.readObject();
    
  }
  
  public static org.drools.guvnor.client.asseteditor.PropertiesHolder instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.PropertiesHolder();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.PropertiesHolder instance) throws SerializationException {
    streamWriter.writeObject(instance.list);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.PropertiesHolder_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.PropertiesHolder_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.PropertiesHolder)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.PropertiesHolder_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.PropertiesHolder)object);
  }
  
}
