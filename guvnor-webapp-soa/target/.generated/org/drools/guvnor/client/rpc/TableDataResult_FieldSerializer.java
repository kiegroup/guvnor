package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class TableDataResult_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.TableDataResult instance) throws SerializationException {
    instance.currentPosition = streamReader.readLong();
    instance.data = (org.drools.guvnor.client.rpc.TableDataRow[]) streamReader.readObject();
    instance.hasNext = streamReader.readBoolean();
    instance.total = streamReader.readLong();
    
  }
  
  public static org.drools.guvnor.client.rpc.TableDataResult instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.TableDataResult();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.TableDataResult instance) throws SerializationException {
    streamWriter.writeLong(instance.currentPosition);
    streamWriter.writeObject(instance.data);
    streamWriter.writeBoolean(instance.hasNext);
    streamWriter.writeLong(instance.total);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.TableDataResult_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.TableDataResult_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.TableDataResult)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.TableDataResult_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.TableDataResult)object);
  }
  
}
