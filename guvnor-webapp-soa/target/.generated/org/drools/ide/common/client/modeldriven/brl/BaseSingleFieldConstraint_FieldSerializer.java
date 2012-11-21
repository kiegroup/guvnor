package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class BaseSingleFieldConstraint_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint instance) throws SerializationException {
    instance.constraintValueType = streamReader.readInt();
    instance.expression = (org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine) streamReader.readObject();
    instance.operator = streamReader.readString();
    instance.parameters = (java.util.Map) streamReader.readObject();
    instance.value = streamReader.readString();
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint instance) throws SerializationException {
    streamWriter.writeInt(instance.constraintValueType);
    streamWriter.writeObject(instance.expression);
    streamWriter.writeString(instance.operator);
    streamWriter.writeObject(instance.parameters);
    streamWriter.writeString(instance.value);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint)object);
  }
  
}
