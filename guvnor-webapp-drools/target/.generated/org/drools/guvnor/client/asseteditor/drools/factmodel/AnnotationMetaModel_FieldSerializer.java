package org.drools.guvnor.client.asseteditor.drools.factmodel;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AnnotationMetaModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel instance) throws SerializationException {
    instance.name = streamReader.readString();
    instance.values = (java.util.Map) streamReader.readObject();
    
  }
  
  public static org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel instance) throws SerializationException {
    streamWriter.writeString(instance.name);
    streamWriter.writeObject(instance.values);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel)object);
  }
  
}
