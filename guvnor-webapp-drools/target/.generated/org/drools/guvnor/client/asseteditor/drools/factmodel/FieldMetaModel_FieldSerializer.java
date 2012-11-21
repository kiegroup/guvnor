package org.drools.guvnor.client.asseteditor.drools.factmodel;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FieldMetaModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel instance) throws SerializationException {
    instance.name = streamReader.readString();
    instance.type = streamReader.readString();
    
  }
  
  public static org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel instance) throws SerializationException {
    streamWriter.writeString(instance.name);
    streamWriter.writeString(instance.type);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel)object);
  }
  
}
