package org.drools.guvnor.client.asseteditor.drools.factmodel;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FactModels_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels instance) throws SerializationException {
    instance.models = (java.util.List) streamReader.readObject();
    
  }
  
  public static org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels instance) throws SerializationException {
    streamWriter.writeObject(instance.models);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels)object);
  }
  
}
