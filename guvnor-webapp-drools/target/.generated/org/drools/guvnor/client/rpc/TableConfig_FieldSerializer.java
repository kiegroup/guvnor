package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class TableConfig_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.TableConfig instance) throws SerializationException {
    instance.headerTypes = (java.lang.String[]) streamReader.readObject();
    instance.headers = (java.lang.String[]) streamReader.readObject();
    instance.rowsPerPage = streamReader.readInt();
    
  }
  
  public static org.drools.guvnor.client.rpc.TableConfig instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.TableConfig();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.TableConfig instance) throws SerializationException {
    streamWriter.writeObject(instance.headerTypes);
    streamWriter.writeObject(instance.headers);
    streamWriter.writeInt(instance.rowsPerPage);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.TableConfig_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.TableConfig_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.TableConfig)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.TableConfig_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.TableConfig)object);
  }
  
}
