package org.drools.guvnor.client.asseteditor.drools.modeldriven;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SetFactTypeFilter_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.drools.modeldriven.SetFactTypeFilter instance) throws SerializationException {
    
  }
  
  public static org.drools.guvnor.client.asseteditor.drools.modeldriven.SetFactTypeFilter instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.drools.modeldriven.SetFactTypeFilter();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.drools.modeldriven.SetFactTypeFilter instance) throws SerializationException {
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.drools.modeldriven.SetFactTypeFilter_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.modeldriven.SetFactTypeFilter_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.drools.modeldriven.SetFactTypeFilter)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.modeldriven.SetFactTypeFilter_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.drools.modeldriven.SetFactTypeFilter)object);
  }
  
}
