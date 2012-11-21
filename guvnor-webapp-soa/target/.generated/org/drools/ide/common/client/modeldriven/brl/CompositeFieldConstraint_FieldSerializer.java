package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class CompositeFieldConstraint_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint instance) throws SerializationException {
    instance.compositeJunctionType = streamReader.readString();
    instance.constraints = (org.drools.ide.common.client.modeldriven.brl.FieldConstraint[]) streamReader.readObject();
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint instance) throws SerializationException {
    streamWriter.writeString(instance.compositeJunctionType);
    streamWriter.writeObject(instance.constraints);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint)object);
  }
  
}
