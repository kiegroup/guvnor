package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class TableDataRow_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.TableDataRow instance) throws SerializationException {
    instance.format = streamReader.readString();
    instance.id = streamReader.readString();
    instance.values = (java.lang.String[]) streamReader.readObject();
    
  }
  
  public static org.drools.guvnor.client.rpc.TableDataRow instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.TableDataRow();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.TableDataRow instance) throws SerializationException {
    streamWriter.writeString(instance.format);
    streamWriter.writeString(instance.id);
    streamWriter.writeObject(instance.values);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.TableDataRow_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.TableDataRow_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.TableDataRow)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.TableDataRow_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.TableDataRow)object);
  }
  
}
